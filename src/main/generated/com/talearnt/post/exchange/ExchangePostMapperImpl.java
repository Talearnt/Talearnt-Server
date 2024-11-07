package com.talearnt.post.exchange;

import com.talearnt.enums.UserRole;
import com.talearnt.join.User;
import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.entity.PostTalentCategory;
import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.post.exchange.response.ExchangePostReadResDTO;
import com.talearnt.util.jwt.UserInfo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-08T04:23:37+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
public class ExchangePostMapperImpl implements ExchangePostMapper {

    @Override
    public ExchangePostReadResDTO toExchangePostReadResDTO(ExchangePost exchangePost) {
        if ( exchangePost == null ) {
            return null;
        }

        ExchangePostReadResDTO.ExchangePostReadResDTOBuilder exchangePostReadResDTO = ExchangePostReadResDTO.builder();

        exchangePostReadResDTO.userNo( exchangePostUserUserNo( exchangePost ) );
        exchangePostReadResDTO.userId( exchangePostUserUserId( exchangePost ) );
        exchangePostReadResDTO.nickname( exchangePostUserNickname( exchangePost ) );
        exchangePostReadResDTO.profileImg( exchangePostUserProfileImg( exchangePost ) );
        exchangePostReadResDTO.authority( exchangePostUserAuthority( exchangePost ) );
        exchangePostReadResDTO.exchangePostNo( exchangePost.getExchangePostNo() );
        exchangePostReadResDTO.giveTalent( postTalentCategoryListToPostTalentCategoryDTOList( exchangePost.getGiveTalent() ) );
        exchangePostReadResDTO.receiveTalent( postTalentCategoryListToPostTalentCategoryDTOList( exchangePost.getReceiveTalent() ) );
        exchangePostReadResDTO.title( exchangePost.getTitle() );
        exchangePostReadResDTO.content( exchangePost.getContent() );
        exchangePostReadResDTO.exchangeType( exchangePost.getExchangeType() );
        exchangePostReadResDTO.badgeRequired( exchangePost.isBadgeRequired() );
        exchangePostReadResDTO.duration( exchangePost.getDuration() );
        exchangePostReadResDTO.createdAt( exchangePost.getCreatedAt() );

        return exchangePostReadResDTO.build();
    }

    @Override
    public ExchangePost toEntity(ExchangePostReqDTO exchangePostReqDTO) {
        if ( exchangePostReqDTO == null ) {
            return null;
        }

        ExchangePost exchangePost = new ExchangePost();

        exchangePost.setUser( userInfoToUser( exchangePostReqDTO.getUserInfo() ) );
        exchangePost.setGiveTalent( postTalentCategoryDTOListToPostTalentCategoryList( exchangePostReqDTO.getGiveTalent() ) );
        exchangePost.setReceiveTalent( postTalentCategoryDTOListToPostTalentCategoryList( exchangePostReqDTO.getReceiveTalent() ) );
        exchangePost.setTitle( exchangePostReqDTO.getTitle() );
        exchangePost.setContent( exchangePostReqDTO.getContent() );
        exchangePost.setExchangeType( exchangePostReqDTO.getExchangeType() );
        exchangePost.setBadgeRequired( exchangePostReqDTO.isBadgeRequired() );
        exchangePost.setDuration( exchangePostReqDTO.getDuration() );

        return exchangePost;
    }

