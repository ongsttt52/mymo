export function formatDate(dateString: string): string {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return dateString;
    return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
    });
}

export function formatDateTime(dateString: string): string {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return dateString;
    return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
    });
}

/** 로컬 타임존 기준 오늘 날짜를 YYYY-MM-DD 형식으로 반환 */
export function getTodayString(): string {
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
}

/** URL이 https:// 로 시작하는지 검증 */
export function isValidHttpsUrl(url: string): boolean {
    try {
        const parsed = new URL(url);
        return parsed.protocol === 'https:';
    } catch {
        return false;
    }
}

/** URL이 YouTube 도메인인지 검증 */
export function isYouTubeUrl(url: string): boolean {
    try {
        const parsed = new URL(url);
        const host = parsed.hostname.replace('www.', '');
        return host === 'youtube.com' || host === 'youtu.be' || host === 'music.youtube.com';
    } catch {
        return false;
    }
}
