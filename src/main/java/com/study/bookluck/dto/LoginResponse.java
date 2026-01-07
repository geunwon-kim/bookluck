package com.study.bookluck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private Integer userId;
    private String userName;
    private String email;
    private boolean isNewUser;  // 신규 회원가입 여부
}
