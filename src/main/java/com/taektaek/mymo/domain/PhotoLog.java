package com.taektaek.mymo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "photo_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl; // 저장된 사진 경로 또는 URL

    private String location; // 장소 (예: 성수동 카페)

    @Column(columnDefinition = "TEXT")
    private String description; // 사진에 대한 설명

    private LocalDate date; // 찍은 날짜

    // 추후 분위기(mood)나 태그(tags) 컬럼 추가 예정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public PhotoLog(String imageUrl, String location, String description, LocalDate date, Member member) {
        this.imageUrl = imageUrl;
        this.location = location;
        this.description = description;
        this.date = date;
        this.member = member;
    }
}