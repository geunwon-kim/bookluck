package com.study.bookluck.entity;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
 
@Getter
@Setter
@ToString
public class Book {
    private int id;	// 게시물의 ID
    private String title;
    private String link;
    private String image;
    private String author;
    private String publisher;
    private String description;
    private Date pubdate;
    private int isbn;
    private int created_by;
    private LocalDateTime created_at;	// 작성일자
    private LocalDateTime updated_at;	// 수정일자
}
