package com.talearnt.post.community;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.post.community.entity.CommunityPost;
import com.talearnt.post.community.repository.CommunityPostQueryRepository;
import com.talearnt.post.community.repository.CommunityPostRepository;
import com.talearnt.post.community.request.CommunityPostReqDTO;
import com.talearnt.post.community.response.CommunityPostDetailResDTO;
import com.talearnt.s3.FileUploadService;
import com.talearnt.util.common.PostUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;
    private final FileUploadService fileUploadService;
    private final CommunityPostQueryRepository communityPostQueryRepository;

    /**커뮤니티 게시글 상세보기
     * 조건)
     * - 존재하는 게시글인가?
     * - 조회수 증가*/
    @Transactional
    public CommunityPostDetailResDTO getCommunityPostDetail(Long postNo, Authentication auth){
        log.info("커뮤니티 게시글 상세보기 시작 : {}", postNo);

        //유저 번호 얻어오기 -> 좋아요 게시글 여부
        Long userNo = PostUtil.getCurrentUserNo(auth);

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
        if (uploadCount != null && uploadCount < 1){
            log.error("커뮤니티 게시글 등록 실패 - 이미지 업로드 실패 : {}", ErrorCode.FILE_FAILED_UPLOAD);
            throw new CustomRuntimeException(ErrorCode.FILE_FAILED_UPLOAD);
        }

        log.info("커뮤니티 게시글 등록 끝");
        return "성공적으로 커뮤니티 게시글을 등록하였습니다.";
    }
}
