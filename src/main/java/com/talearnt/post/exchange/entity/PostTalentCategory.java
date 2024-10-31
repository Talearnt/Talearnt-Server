package com.talearnt.post.exchange.entity;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Embeddable
@Getter
@Setter
@ToString
public class PostTalentCategory {
    private String categoryName;
    private List<String> talentName;
}
