export interface MusicLogCreateRequest {
    title: string;
    artist?: string;
    album?: string;
    genre?: string;
    youtubeUrl?: string;
    description?: string;
    date?: string;
}

export interface MusicLogResponse {
    id: number;
    title: string;
    artist: string | null;
    album: string | null;
    genre: string | null;
    youtubeUrl: string | null;
    description: string | null;
    date: string | null;
    createdAt: string;
    updatedAt: string;
}

export interface MusicLogUpdateRequest {
    title: string;
    artist?: string;
    album?: string;
    genre?: string;
    youtubeUrl?: string;
    description?: string;
    date?: string;
}
