'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { motion } from 'framer-motion';
import {
  LayoutDashboard,
  FolderOpen,
  Database,
  Users,
  Bell,
  Settings,
  Sparkles,
  LogOut,
  ChevronRight,
  Cpu,
} from 'lucide-react';
import { useAuthStore } from '@/store/auth';
import { cn } from '@/lib/utils';

const NAV_ITEMS = [
  { href: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { href: '/projects', label: 'Projects', icon: FolderOpen },
  { href: '/teams', label: 'Teams', icon: Users },
  { href: '/notifications', label: 'Notifications', icon: Bell },
  { href: '/settings', label: 'Settings', icon: Settings },
];

export function AppSidebar() {
  const pathname = usePathname();
  const { user, logout } = useAuthStore();

  const initials = user?.fullName
    ?.split(' ')
    .map((n) => n[0])
    .join('')
    .toUpperCase()
    .slice(0, 2) ?? '?';

  return (
    <aside className="hidden lg:flex flex-col w-60 border-r border-border bg-card h-screen sticky top-0 shrink-0">
      {/* Logo */}
      <div className="flex items-center gap-2.5 px-5 py-4 border-b border-border">
        <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-violet-600 to-indigo-600 flex items-center justify-center shrink-0">
          <Cpu className="w-4 h-4 text-white" />
        </div>
        <span className="font-semibold text-sm tracking-tight text-foreground">SchemaForge AI</span>
      </div>

      {/* Navigation */}
      <nav className="flex-1 py-4 px-3 space-y-0.5 overflow-y-auto">
        {NAV_ITEMS.map(({ href, label, icon: Icon }) => {
          const isActive = pathname === href || pathname.startsWith(href + '/');
          return (
            <Link
              key={href}
              href={href}
              className={cn(
                'flex items-center gap-3 px-3 py-2 rounded-md text-sm transition-all duration-150 group relative',
                isActive
                  ? 'bg-primary/10 text-primary font-medium'
                  : 'text-muted-foreground hover:text-foreground hover:bg-secondary'
              )}
            >
              {isActive && (
                <motion.span
                  layoutId="sidebar-indicator"
                  className="absolute left-0 inset-y-1 w-0.5 rounded-full bg-primary"
                />
              )}
              <Icon className="h-4 w-4 shrink-0" />
              {label}
              {isActive && <ChevronRight className="h-3 w-3 ml-auto opacity-50" />}
            </Link>
          );
        })}
      </nav>

      {/* User */}
      <div className="border-t border-border p-3">
        <div className="flex items-center gap-3 px-2 py-2 rounded-md">
          <div className="w-7 h-7 rounded-full bg-gradient-to-br from-violet-500 to-indigo-500 flex items-center justify-center text-white text-xs font-bold shrink-0">
            {initials}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-xs font-medium text-foreground truncate">{user?.fullName}</p>
            <p className="text-[10px] text-muted-foreground truncate">{user?.email}</p>
          </div>
        </div>
        <div className="mt-1 px-2 py-1 flex items-center justify-between">
          <span className="text-[10px] text-muted-foreground flex items-center gap-1">
            <Sparkles className="w-3 h-3 text-amber-400" />
            {user?.aiCredits ?? 0} credits
          </span>
          <button
            onClick={logout}
            className="text-[10px] text-muted-foreground hover:text-red-400 transition-colors flex items-center gap-1"
          >
            <LogOut className="w-3 h-3" />
            Sign out
          </button>
        </div>
      </div>
    </aside>
  );
}
