package com.taektaek.mymo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "daily_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date; // 기록 날짜 (2024-05-20 등)

    @Lob // 긴 텍스트 저장을 위해 사용
    private String resolution; // 오늘의 다짐

    @Lob
    private String reflection; // 오늘의 회고

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 생성자
    public DailyLog(LocalDate date, String resolution, String reflection, Member member) {
        this.date = date;
        this.resolution = resolution;
        this.reflection = reflection;
        this.member = member;
    }
}