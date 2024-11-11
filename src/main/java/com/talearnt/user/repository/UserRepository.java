package com.talearnt.user.repository;

import com.talearnt.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUserId(String userId);
    boolean existsByUserId(String userId);
    boolean existsByNickname(String nickname);
}
