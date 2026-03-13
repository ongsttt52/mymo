package com.taektaek.mymo.service;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.domain.Memo;
import com.taektaek.mymo.dto.common.PagedResponse;
import com.taektaek.mymo.dto.memo.MemoCreateRequest;
import com.taektaek.mymo.dto.memo.MemoResponse;
import com.taektaek.mymo.dto.memo.MemoUpdateRequest;
import com.taektaek.mymo.exception.MemberNotFoundException;
import com.taektaek.mymo.exception.MemoNotFoundException;
import com.taektaek.mymo.exception.ResourceAccessDeniedException;
import com.taektaek.mymo.repository.MemberRepository;
import com.taektaek.mymo.repository.MemoRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemoService {

  private static final int MAX_PAGE_SIZE = 100;

  private final MemoRepository memoRepository;
  private final MemberRepository memberRepository;

  public MemoService(MemoRepository memoRepository, MemberRepository memberRepository) {
    this.memoRepository = memoRepository;
    this.memberRepository = memberRepository;
  }

  @Transactional
  public MemoResponse createMemo(Long memberId, MemoCreateRequest request) {
    Member member = findMemberById(memberId);

    Memo memo = new Memo(request.content(), member);
    Memo savedMemo = memoRepository.save(memo);
    return MemoResponse.from(savedMemo);
  }

  public MemoResponse getMemo(Long id, Long memberId) {
    Memo memo = findMemoById(id);
    validateOwnership(memo, memberId);
    return MemoResponse.from(memo);
  }

  public List<MemoResponse> getMemosByMember(Long memberId) {
    return memoRepository.findByMemberIdOrderByUpdatedAtDesc(memberId).stream()
        .map(MemoResponse::from)
        .toList();
  }

  public PagedResponse<MemoResponse> searchMemos(
      Long memberId, String keyword, int page, int size) {
    String normalizedKeyword = normalizeKeyword(keyword);
    PageRequest pageRequest =
        PageRequest.of(
            page, Math.min(size, MAX_PAGE_SIZE), Sort.by(Sort.Direction.DESC, "updatedAt"));

    Page<MemoResponse> result =
        memoRepository
            .searchByMemberId(memberId, normalizedKeyword, pageRequest)
            .map(MemoResponse::from);

    return PagedResponse.from(result);
  }

  @Transactional
  public MemoResponse updateMemo(Long id, Long memberId, MemoUpdateRequest request) {
    Memo memo = findMemoById(id);
    validateOwnership(memo, memberId);
    memo.updateContent(request.content());
    return MemoResponse.from(memo);
  }

  @Transactional
  public void deleteMemo(Long id, Long memberId) {
    Memo memo = findMemoById(id);
    validateOwnership(memo, memberId);
    memoRepository.delete(memo);
  }

  private Memo findMemoById(Long id) {
    return memoRepository.findById(id).orElseThrow(MemoNotFoundException::new);
  }

  private Member findMemberById(Long memberId) {
    return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
  }

  private void validateOwnership(Memo memo, Long memberId) {
    if (!memo.getMember().getId().equals(memberId)) {
      throw new ResourceAccessDeniedException();
    }
  }

  private String normalizeKeyword(String keyword) {
    return (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
  }
}
