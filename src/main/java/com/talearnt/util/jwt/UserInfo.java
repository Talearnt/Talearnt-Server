package com.talearnt.util.jwt;

import com.talearnt.enums.UserRole;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserInfo implements UserDetails {
    private Long userNo;
    private String userId;
    private String nickname;
    private String profileImg;
    private UserRole authority;;


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

}
