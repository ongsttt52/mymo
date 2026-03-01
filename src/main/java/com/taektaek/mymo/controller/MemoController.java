package com.taektaek.mymo.controller;

import com.taektaek.mymo.dto.memo.MemoCreateRequest;
import com.taektaek.mymo.dto.memo.MemoResponse;
import com.taektaek.mymo.dto.memo.MemoUpdateRequest;
import com.taektaek.mymo.security.CurrentMemberId;
import com.taektaek.mymo.service.MemoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memos")
public class MemoController {

    private final MemoService memoService;

    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    @PostMapping
    public ResponseEntity<MemoResponse> createMemo(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody MemoCreateRequest request) {
        MemoResponse response = memoService.createMemo(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemoResponse> getMemo(
            @CurrentMemberId Long memberId,
            @PathVariable Long id) {
        MemoResponse response = memoService.getMemo(id, memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MemoResponse>> getMemosByMember(@CurrentMemberId Long memberId) {
        List<MemoResponse> responses = memoService.getMemosByMember(memberId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemoResponse> updateMemo(
            @CurrentMemberId Long memberId,
            @PathVariable Long id,
            @Valid @RequestBody MemoUpdateRequest request) {
        MemoResponse response = memoService.updateMemo(id, memberId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(
            @CurrentMemberId Long memberId,
            @PathVariable Long id) {
        memoService.deleteMemo(id, memberId);
        return ResponseEntity.noContent().build();
    }
}
