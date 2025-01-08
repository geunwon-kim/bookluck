package com.study.bookluck.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
 
@Getter
@Setter
@ToString
public class Phrase {
    private int id;	// 명언의 ID
    private String phrase;
    private String name;
}
