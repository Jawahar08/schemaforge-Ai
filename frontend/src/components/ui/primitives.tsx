'use client';

import { cn } from '@/lib/utils';

// ─── Skeleton ──────────────────────────────────────────────────────────────────
export function Skeleton({ className }: { className?: string }) {
  return (
    <div className={cn('animate-pulse rounded-md bg-secondary', className)} />
  );
}

// ─── Card ──────────────────────────────────────────────────────────────────────
export function Card({
  children,
  className,
  hover = false,
}: {
  children: React.ReactNode;
  className?: string;
  hover?: boolean;
}) {
  return (
    <div
      className={cn(
        'rounded-xl border border-border bg-card p-5',
        hover && 'hover:border-primary/30 hover:bg-card/80 transition-all duration-150 cursor-pointer',
        className
      )}
    >
      {children}
    </div>
  );
}

// ─── Badge ─────────────────────────────────────────────────────────────────────
type BadgeVariant = 'default' | 'success' | 'warning' | 'danger' | 'info' | 'muted';

const badgeVariants: Record<BadgeVariant, string> = {
  default: 'bg-primary/10 text-primary border-primary/20',
  success: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
  warning: 'bg-amber-500/10 text-amber-400 border-amber-500/20',
  danger: 'bg-red-500/10 text-red-400 border-red-500/20',
  info: 'bg-sky-500/10 text-sky-400 border-sky-500/20',
  muted: 'bg-secondary text-muted-foreground border-border',
};

export function Badge({
  children,
  variant = 'default',
  className,
}: {
  children: React.ReactNode;
  variant?: BadgeVariant;
  className?: string;
}) {
  return (
    <span
      className={cn(
        'inline-flex items-center gap-1 rounded-md border px-2 py-0.5 text-xs font-medium',
        badgeVariants[variant],
        className
      )}
    >
      {children}
    </span>
  );
}

// ─── Button ────────────────────────────────────────────────────────────────────
type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'danger' | 'outline';
type ButtonSize = 'sm' | 'md' | 'lg';

const btnVariants: Record<ButtonVariant, string> = {
  primary:
    'bg-gradient-to-r from-violet-600 to-indigo-600 text-white hover:from-violet-500 hover:to-indigo-500 shadow-lg shadow-violet-500/20',
  secondary: 'bg-secondary text-foreground hover:bg-secondary/80 border border-border',
  ghost: 'text-muted-foreground hover:text-foreground hover:bg-secondary',
  danger: 'bg-red-500/10 text-red-400 hover:bg-red-500/20 border border-red-500/20',
  outline: 'border border-border text-foreground hover:bg-secondary',
};

const btnSizes: Record<ButtonSize, string> = {
  sm: 'px-3 py-1.5 text-xs gap-1.5 rounded-md',
  md: 'px-4 py-2 text-sm gap-2 rounded-lg',
  lg: 'px-5 py-2.5 text-sm gap-2 rounded-lg',
};

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  loading?: boolean;
  icon?: React.ReactNode;
}

export function Button({
  children,
  variant = 'primary',
  size = 'md',
  loading,
  icon,
  className,
  disabled,
  ...rest
}: ButtonProps) {
  return (
    <button
      {...rest}
      disabled={disabled || loading}
      className={cn(
        'inline-flex items-center justify-center font-medium transition-all duration-150 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary disabled:opacity-50 disabled:cursor-not-allowed',
        btnVariants[variant],
        btnSizes[size],
        className
      )}
    >
      {loading ? (
        <span className="h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent" />
      ) : (
        icon
      )}
      {children}
    </button>
  );
}

// ─── Input ─────────────────────────────────────────────────────────────────────
export function Input({
  className,
  error,
  ...rest
}: React.InputHTMLAttributes<HTMLInputElement> & { error?: string }) {
  return (
    <div className="flex flex-col gap-1">
      <input
        {...rest}
        className={cn(
          'w-full rounded-lg border bg-secondary/50 px-3 py-2 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary/50 transition-all',
          error ? 'border-red-500/50' : 'border-border',
          className
        )}
      />
      {error && <p className="text-xs text-red-400">{error}</p>}
    </div>
  );
}

