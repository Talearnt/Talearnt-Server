package com.talearnt.user.talent.repository;

import com.talearnt.user.talent.entity.MyTalent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyTalentRepository extends JpaRepository<MyTalent, Long> {
}
