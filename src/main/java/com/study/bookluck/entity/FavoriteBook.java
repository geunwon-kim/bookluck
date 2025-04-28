package com.study.bookluck.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;


@Getter
@Setter
@Builder
@ToString
public class FavoriteBook {

    private Long id;
    private String userId;
    private String bookId;
}