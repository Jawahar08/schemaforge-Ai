import { type ClassValue, clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]): string {
  return twMerge(clsx(inputs));
}

export function formatDate(date: string | null | undefined): string {
  if (!date) return '—';
  return new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  }).format(new Date(date));
}

export function formatRelative(date: string | null | undefined): string {
  if (!date) return '—';
  const d = new Date(date);
  const now = new Date();
  const diff = now.getTime() - d.getTime();
  const mins = Math.floor(diff / 60_000);
  if (mins < 1) return 'just now';
  if (mins < 60) return `${mins}m ago`;
  const hrs = Math.floor(mins / 60);
  if (hrs < 24) return `${hrs}h ago`;
  const days = Math.floor(hrs / 24);
  if (days < 7) return `${days}d ago`;
  return formatDate(date);
}

export function dialectLabel(dialect: string): string {
  const map: Record<string, string> = {
    postgresql: 'PostgreSQL',
    mysql: 'MySQL',
    sqlserver: 'SQL Server',
    oracle: 'Oracle',
    POSTGRESQL: 'PostgreSQL',
    MYSQL: 'MySQL',
    SQLSERVER: 'SQL Server',
    ORACLE: 'Oracle',
  };
  return map[dialect] ?? dialect;
}

export function normalizationLabel(target: string | null | undefined): string {
  if (!target) return '—';
  const map: Record<string, string> = {
    TWO_NF: '2NF',
    THREE_NF: '3NF',
    BCNF: 'BCNF',
  };
  return map[target] ?? target;
}

export function truncate(str: string, maxLen = 60): string {
  return str.length > maxLen ? str.slice(0, maxLen) + '…' : str;
}
