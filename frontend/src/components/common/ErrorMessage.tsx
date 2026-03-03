interface ErrorMessageProps {
    message: string;
    onRetry?: () => void;
}

function ErrorMessage({ message, onRetry }: ErrorMessageProps) {
    return (
        <div className="rounded-lg bg-red-50 px-4 py-3">
            <p className="text-sm text-red-600">{message}</p>
            {onRetry && (
                <button
                    onClick={onRetry}
                    className="mt-2 text-sm font-medium text-red-600 hover:text-red-800"
                >
                    다시 시도
                </button>
            )}
        </div>
    );
}

export default ErrorMessage;
