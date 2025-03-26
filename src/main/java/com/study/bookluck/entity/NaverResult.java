package com.study.bookluck.entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
 
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NaverResult {
	private String lastBuildDate;
	private int total;
	private int start;
	private int display;
	private List<Book> items;
}