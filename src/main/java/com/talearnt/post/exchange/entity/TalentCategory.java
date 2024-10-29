package com.talearnt.post.exchange.entity;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Embeddable
@Getter
@Builder
@ToString
public class TalentCategory {
    private String categoryName;
    private List<String> talentName;
}
