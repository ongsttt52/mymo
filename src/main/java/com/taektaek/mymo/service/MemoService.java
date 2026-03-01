package com.taektaek.mymo.service;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.domain.Memo;
import com.taektaek.mymo.dto.memo.MemoCreateRequest;
import com.taektaek.mymo.dto.memo.MemoResponse;
import com.taektaek.mymo.dto.memo.MemoUpdateRequest;
import com.taektaek.mymo.exception.MemberNotFoundException;
import com.taektaek.mymo.exception.MemoNotFoundException;
import com.taektaek.mymo.repository.MemberRepository;
import com.taektaek.mymo.repository.MemoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemoService {

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

    public MemoResponse getMemo(Long id) {
        Memo memo = findMemoById(id);
        return MemoResponse.from(memo);
    }

    public List<MemoResponse> getMemosByMember(Long memberId) {
        return memoRepository.findByMemberIdOrderByUpdatedAtDesc(memberId).stream()
                .map(MemoResponse::from)
                .toList();
    }

    @Transactional
    public MemoResponse updateMemo(Long id, MemoUpdateRequest request) {
        Memo memo = findMemoById(id);
        memo.updateContent(request.content());
        return MemoResponse.from(memo);
    }

    @Transactional
    public void deleteMemo(Long id) {
        Memo memo = findMemoById(id);
        memoRepository.delete(memo);
    }

    private Memo findMemoById(Long id) {
        return memoRepository.findById(id)
                .orElseThrow(MemoNotFoundException::new);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }
}