    @Override
    public ExchangePost toUpdateEntity(ExchangePostReqDTO exchangePostReqDTO, ExchangePost exchangePost) {
        if ( exchangePostReqDTO == null ) {
            return exchangePost;
        }

        if ( exchangePostReqDTO.getUserInfo() != null ) {
            if ( exchangePost.getUser() == null ) {
                exchangePost.setUser( new User() );
            }
            userInfoToUser1( exchangePostReqDTO.getUserInfo(), exchangePost.getUser() );
        }
        else {
            exchangePost.setUser( null );
        }
        if ( exchangePost.getGiveTalent() != null ) {
            List<PostTalentCategory> list = postTalentCategoryDTOListToPostTalentCategoryList( exchangePostReqDTO.getGiveTalent() );
            if ( list != null ) {
                exchangePost.getGiveTalent().clear();
                exchangePost.getGiveTalent().addAll( list );
            }
            else {
                exchangePost.setGiveTalent( null );
            }
        }
        else {
            List<PostTalentCategory> list = postTalentCategoryDTOListToPostTalentCategoryList( exchangePostReqDTO.getGiveTalent() );
            if ( list != null ) {
                exchangePost.setGiveTalent( list );
            }
        }
        if ( exchangePost.getReceiveTalent() != null ) {
            List<PostTalentCategory> list1 = postTalentCategoryDTOListToPostTalentCategoryList( exchangePostReqDTO.getReceiveTalent() );
            if ( list1 != null ) {
                exchangePost.getReceiveTalent().clear();
                exchangePost.getReceiveTalent().addAll( list1 );
            }
            else {
                exchangePost.setReceiveTalent( null );
            }
        }
        else {
            List<PostTalentCategory> list1 = postTalentCategoryDTOListToPostTalentCategoryList( exchangePostReqDTO.getReceiveTalent() );
            if ( list1 != null ) {
                exchangePost.setReceiveTalent( list1 );
            }
        }
        exchangePost.setTitle( exchangePostReqDTO.getTitle() );
        exchangePost.setContent( exchangePostReqDTO.getContent() );
        exchangePost.setExchangeType( exchangePostReqDTO.getExchangeType() );
        exchangePost.setBadgeRequired( exchangePostReqDTO.isBadgeRequired() );
        exchangePost.setDuration( exchangePostReqDTO.getDuration() );

        return exchangePost;
    }

    @Override
    public ExchangePostListResDTO toExchangePostListResDTO(ExchangePost exchangePosts) {
        if ( exchangePosts == null ) {
            return null;
        }

        ExchangePostListResDTO.ExchangePostListResDTOBuilder exchangePostListResDTO = ExchangePostListResDTO.builder();

        exchangePostListResDTO.nickname( exchangePostUserNickname( exchangePosts ) );
        exchangePostListResDTO.authority( exchangePostUserAuthority( exchangePosts ) );
        exchangePostListResDTO.exchangePostNo( exchangePosts.getExchangePostNo() );
        exchangePostListResDTO.giveTalent( postTalentCategoryListToPostTalentCategoryDTOList( exchangePosts.getGiveTalent() ) );
        exchangePostListResDTO.receiveTalent( postTalentCategoryListToPostTalentCategoryDTOList( exchangePosts.getReceiveTalent() ) );
        exchangePostListResDTO.title( exchangePosts.getTitle() );
        exchangePostListResDTO.content( exchangePosts.getContent() );
        exchangePostListResDTO.status( exchangePosts.getStatus() );
        exchangePostListResDTO.createdAt( exchangePosts.getCreatedAt() );

        return exchangePostListResDTO.build();
    }

    @Override
    public List<ExchangePostListResDTO> toListWithExchangePostListResDTOList(List<ExchangePost> exchangePosts) {
        if ( exchangePosts == null ) {
            return null;
        }

        List<ExchangePostListResDTO> list = new ArrayList<ExchangePostListResDTO>( exchangePosts.size() );
        for ( ExchangePost exchangePost : exchangePosts ) {
            list.add( toExchangePostListResDTO( exchangePost ) );
        }

        return list;
    }

    private long exchangePostUserUserNo(ExchangePost exchangePost) {
        if ( exchangePost == null ) {
            return 0L;
        }
        User user = exchangePost.getUser();
        if ( user == null ) {
            return 0L;
        }
        long userNo = user.getUserNo();
        return userNo;
    }

    private String exchangePostUserUserId(ExchangePost exchangePost) {
        if ( exchangePost == null ) {
            return null;
        }
        User user = exchangePost.getUser();
        if ( user == null ) {
            return null;
        }
        String userId = user.getUserId();
        if ( userId == null ) {
            return null;
        }
        return userId;
    }

