package com.study.bookluck.entity;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
 
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)  // JSON의 알 수 없는 필드 무시
public class BookRecord {
    private String userNo;
    private String status;
    private String bookId;
    private Date endDate;
    private long isbn;
    private int duration;
    private String review;
}