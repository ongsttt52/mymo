export interface PagedResponse<T> {
    content: T[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
}

export interface SearchParams {
    keyword?: string;
    page?: number;
    size?: number;
}

export interface DateRangeSearchParams extends SearchParams {
    startDate?: string;
    endDate?: string;
}

export interface MusicLogSearchParams extends SearchParams {
    genre?: string;
}
