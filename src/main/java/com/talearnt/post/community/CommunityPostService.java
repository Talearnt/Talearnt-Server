package com.talearnt.post.community;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.post.community.entity.CommunityPost;
import com.talearnt.post.community.entity.LikeCommunity;
import com.talearnt.post.community.repository.CommunityPostQueryRepository;
import com.talearnt.post.community.repository.CommunityPostRepository;
import com.talearnt.post.community.repository.LikeCommunityQueryRepository;
import com.talearnt.post.community.repository.LikeCommunityRepository;
import com.talearnt.post.community.request.CommunityPostReqDTO;
import com.talearnt.post.community.request.CommunityPostSearchCondition;
import com.talearnt.post.community.response.CommunityPostDetailResDTO;
import com.talearnt.post.community.response.CommunityPostListResDTO;
import com.talearnt.s3.FileUploadService;
import com.talearnt.s3.entity.FileUpload;
import com.talearnt.util.common.PageUtil;
import com.talearnt.util.common.PostUtil;
import com.talearnt.util.common.S3Util;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.filter.UserRequestLimiter;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.log.LogRunningTime;
import com.talearnt.util.pagination.PagedListWrapper;
import com.talearnt.util.response.PaginatedResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class CommunityPostService {

    private final UserRequestLimiter limiter;
    private final CommunityPostRepository communityPostRepository;
    private final FileUploadService fileUploadService;
    private final CommunityPostQueryRepository communityPostQueryRepository;
    private final LikeCommunityRepository  likeCommunityRepository;
    private final LikeCommunityQueryRepository likeCommunityQueryRepository;


    /**
     * 커뮤니티 게시글 목록 조회
     * 조건)
     * - 내가 좋아요 게시글 눌른 것이 있는가?
     * - 모바일인가? 웹인가?
     */
    @LogRunningTime
    public PaginatedResponse<List<CommunityPostListResDTO>> getCommunityPostList(Authentication authentication, String postType, String order, String path, String lastNo, String page, String size) {
        log.info("커뮤니티 게시글 목록 조회 시작 : {} - {} - {}", postType, order, path);

        //로그인 여부 확인, 안했을 경우 user no = 0; 좋아요 여부 확인용!
        Long userNo = PostUtil.getCurrentUserNo("커뮤니티 게시글 목록 조회", authentication);

        //Search Condition 생성
        CommunityPostSearchCondition condition = CommunityPostSearchCondition.builder()
                .postType(postType)
                .order(order)
                .path(path)
                .page(page)
                .size(size)
                .lastNo(lastNo)
                .build();

        //웹 게시글 목록 가져오기
        if ("web".equalsIgnoreCase(path)) {
            // LastNo가 null이 아니면 Exception 발생
            if (condition.getLastNo() != null) {
                log.error("커뮤니티 게시글 목록 조회 실패 - 웹 : lastNo 포함 : {}", ErrorCode.POST_FAILED_CALL_LIST);
                throw new CustomRuntimeException(ErrorCode.POST_FAILED_CALL_LIST);
            }

            //웹 게시글 목록 조회
            PagedListWrapper<CommunityPostListResDTO> wrapper = communityPostQueryRepository.getCommunityPostListToWeb(userNo, condition);
            //페이지 정보로 변환
            Page<CommunityPostListResDTO> result = new PageImpl<>(wrapper.getList(), condition.getPage(), wrapper.getPagedData().getTotal());
            //반환
            return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToWeb(result, wrapper.getPagedData().getLatestCreatedAt()));
        }

        //모바일 게시글 목록 가져오기
        Page<CommunityPostListResDTO> result = communityPostQueryRepository.getCommunityPostListToMobile(userNo, condition);

        log.info("커뮤니티 게시글 목록 조회 끝");
        return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToMobile(result));
    }


    /**
     * 커뮤니티 게시글 상세보기
     * 조건)
     * - 존재하는 게시글인가?
     * - 조회수 증가
     */
    @Transactional
    @LogRunningTime
    public CommunityPostDetailResDTO getCommunityPostDetail(Long postNo, Authentication auth) {
        log.info("커뮤니티 게시글 상세보기 시작 : {}", postNo);

        //유저 번호 얻어오기 -> 좋아요 게시글 여부
        Long userNo = PostUtil.getCurrentUserNo("커뮤니티 게시글 상세보기", auth);

        //게시글 Count++ 및 게시글 조회
        CommunityPostDetailResDTO result = communityPostQueryRepository.getCommunityPostByPostNo(postNo, userNo)
                .orElseThrow(() -> {
                    log.error("커뮤니티 게시글 상세보기 실패 - 찾는 게시글이 없음 : {}", ErrorCode.POST_NOT_FOUND);
                    return new CustomRuntimeException(ErrorCode.POST_NOT_FOUND);
                });

        log.info("커뮤니티 게시글 상세보기 끝");
        return result;
    }


    /**
     * 커뮤니티 게시글 등록
     * 조건)
     * - 로그인 하였는가?
     * - Request DTO 값이 제대로 되었는가?
     * - 이미지 경로가 제대로 되었는가? (테스트 완료 후 추가)
     */
    @Transactional
    @LogRunningTime
    public Long addCommunityPost(CommunityPostReqDTO reqDTO) {
        log.info("커뮤니티 게시글 등록 시작 : {}", reqDTO);

        //Req -> Enitiy로 변환
        CommunityPost communityPostEntity = CommunityPostMapper.INSTANCE.toEntity(reqDTO);

        //커뮤니티 게시글 등록
        CommunityPost addedCommunityPost = communityPostRepository.save(communityPostEntity);

        //이미지 업로드
        Integer uploadCount = fileUploadService.addPostFileUploads(addedCommunityPost.getCommunityPostNo(), reqDTO.getPostType(), reqDTO.getUserInfo().getUserNo(), reqDTO.getImageUrls());
        if (reqDTO.getImageUrls() != null && !reqDTO.getImageUrls().isEmpty() && uploadCount < 1) {
            log.error("커뮤니티 게시글 등록 실패 - 이미지 업로드 실패 : {}", ErrorCode.FILE_FAILED_UPLOAD);
            throw new CustomRuntimeException(ErrorCode.FILE_FAILED_UPLOAD);
        }

        log.info("커뮤니티 게시글 등록 끝");
        return addedCommunityPost.getCommunityPostNo();
    }


    /**
     * 커뮤니티 게시글 수정
     * 이미지 수정을 했을 경우에 DB에 존재하는 이미지와 입력받은 이미지 중 없는 것은 삭제하고,
     * 만약 추가 이미지가 있다면, 삭제할 번호와 교체하여 수정 작업을 진행한다.
     * 추가만 있을 경우에는 이미지를 추가한다.
     * 조건)
     * - 로그인을 했는가?
     * - 본인의 게시글이 맞는가?
     * - 게시글이 삭제 되었는가?
     * - 이미지 수정했는가?
     */
    @Transactional
    @LogRunningTime
    public Void updateCommunityPost(Long postNo, CommunityPostReqDTO communityPostReqDTO) {
        log.info("커뮤니티 게시글 수정 시작 : {}", postNo);

        CommunityPost communityPost = validatePostAccess("커뮤니티 게시글 수정",postNo, communityPostReqDTO.getUserInfo().getUserNo());

        //Mapper -> Enitity 
        communityPost = CommunityPostMapper.INSTANCE.updateEntity(postNo, communityPostReqDTO);
        
        //게시글 수정
        communityPostRepository.save(communityPost);

        //이미지 호출
        List<FileUpload> fileUploads = fileUploadService.findFileUploads(postNo, communityPostReqDTO.getPostType(),communityPostReqDTO.getUserInfo().getUserNo());

        //추가할 이미지 추출
        List<String> addFileUploadUrls = S3Util.willAddFileUploadUrls(fileUploads, communityPostReqDTO.getImageUrls());

        //추가할 이미지가 있는 경우
        if (!addFileUploadUrls.isEmpty()) {
            //추가할 이미지 추가
            fileUploadService.addPostFileUploads(postNo, communityPostReqDTO.getPostType(), communityPostReqDTO.getUserInfo().getUserNo(), addFileUploadUrls);
        }

        //삭제할 이미지 추출
        List<FileUpload> deleteFileUploads = S3Util.willDeleteFileUploads(fileUploads, communityPostReqDTO.getImageUrls());

        //삭제할 이미지가 있는 경우
        if (!deleteFileUploads.isEmpty()) {
            //삭제할 이미지 삭제
            fileUploadService.deleteFileUploads(deleteFileUploads, communityPostReqDTO.getUserInfo().getUserNo());
        }

        log.info("커뮤니티 게시글 수정 끝");
        return null;
    }


    /**
     * 커뮤니티 게시글 삭제
     * 커뮤니티 게시글 삭제 시 이미지를 지울 것인가에 대한 기획이 되어있지 않습니다.
     * 조건)
     * - 로그인을 했는가?
     * - 나의 게시글이 맞는가?
     * - 삭제된 게시글인가?
     */
    @Transactional
    @LogRunningTime
    public Void deleteCommunityPost(Long postNo, Authentication authentication) {
        log.info("커뮤니티 게시글 삭제 시작 : {} ", postNo);

        //로그인 여부 검증
        UserInfo userInfo = UserUtil.validateAuthentication("커뮤니티 게시글 삭제", authentication);

        //게시글 조건 탐색 후 Entity 가져오기
        CommunityPost communityPost = validatePostAccess("커뮤니티 게시글 삭제", postNo, userInfo.getUserNo());

        //게시물 삭제
        communityPost.setDeletedAt(LocalDateTime.now());

        //이미지 호출
        List<FileUpload> deleteFileUploads = fileUploadService.findFileUploads(postNo, communityPost.getPostType(), userInfo.getUserNo());

        //삭제할 이미지가 있는 경우
        if (!deleteFileUploads.isEmpty()) {
            //삭제할 이미지 삭제
            fileUploadService.deleteFileUploads(deleteFileUploads,userInfo.getUserNo());
        }


        log.info("커뮤니티 게시글 삭제 끝");
        return null;
    }


    /**
     * 커뮤니티 게시글 좋아요
     * 조건)
     * - 로그인 했는가?
     * - 게시글 존재 여부 확인
     * - 삭제된 게시글인가?
     * 클라이언트 쪽에서 한 번 이벤트 발생후 최소 2초 이상의 시간을 두고 다시 이벤트를 보내도록 설정하지 않으면 비동기 통신에서 스케쥴러가 쌓일 가능성이 높다.
     */
    @Async
    @LogRunningTime
    @Transactional
    public void likeCommunityPost(Long postNo, boolean isLike, UserInfo userInfo) {
        log.info("커뮤니티 게시글 좋아요 시작 : {}", postNo);


        if (!limiter.isAllowed(userInfo.getUserNo())){
            log.error("커뮤니티 게시글 좋아요 실패 - 요청 제한 초과 : {} - {}",userInfo.getUserNo(), ErrorCode.TOO_MANY_REQUESTS);
            throw new CustomRuntimeException(ErrorCode.TOO_MANY_REQUESTS);
        }

        //게시글 존재 여부 확인
        CommunityPost communityPost = communityPostRepository.findById(postNo).orElseThrow(()->{
            log.error("커뮤니티 게시글 좋아요 실패 - 찾는 게시글이 없음 : {}", ErrorCode.POST_NOT_FOUND);
            return new CustomRuntimeException(ErrorCode.POST_NOT_FOUND);
        });

        //게시글 삭제 여부 확인
        if (communityPost.getDeletedAt() != null) {
            log.error("커뮤니티 게시글 좋아요 실패 - 삭제된 게시글 : {}", ErrorCode.POST_NOT_FOUND);
            throw new CustomRuntimeException(ErrorCode.POST_NOT_FOUND);
        }

        //좋아요 여부 확인
        LikeCommunity likeCommunity = likeCommunityQueryRepository.findByPostNoAndUserNo(postNo, userInfo.getUserNo()).orElse(null);

        //좋아요을 누른 경우
        if (likeCommunity != null){
            //좋아요 토글                        삭제가 되었는데                    좋아요 누르면 좋아요로 변경
            LocalDateTime isCanceled = likeCommunity.getCanceledAt() != null && isLike ? null: LocalDateTime.now();
            likeCommunity.setCanceledAt(isCanceled);
        }else{
            //좋아요 엔티티 생성
            likeCommunity= new LikeCommunity(null, communityPost, userInfo.getUserNo(), null, null);
        }

        //엔티티 등록 또는 수정
        likeCommunityRepository.save(likeCommunity);

        log.info("커뮤니티 게시글 좋아요 끝");
    }



    /**
     * 내가 작성한 커뮤니티 게시글 목록 조회
     * 조건)
     * - 로그인 했는가?
     * - 내가 작성한 게시글인가?
     * - 웹, 모바일 구분
     */
    public PaginatedResponse<List<CommunityPostListResDTO>> getMyCommunityPostList(Authentication authentication, String postType, String order, String path, String lastNo, String page, String size) {
        log.info("내가 작성한 커뮤니티 게시글 목록 조회 시작 : {} - {} - {}", postType, order, path);

        UserInfo userInfo = UserUtil.validateAuthentication("내가 작성한 커뮤니티 게시글 목록 조회", authentication);

        CommunityPostSearchCondition condition = CommunityPostSearchCondition.builder()
                .postType(postType)
                .order(order)
                .path(path)
                .page(page)
                .size(size)
                .lastNo(lastNo)
                .build();

        // 내가 작성한 게시글만 조회
        if ("web".equalsIgnoreCase(path)) {
            PagedListWrapper<CommunityPostListResDTO> wrapper = communityPostQueryRepository.getCommunityPostListToWebByMyUserNo(userInfo.getUserNo(), condition);
            Page<CommunityPostListResDTO> result = new PageImpl<>(wrapper.getList(), condition.getPage(), wrapper.getPagedData().getTotal());
            return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToWeb(result, wrapper.getPagedData().getLatestCreatedAt()));
        }

        Page<CommunityPostListResDTO> result = communityPostQueryRepository.getCommunityPostListToMobileByMyUserNo(userInfo.getUserNo(), condition);

        log.info("내가 작성한 커뮤니티 게시글 목록 조회 끝");
        return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToMobile(result));
    }




    private CommunityPost validatePostAccess(String errorLocation, Long postNo, Long userNo) {
        //게시물이 삭제하지 않고 존재하는가?
        CommunityPost communityPost = communityPostQueryRepository.findByPostNo(postNo).orElseThrow(() -> {
            log.error("커뮤니티 게시글 {} 실패 - 찾는 게시글이 삭제되었거나 없음 : {}", errorLocation, ErrorCode.POST_NOT_FOUND);
            return new CustomRuntimeException(ErrorCode.POST_NOT_FOUND);
        });

        //내 게시글이 맞는 지 확인
        if (!communityPost.getUser().getUserNo().equals(userNo)) {
            log.error("커뮤니티 게시글 {} 실패 - 본인 게시글이 아님 : {}", errorLocation, ErrorCode.POST_ACCESS_DENIED);
            throw new CustomRuntimeException(ErrorCode.POST_ACCESS_DENIED);
        }

        return communityPost;
    }





}