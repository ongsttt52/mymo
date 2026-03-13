package com.taektaek.mymo.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyLog extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDate date; // 기록 날짜 (2024-05-20 등)

  @Column(columnDefinition = "TEXT")
  private String resolution; // 오늘의 다짐

  @Column(columnDefinition = "TEXT")
  private String reflection; // 오늘의 회고

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  public DailyLog(LocalDate date, String resolution, String reflection, Member member) {
    this.date = date;
    this.resolution = resolution;
    this.reflection = reflection;
    this.member = member;
  }

  public void update(String resolution, String reflection) {
    this.resolution = resolution;
    this.reflection = reflection;
  }
}
