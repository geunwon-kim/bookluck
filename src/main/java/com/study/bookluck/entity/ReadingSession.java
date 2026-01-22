package com.study.bookluck.entity;

import java.time.LocalDateTime;

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
public class ReadingSession {
    private Integer id;
    private Integer userId;
    private String status;              // READING, PAUSED
    private LocalDateTime startDate;     // 독서 시작 시간
    private LocalDateTime lastPausedAt;  // 마지막 일시정지 시간
    private LocalDateTime lastResumedAt; // 마지막 재개 시간
    private Integer accumulatedDuration; // 누적 독서 시간 (분)
    private LocalDateTime endDate;       // 독서 종료 시간
    private Integer finalDuration;       // 최종 독서 시간 (분) - 종료 시 계산됨
}
