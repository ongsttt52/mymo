interface PaginationProps {
    page: number;
    totalPages: number;
    onPageChange: (page: number) => void;
}

function Pagination({ page, totalPages, onPageChange }: PaginationProps) {
    if (totalPages <= 1) return null;

    const getPageNumbers = () => {
        const pages: (number | '...')[] = [];
        const maxVisible = 5;

        if (totalPages <= maxVisible) {
            for (let i = 0; i < totalPages; i++) pages.push(i);
        } else {
            pages.push(0);

            const start = Math.max(1, page - 1);
            const end = Math.min(totalPages - 2, page + 1);

            if (start > 1) pages.push('...');
            for (let i = start; i <= end; i++) pages.push(i);
            if (end < totalPages - 2) pages.push('...');

            pages.push(totalPages - 1);
        }
        return pages;
    };

    return (
        <nav className="mt-6 flex items-center justify-center gap-1">
            <button
                onClick={() => onPageChange(page - 1)}
                disabled={page === 0}
                className="rounded-lg px-3 py-2 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-100 disabled:cursor-not-allowed disabled:text-gray-300"
            >
                이전
            </button>
            {getPageNumbers().map((p, idx) =>
                p === '...' ? (
                    <span key={`ellipsis-${idx}`} className="px-2 text-gray-400">
                        ...
                    </span>
                ) : (
                    <button
                        key={p}
                        onClick={() => onPageChange(p)}
                        className={`rounded-lg px-3 py-2 text-sm font-medium transition-colors ${
                            p === page
                                ? 'bg-primary text-white'
                                : 'text-gray-700 hover:bg-gray-100'
                        }`}
                    >
                        {p + 1}
                    </button>
                )
            )}
            <button
                onClick={() => onPageChange(page + 1)}
                disabled={page >= totalPages - 1}
                className="rounded-lg px-3 py-2 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-100 disabled:cursor-not-allowed disabled:text-gray-300"
            >
                다음
            </button>
        </nav>
    );
}

export default Pagination;
