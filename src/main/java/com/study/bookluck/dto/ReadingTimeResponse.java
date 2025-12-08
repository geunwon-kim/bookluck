package com.study.bookluck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadingTimeResponse {
    private String date;   // "MM.dd"
    private double hours;  // 예: 2.3
}
