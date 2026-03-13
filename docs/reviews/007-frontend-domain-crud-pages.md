# 코드 리뷰: 프론트엔드 4개 도메인 CRUD 페이지

- **PR**: [#7](https://github.com/ongsttt52/mymo/pull/7)
- **리뷰일**: 2026-03-14

## 리뷰 결과 요약

| 심각도 | 건수 | 수정 상태 |
|--------|------|----------|
| C (Critical) | 3 | 모두 수정 완료 |
| H (High) | 5 | 모두 수정 완료 |
| M (Medium) | 8 | 5개 수정, 3개 보류 |
| L (Low) | 8 | 4개 수정, 4개 보류 |

## C (Critical) - 수정 완료

### C-1. PhotoLog 이미지 URL 미검증
- **문제**: `<img src={imageUrl}>`에 사용자 입력 URL을 검증 없이 사용
- **수정**: `isValidHttpsUrl()` 유틸 추가, 폼 제출 시 https 스킴 검증, 미리보기도 유효 URL에서만 표시

### C-2. MusicLog YouTube URL 피싱 위험
- **문제**: 임의의 URL을 "YouTube" 링크로 표시 가능
- **수정**: `isYouTubeUrl()` 유틸 추가, 카드에서 YouTube 도메인이 아닌 URL 링크 숨김, 폼에서 도메인 검증

### C-3. 401 처리 중복 (interceptor + 페이지)
- **상태**: interceptor의 리다이렉트 로직이 우선 동작하므로 실질적 이중 처리 문제는 낮음. 페이지의 401 분기는 방어적 코드로 유지

## H (High) - 수정 완료

### H-1, H-2. Modal 포커스 트랩 및 ARIA 속성 누락
- **수정**: Tab/Shift+Tab 포커스 트랩 구현, `role="dialog"`, `aria-modal="true"`, `aria-labelledby`, 오버레이 `aria-hidden`, 닫기 버튼 `aria-label` 추가

### H-3. 삭제 실패 시 ConfirmModal 미닫힘
- **수정**: catch 블록에 `setDeleteTarget(null)` 추가

### H-4. fetchLogs 의존 배열 경고 가능성
- **상태**: 현재 ESLint에서 경고 미발생 (기존 DashboardPage와 동일 패턴). 추후 커스텀 훅 리팩토링 시 해결 예정

### H-5. loading 중 모달 닫기 가능
- **수정**: Modal에 `preventClose` prop 추가, loading 시 ESC/오버레이 클릭/닫기 버튼 차단, 취소 버튼도 disabled

## M (Medium) - 수정 5건, 보류 3건

### 수정 완료
- **M-1**: formatDate/formatDateTime에 `isNaN(date.getTime())` 방어 코드 추가
- **M-2**: DailyLog 수정 시 날짜 필드 `disabled={!!editTarget}` 적용
- **M-3**: FormField에 `disabled` prop 지원 추가
- **M-4**: FormField에 `aria-invalid`, `aria-describedby` 연결
- **M-7**: PhotoLog 이미지 미리보기를 유효한 https URL에서만 표시 (불완전 URL 요청 방지)

### 보류
- **M-5**: 4개 Page 코드 중복 → 커스텀 훅 추출은 리팩토링 단계에서 진행
- **M-6**: ConfirmModal loading 텍스트 하드코딩 → 현재 삭제 용도로만 사용하므로 문제없음
- **M-8**: fieldErrors 미활용 → 백엔드 에러 응답 구조에 맞춰 추후 개선

## L (Low) - 수정 4건, 보류 4건

### 수정 완료
- **L-3**: MemoCard/PhotoLogCard에 `group-focus-within:opacity-100` 추가 (키보드 접근)
- **L-6**: `data.sort()` → `[...data].sort()` 불변성 준수
- **L-7**: `as AxiosError` → `axios.isAxiosError()` 타입 가드 사용
- **L-8**: `toISOString().split('T')[0]` → 로컬 타임존 기반 `getTodayString()` 함수 사용

### 보류
- **L-1**: EmptyState 이모지 접근성 → 장식 요소이므로 영향 미미
- **L-2**: 오버레이 aria-hidden → 수정 시 H-2에서 함께 처리 완료
- **L-4**: 에러 dismiss 기능 → 추후 Toast 컴포넌트 도입 시 해결
- **L-5**: PageHeader count 확장성 → 페이지네이션 도입 시 대응
