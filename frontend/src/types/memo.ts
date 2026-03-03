export interface MemoCreateRequest {
    content: string;
}

export interface MemoResponse {
    id: number;
    content: string;
    createdAt: string;
    updatedAt: string;
}

export interface MemoUpdateRequest {
    content: string;
}
