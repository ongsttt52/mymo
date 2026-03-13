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

### 프론트엔드 초기 셋업
- **PR**: [#5](https://github.com/ongsttt52/mymo/pull/5) (`feat/frontend-setup` → `main`, 스쿼시 머지)
- **기술 스택**: Vite + React 19 + TypeScript + Tailwind CSS v4

#### 프로젝트 구조
- Vite + React + TypeScript 프로젝트 생성 (`frontend/`)
- Tailwind CSS v4 설정 (`@tailwindcss/vite` 플러그인, 커스텀 테마)
- Vite dev proxy 설정 (`/api` → `http://localhost:8080`)

#### TypeScript 타입 + API 클라이언트
- 백엔드 DTO 미러링 TypeScript 타입 (`src/types/`)
- axios 기반 API 클라이언트 (`src/api/client.ts`): JWT 자동 첨부 interceptor, 401 응답 시 로그인 리다이렉트
- 도메인별 CRUD API 함수: auth, member, dailyLog, memo, photoLog, musicLog

#### 상태 관리 + 라우팅
- Zustand 인증 스토어 (`useAuthStore`): 토큰/사용자 정보 관리, localStorage 연동
- React Router v7 라우팅: 공개(`/login`, `/signup`) + 인증(`/dashboard`) 라우트
- `ProtectedRoute` 컴포넌트: 미인증 시 `/login` 리다이렉트

#### UI 컴포넌트
- 레이아웃: `Layout` (Header + Sidebar + Main), `Header` (이메일, 로그아웃), `Sidebar` (5개 네비게이션)
- 인증: `LoginForm` (이메일/비밀번호, 에러 표시), `SignupForm` (사용자명/이메일/비밀번호, 필드별 에러)
- 페이지: `LoginPage`, `SignupPage`, `DashboardPage` (환영 메시지 + 4개 도메인 요약 카드), `NotFoundPage`
- 공통: `LoadingSpinner`, `ErrorMessage` (재시도 버튼)

#### 의존성
| 패키지 | 용도 |
|--------|------|
| axios | HTTP 클라이언트 + JWT interceptor |
| zustand | 인증 상태 관리 |
| react-router | 클라이언트 사이드 라우팅 |
| tailwindcss + @tailwindcss/vite | CSS 프레임워크 |

### 프론트엔드 4개 도메인 CRUD 페이지
- **PR**: (feat/frontend-domain-crud-pages → dev)
- **기술**: 모달 기반 CRUD (별도 페이지 라우팅 없이 목록 페이지 1개 + 생성/수정 모달 + 삭제 확인 모달)

#### 공통 컴포넌트 (6개)
- `Modal`: `createPortal(_, document.body)` 기반 범용 모달 (ESC 닫기, 오버레이 클릭 닫기, body 스크롤 잠금)
- `ConfirmModal`: 삭제 확인 모달 (Modal 재사용, 빨간 확인 버튼, 로딩 상태)
- `FormField`: 폼 입력 필드 (text/date/url/textarea, 에러 표시, required 마크)
- `EmptyState`: 빈 상태 안내 (아이콘 + 텍스트 + 새로 만들기 버튼)
- `PageHeader`: 페이지 헤더 (제목 + 건수 + 새로 만들기 버튼)
- `format.ts`: 날짜 포맷팅 유틸 (formatDate, formatDateTime)

#### 도메인별 페이지
| 도메인 | 레이아웃 | 색상 테마 | 정렬 기준 | 파일 수 |
|--------|---------|----------|----------|---------|
| DailyLog | 세로 리스트 | indigo | 날짜 내림차순 | 4개 |
| Memo | 카드 그리드 (2~3열) | amber | 수정일 내림차순 | 4개 |
| PhotoLog | 카드 그리드 (2~3열) | emerald | 생성일 내림차순 | 4개 |
| MusicLog | 세로 리스트 | rose | 생성일 내림차순 | 4개 |

#### 주요 UX 특징
- 모든 도메인: 목록 → 빈 상태 안내 / 목록 표시 + CRUD 모달
- PhotoLog: 이미지 URL 미리보기, 이미지 로드 실패 fallback
- MusicLog: YouTube 외부 링크, 앨범/장르 뱃지, 7개 필드 모달 내 스크롤
- Memo: hover 시 수정/삭제 버튼 표시, 내용 6줄 클램핑

#### 라우트 추가
| 경로 | 페이지 |
|------|--------|
| `/daily-logs` | DailyLogPage |
| `/memos` | MemoPage |
| `/photo-logs` | PhotoLogPage |
| `/music-logs` | MusicLogPage |

### 인프라 개선: PostgreSQL 전환 + JWT 환경변수 분리 + API 문서화
- **PR**: (feat/infra-improvements → dev)
- 프로덕션 대비 인프라 3종 개선

#### PostgreSQL 전환 + Spring Profile 분리
- H2 File DB → PostgreSQL 17 전환 (Docker Compose로 컨테이너 관리)
- `application.yaml`을 공통 설정으로 재구성, `application-dev.yaml`(PostgreSQL) 프로파일 분리
- 테스트는 기존 H2 in-memory 유지 (`testRuntimeOnly`)
- DailyLog 엔티티: `@Lob` → `@Column(columnDefinition = "TEXT")` (PostgreSQL 호환)

#### JWT 시크릿 환경변수 분리
- `${JWT_SECRET:기본값}`, `${JWT_EXPIRATION:86400000}` 패턴 적용
- 환경변수 미설정 시 개발용 기본값 사용, 프로덕션에서는 환경변수로 주입

#### SpringDoc API 문서화
- `springdoc-openapi-starter-webmvc-ui 2.8.5` 의존성 추가
- `OpenApiConfig`: API 정보(제목, 버전, 설명) + JWT Bearer 인증 스키마 설정
- `SecurityConfig`: Swagger 경로(`/swagger-ui/**`, `/v3/api-docs/**`) permitAll 추가
- `@CurrentMemberId`에 `@Parameter(hidden = true)` 추가하여 Swagger UI에서 숨김
- 6개 컨트롤러에 `@Tag` 어노테이션 추가 (인증, 회원, 일일 기록, 메모, 사진 기록, 음악 기록)
- Swagger UI 접속: `http://localhost:8080/swagger-ui/index.html`

#### 변경 파일
| 구분 | 파일 |
|------|------|
| 수정 | `build.gradle`, `application.yaml`, `DailyLog.java`, `SecurityConfig.java`, `CurrentMemberId.java`, 6개 컨트롤러 |
| 생성 | `application-dev.yaml`, `docker-compose.yaml`, `OpenApiConfig.java` |

### Docker 컨테이너화
- **PR**: (feat/infra-improvements → dev)
- 전체 스택을 `docker compose up`으로 한 번에 기동할 수 있도록 컨테이너화

#### 아키텍처
```
Browser → Nginx(3000:80)
  ├── /api/*          → backend:8080 (reverse proxy)
  ├── /swagger-ui/*   → backend:8080 (reverse proxy)
  ├── /v3/api-docs/*  → backend:8080 (reverse proxy)
  └── /*              → React 정적 파일 (SPA)
```

#### 백엔드 Dockerfile
- 멀티스테이지 빌드: `gradle:8-jdk21` (빌드) → `eclipse-temurin:21-jre` (런타임)
- Gradle 캐시 활용을 위해 빌드 설정 파일 먼저 복사 후 소스 복사

#### 프론트엔드 Dockerfile + Nginx
- 멀티스테이지 빌드: `node:22-alpine` (빌드) → `nginx:alpine` (런타임)
- Nginx 설정: API/Swagger 리버스 프록시 + SPA `try_files` 라우팅 + gzip 압축
- CORS 설정 변경 없이 동일 출처로 동작 (Nginx 리버스 프록시)

#### docker-compose.yaml
- `backend`: Dockerfile 빌드, `SPRING_DATASOURCE_URL`로 DB 호스트 오버라이드, postgres 헬스체크 의존
- `frontend`: Nginx 기반, 포트 3000:80 매핑, backend 의존
- `postgres`: `pg_isready` 헬스체크 추가

#### 생성/수정 파일
| 구분 | 파일 |
|------|------|
| 생성 | `Dockerfile`, `frontend/Dockerfile`, `frontend/nginx.conf`, `.dockerignore` |
| 수정 | `docker-compose.yaml` |

#### 실행 방법
```bash
docker compose build    # 이미지 빌드
docker compose up -d    # 전체 스택 기동
# http://localhost:3000  → 프론트엔드
# http://localhost:3000/swagger-ui/index.html → Swagger UI (Nginx 프록시 경유)
```

---

## 미구현 작업

### API 개선
- 페이징 처리 (목록 조회 API)
- 검색/필터링 (날짜 범위, 키워드 등)

### 기능 확장
- PhotoLog에 mood, tags 컬럼 추가

### 프론트엔드
- 프로필 수정 / 회원 탈퇴 페이지
- 반응형 디자인 (모바일 대응)
