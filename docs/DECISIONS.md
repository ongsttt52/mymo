# 아키텍처 결정사항

> 핵심 기술 선택과 구조 변경의 근거를 기록합니다.

## H2 → PostgreSQL 전환
- H2 인메모리는 서버 재시작 시 데이터 소실, 프로덕션 부적합
- 테스트는 H2 in-memory 유지 (`testRuntimeOnly`) — 빠른 피드백 루프

## Spring Profile 분리
- `application.yaml`: 공통 설정 (서버 포트, JPA 공통, JWT)
- `application-dev.yaml`: PostgreSQL 연결 (개발)
- `src/test/resources/application.yaml`: H2 (테스트)

## JWT 인증 방식
- Stateless 세션 (서버 측 세션 저장 불필요)
- `@CurrentMemberId` 커스텀 어노테이션으로 컨트롤러에서 memberId 자동 추출
- 시크릿은 `${JWT_SECRET:기본값}` 패턴으로 환경변수 주입

## Nginx 리버스 프록시 아키텍처
- 프론트엔드가 상대경로 `/api`를 사용하므로, Nginx가 리버스 프록시하면 CORS 불필요
- `/api/*`, `/swagger-ui/*`, `/v3/api-docs/*` → backend:8080
- `/*` → React SPA (`try_files $uri /index.html`)

## 엔티티 설계 원칙
- Setter 금지, 도메인 메서드로 상태 변경
- 모든 1:N 관계는 CASCADE ALL + LAZY 로딩
- `@Lob` 대신 `@Column(columnDefinition = "TEXT")` 사용 (PostgreSQL 호환)

## 페이징 응답 구조
- Spring `Page<T>` 직접 직렬화 대신 커스텀 `PagedResponse<T>` 래퍼 사용
- `Page<T>`는 `sort`, `pageable` 등 불필요한 필드를 다수 포함하므로, 프론트엔드에 필요한 필드만 전달

## 검색/필터링 방식
- `@Query` JPQL 사용 — 조건이 2~3개 수준으로 Specification은 과잉, 쿼리 메서드명은 과도하게 길어짐
- `(:param IS NULL OR ...)` 패턴으로 선택적 조건 처리 (null이면 해당 조건 무시)
- 정렬은 도메인별 고정 (DailyLog=date DESC, Memo=updatedAt DESC 등) — 사용자 정렬 변경 니즈 없음
- 페이지 크기 상한 100 적용으로 악의적 요청 방지
