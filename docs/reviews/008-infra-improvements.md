# 코드 리뷰: 인프라 개선 - PostgreSQL + JWT + SpringDoc + Docker 컨테이너화

- **PR**: [#8](https://github.com/ongsttt52/mymo/pull/8)
- **리뷰일**: 2026-03-14

## 리뷰 결과 요약

| 심각도 | 건수 | 수정 상태 |
|--------|------|----------|
| C (Critical) | 2 | 보류 (개발 단계, 프로덕션 배포 전 반드시 수정) |
| H (High) | 4 | 보류 |
| M (Medium) | 7 | 보류 |
| L (Low) | 5 | 보류 |

## C (Critical)

### C-1. `application-dev.yaml` DB 비밀번호 하드코딩
- **파일**: `application-dev.yaml:6`
- **문제**: `password: mymo`가 소스코드에 하드코딩. JWT 시크릿은 환경변수 패턴을 적용했으면서 DB에는 미적용
- **제안**: `${DB_PASSWORD:mymo}` 패턴 적용
- **보류 사유**: 개발 단계에서 편의를 위해 유지. 프로덕션 프로파일 추가 시 반드시 환경변수화

### C-2. `docker-compose.yaml` DB 비밀번호 하드코딩
- **파일**: `docker-compose.yaml:8`
- **문제**: `POSTGRES_PASSWORD: mymo` 하드코딩. `.env` 파일 연동 미설정
- **제안**: `.env.example` 생성, `.gitignore`에 `.env` 추가, `${POSTGRES_PASSWORD:-mymo}` 형태로 변경
- **보류 사유**: 로컬 개발 환경 전용. 프로덕션 배포 시 수정 필수

## H (High)

### H-1. `spring.profiles.active: dev` 하드코딩
- **파일**: `application.yaml:8`
- **문제**: 프로덕션 배포 시 환경변수로 오버라이드하지 않으면 dev 프로파일로 기동
- **제안**: `${SPRING_PROFILES_ACTIVE:dev}` 패턴 적용

### H-2. AuthController Swagger UI에서 인증 필요로 표시
- **파일**: `AuthController.java`
- **문제**: 전역 `@SecurityRequirement`로 인해 인증 불필요한 signup/login에도 자물쇠 표시
- **제안**: AuthController에 `@SecurityRequirements` 어노테이션으로 전역 설정 오버라이드

### H-3. PostgreSQL + `ddl-auto: update` 조합 위험
- **파일**: `application-dev.yaml:10`
- **문제**: 컬럼 삭제/타입 변경/이름 변경이 `update`로 처리되지 않아 스키마 불일치 위험
- **제안**: Flyway/Liquibase 도입 검토. 최소한 `validate`로 변경하여 불일치 감지

## M (Medium)

### M-1. Hibernate dialect 명시 불필요
- **파일**: `application-dev.yaml:13`
- **문제**: Hibernate 6부터 JDBC URL에서 dialect 자동 감지. 불필요한 설정
- **제안**: dialect 설정 제거

### M-2. H2 Console permitAll 잔존
- **파일**: `SecurityConfig.java:47`
- **문제**: PostgreSQL 전환 후에도 `/h2-console/**` permitAll 남아 있음
- **제안**: H2 관련 설정 제거 또는 프로파일 기반 분리

### M-3. Docker Compose healthcheck 미설정
- **파일**: `docker-compose.yaml`
- **문제**: PostgreSQL 컨테이너에 healthcheck 없어 앱이 먼저 기동될 경우 연결 실패 가능
- **제안**: `pg_isready` 기반 healthcheck 추가

### M-4. Member 엔티티 orphanRemoval 미설정
- **파일**: `Member.java` OneToMany 관계
- **문제**: `CascadeType.ALL`에 `orphanRemoval = true` 누락
- **제안**: 의도적 미설정이 아니라면 추가

### M-5. 가상 스레드 `enabled: false` 의도 불명확
- **파일**: `application.yaml:22`
- **문제**: 기본값이 false인데 명시적으로 false 설정. 의도 불분명
- **제안**: 의도적이면 주석 추가, 불필요하면 제거

## L (Low)

### L-1. OpenApiConfig 버전 하드코딩
- **파일**: `OpenApiConfig.java:13`
- **문제**: `version = "1.0"` vs `build.gradle`의 `0.0.1-SNAPSHOT` 불일치

### L-2. @Tag name 한국어만 사용
- **파일**: 6개 컨트롤러
- **문제**: 코드 생성 도구 사용 시 한국어 태그명이 문제될 수 있음
- **제안**: `@Tag(name = "Auth", description = "인증")` 형태 고려

### L-3. DailyLog 복합 유니크 제약 미설정
- **파일**: `DailyLog.java:19`
- **문제**: `(member_id, date)` 유니크 제약이 DB 레벨에 없어 동시 요청 시 중복 가능
- **제안**: `@Table(uniqueConstraints = ...)` 추가

### L-4. build.gradle 테스트 의존성 중복
- **파일**: `build.gradle:43`
- **문제**: `testImplementation 'spring-boot-starter-security'`가 `implementation`과 중복
- **제안**: 제거 (spring-security-test만 유지)

---

## Docker 컨테이너화 리뷰 (추가분)

### H (High)

### H-4. 백엔드 헬스체크 미설정
- **파일**: `docker-compose.yaml` backend 서비스
- **문제**: PostgreSQL은 healthcheck가 있지만 backend에는 없음. frontend가 `depends_on: backend`만 설정되어 백엔드 준비 전에 프론트엔드가 기동될 수 있음
- **제안**: Spring Boot Actuator 활성화 후 `curl -f http://localhost:8080/actuator/health` 헬스체크 추가, frontend에 `condition: service_healthy` 적용

### M (Medium)

### M-6. Nginx 보안 헤더 누락
- **파일**: `frontend/nginx.conf`
- **문제**: X-Frame-Options, X-Content-Type-Options 등 보안 응답 헤더 미설정
- **제안**: `add_header X-Frame-Options "SAMEORIGIN"`, `add_header X-Content-Type-Options "nosniff"` 등 추가

### M-7. 로깅 로테이션 미설정
- **파일**: `docker-compose.yaml`
- **문제**: 컨테이너 로그 드라이버 미설정으로 디스크 공간 고갈 위험
- **제안**: `logging: { driver: "json-file", options: { max-size: "10m", max-file: "3" } }` 추가

### L (Low)

### L-5. Nginx 베이스 이미지 버전 미명시
- **파일**: `frontend/Dockerfile`
- **문제**: `nginx:alpine`으로 버전 미지정. 이미지 업데이트 시 의도치 않은 변경 가능
- **제안**: `nginx:1.27-alpine` 등 구체적 버전 지정
