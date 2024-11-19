package com.talearnt.admin.category.repository;

import com.talearnt.admin.category.entity.BigCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BigCategoryRepository extends JpaRepository<BigCategory,Integer> {

    boolean existsByCategoryName(String categoryName);
}
