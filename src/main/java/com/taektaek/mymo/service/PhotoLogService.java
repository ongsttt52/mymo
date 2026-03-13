package com.taektaek.mymo.service;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.domain.PhotoLog;
import com.taektaek.mymo.dto.common.PagedResponse;
import com.taektaek.mymo.dto.photolog.PhotoLogCreateRequest;
import com.taektaek.mymo.dto.photolog.PhotoLogResponse;
import com.taektaek.mymo.dto.photolog.PhotoLogUpdateRequest;
import com.taektaek.mymo.exception.MemberNotFoundException;
import com.taektaek.mymo.exception.PhotoLogNotFoundException;
import com.taektaek.mymo.exception.ResourceAccessDeniedException;
import com.taektaek.mymo.repository.MemberRepository;
import com.taektaek.mymo.repository.PhotoLogRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PhotoLogService {

  private static final int MAX_PAGE_SIZE = 100;

  private final PhotoLogRepository photoLogRepository;
  private final MemberRepository memberRepository;

  public PhotoLogService(PhotoLogRepository photoLogRepository, MemberRepository memberRepository) {
    this.photoLogRepository = photoLogRepository;
    this.memberRepository = memberRepository;
  }

  @Transactional
  public PhotoLogResponse createPhotoLog(Long memberId, PhotoLogCreateRequest request) {
    Member member = findMemberById(memberId);

    PhotoLog photoLog =
        new PhotoLog(
            request.imageUrl(), request.location(), request.description(), request.date(), member);
    PhotoLog savedPhotoLog = photoLogRepository.save(photoLog);
    return PhotoLogResponse.from(savedPhotoLog);
  }

  public PhotoLogResponse getPhotoLog(Long id, Long memberId) {
    PhotoLog photoLog = findPhotoLogById(id);
    validateOwnership(photoLog, memberId);
    return PhotoLogResponse.from(photoLog);
  }

  public List<PhotoLogResponse> getPhotoLogsByMember(Long memberId) {
    return photoLogRepository.findByMemberIdOrderByDateDesc(memberId).stream()
        .map(PhotoLogResponse::from)
        .toList();
  }

  public PagedResponse<PhotoLogResponse> searchPhotoLogs(
      Long memberId, LocalDate startDate, LocalDate endDate, String keyword, int page, int size) {
    String normalizedKeyword = normalizeKeyword(keyword);
    PageRequest pageRequest =
        PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE), Sort.by(Sort.Direction.DESC, "date"));

    Page<PhotoLogResponse> result =
        photoLogRepository
            .searchByMemberId(memberId, startDate, endDate, normalizedKeyword, pageRequest)
            .map(PhotoLogResponse::from);

    return PagedResponse.from(result);
  }

  @Transactional
  public PhotoLogResponse updatePhotoLog(Long id, Long memberId, PhotoLogUpdateRequest request) {
    PhotoLog photoLog = findPhotoLogById(id);
    validateOwnership(photoLog, memberId);
    photoLog.update(request.imageUrl(), request.location(), request.description(), request.date());
    return PhotoLogResponse.from(photoLog);
  }

  @Transactional
  public void deletePhotoLog(Long id, Long memberId) {
    PhotoLog photoLog = findPhotoLogById(id);
    validateOwnership(photoLog, memberId);
    photoLogRepository.delete(photoLog);
  }

  private PhotoLog findPhotoLogById(Long id) {
    return photoLogRepository.findById(id).orElseThrow(PhotoLogNotFoundException::new);
  }

  private Member findMemberById(Long memberId) {
    return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
  }

  private void validateOwnership(PhotoLog photoLog, Long memberId) {
    if (!photoLog.getMember().getId().equals(memberId)) {
      throw new ResourceAccessDeniedException();
    }
  }

  private String normalizeKeyword(String keyword) {
    return (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
  }
}