    private String exchangePostUserNickname(ExchangePost exchangePost) {
        if ( exchangePost == null ) {
            return null;
        }
        User user = exchangePost.getUser();
        if ( user == null ) {
            return null;
        }
        String nickname = user.getNickname();
        if ( nickname == null ) {
            return null;
        }
        return nickname;
    }

    private String exchangePostUserProfileImg(ExchangePost exchangePost) {
        if ( exchangePost == null ) {
            return null;
        }
        User user = exchangePost.getUser();
        if ( user == null ) {
            return null;
        }
        String profileImg = user.getProfileImg();
        if ( profileImg == null ) {
            return null;
        }
        return profileImg;
    }

    private UserRole exchangePostUserAuthority(ExchangePost exchangePost) {
        if ( exchangePost == null ) {
            return null;
        }
        User user = exchangePost.getUser();
        if ( user == null ) {
            return null;
        }
        UserRole authority = user.getAuthority();
        if ( authority == null ) {
            return null;
        }
        return authority;
    }

    protected PostTalentCategoryDTO postTalentCategoryToPostTalentCategoryDTO(PostTalentCategory postTalentCategory) {
        if ( postTalentCategory == null ) {
            return null;
        }

        PostTalentCategoryDTO.PostTalentCategoryDTOBuilder postTalentCategoryDTO = PostTalentCategoryDTO.builder();

        postTalentCategoryDTO.categoryName( postTalentCategory.getCategoryName() );
        List<String> list = postTalentCategory.getTalentName();
        if ( list != null ) {
            postTalentCategoryDTO.talentName( new ArrayList<String>( list ) );
        }

        return postTalentCategoryDTO.build();
    }

    protected List<PostTalentCategoryDTO> postTalentCategoryListToPostTalentCategoryDTOList(List<PostTalentCategory> list) {
        if ( list == null ) {
            return null;
        }

        List<PostTalentCategoryDTO> list1 = new ArrayList<PostTalentCategoryDTO>( list.size() );
        for ( PostTalentCategory postTalentCategory : list ) {
            list1.add( postTalentCategoryToPostTalentCategoryDTO( postTalentCategory ) );
        }

        return list1;
    }

    protected User userInfoToUser(UserInfo userInfo) {
        if ( userInfo == null ) {
            return null;
        }

        User user = new User();

        user.setUserNo( userInfo.getUserNo() );
        user.setUserId( userInfo.getUserId() );
        user.setProfileImg( userInfo.getProfileImg() );
        user.setNickname( userInfo.getNickname() );
        user.setAuthority( userInfo.getAuthority() );

        return user;
    }

    protected PostTalentCategory postTalentCategoryDTOToPostTalentCategory(PostTalentCategoryDTO postTalentCategoryDTO) {
        if ( postTalentCategoryDTO == null ) {
            return null;
        }

        PostTalentCategory postTalentCategory = new PostTalentCategory();

        postTalentCategory.setCategoryName( postTalentCategoryDTO.getCategoryName() );
        List<String> list = postTalentCategoryDTO.getTalentName();
        if ( list != null ) {
            postTalentCategory.setTalentName( new ArrayList<String>( list ) );
        }

        return postTalentCategory;
    }

    protected List<PostTalentCategory> postTalentCategoryDTOListToPostTalentCategoryList(List<PostTalentCategoryDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<PostTalentCategory> list1 = new ArrayList<PostTalentCategory>( list.size() );
        for ( PostTalentCategoryDTO postTalentCategoryDTO : list ) {
            list1.add( postTalentCategoryDTOToPostTalentCategory( postTalentCategoryDTO ) );
        }

        return list1;
    }

    protected void userInfoToUser1(UserInfo userInfo, User mappingTarget) {
        if ( userInfo == null ) {
            return;
        }

        mappingTarget.setUserNo( userInfo.getUserNo() );
        mappingTarget.setUserId( userInfo.getUserId() );
        mappingTarget.setProfileImg( userInfo.getProfileImg() );
        mappingTarget.setNickname( userInfo.getNickname() );
        mappingTarget.setAuthority( userInfo.getAuthority() );
    }
}
