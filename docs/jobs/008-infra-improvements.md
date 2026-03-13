# 작업 기록: 인프라 개선 - PostgreSQL 전환 + JWT 환경변수 분리 + API 문서화 + Docker 컨테이너화

- **PR**: [#8](https://github.com/ongsttt52/mymo/pull/8)
- **브랜치**: `feat/infra-improvements` → `dev`
- **작업일**: 2026-03-14

## 작업 배경

H2 File DB를 사용 중이어서 프로덕션 배포에 부적합하고, JWT 시크릿이 `application.yaml`에 하드코딩되어 보안 위험이 있으며, API 문서가 없어 프론트엔드 개발 시 소통 비용이 높은 상태였습니다.

## 설계 결정

### Spring Profile 분리 전략
- `application.yaml`: 공통 설정 (서버 포트, JPA 공통, JWT)
- `application-dev.yaml`: PostgreSQL 연결 (개발 환경)
- `src/test/resources/application.yaml`: H2 in-memory (테스트 환경, 기존 유지)
- 근거: 환경별 DB를 분리하면서 테스트는 빠른 H2를 유지하여 피드백 루프 최소화

### JWT 환경변수 패턴
- `${JWT_SECRET:기본값}` 형태로 환경변수 우선, 미설정 시 개발용 기본값 사용
- 근거: 개발 편의성(기본값)과 프로덕션 보안(환경변수 주입)을 동시에 충족

### SpringDoc 선택 이유
- springfox 대신 springdoc-openapi 사용 (Spring Boot 3.x 공식 지원)
- 어노테이션 기반 설정 (`@OpenAPIDefinition`, `@SecurityScheme`)으로 코드 간결성 유지

## 구현 내용

### 수정 파일 (10개)

| 파일 | 변경 내용 |
|------|----------|
| `build.gradle` | PostgreSQL 드라이버 추가, H2를 `testRuntimeOnly`로, springdoc 추가 |
| `application.yaml` | 공통 설정 재구성, JWT 환경변수 패턴 적용 |
| `DailyLog.java` | `@Lob` → `@Column(columnDefinition = "TEXT")` |
| `SecurityConfig.java` | Swagger 경로 permitAll 추가 |
| `CurrentMemberId.java` | `@Parameter(hidden = true)` 추가 |
| `AuthController.java` | `@Tag(name = "인증")` |
| `MemberController.java` | `@Tag(name = "회원")` |
| `DailyLogController.java` | `@Tag(name = "일일 기록")` |
| `MemoController.java` | `@Tag(name = "메모")` |
| `PhotoLogController.java` | `@Tag(name = "사진 기록")` |
| `MusicLogController.java` | `@Tag(name = "음악 기록")` |

### 생성 파일 (3개)

| 파일 | 설명 |
|------|------|
| `application-dev.yaml` | PostgreSQL 연결 설정 |
| `docker-compose.yaml` | PostgreSQL 17 + volume |
| `OpenApiConfig.java` | OpenAPI 설정 + JWT Bearer 인증 스키마 |

## 커밋 이력

| 커밋 | 내용 |
|------|------|
| `b268427` | PostgreSQL 전환 및 Spring Profile 분리 |
| `d4a296e` | JWT 시크릿 환경변수 분리 |
| `2ae8d21` | SpringDoc Swagger UI 및 OpenAPI 설정 |
| `026018f` | PROGRESS.md 업데이트 |

## Docker 컨테이너화

### 작업 배경

PostgreSQL만 Docker로 관리하고 백엔드/프론트엔드는 로컬에서 직접 실행하는 구조였습니다. 전체 스택을 `docker compose up`으로 한 번에 기동할 수 있도록 컨테이너화했습니다.

### 설계 결정

#### Nginx 리버스 프록시 아키텍처
```
Browser → Nginx(3000:80)
  ├── /api/*          → backend:8080 (reverse proxy)
  ├── /swagger-ui/*   → backend:8080 (reverse proxy)
  ├── /v3/api-docs/*  → backend:8080 (reverse proxy)
  └── /*              → React 정적 파일 (SPA)
```
- 근거: 프론트엔드 API 클라이언트가 이미 상대경로 `/api`를 사용하므로, Nginx 리버스 프록시로 CORS 설정 변경 없이 동일 출처 동작
- SPA 라우팅을 위해 `try_files $uri /index.html` 설정

#### 멀티스테이지 빌드
- 백엔드: `gradle:8-jdk21` (빌드) → `eclipse-temurin:21-jre` (런타임) — 빌드 도구 제거로 이미지 경량화
- 프론트엔드: `node:22-alpine` (빌드) → `nginx:alpine` (런타임) — Node.js 불필요, 정적 파일만 서빙

#### DB 호스트 환경변수 오버라이드
- Docker 네트워크에서는 `localhost` 대신 서비스명 `postgres`로 접근
- `SPRING_DATASOURCE_URL`을 docker-compose에서 오버라이드하여 로컬 개발 설정과 분리

### 생성 파일 (4개)

| 파일 | 설명 |
|------|------|
| `Dockerfile` | 백엔드 멀티스테이지 빌드 (Gradle → JDK 21 slim) |
| `frontend/Dockerfile` | 프론트엔드 멀티스테이지 빌드 (Node → Nginx) |
| `frontend/nginx.conf` | Nginx 설정 (API 리버스 프록시 + SPA 라우팅 + gzip) |
| `.dockerignore` | 빌드 컨텍스트에서 불필요한 파일 제외 |

### 수정 파일 (1개)

| 파일 | 변경 내용 |
|------|----------|
| `docker-compose.yaml` | backend, frontend 서비스 추가, postgres healthcheck 추가 |

### 추가 커밋 이력

| 커밋 | 내용 |
|------|------|
| `12bbb7b` | 백엔드 Dockerfile + .dockerignore |
| `4b1ab78` | 프론트엔드 Dockerfile + Nginx 설정 |
| `514cf4f` | docker-compose.yaml 전체 서비스 통합 |
| `0729905` | PROGRESS.md 업데이트 |

## 검증

- `./gradlew test` 전체 통과 (H2 in-memory 기반)
- PostgreSQL 연결은 `docker compose up -d` 후 `./gradlew bootRun`으로 검증 필요
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Docker 전체 스택: `docker compose build && docker compose up -d` → `http://localhost:3000`
