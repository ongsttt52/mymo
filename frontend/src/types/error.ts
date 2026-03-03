export interface FieldError {
    field: string;
    value: string;
    reason: string;
}

export interface ErrorResponse {
    code: string;
    message: string;
    fieldErrors: FieldError[];
    timestamp: string;
}
