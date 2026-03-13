package com.taektaek.mymo.controller;

import com.taektaek.mymo.dto.common.PagedResponse;
import com.taektaek.mymo.dto.dailylog.DailyLogCreateRequest;
import com.taektaek.mymo.dto.dailylog.DailyLogResponse;
import com.taektaek.mymo.dto.dailylog.DailyLogUpdateRequest;
import com.taektaek.mymo.security.CurrentMemberId;
import com.taektaek.mymo.service.DailyLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "일일 기록")
@RestController
@RequestMapping("/api/daily-logs")
public class DailyLogController {

  private final DailyLogService dailyLogService;

  public DailyLogController(DailyLogService dailyLogService) {
    this.dailyLogService = dailyLogService;
  }

  @PostMapping
  public ResponseEntity<DailyLogResponse> createDailyLog(
      @CurrentMemberId Long memberId, @Valid @RequestBody DailyLogCreateRequest request) {
    DailyLogResponse response = dailyLogService.createDailyLog(memberId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<DailyLogResponse> getDailyLog(
      @CurrentMemberId Long memberId, @PathVariable Long id) {
    DailyLogResponse response = dailyLogService.getDailyLog(id, memberId);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<PagedResponse<DailyLogResponse>> getDailyLogsByMember(
      @CurrentMemberId Long memberId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    PagedResponse<DailyLogResponse> response =
        dailyLogService.searchDailyLogs(memberId, startDate, endDate, keyword, page, size);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<DailyLogResponse> updateDailyLog(
      @CurrentMemberId Long memberId,
      @PathVariable Long id,
      @Valid @RequestBody DailyLogUpdateRequest request) {
    DailyLogResponse response = dailyLogService.updateDailyLog(id, memberId, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDailyLog(
      @CurrentMemberId Long memberId, @PathVariable Long id) {
    dailyLogService.deleteDailyLog(id, memberId);
    return ResponseEntity.noContent().build();
  }
}
