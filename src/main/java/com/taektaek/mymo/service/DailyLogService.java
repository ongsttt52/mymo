package com.taektaek.mymo.service;

import com.taektaek.mymo.domain.DailyLog;
import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.dto.dailylog.DailyLogCreateRequest;
import com.taektaek.mymo.dto.dailylog.DailyLogResponse;
import com.taektaek.mymo.dto.dailylog.DailyLogUpdateRequest;
import com.taektaek.mymo.exception.DailyLogNotFoundException;
import com.taektaek.mymo.exception.DuplicateDailyLogDateException;
import com.taektaek.mymo.exception.MemberNotFoundException;
import com.taektaek.mymo.exception.ResourceAccessDeniedException;
import com.taektaek.mymo.repository.DailyLogRepository;
import com.taektaek.mymo.repository.MemberRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DailyLogService {

    private final DailyLogRepository dailyLogRepository;
    private final MemberRepository memberRepository;

    public DailyLogService(DailyLogRepository dailyLogRepository, MemberRepository memberRepository) {
        this.dailyLogRepository = dailyLogRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public DailyLogResponse createDailyLog(Long memberId, DailyLogCreateRequest request) {
        Member member = findMemberById(memberId);
        validateDuplicateDate(memberId, request);

        DailyLog dailyLog = new DailyLog(request.date(), request.resolution(), request.reflection(), member);
        DailyLog savedDailyLog = dailyLogRepository.save(dailyLog);
        return DailyLogResponse.from(savedDailyLog);
    }

    public DailyLogResponse getDailyLog(Long id, Long memberId) {
        DailyLog dailyLog = findDailyLogById(id);
        validateOwnership(dailyLog, memberId);
        return DailyLogResponse.from(dailyLog);
    }

    public List<DailyLogResponse> getDailyLogsByMember(Long memberId) {
        return dailyLogRepository.findByMemberIdOrderByDateDesc(memberId).stream()
                .map(DailyLogResponse::from)
                .toList();
    }

    @Transactional
    public DailyLogResponse updateDailyLog(Long id, Long memberId, DailyLogUpdateRequest request) {
        DailyLog dailyLog = findDailyLogById(id);
        validateOwnership(dailyLog, memberId);
        dailyLog.update(request.resolution(), request.reflection());
        return DailyLogResponse.from(dailyLog);
    }

    @Transactional
    public void deleteDailyLog(Long id, Long memberId) {
        DailyLog dailyLog = findDailyLogById(id);
        validateOwnership(dailyLog, memberId);
        dailyLogRepository.delete(dailyLog);
    }

    private DailyLog findDailyLogById(Long id) {
        return dailyLogRepository.findById(id)
                .orElseThrow(DailyLogNotFoundException::new);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private void validateDuplicateDate(Long memberId, DailyLogCreateRequest request) {
        if (dailyLogRepository.existsByMemberIdAndDate(memberId, request.date())) {
            throw new DuplicateDailyLogDateException();
        }
    }

    private void validateOwnership(DailyLog dailyLog, Long memberId) {
        if (!dailyLog.getMember().getId().equals(memberId)) {
            throw new ResourceAccessDeniedException();
        }
    }
}