// ─── Textarea ──────────────────────────────────────────────────────────────────
export function Textarea({
  className,
  error,
  ...rest
}: React.TextareaHTMLAttributes<HTMLTextAreaElement> & { error?: string }) {
  return (
    <div className="flex flex-col gap-1">
      <textarea
        {...rest}
        className={cn(
          'w-full rounded-lg border bg-secondary/50 px-3 py-2 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary/50 transition-all resize-none',
          error ? 'border-red-500/50' : 'border-border',
          className
        )}
      />
      {error && <p className="text-xs text-red-400">{error}</p>}
    </div>
  );
}

// ─── Select ────────────────────────────────────────────────────────────────────
export function Select({
  className,
  ...rest
}: React.SelectHTMLAttributes<HTMLSelectElement>) {
  return (
    <select
      {...rest}
      className={cn(
        'w-full rounded-lg border border-border bg-secondary/50 px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary transition-all appearance-none cursor-pointer',
        className
      )}
    />
  );
}

// ─── Label ─────────────────────────────────────────────────────────────────────
export function Label({
  children,
  required,
  className,
}: {
  children: React.ReactNode;
  required?: boolean;
  className?: string;
}) {
  return (
    <label className={cn('text-xs font-medium text-muted-foreground', className)}>
      {children}
      {required && <span className="ml-0.5 text-red-400">*</span>}
    </label>
  );
}

// ─── FormField ────────────────────────────────────────────────────────────────
export function FormField({
  label,
  required,
  error,
  children,
  className,
}: {
  label?: string;
  required?: boolean;
  error?: string;
  children: React.ReactNode;
  className?: string;
}) {
  return (
    <div className={cn('flex flex-col gap-1.5', className)}>
      {label && <Label required={required}>{label}</Label>}
      {children}
      {error && <p className="text-xs text-red-400">{error}</p>}
    </div>
  );
}

// ─── Empty State ──────────────────────────────────────────────────────────────
export function EmptyState({
  icon,
  title,
  description,
  action,
}: {
  icon?: React.ReactNode;
  title: string;
  description?: string;
  action?: React.ReactNode;
}) {
  return (
    <div className="flex flex-col items-center justify-center py-16 px-8 text-center">
      {icon && <div className="mb-4 text-muted-foreground/50">{icon}</div>}
      <h3 className="text-sm font-medium text-foreground mb-1">{title}</h3>
      {description && <p className="text-xs text-muted-foreground mb-4 max-w-xs">{description}</p>}
      {action}
    </div>
  );
}

// ─── Spinner ──────────────────────────────────────────────────────────────────
export function Spinner({ size = 'md' }: { size?: 'sm' | 'md' | 'lg' }) {
  const sz = { sm: 'h-4 w-4', md: 'h-6 w-6', lg: 'h-8 w-8' }[size];
  return (
    <div
      className={cn(
        'animate-spin rounded-full border-2 border-current border-t-transparent text-primary',
        sz
      )}
    />
  );
}

// ─── Page header ──────────────────────────────────────────────────────────────
export function PageHeader({
  title,
  subtitle,
  actions,
}: {
  title: string;
  subtitle?: string;
  actions?: React.ReactNode;
}) {
  return (
    <div className="flex items-start justify-between gap-4 mb-6">
      <div>
        <h1 className="text-xl font-semibold text-foreground">{title}</h1>
        {subtitle && <p className="text-sm text-muted-foreground mt-0.5">{subtitle}</p>}
      </div>
      {actions && <div className="flex items-center gap-2 shrink-0">{actions}</div>}
    </div>
  );
}

// ─── Dialog ───────────────────────────────────────────────────────────────────
export function Dialog({
  open,
  onClose,
  title,
  children,
}: {
  open: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
}) {
  if (!open) return null;
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div
        className="absolute inset-0 bg-black/60 backdrop-blur-sm"
        onClick={onClose}
      />
      <div className="relative z-10 w-full max-w-lg rounded-xl border border-border bg-card shadow-2xl">
        <div className="flex items-center justify-between px-6 py-4 border-b border-border">
          <h2 className="text-sm font-semibold text-foreground">{title}</h2>
          <button
            onClick={onClose}
            className="text-muted-foreground hover:text-foreground transition-colors"
          >
            ✕
          </button>
        </div>
        <div className="p-6">{children}</div>
      </div>
    </div>
  );
}
