package com.taektaek.mymo.service;

import com.taektaek.mymo.domain.Member;
import com.taektaek.mymo.domain.PhotoLog;
import com.taektaek.mymo.dto.photolog.PhotoLogCreateRequest;
import com.taektaek.mymo.dto.photolog.PhotoLogResponse;
import com.taektaek.mymo.dto.photolog.PhotoLogUpdateRequest;
import com.taektaek.mymo.exception.MemberNotFoundException;
import com.taektaek.mymo.exception.PhotoLogNotFoundException;
import com.taektaek.mymo.repository.MemberRepository;
import com.taektaek.mymo.repository.PhotoLogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PhotoLogService {

    private final PhotoLogRepository photoLogRepository;
    private final MemberRepository memberRepository;

    public PhotoLogService(PhotoLogRepository photoLogRepository, MemberRepository memberRepository) {
        this.photoLogRepository = photoLogRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public PhotoLogResponse createPhotoLog(Long memberId, PhotoLogCreateRequest request) {
        Member member = findMemberById(memberId);

        PhotoLog photoLog = new PhotoLog(
                request.imageUrl(), request.location(), request.description(), request.date(), member
        );
        PhotoLog savedPhotoLog = photoLogRepository.save(photoLog);
        return PhotoLogResponse.from(savedPhotoLog);
    }

    public PhotoLogResponse getPhotoLog(Long id) {
        PhotoLog photoLog = findPhotoLogById(id);
        return PhotoLogResponse.from(photoLog);
    }

    public List<PhotoLogResponse> getPhotoLogsByMember(Long memberId) {
        return photoLogRepository.findByMemberIdOrderByDateDesc(memberId).stream()
                .map(PhotoLogResponse::from)
                .toList();
    }

    @Transactional
    public PhotoLogResponse updatePhotoLog(Long id, PhotoLogUpdateRequest request) {
        PhotoLog photoLog = findPhotoLogById(id);
        photoLog.update(request.imageUrl(), request.location(), request.description(), request.date());
        return PhotoLogResponse.from(photoLog);
    }

    @Transactional
    public void deletePhotoLog(Long id) {
        PhotoLog photoLog = findPhotoLogById(id);
        photoLogRepository.delete(photoLog);
    }

    private PhotoLog findPhotoLogById(Long id) {
        return photoLogRepository.findById(id)
                .orElseThrow(PhotoLogNotFoundException::new);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }
}
