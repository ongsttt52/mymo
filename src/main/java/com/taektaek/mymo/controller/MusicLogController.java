package com.taektaek.mymo.controller;

import com.taektaek.mymo.dto.musiclog.MusicLogCreateRequest;
import com.taektaek.mymo.dto.musiclog.MusicLogResponse;
import com.taektaek.mymo.dto.musiclog.MusicLogUpdateRequest;
import com.taektaek.mymo.service.MusicLogService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/music-logs")
public class MusicLogController {

    private final MusicLogService musicLogService;

    public MusicLogController(MusicLogService musicLogService) {
        this.musicLogService = musicLogService;
    }

    @PostMapping
    public ResponseEntity<MusicLogResponse> createMusicLog(
            @RequestParam Long memberId,
            @Valid @RequestBody MusicLogCreateRequest request) {
        MusicLogResponse response = musicLogService.createMusicLog(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MusicLogResponse> getMusicLog(@PathVariable Long id) {
        MusicLogResponse response = musicLogService.getMusicLog(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MusicLogResponse>> getMusicLogsByMember(@RequestParam Long memberId) {
        List<MusicLogResponse> responses = musicLogService.getMusicLogsByMember(memberId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MusicLogResponse> updateMusicLog(
            @PathVariable Long id,
            @Valid @RequestBody MusicLogUpdateRequest request) {
        MusicLogResponse response = musicLogService.updateMusicLog(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMusicLog(@PathVariable Long id) {
        musicLogService.deleteMusicLog(id);
        return ResponseEntity.noContent().build();
    }
}
