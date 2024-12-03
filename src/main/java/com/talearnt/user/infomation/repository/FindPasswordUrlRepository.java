package com.talearnt.user.infomation.repository;

import com.talearnt.auth.find.entity.FindPasswrodUrl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindPasswordUrlRepository extends JpaRepository<FindPasswrodUrl,Long> {
}
