import client from './client';
import type {
    MusicLogCreateRequest,
    MusicLogResponse,
    MusicLogUpdateRequest,
} from '../types/musicLog';

export const createMusicLog = (data: MusicLogCreateRequest) =>
    client.post<MusicLogResponse>('/music-logs', data);

export const getMusicLog = (id: number) =>
    client.get<MusicLogResponse>(`/music-logs/${id}`);

export const getMusicLogs = () =>
    client.get<MusicLogResponse[]>('/music-logs');

export const updateMusicLog = (id: number, data: MusicLogUpdateRequest) =>
    client.put<MusicLogResponse>(`/music-logs/${id}`, data);

export const deleteMusicLog = (id: number) =>
    client.delete<void>(`/music-logs/${id}`);
