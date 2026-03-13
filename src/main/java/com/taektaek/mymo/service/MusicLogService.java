package com.taektaek.mymo.service;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.domain.MusicLog;
import com.taektaek.mymo.dto.common.PagedResponse;
import com.taektaek.mymo.dto.musiclog.MusicLogCreateRequest;
import com.taektaek.mymo.dto.musiclog.MusicLogResponse;
import com.taektaek.mymo.dto.musiclog.MusicLogUpdateRequest;
import com.taektaek.mymo.exception.MemberNotFoundException;
import com.taektaek.mymo.exception.MusicLogNotFoundException;
import com.taektaek.mymo.exception.ResourceAccessDeniedException;
import com.taektaek.mymo.repository.MemberRepository;
import com.taektaek.mymo.repository.MusicLogRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MusicLogService {

  private static final int MAX_PAGE_SIZE = 100;

  private final MusicLogRepository musicLogRepository;
  private final MemberRepository memberRepository;

  public MusicLogService(MusicLogRepository musicLogRepository, MemberRepository memberRepository) {
    this.musicLogRepository = musicLogRepository;
    this.memberRepository = memberRepository;
  }

  @Transactional
  public MusicLogResponse createMusicLog(Long memberId, MusicLogCreateRequest request) {
    Member member = findMemberById(memberId);

    MusicLog musicLog =
        new MusicLog(
            request.title(),
            request.artist(),
            request.album(),
            request.genre(),
            request.youtubeUrl(),
            request.description(),
            request.date(),
            member);
    MusicLog savedMusicLog = musicLogRepository.save(musicLog);
    return MusicLogResponse.from(savedMusicLog);
  }

  public MusicLogResponse getMusicLog(Long id, Long memberId) {
    MusicLog musicLog = findMusicLogById(id);
    validateOwnership(musicLog, memberId);
    return MusicLogResponse.from(musicLog);
  }

  public List<MusicLogResponse> getMusicLogsByMember(Long memberId) {
    return musicLogRepository.findByMemberIdOrderByDateDesc(memberId).stream()
        .map(MusicLogResponse::from)
        .toList();
  }

  public PagedResponse<MusicLogResponse> searchMusicLogs(
      Long memberId, String genre, String keyword, int page, int size) {
    String normalizedKeyword = normalizeKeyword(keyword);
    String normalizedGenre = normalizeKeyword(genre);
    PageRequest pageRequest =
        PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE), Sort.by(Sort.Direction.DESC, "date"));

    Page<MusicLogResponse> result =
        musicLogRepository
            .searchByMemberId(memberId, normalizedGenre, normalizedKeyword, pageRequest)
            .map(MusicLogResponse::from);

    return PagedResponse.from(result);
  }

  @Transactional
  public MusicLogResponse updateMusicLog(Long id, Long memberId, MusicLogUpdateRequest request) {
    MusicLog musicLog = findMusicLogById(id);
    validateOwnership(musicLog, memberId);
    musicLog.update(
        request.title(),
        request.artist(),
        request.album(),
        request.genre(),
        request.youtubeUrl(),
        request.description(),
        request.date());
    return MusicLogResponse.from(musicLog);
  }

  @Transactional
  public void deleteMusicLog(Long id, Long memberId) {
    MusicLog musicLog = findMusicLogById(id);
    validateOwnership(musicLog, memberId);
    musicLogRepository.delete(musicLog);
  }

  private MusicLog findMusicLogById(Long id) {
    return musicLogRepository.findById(id).orElseThrow(MusicLogNotFoundException::new);
  }

  private Member findMemberById(Long memberId) {
    return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
  }

  private void validateOwnership(MusicLog musicLog, Long memberId) {
    if (!musicLog.getMember().getId().equals(memberId)) {
      throw new ResourceAccessDeniedException();
    }
  }

  private String normalizeKeyword(String keyword) {
    return (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
  }
}
