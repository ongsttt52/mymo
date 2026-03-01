package com.taektaek.mymo.dto.dailylog;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DailyLogCreateRequest(
        @NotNull(message = "날짜는 필수입니다.")
        LocalDate date,

        String resolution,

        String reflection
) {
}
