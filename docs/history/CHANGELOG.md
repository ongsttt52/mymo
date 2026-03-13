# 작업 이력

> 완료된 작업의 상세 기록. PROGRESS.md에서 아카이빙된 항목들.

---

## 프로젝트 초기 설정
- **브랜치**: `main` (직접 커밋)
- Spring Boot 3.5.10 + Java 21 프로젝트 생성
- H2 인메모리 DB, Spring Data JPA, Lombok, Validation 의존성 설정

## Domain 엔티티 구현
- **브랜치**: `main` (직접 커밋)
- `Member`, `DailyLog`, `Memo`, `PhotoLog`, `MusicLog` 5개 엔티티 생성
- 엔티티 간 관계 매핑 (Member 1:N 나머지 4개, CascadeType.ALL, LAZY 로딩)
- `BaseEntity` 추상 클래스로 `createdAt`, `updatedAt` 공통 관리

## Member CRUD + 예외 처리 인프라
- **PR**: [#1](https://github.com/ongsttt52/mymo/pull/1) (`feat/member-crud` → `dev`)
- 예외 처리 인프라: `ErrorCode`, `ErrorResponse`, `BusinessException`, `GlobalExceptionHandler`
- `MemberRepository`, `MemberService` (CRUD + 중복 검증), `MemberController` (5개 엔드포인트)
- 테스트: Repository 5개, Service 10개

## 4개 도메인 CRUD 구현
- **PR**: [#3](https://github.com/ongsttt52/mymo/pull/3) (`feat/domain-crud` → `dev`)
- DailyLog, Memo, PhotoLog, MusicLog CRUD 전체 구현
- 각 도메인별 NotFoundException + ErrorCode 추가
- 테스트: Repository 9개, Service 35개

## Spring Security + JWT 인증
- **PR**: [#4](https://github.com/ongsttt52/mymo/pull/4) (`feat/security-jwt` → `dev`)
- JWT 인프라 + Spring Security 설정
- 인증 API: signup, login
- `@CurrentMemberId` 커스텀 어노테이션으로 JWT에서 memberId 자동 추출
- 4개 도메인 서비스에 소유권 검증 추가
- 테스트: JWT 7개, Auth 5개 + 기존 테스트 소유권 검증 추가

## 프론트엔드 초기 셋업
- **PR**: [#5](https://github.com/ongsttt52/mymo/pull/5) (`feat/frontend-setup` → `main`)
- Vite + React 19 + TypeScript + Tailwind CSS v4
- axios JWT interceptor, Zustand 인증 스토어, React Router v7
- 레이아웃, 인증 폼, 대시보드 페이지

## 프론트엔드 4개 도메인 CRUD 페이지
- **PR**: [#7](https://github.com/ongsttt52/mymo/pull/7) (`feat/frontend-domain-crud` → `dev`)
- DailyLog, Memo, PhotoLog, MusicLog CRUD 페이지 구현
- 각 도메인별 생성/수정 폼, 목록/상세 페이지
