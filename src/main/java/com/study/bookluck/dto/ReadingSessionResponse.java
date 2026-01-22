package com.study.bookluck.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingSessionResponse {
    private Integer id;
    private Integer userId;
    private String bookId;
    private String status;  // READING, PAUSED
    private LocalDateTime startDate;
    private LocalDateTime lastPausedAt;
    private LocalDateTime lastResumedAt;
    private Integer accumulatedDuration;  // 누적 독서 시간 (분)
    private Integer currentSessionDuration;  // 현재 세션의 독서 시간 (분) - UI 표시용
}
