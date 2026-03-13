import Modal from './Modal';

interface ConfirmModalProps {
    open: boolean;
    onClose: () => void;
    onConfirm: () => void;
    title: string;
    message: string;
    confirmLabel?: string;
    loading?: boolean;
}

function ConfirmModal({ open, onClose, onConfirm, title, message, confirmLabel = '삭제', loading = false }: ConfirmModalProps) {
    return (
        <Modal open={open} onClose={onClose} title={title}>
            <p className="mb-6 text-sm text-gray-600">{message}</p>
            <div className="flex justify-end gap-2">
                <button
                    onClick={onClose}
                    disabled={loading}
                    className="rounded-lg bg-gray-100 px-4 py-2 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-200 disabled:opacity-50"
                >
                    취소
                </button>
                <button
                    onClick={onConfirm}
                    disabled={loading}
                    className="rounded-lg bg-red-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-50"
                >
                    {loading ? '삭제 중...' : confirmLabel}
                </button>
            </div>
        </Modal>
    );
}

export default ConfirmModal;
