# CLAUDE.md

> **이 문서는 Claude Code가 프로젝트를 이해하고 개발을 진행하기 위한 가이드입니다.**

---

# 프로젝트 개요

## 서비스 설명

**mymo (My Memory)** - 개인 일상 기록 애플리케이션. 사용자의 일상을 다양한 형태(일일 기록, 메모, 사진, 음악)로 저장하고 관리하는 백엔드 서비스

## 핵심 기능

1. **회원 관리**: 회원가입, 로그인, 프로필 관리
2. **일일 기록 (DailyLog)**: 날짜별 다짐(resolution)과 회고(reflection) 작성
3. **메모 (Memo)**: 자유로운 텍스트 메모, 생성/수정 시간 자동 기록
4. **사진 기록 (PhotoLog)**: 사진 URL, 장소, 설명과 함께 추억 기록
5. **음악 기록 (MusicLog)**: 좋아하는 음악의 제목, 아티스트, 감상 기록

## 아키텍처

```
┌─────────────────────────────────────────────────┐
│              Spring Boot Application            │
│  ┌───────────┐  ┌───────────┐  ┌─────────────┐  │
│  │ Controller│→ │  Service  │→ │ Repository  │  │
│  │  (REST)   │  │ (Business)│  │   (JPA)     │  │
│  └───────────┘  └───────────┘  └─────────────┘  │
│                                       │          │
│                                       ▼          │
│                               ┌─────────────┐   │
│                               │   Domain     │   │
│                               │  (Entity)    │   │
│                               └─────────────┘   │
└─────────────────────────────────────────────────┘
                        │
                        ▼
               ┌─────────────────┐
               │   H2 Database   │
               │   (In-Memory)   │
               └─────────────────┘
```

---

# 사용 기술 및 버전

## Core

| 기술 | 버전 | 용도 |
|------|------|------|
| Java | 21 | 프로그래밍 언어 |
| Spring Boot | 3.5.10 | 웹 프레임워크 |
| Gradle | 8.14.4 | 빌드 도구 |

## Dependencies

| 기술 | 용도 |
|------|------|
| Spring Data JPA | ORM 및 데이터 접근 |
| Spring Validation | 입력값 검증 (Bean Validation) |
| Spring Web | REST API |
| H2 Database | 개발용 인메모리 데이터베이스 |
| Lombok | 보일러플레이트 코드 제거 |
| Spring Boot DevTools | 개발 시 핫 리로드 |
| JUnit 5 | 테스트 프레임워크 |

---

# 데이터베이스 구조

## ERD

```
Member (1)
  ├── 1:N ──▶ DailyLog (일일 기록)
  ├── 1:N ──▶ Memo (메모)
  ├── 1:N ──▶ PhotoLog (사진 기록)
  └── 1:N ──▶ MusicLog (음악 기록)

* 모든 관계는 CASCADE ALL (회원 삭제 시 연관 데이터 모두 삭제)
* 모든 N:1 방향은 LAZY 로딩
```

## 테이블 정의

### members (회원)

| 컬럼 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| username | VARCHAR | UNIQUE, NOT NULL | 사용자명 |
| email | VARCHAR | UNIQUE, NOT NULL | 이메일 |
| password | VARCHAR | NOT NULL | 비밀번호 (암호화 미적용) |

### daily_logs (일일 기록)

| 컬럼 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| date | DATE | NOT NULL | 기록 날짜 |
| resolution | TEXT | | 오늘의 다짐 |
| reflection | TEXT | | 오늘의 회고 |
| member_id | BIGINT | FK | 작성자 |

### memos (메모)

| 컬럼 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| content | TEXT | NOT NULL | 메모 내용 |
| created_at | DATETIME | NOT NULL, 수정 불가 | 생성 시간 (자동) |
| updated_at | DATETIME | | 수정 시간 (자동) |
| member_id | BIGINT | FK | 작성자 |

### photo_logs (사진 기록)

| 컬럼 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| image_url | VARCHAR | NOT NULL | 사진 경로/URL |
| location | VARCHAR | | 장소 |
| description | TEXT | | 사진 설명 |
| date | DATE | | 촬영 날짜 |
| member_id | BIGINT | FK | 작성자 |

### music_logs (음악 기록)

| 컬럼 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| title | VARCHAR | NOT NULL | 노래 제목 |
| artist | VARCHAR | | 가수명 |
| album | VARCHAR | | 앨범명 |
| genre | VARCHAR | | 장르 |
| youtube_url | VARCHAR | | 유튜브 링크 |
| description | TEXT | | 감상 기록 |
| date | DATE | | 감상 날짜 |
| member_id | BIGINT | FK | 작성자 |

---

# 폴더 구조

```
src/
├── main/
│   ├── java/com/taektaek/mymo/
│   │   ├── MymoApplication.java         # Spring Boot 진입점
│   │   ├── domain/                      # JPA 엔티티
│   │   │   ├── Member.java
│   │   │   ├── DailyLog.java
│   │   │   ├── Memo.java
│   │   │   ├── PhotoLog.java
│   │   │   └── MusicLog.java
│   │   ├── repository/                  # Spring Data JPA Repository
│   │   ├── service/                     # 비즈니스 로직
│   │   └── controller/                  # REST API 컨트롤러
│   └── resources/
│       ├── application.yaml             # 애플리케이션 설정
│       ├── static/                      # 정적 파일
│       └── templates/                   # 템플릿 파일
└── test/
    └── java/com/taektaek/mymo/
        └── MymoApplicationTests.java    # 테스트
```

---

# 개발 환경

## 빌드 및 실행

```bash
# 빌드
./gradlew build

# 실행 (http://localhost:8080)
./gradlew bootRun

# 테스트
./gradlew test

# H2 콘솔 접속
# http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:mymo-db-test
# Username: sa / Password: (빈값)
```

## 설정 요약

| 항목 | 값 |
|------|------|
| 서버 포트 | 8080 |
| DB | H2 In-Memory (`jdbc:h2:mem:mymo-db-test`) |
| DDL 전략 | `update` (엔티티 변경 시 자동 반영) |
| SQL 로깅 | 활성화 (포맷팅 + 하이라이트) |

---

# 구현 현황

| 계층 | 상태 | 비고 |
|------|------|------|
| Domain (Entity) | 완료 | 5개 엔티티 + 관계 정의 |
| Repository | 미구현 | JPA Repository 인터페이스 필요 |
| Service | 미구현 | 비즈니스 로직 구현 필요 |
| Controller | 미구현 | REST API 엔드포인트 정의 필요 |
| DTO | 미구현 | 요청/응답 DTO 클래스 필요 |
| 예외 처리 | 미구현 | 커스텀 예외 + GlobalExceptionHandler 필요 |
| 보안 | 미구현 | Spring Security + 비밀번호 암호화 필요 |
| 테스트 | 기본만 | contextLoads 테스트만 존재 |

---

# 주의사항

- `Memo` 엔티티는 `@EntityListeners(AuditingEntityListener.class)`를 사용하므로, JPA Auditing 설정(`@EnableJpaAuditing`)이 필요함
- 현재 H2 인메모리 DB 사용 중이므로 서버 재시작 시 데이터가 초기화됨
- `Member.password`는 평문 저장 상태이며, 추후 BCrypt 등 암호화 적용 필요
- `PhotoLog`에 mood, tags 컬럼 추가 예정 (코드 주석 참고)
