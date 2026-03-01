# 작업 진행상황

## 완료된 작업

### 프로젝트 초기 설정
- **브랜치**: `main` (직접 커밋)
- Spring Boot 3.5.10 + Java 21 프로젝트 생성
- H2 인메모리 DB, Spring Data JPA, Lombok, Validation 의존성 설정
- `application.yaml` 설정 (DDL auto update, SQL 로깅 등)

### Domain 엔티티 구현
- **브랜치**: `main` (직접 커밋)
- `Member`, `DailyLog`, `Memo`, `PhotoLog`, `MusicLog` 5개 엔티티 생성
- 엔티티 간 관계 매핑 (Member 1:N 나머지 4개, CascadeType.ALL, LAZY 로딩)
- `BaseEntity` 추상 클래스로 `createdAt`, `updatedAt` 공통 관리 (`@MappedSuperclass`)

### Member CRUD + 예외 처리 인프라
- **PR**: [#1](https://github.com/ongsttt52/mymo/pull/1) (`feat/member-crud` → `dev`, 스쿼시 머지)
- `@EnableJpaAuditing` 추가
- `Member` 엔티티에 `memos` 관계 추가, `updateProfile()` / `updatePassword()` 도메인 메서드 추가
- 예외 처리 인프라: `ErrorCode`, `ErrorResponse`, `BusinessException`, `GlobalExceptionHandler`
- 커스텀 예외: `MemberNotFoundException`, `DuplicateMemberException`
- `MemberRepository` (커스텀 쿼리 메서드 4개)
- DTO: `MemberCreateRequest`, `MemberUpdateRequest`, `MemberResponse` (record)
- `MemberService` (CRUD + 중복 검증)
- `MemberController` (REST API 5개 엔드포인트)
- 테스트: `MemberRepositoryTest` 5개, `MemberServiceTest` 10개 (전체 통과)

#### API 엔드포인트
| Method | URI | 설명 | 응답 |
|--------|-----|------|------|
| POST | `/api/members` | 회원가입 | 201 |
| GET | `/api/members/{id}` | 단건 조회 | 200 |
| GET | `/api/members` | 전체 조회 | 200 |
| PUT | `/api/members/{id}` | 정보 수정 | 200 |
| DELETE | `/api/members/{id}` | 삭제 | 204 |

### 4개 도메인 CRUD 구현
- **PR**: [#3](https://github.com/ongsttt52/mymo/pull/3) (`feat/domain-crud` → `dev`, 스쿼시 머지)
- `BaseEntity` 리팩토링: 모든 엔티티에 `createdAt`, `updatedAt` 공통 적용
- DailyLog CRUD: 날짜 중복 검증, 회원별 날짜 내림차순 조회
- Memo CRUD: 회원별 수정일 내림차순 조회
- PhotoLog CRUD: 선택 필드(location, description, date) 지원
- MusicLog CRUD: 선택 필드(artist, album, genre, youtubeUrl, description, date) 지원
- 각 도메인별 NotFoundException + ErrorCode 추가, GlobalExceptionHandler 확장
- 테스트: Repository 9개, Service 35개 (전체 통과)

#### API 엔드포인트
| Method | URI | 설명 | 응답 |
|--------|-----|------|------|
| POST | `/api/daily-logs?memberId={id}` | 일일 기록 생성 | 201 |
| GET | `/api/daily-logs/{id}` | 일일 기록 단건 조회 | 200 |
| GET | `/api/daily-logs?memberId={id}` | 회원별 일일 기록 조회 | 200 |
| PUT | `/api/daily-logs/{id}` | 일일 기록 수정 | 200 |
| DELETE | `/api/daily-logs/{id}` | 일일 기록 삭제 | 204 |
| POST | `/api/memos?memberId={id}` | 메모 생성 | 201 |
| GET | `/api/memos/{id}` | 메모 단건 조회 | 200 |
| GET | `/api/memos?memberId={id}` | 회원별 메모 조회 | 200 |
| PUT | `/api/memos/{id}` | 메모 수정 | 200 |
| DELETE | `/api/memos/{id}` | 메모 삭제 | 204 |
| POST | `/api/photo-logs?memberId={id}` | 사진 기록 생성 | 201 |
| GET | `/api/photo-logs/{id}` | 사진 기록 단건 조회 | 200 |
| GET | `/api/photo-logs?memberId={id}` | 회원별 사진 기록 조회 | 200 |
| PUT | `/api/photo-logs/{id}` | 사진 기록 수정 | 200 |
| DELETE | `/api/photo-logs/{id}` | 사진 기록 삭제 | 204 |
| POST | `/api/music-logs?memberId={id}` | 음악 기록 생성 | 201 |
| GET | `/api/music-logs/{id}` | 음악 기록 단건 조회 | 200 |
| GET | `/api/music-logs?memberId={id}` | 회원별 음악 기록 조회 | 200 |
| PUT | `/api/music-logs/{id}` | 음악 기록 수정 | 200 |
| DELETE | `/api/music-logs/{id}` | 음악 기록 삭제 | 204 |

### Spring Security + JWT 인증 도입
- **PR**: [#4](https://github.com/ongsttt52/mymo/pull/4) (`feat/security-jwt` → `dev`, 스쿼시 머지)
- Spring Security + JWT(jjwt 0.12.6) 의존성 추가
- JWT 인프라: `JwtProperties`, `JwtTokenProvider` (토큰 생성/검증/파싱)
- Spring Security 설정: `SecurityConfig` (CSRF 비활성화, stateless 세션, JWT 필터)
- `CustomUserDetails`, `CustomUserDetailsService`, `JwtAuthenticationFilter`, `JwtAuthenticationEntryPoint`
- 인증 API: `AuthService` + `AuthController` (`POST /api/auth/signup`, `POST /api/auth/login`)
- 비밀번호 BCrypt 암호화 적용
- `@CurrentMemberId` 커스텀 어노테이션 + `CurrentMemberIdArgumentResolver`로 JWT에서 memberId 자동 추출
- `MemberController`: `POST /api/members` → `POST /api/auth/signup`으로 이관, `/{id}` → `/me` 엔드포인트 변경
- 4개 도메인 서비스에 리소스 소유권 검증 (`validateOwnership`) 추가 → 다른 사용자 리소스 접근 시 403
- `ErrorCode`에 `UNAUTHORIZED`, `INVALID_CREDENTIALS`, `ACCESS_DENIED` 추가
- `InvalidCredentialsException`, `ResourceAccessDeniedException` 예외 추가
- 테스트: `JwtTokenProviderTest` 7개, `AuthServiceTest` 5개 신규 + 기존 서비스 테스트 시그니처 변경 및 소유권 검증 실패 테스트 추가 (전체 통과)

#### API 엔드포인트 (변경 후)
| Method | URI | 설명 | 인증 | 응답 |
|--------|-----|------|------|------|
| POST | `/api/auth/signup` | 회원가입 | 불필요 | 201 |
| POST | `/api/auth/login` | 로그인 (JWT 발급) | 불필요 | 200 |
| GET | `/api/members/me` | 내 정보 조회 | 필요 | 200 |
| PUT | `/api/members/me` | 내 정보 수정 | 필요 | 200 |
| DELETE | `/api/members/me` | 회원 탈퇴 | 필요 | 204 |
| POST | `/api/daily-logs` | 일일 기록 생성 | 필요 | 201 |
| GET | `/api/daily-logs/{id}` | 일일 기록 단건 조회 (소유권 검증) | 필요 | 200 |
| GET | `/api/daily-logs` | 내 일일 기록 목록 조회 | 필요 | 200 |
| PUT | `/api/daily-logs/{id}` | 일일 기록 수정 (소유권 검증) | 필요 | 200 |
| DELETE | `/api/daily-logs/{id}` | 일일 기록 삭제 (소유권 검증) | 필요 | 204 |
| POST | `/api/memos` | 메모 생성 | 필요 | 201 |
| GET | `/api/memos/{id}` | 메모 단건 조회 (소유권 검증) | 필요 | 200 |
| GET | `/api/memos` | 내 메모 목록 조회 | 필요 | 200 |
| PUT | `/api/memos/{id}` | 메모 수정 (소유권 검증) | 필요 | 200 |
| DELETE | `/api/memos/{id}` | 메모 삭제 (소유권 검증) | 필요 | 204 |
| POST | `/api/photo-logs` | 사진 기록 생성 | 필요 | 201 |
| GET | `/api/photo-logs/{id}` | 사진 기록 단건 조회 (소유권 검증) | 필요 | 200 |
| GET | `/api/photo-logs` | 내 사진 기록 목록 조회 | 필요 | 200 |
| PUT | `/api/photo-logs/{id}` | 사진 기록 수정 (소유권 검증) | 필요 | 200 |
| DELETE | `/api/photo-logs/{id}` | 사진 기록 삭제 (소유권 검증) | 필요 | 204 |
| POST | `/api/music-logs` | 음악 기록 생성 | 필요 | 201 |
| GET | `/api/music-logs/{id}` | 음악 기록 단건 조회 (소유권 검증) | 필요 | 200 |
| GET | `/api/music-logs` | 내 음악 기록 목록 조회 | 필요 | 200 |
| PUT | `/api/music-logs/{id}` | 음악 기록 수정 (소유권 검증) | 필요 | 200 |
| DELETE | `/api/music-logs/{id}` | 음악 기록 삭제 (소유권 검증) | 필요 | 204 |

---

## 미구현 작업

### API 개선
- 페이징 처리 (목록 조회 API)
- 검색/필터링 (날짜 범위, 키워드 등)

### 기능 확장
- PhotoLog에 mood, tags 컬럼 추가

### 인프라
- 프로덕션 DB 전환 (H2 → MySQL/PostgreSQL)
- JWT secret 환경변수 분리
- API 문서화 (Swagger/SpringDoc)
