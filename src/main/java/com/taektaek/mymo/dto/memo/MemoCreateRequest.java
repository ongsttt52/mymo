package com.taektaek.mymo.dto.memo;

import jakarta.validation.constraints.NotBlank;

public record MemoCreateRequest(
        @NotBlank(message = "메모 내용은 필수입니다.")
        String content
) {
}
