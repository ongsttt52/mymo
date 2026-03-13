import { useState } from 'react';

interface SearchBarProps {
    onSearch: (keyword: string) => void;
    placeholder?: string;
}

function SearchBar({ onSearch, placeholder = '검색어를 입력하세요' }: SearchBarProps) {
    const [value, setValue] = useState('');

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSearch(value.trim());
    };

    const handleReset = () => {
        setValue('');
        onSearch('');
    };

    return (
        <form onSubmit={handleSubmit} className="flex gap-2">
            <input
                type="text"
                value={value}
                onChange={(e) => setValue(e.target.value)}
                placeholder={placeholder}
                className="flex-1 rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary"
            />
            <button
                type="submit"
                className="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-primary-dark"
            >
                검색
            </button>
            {value && (
                <button
                    type="button"
                    onClick={handleReset}
                    className="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-50"
                >
                    초기화
                </button>
            )}
        </form>
    );
}

export default SearchBar;
