package com.taektaek.mymo.dto.musiclog;

import com.taektaek.mymo.domain.MusicLog;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MusicLogResponse(
        Long id,
        String title,
        String artist,
        String album,
        String genre,
        String youtubeUrl,
        String description,
        LocalDate date,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static MusicLogResponse from(MusicLog musicLog) {
        return new MusicLogResponse(
                musicLog.getId(),
                musicLog.getTitle(),
                musicLog.getArtist(),
                musicLog.getAlbum(),
                musicLog.getGenre(),
                musicLog.getYoutubeUrl(),
                musicLog.getDescription(),
                musicLog.getDate(),
                musicLog.getCreatedAt(),
                musicLog.getUpdatedAt()
        );
    }
}
