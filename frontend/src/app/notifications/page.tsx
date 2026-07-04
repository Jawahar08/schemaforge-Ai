'use client';

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { notificationsApi } from '@/lib/api-modules';
import { AuthGuard } from '@/components/auth/Guards';
import { AppShell } from '@/components/layout/AppShell';
import { PageHeader, Card, Button, Badge, Spinner, EmptyState } from '@/components/ui/primitives';
import { Bell, Check, Trash2 } from 'lucide-react';
import { useToast } from '@/components/providers/ToastProvider';
import { formatDate } from '@/lib/utils';

export default function NotificationsPage() {
  const queryClient = useQueryClient();
  const { success, error } = useToast();

  const { data: notifications, isLoading } = useQuery({
    queryKey: ['notifications'],
    queryFn: notificationsApi.list,
  });

  const markAllReadMutation = useMutation({
    mutationFn: notificationsApi.markAllRead,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] });
      queryClient.invalidateQueries({ queryKey: ['notifications', 'unread'] });
      success('Success', 'All notifications marked as read.');
    },
    onError: (err: any) => {
      error('Failed to update', err.message);
    },
  });

  const markReadMutation = useMutation({
    mutationFn: (id: string) => notificationsApi.markRead(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] });
      queryClient.invalidateQueries({ queryKey: ['notifications', 'unread'] });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => notificationsApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] });
      queryClient.invalidateQueries({ queryKey: ['notifications', 'unread'] });
      success('Deleted', 'Notification removed.');
    },
  });

  const hasUnread = notifications?.some((n) => !n.read) ?? false;

  return (
    <AuthGuard>
      <AppShell>
        <PageHeader
          title="Notifications"
          subtitle="Stay updated with team invites, schema analysis runs, and comment threads"
          actions={
            hasUnread && (
              <Button size="sm" variant="secondary" onClick={() => markAllReadMutation.mutate()}>
                Mark all as read
              </Button>
            )
          }
        />

        {isLoading ? (
          <div className="flex justify-center py-12"><Spinner /></div>
        ) : notifications && notifications.length > 0 ? (
          <div className="space-y-3 max-w-3xl">
            {notifications.map((n) => (
              <Card
                key={n.id}
                className={`flex items-start justify-between gap-4 p-4 border transition-all ${
                  n.read ? 'bg-card/50 opacity-70' : 'border-primary/20 bg-primary/5'
                }`}
              >
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2">
                    <span className="font-semibold text-xs text-foreground">{n.title}</span>
                    {!n.read && <Badge variant="default">New</Badge>}
                  </div>
                  <p className="text-xs text-muted-foreground mt-1 leading-relaxed">{n.message}</p>
                  <span className="text-[10px] text-muted-foreground/60 block mt-2">{formatDate(n.createdAt)}</span>
                </div>
                <div className="flex items-center gap-1 shrink-0">
                  {!n.read && (
                    <button
                      onClick={() => markReadMutation.mutate(n.id)}
                      className="p-1 rounded text-muted-foreground hover:text-emerald-400 transition-colors"
                      title="Mark as read"
                    >
                      <Check className="h-4 w-4" />
                    </button>
                  )}
                  <button
                    onClick={() => deleteMutation.mutate(n.id)}
                    className="p-1 rounded text-muted-foreground hover:text-red-400 transition-colors"
                    title="Delete notification"
                  >
                    <Trash2 className="h-4 w-4" />
                  </button>
                </div>
              </Card>
            ))}
          </div>
        ) : (
          <Card>
            <EmptyState
              icon={<Bell className="h-10 w-10 text-muted-foreground" />}
              title="No alerts found"
              description="Any system updates or workspace invites will display here."
            />
          </Card>
        )}
      </AppShell>
    </AuthGuard>
  );
}
