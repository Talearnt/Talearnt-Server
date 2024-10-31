package com.talearnt.util.jwt;

import com.talearnt.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserInfo implements UserDetails {
    private String userId;
    private String nickname;
    private String profileImg;
    private UserRole authority;;
    // 생성자
    public UserInfo(String userId, String nickname, String profileImg, UserRole authority) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.authority = authority;
    }

    // UserDetails 인터페이스 메서드 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> authority.name()); // 권한을 GrantedAuthority 형태로 반환
    }

    @Override
    public String getPassword() {
        return null; // 비밀번호는 필요 없으므로 null 반환
    }

    @Override
    public String getUsername() {
        return userId; // userId를 username으로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Getter 추가
    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public UserRole getAuthority() {
        return authority;
    }
}
