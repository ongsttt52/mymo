package com.taektaek.mymo.dto.musiclog;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record MusicLogUpdateRequest(
    @NotBlank(message = "노래 제목은 필수입니다.") String title,
    String artist,
    String album,
    String genre,
    String youtubeUrl,
    String description,
    LocalDate date) {}
