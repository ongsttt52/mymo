interface PageHeaderProps {
    title: string;
    count: number;
    actionLabel: string;
    onAction: () => void;
}

function PageHeader({ title, count, actionLabel, onAction }: PageHeaderProps) {
    return (
        <div className="mb-6 flex items-center justify-between">
            <div>
                <h2 className="text-2xl font-bold text-gray-900">{title}</h2>
                <p className="mt-1 text-sm text-gray-500">총 {count}개</p>
            </div>
            <button
                onClick={onAction}
                className="rounded-lg bg-primary px-4 py-2.5 text-sm font-medium text-white transition-colors hover:bg-primary-dark"
            >
                {actionLabel}
            </button>
        </div>
    );
}

export default PageHeader;
