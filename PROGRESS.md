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
- `Memo`에 JPA Auditing 적용 (`@CreatedDate`, `@LastModifiedDate`)

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

---

## 미구현 작업

### DailyLog CRUD
- Repository / Service / Controller / DTO
- 테스트

### Memo CRUD
- Repository / Service / Controller / DTO
- 테스트

### PhotoLog CRUD
- Repository / Service / Controller / DTO
- 테스트

### MusicLog CRUD
- Repository / Service / Controller / DTO
- 테스트

### 보안
- Spring Security 도입
- 비밀번호 BCrypt 암호화
- 인증/인가 (JWT 등)

### 기타
- PhotoLog에 mood, tags 컬럼 추가
- 프로덕션 DB 전환 (H2 → MySQL/PostgreSQL)
