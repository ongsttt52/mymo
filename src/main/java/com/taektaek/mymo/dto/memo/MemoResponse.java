package com.taektaek.mymo.dto.memo;

import com.taektaek.mymo.domain.Memo;

import java.time.LocalDateTime;

public record MemoResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static MemoResponse from(Memo memo) {
        return new MemoResponse(
                memo.getId(),
                memo.getContent(),
                memo.getCreatedAt(),
                memo.getUpdatedAt()
        );
    }
}
