package com.taektaek.mymo.dto.auth;

public record LoginResponse(String accessToken, Long memberId, String email) {}
