package com.study.bookluck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BadgeResponse {
    private Integer badgeId;
    private String title;
    private boolean active;
}
