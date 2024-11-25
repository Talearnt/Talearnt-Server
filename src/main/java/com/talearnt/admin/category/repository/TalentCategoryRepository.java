package com.talearnt.admin.category.repository;

import com.talearnt.admin.category.entity.TalentCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TalentCategoryRepository extends JpaRepository<TalentCategory,Integer> {
    boolean existsByTalentName(String talentName);
}
