# 페이징 + 검색/필터링 구현 계획

## Context

현재 4개 도메인(DailyLog, Memo, PhotoLog, MusicLog)의 목록 조회 API가 전체 데이터를 `List<T>`로 반환합니다. 데이터가 쌓이면 성능 문제가 발생하고, 사용자가 원하는 데이터를 찾기 어렵습니다. 페이징과 검색/필터링을 추가하여 이 문제를 해결합니다.

## 설계 결정

| 항목 | 결정 | 이유 |
|------|------|------|
| 응답 구조 | 커스텀 `PagedResponse<T>` 래퍼 | Spring `Page<T>` 직렬화 시 불필요한 필드 다수 포함 |
| 검색 방식 | `@Query` JPQL | 조건 2~3개 수준으로 Specification은 과잉, 쿼리 메서드는 이름이 과도하게 길어짐 |
| 정렬 | 도메인별 고정 (서버 하드코딩) | DailyLog=date DESC, Memo=updatedAt DESC, PhotoLog/MusicLog=date DESC |
| 페이지 크기 | 기본 20 (PhotoLog만 12) | 그리드 레이아웃은 이미지가 커서 적은 수 |
| 페이지 크기 상한 | 100 | `size=10000` 같은 악의적 요청 방지 |

## 커밋 계획

### 커밋 1: `feat: add PagedResponse common DTO`

**생성:**
- `src/main/java/.../dto/common/PagedResponse.java` — `Page<T>` → 프론트 친화적 응답 변환

```java
public record PagedResponse<T>(
    List<T> content, int page, int size,
    long totalElements, int totalPages,
    boolean first, boolean last) {
  public static <T> PagedResponse<T> from(Page<T> page) { ... }
}
```

**검증:** `./gradlew compileJava`

---

### 커밋 2: `feat: add paging and search to DailyLog API`

DailyLog를 레퍼런스로 먼저 구현합니다.

**수정 파일:**

| 파일 | 변경 |
|------|------|
| `repository/DailyLogRepository.java` | `@Query` JPQL `searchByMemberId(memberId, startDate, endDate, keyword, pageable)` 추가 |
| `service/DailyLogService.java` | `searchDailyLogs()` 메서드 추가 |
| `controller/DailyLogController.java` | `@GetMapping` 시그니처를 `PagedResponse` + `@RequestParam`(startDate, endDate, keyword, page, size)로 변경 |
| `test/.../repository/DailyLogRepositoryTest.java` | JPQL 쿼리 검증 테스트 추가 |
| `test/.../service/DailyLogServiceTest.java` | 검색 페이징 테스트 `@Nested` 클래스 추가 |

**검색 조건:** 날짜 범위(startDate, endDate) + 키워드(resolution, reflection에서 LIKE 검색)

**검증:** `./gradlew test`

---

### 커밋 3: `feat: add paging and search to Memo, PhotoLog, MusicLog APIs`

나머지 3개 도메인에 동일 패턴 적용.

**도메인별 검색 조건:**

| 도메인 | 키워드 검색 대상 | 추가 필터 |
|--------|-----------------|-----------|
| Memo | content | 없음 |
| PhotoLog | location, description | startDate, endDate |
| MusicLog | title, artist, album | genre (정확 일치) |

**수정 파일:** 3개 Repository + 3개 Service + 3개 Controller + 3개 Repository 테스트 + 3개 Service 테스트 (총 15개)

**검증:** `./gradlew test`

---

### 커밋 4: `feat: add paging types and update API calls in frontend`

**생성:**
- `frontend/src/types/common.ts` — `PagedResponse<T>`, `SearchParams`, `DateRangeSearchParams`, `MusicLogSearchParams`

**수정:** 4개 API 함수 (dailyLog.ts, memo.ts, photoLog.ts, musicLog.ts)
- 반환 타입: `Response[]` → `PagedResponse<Response>`
- params 파라미터 추가

**검증:** `npx tsc --noEmit`

---

### 커밋 5: `feat: add Pagination, SearchBar, DateRangeFilter components`

**생성:**
- `frontend/src/components/common/Pagination.tsx` — 이전/다음 + 페이지 번호
- `frontend/src/components/common/SearchBar.tsx` — 키워드 입력 + 검색/초기화 버튼
- `frontend/src/components/common/DateRangeFilter.tsx` — 시작일/종료일 date input

**검증:** 빌드 성공

---

### 커밋 6: `feat: integrate paging and search into DailyLogPage`

**수정:** `frontend/src/pages/DailyLogPage.tsx`
- 페이징/검색/필터 상태 추가
- `fetchLogs`에 params 전달, 응답에서 `data.content` 사용
- 클라이언트 사이드 정렬 제거 (서버에서 정렬됨)
- SearchBar + DateRangeFilter + Pagination UI 배치

**검증:** 브라우저에서 페이징/검색/필터 동작 확인

---

### 커밋 7: `feat: integrate paging and search into remaining pages`

**수정:** MemoPage, PhotoLogPage, MusicLogPage (3개 파일)
- 동일 패턴 적용
- MusicLogPage에 장르 드롭다운 `<select>` 추가

**검증:** 4개 페이지 모두 동작 확인

---

## 변경 파일 총괄 (33개)

| 구분 | 신규 | 수정 |
|------|------|------|
| 백엔드 | 1 (PagedResponse) | 12 (4 Repo + 4 Service + 4 Controller) |
| 테스트 | 0 | 8 (4 Repo + 4 Service) |
| 프론트 | 4 (common.ts, Pagination, SearchBar, DateRangeFilter) | 8 (4 API + 4 Page) |

## 주의사항

- `keyword` 빈 문자열은 Service에서 null로 정규화: `(keyword != null && !keyword.isBlank()) ? keyword.trim() : null`
- `size` 상한 100 적용: `Math.min(size, MAX_PAGE_SIZE)`
- H2 테스트 환경에서 JPQL `(:param IS NULL OR ...)` 패턴 정상 동작 확인됨
- 기존 `findByMemberIdOrderByXxxDesc` 메서드는 삭제하지 않고 유지 (내부 사용 가능)

## 검증 방법

1. `./gradlew test` — 전체 테스트 통과
2. `./gradlew spotlessCheck` — 포맷 검증
3. `npx tsc --noEmit` (frontend/) — 타입 검증
4. 브라우저 수동 테스트: 각 페이지에서 페이징 이동, 키워드 검색, 날짜 필터, 빈 결과 확인
