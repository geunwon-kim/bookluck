package com.study.bookluck.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
 
@Getter
@Setter
@ToString
public class User {
    private int user_id;	// 게시물의 ID
    private String user_name;
    private String email;
    private String google_id;	// 구글 계정 ID
    private String comment;
    private LocalDateTime created_at;	// 작성일자
    private LocalDateTime updated_at;	// 수정일자
}
