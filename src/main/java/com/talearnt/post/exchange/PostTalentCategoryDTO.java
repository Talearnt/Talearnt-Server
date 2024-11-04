package com.talearnt.post.exchange;

import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostTalentCategoryDTO {
    private String categoryName;
    private List<String> talentName;
}
