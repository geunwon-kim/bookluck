package com.study.bookluck.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookDto {
    private Long id;
    private String title;
    private String link;
    private String image;
    private String author;
    private String publisher;
    private String description;
    private String pubdate;
    private String isbn;
    private String discount;
    private Integer price;
    private Integer salePrice;
    private String category1;
    private String category2;
    // 필요한 경우, createdAt, updatedAt 등 추가 가능
    // private LocalDateTime createdAt;
    // private LocalDateTime updatedAt;
}