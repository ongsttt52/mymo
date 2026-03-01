package com.taektaek.mymo.dto.photolog;

import com.taektaek.mymo.domain.PhotoLog;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PhotoLogResponse(
        Long id,
        String imageUrl,
        String location,
        String description,
        LocalDate date,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static PhotoLogResponse from(PhotoLog photoLog) {
        return new PhotoLogResponse(
                photoLog.getId(),
                photoLog.getImageUrl(),
                photoLog.getLocation(),
                photoLog.getDescription(),
                photoLog.getDate(),
                photoLog.getCreatedAt(),
                photoLog.getUpdatedAt()
        );
    }
}
