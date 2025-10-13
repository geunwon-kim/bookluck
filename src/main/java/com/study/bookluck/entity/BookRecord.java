package com.study.bookluck.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
 
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // JSON의 알 수 없는 필드 무시
public class BookRecord {
    private Integer id;
    private Integer userId;
    private String status;
    private String bookId;
    private String endDate;
    private long isbn;
    private int duration;
    private String review;
    private String title;
    private String image;
    private String author;
}