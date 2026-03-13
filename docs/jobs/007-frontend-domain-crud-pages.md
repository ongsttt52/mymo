# 작업 기록: 프론트엔드 4개 도메인 CRUD 페이지 구현

- **PR**: [#7](https://github.com/ongsttt52/mymo/pull/7)
- **브랜치**: `feat/frontend-domain-crud-pages` → `dev`
- **작업일**: 2026-03-14

## 작업 배경

백엔드 API 24개 엔드포인트와 프론트엔드 초기 셋업(로그인/회원가입/대시보드)이 완성된 상태이나, 핵심 기능인 4개 도메인(DailyLog, Memo, PhotoLog, MusicLog)의 CRUD 페이지가 미구현되어 서비스를 실질적으로 사용할 수 없었습니다.

## 설계 결정

### 모달 기반 CRUD
- 별도 페이지 대신 **목록 페이지 1개 + 생성/수정 모달 + 삭제 확인 모달**로 구현
- 근거: 데이터 구조가 단순하고(필드 최대 7~8개), 개인 사용 앱이므로 목록 컨텍스트를 유지한 채 CRUD 처리가 자연스러움
- 모달은 `createPortal(_, document.body)` 사용 (Layout의 `overflow-hidden` 회피)

### 도메인별 레이아웃
| 도메인 | 레이아웃 | 색상 | 근거 |
|--------|---------|------|------|
| DailyLog | 세로 리스트 | indigo | 날짜 기반, 시간순 정렬 중요 |
| Memo | 카드 그리드 | amber | 짧은 텍스트, 메모장 느낌 |
| PhotoLog | 카드 그리드 | emerald | 이미지 포함, 시각적 카드 |
| MusicLog | 세로 리스트 | rose | 필드가 많아 가로 공간 필요 |

## 구현 내용

### 생성 파일 (22개) + 수정 파일 (1개)

**공통 컴포넌트 (5개) + 유틸리티 (1개)**
- `Modal.tsx`: 포커스 트랩, ARIA 지원, createPortal 기반 범용 모달
- `ConfirmModal.tsx`: 삭제 확인 모달
- `FormField.tsx`: 폼 입력 필드 (disabled, aria-invalid/describedby 지원)
- `EmptyState.tsx`: 빈 상태 안내
- `PageHeader.tsx`: 페이지 헤더
- `format.ts`: 날짜 포맷팅 + URL 검증 유틸

**도메인별 각 4개 컴포넌트 × 4 도메인 = 16개**
- Page (상태관리 + CRUD 핸들러)
- List/Grid (렌더링)
- Card (개별 항목)
- FormModal (생성/수정 모달)

**수정 파일: `App.tsx`** (4개 라우트 추가)

## 커밋 이력

| 커밋 | 내용 |
|------|------|
| `27a8b08` | 공통 컴포넌트 및 유틸리티 추가 |
| `4444e63` | DailyLog CRUD 페이지 구현 |
| `c39cba6` | Memo CRUD 페이지 구현 |
| `b78aafb` | PhotoLog CRUD 페이지 구현 |
| `5811e4f` | MusicLog CRUD 페이지 구현 |
| `4762e7d` | PROGRESS.md 업데이트 |
| `b5d3813` | 코드 리뷰 피드백 반영 (보안/접근성/UX) |

## 검증

- `npm run build` - TypeScript 컴파일 에러 없음
- `npm run lint` - 새 코드에 ESLint 에러/경고 없음
