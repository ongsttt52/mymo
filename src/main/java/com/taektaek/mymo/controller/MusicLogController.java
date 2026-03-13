package com.taektaek.mymo.controller;

import com.taektaek.mymo.dto.common.PagedResponse;
import com.taektaek.mymo.dto.musiclog.MusicLogCreateRequest;
import com.taektaek.mymo.dto.musiclog.MusicLogResponse;
import com.taektaek.mymo.dto.musiclog.MusicLogUpdateRequest;
import com.taektaek.mymo.security.CurrentMemberId;
import com.taektaek.mymo.service.MusicLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "음악 기록")
@RestController
@RequestMapping("/api/music-logs")
public class MusicLogController {

  private final MusicLogService musicLogService;

  public MusicLogController(MusicLogService musicLogService) {
    this.musicLogService = musicLogService;
  }

  @PostMapping
  public ResponseEntity<MusicLogResponse> createMusicLog(
      @CurrentMemberId Long memberId, @Valid @RequestBody MusicLogCreateRequest request) {
    MusicLogResponse response = musicLogService.createMusicLog(memberId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<MusicLogResponse> getMusicLog(
      @CurrentMemberId Long memberId, @PathVariable Long id) {
    MusicLogResponse response = musicLogService.getMusicLog(id, memberId);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<PagedResponse<MusicLogResponse>> getMusicLogsByMember(
      @CurrentMemberId Long memberId,
      @RequestParam(required = false) String genre,
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    PagedResponse<MusicLogResponse> response =
        musicLogService.searchMusicLogs(memberId, genre, keyword, page, size);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<MusicLogResponse> updateMusicLog(
      @CurrentMemberId Long memberId,
      @PathVariable Long id,
      @Valid @RequestBody MusicLogUpdateRequest request) {
    MusicLogResponse response = musicLogService.updateMusicLog(id, memberId, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMusicLog(
      @CurrentMemberId Long memberId, @PathVariable Long id) {
    musicLogService.deleteMusicLog(id, memberId);
    return ResponseEntity.noContent().build();
  }
}
