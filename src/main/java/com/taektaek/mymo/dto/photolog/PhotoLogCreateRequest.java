package com.taektaek.mymo.dto.photolog;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record PhotoLogCreateRequest(
    @NotBlank(message = "이미지 URL은 필수입니다.") String imageUrl,
    String location,
    String description,
    LocalDate date) {}
