package com.taektaek.mymo.dto.dailylog;

import com.taektaek.mymo.domain.DailyLog;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DailyLogResponse(
        Long id,
        LocalDate date,
        String resolution,
        String reflection,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DailyLogResponse from(DailyLog dailyLog) {
        return new DailyLogResponse(
                dailyLog.getId(),
                dailyLog.getDate(),
                dailyLog.getResolution(),
                dailyLog.getReflection(),
                dailyLog.getCreatedAt(),
                dailyLog.getUpdatedAt()
        );
    }
}
