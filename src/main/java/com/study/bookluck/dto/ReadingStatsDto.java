package com.study.bookluck.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReadingStatsDto {

    private Integer year;
    private Integer readingMinutes;
    private List<MonthData> data;

    @Getter
    @Builder
    public static class MonthData {
        private String month;
        private Integer minutes;
    }
}
