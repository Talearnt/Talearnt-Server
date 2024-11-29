package com.talearnt.user.repository;

import com.talearnt.user.entity.MyTalent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyTalentRepository extends JpaRepository<MyTalent, Long> {
}
