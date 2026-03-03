export interface MemberCreateRequest {
    username: string;
    email: string;
    password: string;
}

export interface MemberResponse {
    id: number;
    username: string;
    email: string;
}

export interface MemberUpdateRequest {
    username: string;
    email: string;
}
