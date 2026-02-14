package com.taektaek.mymo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "music_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MusicLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 노래 제목

    private String artist; // 가수명

    private String album;  // 앨범명

    private String genre;  // 장르

    private String youtubeUrl; // 유튜브 링크 (자동 완성)

    @Column(columnDefinition = "TEXT")
    private String description; // 이 음악에 대한 감상이나 기록

    private LocalDate date; // 들은 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public MusicLog(String title, String artist, String album, String genre, String youtubeUrl, String description, LocalDate date, Member member) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.youtubeUrl = youtubeUrl;
        this.description = description;
        this.date = date;
        this.member = member;
    }
}