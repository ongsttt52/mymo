import { useEffect, useRef } from 'react';
import { createPortal } from 'react-dom';

interface ModalProps {
    open: boolean;
    onClose: () => void;
    title: string;
    children: React.ReactNode;
    /** true이면 ESC/오버레이 클릭/닫기 버튼으로 닫기 방지 */
    preventClose?: boolean;
}

function Modal({ open, onClose, title, children, preventClose = false }: ModalProps) {
    const dialogRef = useRef<HTMLDivElement>(null);
    const titleId = `modal-title-${title.replace(/\s+/g, '-')}`;

    useEffect(() => {
        if (!open) return;

        const handleKeyDown = (e: KeyboardEvent) => {
            if (e.key === 'Escape' && !preventClose) onClose();

            // 포커스 트랩: Tab 키가 모달 안에서만 순환
            if (e.key === 'Tab' && dialogRef.current) {
                const focusable = dialogRef.current.querySelectorAll<HTMLElement>(
                    'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])',
                );
                if (focusable.length === 0) return;

                const first = focusable[0];
                const last = focusable[focusable.length - 1];

                if (e.shiftKey && document.activeElement === first) {
                    e.preventDefault();
                    last.focus();
                } else if (!e.shiftKey && document.activeElement === last) {
                    e.preventDefault();
                    first.focus();
                }
            }
        };

        document.addEventListener('keydown', handleKeyDown);
        document.body.style.overflow = 'hidden';

        // 모달 열릴 때 첫 번째 포커스 가능한 요소에 포커스
        requestAnimationFrame(() => {
            const first = dialogRef.current?.querySelector<HTMLElement>(
                'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])',
            );
            first?.focus();
        });

        return () => {
            document.removeEventListener('keydown', handleKeyDown);
            document.body.style.overflow = '';
        };
    }, [open, onClose, preventClose]);

    if (!open) return null;

    const handleOverlayClick = () => {
        if (!preventClose) onClose();
    };

    const handleCloseClick = () => {
        if (!preventClose) onClose();
    };

    return createPortal(
        <div className="fixed inset-0 z-50 flex items-center justify-center">
            <div
                className="fixed inset-0 bg-black/40"
                onClick={handleOverlayClick}
                aria-hidden="true"
            />
            <div
                ref={dialogRef}
                role="dialog"
                aria-modal="true"
                aria-labelledby={titleId}
                className="relative z-10 w-full max-w-lg rounded-2xl bg-white p-6 shadow-xl"
            >
                <div className="mb-4 flex items-center justify-between">
                    <h2 id={titleId} className="text-lg font-bold text-gray-900">{title}</h2>
                    <button
                        onClick={handleCloseClick}
                        aria-label="닫기"
                        className="rounded-lg p-1 text-gray-400 transition-colors hover:bg-gray-100 hover:text-gray-600"
                    >
                        <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>
                {children}
            </div>
        </div>,
        document.body,
    );
}

export default Modal;
