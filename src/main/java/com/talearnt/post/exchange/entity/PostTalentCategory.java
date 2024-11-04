package com.talearnt.post.exchange.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.List;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostTalentCategory {
    private String categoryName;
    private List<String> talentName;
}
