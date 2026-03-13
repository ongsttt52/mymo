interface EmptyStateProps {
    icon: string;
    message: string;
    actionLabel: string;
    onAction: () => void;
}

function EmptyState({ icon, message, actionLabel, onAction }: EmptyStateProps) {
    return (
        <div className="flex flex-col items-center justify-center py-16">
            <span className="text-5xl">{icon}</span>
            <p className="mt-4 text-gray-500">{message}</p>
            <button
                onClick={onAction}
                className="mt-4 rounded-lg bg-primary px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-primary-dark"
            >
                {actionLabel}
            </button>
        </div>
    );
}

export default EmptyState;
