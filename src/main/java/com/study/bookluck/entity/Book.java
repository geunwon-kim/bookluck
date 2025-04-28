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
public class Book {
    private String title;
    private String link;
    private String image;
    private String author;
    private String publisher;
    private String description;
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private String pubdate;
    private String isbn;
    private String discount;
    private Integer price;
    private Integer sale_price;
    private String category1;
    private String category2;
}
