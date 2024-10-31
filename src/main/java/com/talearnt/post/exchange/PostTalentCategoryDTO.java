package com.talearnt.post.exchange;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class PostTalentCategoryDTO {
    private String categoryName;
    private List<String> talentName;
}
