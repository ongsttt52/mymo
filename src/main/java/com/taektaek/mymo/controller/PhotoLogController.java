package com.taektaek.mymo.controller;

import com.taektaek.mymo.dto.photolog.PhotoLogCreateRequest;
import com.taektaek.mymo.dto.photolog.PhotoLogResponse;
import com.taektaek.mymo.dto.photolog.PhotoLogUpdateRequest;
import com.taektaek.mymo.security.CurrentMemberId;
import com.taektaek.mymo.service.PhotoLogService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "사진 기록")
@RestController
@RequestMapping("/api/photo-logs")
public class PhotoLogController {

    private final PhotoLogService photoLogService;

    public PhotoLogController(PhotoLogService photoLogService) {
        this.photoLogService = photoLogService;
    }

    @PostMapping
    public ResponseEntity<PhotoLogResponse> createPhotoLog(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody PhotoLogCreateRequest request) {
        PhotoLogResponse response = photoLogService.createPhotoLog(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhotoLogResponse> getPhotoLog(
            @CurrentMemberId Long memberId,
            @PathVariable Long id) {
        PhotoLogResponse response = photoLogService.getPhotoLog(id, memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PhotoLogResponse>> getPhotoLogsByMember(@CurrentMemberId Long memberId) {
        List<PhotoLogResponse> responses = photoLogService.getPhotoLogsByMember(memberId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhotoLogResponse> updatePhotoLog(
            @CurrentMemberId Long memberId,
            @PathVariable Long id,
            @Valid @RequestBody PhotoLogUpdateRequest request) {
        PhotoLogResponse response = photoLogService.updatePhotoLog(id, memberId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhotoLog(
            @CurrentMemberId Long memberId,
            @PathVariable Long id) {
        photoLogService.deletePhotoLog(id, memberId);
        return ResponseEntity.noContent().build();
    }
}
