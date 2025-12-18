package com.study.bookluck.entity;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class UserBadge {
    private Integer userId;
    private Integer badgeId;
    private Boolean active;   // 중요!
    private String title;
}
