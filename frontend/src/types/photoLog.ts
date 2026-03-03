export interface PhotoLogCreateRequest {
    imageUrl: string;
    location?: string;
    description?: string;
    date?: string;
}

export interface PhotoLogResponse {
    id: number;
    imageUrl: string;
    location: string | null;
    description: string | null;
    date: string | null;
    createdAt: string;
    updatedAt: string;
}

export interface PhotoLogUpdateRequest {
    imageUrl: string;
    location?: string;
    description?: string;
    date?: string;
}
