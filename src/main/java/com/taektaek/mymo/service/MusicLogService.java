package com.taektaek.mymo.service;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.domain.MusicLog;
import com.taektaek.mymo.dto.musiclog.MusicLogCreateRequest;
import com.taektaek.mymo.dto.musiclog.MusicLogResponse;
import com.taektaek.mymo.dto.musiclog.MusicLogUpdateRequest;
import com.taektaek.mymo.exception.MemberNotFoundException;
import com.taektaek.mymo.exception.MusicLogNotFoundException;
import com.taektaek.mymo.repository.MemberRepository;
import com.taektaek.mymo.repository.MusicLogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MusicLogService {

    private final MusicLogRepository musicLogRepository;
    private final MemberRepository memberRepository;

    public MusicLogService(MusicLogRepository musicLogRepository, MemberRepository memberRepository) {
        this.musicLogRepository = musicLogRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MusicLogResponse createMusicLog(Long memberId, MusicLogCreateRequest request) {
        Member member = findMemberById(memberId);

        MusicLog musicLog = new MusicLog(
                request.title(), request.artist(), request.album(), request.genre(),
                request.youtubeUrl(), request.description(), request.date(), member
        );
        MusicLog savedMusicLog = musicLogRepository.save(musicLog);
        return MusicLogResponse.from(savedMusicLog);
    }

    public MusicLogResponse getMusicLog(Long id) {
        MusicLog musicLog = findMusicLogById(id);
        return MusicLogResponse.from(musicLog);
    }

    public List<MusicLogResponse> getMusicLogsByMember(Long memberId) {
        return musicLogRepository.findByMemberIdOrderByDateDesc(memberId).stream()
                .map(MusicLogResponse::from)
                .toList();
    }

    @Transactional
    public MusicLogResponse updateMusicLog(Long id, MusicLogUpdateRequest request) {
        MusicLog musicLog = findMusicLogById(id);
        musicLog.update(
                request.title(), request.artist(), request.album(), request.genre(),
                request.youtubeUrl(), request.description(), request.date()
        );
        return MusicLogResponse.from(musicLog);
    }

    @Transactional
    public void deleteMusicLog(Long id) {
        MusicLog musicLog = findMusicLogById(id);
        musicLogRepository.delete(musicLog);
    }

    private MusicLog findMusicLogById(Long id) {
        return musicLogRepository.findById(id)
                .orElseThrow(MusicLogNotFoundException::new);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }
}
