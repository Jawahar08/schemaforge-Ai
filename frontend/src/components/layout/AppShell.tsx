'use client';

import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Bell,
  Menu,
  X,
  LayoutDashboard,
  FolderOpen,
  Users,
  Settings,
  LogOut,
  Sparkles,
  Cpu,
  Database,
} from 'lucide-react';
import { useAuthStore } from '@/store/auth';
import { cn } from '@/lib/utils';
import { AppSidebar } from './AppSidebar';
import { useQuery } from '@tanstack/react-query';
import { notificationsApi } from '@/lib/api-modules';

const MOBILE_NAV = [
  { href: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { href: '/projects', label: 'Projects', icon: FolderOpen },
  { href: '/teams', label: 'Teams', icon: Users },
  { href: '/notifications', label: 'Alerts', icon: Bell },
  { href: '/settings', label: 'Settings', icon: Settings },
];

export function AppShell({ children }: { children: React.ReactNode }) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const { user, logout } = useAuthStore();
  const router = useRouter();
  const pathname = usePathname();

  const { data: unread } = useQuery({
    queryKey: ['notifications', 'unread'],
    queryFn: notificationsApi.listUnread,
    refetchInterval: 30_000,
  });

  const unreadCount = unread?.length ?? 0;

  return (
    <div className="flex h-screen bg-background overflow-hidden">
      {/* Sidebar (desktop) */}
      <AppSidebar />

      {/* Mobile nav overlay */}
      <AnimatePresence>
        {mobileOpen && (
          <>
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="fixed inset-0 z-40 bg-black/60 lg:hidden"
              onClick={() => setMobileOpen(false)}
            />
            <motion.aside
              initial={{ x: -280 }}
              animate={{ x: 0 }}
              exit={{ x: -280 }}
              transition={{ type: 'spring', damping: 30 }}
              className="fixed left-0 top-0 bottom-0 z-50 w-64 flex flex-col bg-card border-r border-border lg:hidden"
            >
              <div className="flex items-center justify-between px-5 py-4 border-b border-border">
                <div className="flex items-center gap-2">
                  <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-violet-600 to-indigo-600 flex items-center justify-center">
                    <Cpu className="w-3.5 h-3.5 text-white" />
                  </div>
                  <span className="font-semibold text-sm">SchemaForge AI</span>
                </div>
                <button onClick={() => setMobileOpen(false)} className="text-muted-foreground">
                  <X className="h-4 w-4" />
                </button>
              </div>
              <nav className="flex-1 p-3 space-y-0.5">
                {MOBILE_NAV.map(({ href, label, icon: Icon }) => {
                  const active = pathname === href || pathname.startsWith(href + '/');
                  return (
                    <Link
                      key={href}
                      href={href}
                      onClick={() => setMobileOpen(false)}
                      className={cn(
                        'flex items-center gap-3 px-3 py-2 rounded-md text-sm transition-all',
                        active
                          ? 'bg-primary/10 text-primary font-medium'
                          : 'text-muted-foreground hover:text-foreground hover:bg-secondary'
                      )}
                    >
                      <Icon className="h-4 w-4" />
                      {label}
                      {href === '/notifications' && unreadCount > 0 && (
                        <span className="ml-auto bg-primary text-primary-foreground text-[10px] font-bold rounded-full px-1.5 py-0.5">
                          {unreadCount}
                        </span>
                      )}
                    </Link>
                  );
                })}
              </nav>
              <div className="p-3 border-t border-border">
                <button
                  onClick={() => { logout(); router.push('/login'); }}
                  className="flex items-center gap-2 text-sm text-muted-foreground hover:text-red-400 transition-colors px-3 py-2 w-full"
                >
                  <LogOut className="h-4 w-4" />
                  Sign out
                </button>
              </div>
            </motion.aside>
          </>
        )}
      </AnimatePresence>

      {/* Main content */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Top bar */}
        <header className="flex items-center justify-between px-4 lg:px-6 py-3 border-b border-border bg-card/50 backdrop-blur-sm shrink-0">
          <button
            onClick={() => setMobileOpen(true)}
            className="lg:hidden text-muted-foreground hover:text-foreground"
          >
            <Menu className="h-5 w-5" />
          </button>
          <div className="hidden lg:flex items-center gap-1 text-xs text-muted-foreground">
            <Database className="h-3 w-3" />
            <span>Database Engineering Platform</span>
          </div>
          <div className="flex items-center gap-3 ml-auto">
            <span className="hidden sm:flex items-center gap-1 text-xs text-muted-foreground bg-secondary px-2.5 py-1 rounded-md border border-border">
              <Sparkles className="w-3 h-3 text-amber-400" />
              {user?.aiCredits ?? 0} credits
            </span>
            <Link
              href="/notifications"
              className="relative text-muted-foreground hover:text-foreground transition-colors"
            >
              <Bell className="h-4.5 w-4.5 h-[18px] w-[18px]" />
              {unreadCount > 0 && (
                <span className="absolute -top-1 -right-1 h-3.5 w-3.5 bg-primary rounded-full text-[9px] text-white font-bold flex items-center justify-center">
                  {unreadCount > 9 ? '9+' : unreadCount}
                </span>
              )}
            </Link>
          </div>
        </header>

        {/* Page content */}
        <main className="flex-1 overflow-auto">
          <div className="max-w-7xl mx-auto p-4 lg:p-6">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
}
