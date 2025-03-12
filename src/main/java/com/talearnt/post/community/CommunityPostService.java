package com.talearnt.post.community;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.post.community.entity.CommunityPost;
import com.talearnt.post.community.repository.CommunityPostQueryRepository;
import com.talearnt.post.community.repository.CommunityPostRepository;
import com.talearnt.post.community.request.CommunityPostReqDTO;
import com.talearnt.post.community.request.CommunityPostSearchConditionDTO;
import com.talearnt.post.community.response.CommunityPostDetailResDTO;
import com.talearnt.post.community.response.CommunityPostListResDTO;
import com.talearnt.s3.FileUploadService;
import com.talearnt.util.common.PageUtil;
import com.talearnt.util.common.PostUtil;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.log.LogRunningTime;
import com.talearnt.util.response.PaginatedResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;
    private final FileUploadService fileUploadService;
    private final CommunityPostQueryRepository communityPostQueryRepository;

    /** 커뮤니티 게시글 목록 조회
     * 조건)
     * - 내가 좋아요 게시글 눌른 것이 있는가?
     * - 모바일인가? 웹인가?
     * */
    @LogRunningTime
    public PaginatedResponse<List<CommunityPostListResDTO>> getCommunityPostList(Authentication authentication, String postType, String order, String path, String lastNo, String page, String size){
        log.info("커뮤니티 게시글 목록 조회 시작 : {} - {} - {}",postType,order,path);

        //로그인 여부 확인, 안했을 경우 user no = 0; 좋아요 여부 확인용!
        Long userNo = PostUtil.getCurrentUserNo("커뮤니티 게시글 목록 조회", authentication);

        //Search Condition 생성
        CommunityPostSearchConditionDTO condition = CommunityPostSearchConditionDTO.builder()
                .postType(postType)
                .order(order)
                .path(path)
                .page(page)
                .size(size)
                .lastNo(lastNo)
                .build();

        //웹 게시글 목록 가져오기


        //모바일 게시글 목록 가져오기
        Page<CommunityPostListResDTO> result = communityPostQueryRepository.getCommunityPostList(userNo,condition);

        log.info("커뮤니티 게시글 목록 조회 끝");
        return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToMobile(result));
    }


    /**커뮤니티 게시글 상세보기
     * 조건)
     * - 존재하는 게시글인가?
     * - 조회수 증가*/
    @Transactional
    public CommunityPostDetailResDTO getCommunityPostDetail(Long postNo, Authentication auth){
        log.info("커뮤니티 게시글 상세보기 시작 : {}", postNo);

        //유저 번호 얻어오기 -> 좋아요 게시글 여부
        Long userNo = PostUtil.getCurrentUserNo("커뮤니티 게시글 상세보기",auth);

        //게시글 Count++ 및 게시글 조회
        CommunityPostDetailResDTO result = communityPostQueryRepository.getCommunityPostByPostNo(postNo,userNo)
                .orElseThrow(()->{
                    log.error("커뮤니티 게시글 상세보기 실패 - 찾는 게시글이 없음 : {}",ErrorCode.POST_NOT_FOUND);
                    return new CustomRuntimeException(ErrorCode.POST_NOT_FOUND);
                });

        log.info("커뮤니티 게시글 상세보기 끝");
        return result;
    }



    /** 커뮤니티 게시글 등록
     * 조건)
     * - 로그인 하였는가?
     * - Request DTO 값이 제대로 되었는가?
     * - 이미지 경로가 제대로 되었는가? (테스트 완료 후 추가)
     * */
    @Transactional
    public String addCommunityPost(CommunityPostReqDTO reqDTO){
        log.info("커뮤니티 게시글 등록 시작 : {}", reqDTO);

        //Req -> Enitiy로 변환
        CommunityPost communityPostEntity = CommunityPostMapper.INSTANCE.toEntity(reqDTO);

        //커뮤니티 게시글 등록
        CommunityPost addedCommunityPost = communityPostRepository.save(communityPostEntity);

        //이미지 업로드
        Integer uploadCount = fileUploadService.addPostFileUploads(addedCommunityPost.getCommunityPostNo(),reqDTO.getPostType(),reqDTO.getUserInfo().getUserNo(),reqDTO.getImageUrls());
        if (reqDTO.getImageUrls() != null && !reqDTO.getImageUrls().isEmpty() && uploadCount < 1){
            log.error("커뮤니티 게시글 등록 실패 - 이미지 업로드 실패 : {}", ErrorCode.FILE_FAILED_UPLOAD);
            throw new CustomRuntimeException(ErrorCode.FILE_FAILED_UPLOAD);
        }

        log.info("커뮤니티 게시글 등록 끝");
        return "성공적으로 커뮤니티 게시글을 등록하였습니다.";
    }


    /** 커뮤니티 게시글 수정
     * 이미지 수정을 했을 경우에 DB에 존재하는 이미지와 입력받은 이미지 중 없는 것은 삭제하고,
     * 만약 추가 이미지가 있다면, 삭제할 번호와 교체하여 수정 작업을 진행한다.
     * 추가만 있을 경우에는 이미지를 추가한다.
     * 조건)
     * - 로그인을 했는가?
     * - 본인의 게시글이 맞는가?
     * - 게시글이 삭제 되었는가?
     * - 이미지 수정했는가?
     * */
    @Transactional
    public Void updateCommunityPost(Long postNo, CommunityPostReqDTO communityPostReqDTO){
        log.info("커뮤니티 게시글 수정 시작 : {}",postNo);

        //삭제된 게시글인지 확인
        if(communityPostQueryRepository.isDeletedCommunityPost(postNo)){
            log.error("커뮤니티 게시글 수정 실패 - 삭제된 게시글임 : {}", ErrorCode.POST_FAILED_UPDATE);
            throw new CustomRuntimeException(ErrorCode.POST_FAILED_UPDATE);
        }

        //내 게시글이 맞는 지 확인
        if(communityPostQueryRepository.isMyCommunityPostByUserNo(postNo,communityPostReqDTO.getUserInfo().getUserNo())){
            log.error("커뮤니티 게시글 수정 실패 - 본인 게시글이 아님 : {}", ErrorCode.POST_ACCESS_DENIED);
            throw new CustomRuntimeException(ErrorCode.POST_ACCESS_DENIED);
        }

        //엔티티로 변환
        CommunityPost communityPost = CommunityPostMapper.INSTANCE.toEntity(communityPostReqDTO);
        communityPost.setCommunityPostNo(postNo);

        //엔티티 수정
        communityPostRepository.save(communityPost);
        //이미지 수정 메소드 미구현


        log.info("커뮤니티 게시글 수정 끝");
        return null;
    }


    /**커뮤니티 게시글 삭제
     * 커뮤니티 게시글 삭제 시 이미지를 지울 것인가에 대한 기획이 되어있지 않습니다.
     * 조건)
     * - 로그인을 했는가?
     * - 나의 게시글이 맞는가?
     * - 삭제된 게시글인가?
     * */
    @Transactional
    public Void deleteCommunityPost(Long postNo, Authentication authentication){
        log.info("커뮤니티 게시글 삭제 시작 : {} ", postNo);

        //로그인 여부 검증
        UserInfo userInfo = UserUtil.validateAuthentication("커뮤니티 게시글 삭제",authentication);

        //나의 게시글 맞는가? true == 아님 , false == 맞음
        if(communityPostQueryRepository.isMyCommunityPostByUserNo(postNo,userInfo.getUserNo())){
            log.error("커뮤니티 게시글 삭제 실패 - 본인 게시글이 아님 : {}", ErrorCode.POST_ACCESS_DENIED);
            throw new CustomRuntimeException(ErrorCode.POST_ACCESS_DENIED);
        }

        //커뮤니티 게시글 삭제
        if(communityPostQueryRepository.deleteCommunityPostByPostNo(postNo) != 1){
            log.error("커뮤니티 게시글 삭제 실패 - 0개 또는 여러 개의 게시글을 삭제 시도 : {}", ErrorCode.POST_FAILED_DELETE);
            throw new CustomRuntimeException(ErrorCode.POST_FAILED_DELETE);
        }

        log.info("커뮤니티 게시글 삭제 끝");
        return null;
    }


}
