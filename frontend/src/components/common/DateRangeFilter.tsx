interface DateRangeFilterProps {
    startDate: string;
    endDate: string;
    onStartDateChange: (date: string) => void;
    onEndDateChange: (date: string) => void;
}

function DateRangeFilter({
    startDate,
    endDate,
    onStartDateChange,
    onEndDateChange,
}: DateRangeFilterProps) {
    return (
        <div className="flex items-center gap-2">
            <input
                type="date"
                value={startDate}
                onChange={(e) => onStartDateChange(e.target.value)}
                className="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary"
            />
            <span className="text-sm text-gray-500">~</span>
            <input
                type="date"
                value={endDate}
                onChange={(e) => onEndDateChange(e.target.value)}
                className="rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary"
            />
        </div>
    );
}

export default DateRangeFilter;
