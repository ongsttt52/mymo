export interface DailyLogCreateRequest {
    date: string;
    resolution?: string;
    reflection?: string;
}

export interface DailyLogResponse {
    id: number;
    date: string;
    resolution: string | null;
    reflection: string | null;
    createdAt: string;
    updatedAt: string;
}

export interface DailyLogUpdateRequest {
    resolution?: string;
    reflection?: string;
}
