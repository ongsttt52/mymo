interface FormFieldProps {
    label: string;
    id: string;
    type?: 'text' | 'date' | 'url' | 'textarea';
    value: string;
    onChange: (value: string) => void;
    placeholder?: string;
    required?: boolean;
    disabled?: boolean;
    error?: string;
    rows?: number;
}

function FormField({ label, id, type = 'text', value, onChange, placeholder, required = false, disabled = false, error, rows = 3 }: FormFieldProps) {
    const inputClass = `w-full rounded-lg border px-3 py-2.5 text-sm outline-none transition-colors focus:border-primary focus:ring-2 focus:ring-primary/20 ${
        error ? 'border-red-300' : 'border-gray-300'
    } ${disabled ? 'cursor-not-allowed bg-gray-50 text-gray-500' : ''}`;

    return (
        <div className="flex flex-col gap-1.5">
            <label htmlFor={id} className="text-sm font-medium text-gray-700">
                {label}
                {required && <span className="ml-0.5 text-red-500">*</span>}
            </label>
            {type === 'textarea' ? (
                <textarea
                    id={id}
                    value={value}
                    onChange={(e) => onChange(e.target.value)}
                    placeholder={placeholder}
                    required={required}
                    disabled={disabled}
                    rows={rows}
                    aria-invalid={!!error}
                    aria-describedby={error ? `${id}-error` : undefined}
                    className={`${inputClass} resize-none`}
                />
            ) : (
                <input
                    id={id}
                    type={type}
                    value={value}
                    onChange={(e) => onChange(e.target.value)}
                    placeholder={placeholder}
                    required={required}
                    disabled={disabled}
                    aria-invalid={!!error}
                    aria-describedby={error ? `${id}-error` : undefined}
                    className={inputClass}
                />
            )}
            {error && <p id={`${id}-error`} role="alert" className="text-xs text-red-500">{error}</p>}
        </div>
    );
}

export default FormField;
