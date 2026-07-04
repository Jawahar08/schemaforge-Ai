'use client';

import { useQuery } from '@tanstack/react-query';
import { useAuthStore } from '@/store/auth';
import { userApi } from '@/lib/api-modules';
import { AuthGuard } from '@/components/auth/Guards';
import { AppShell } from '@/components/layout/AppShell';
import { PageHeader, Card, Badge } from '@/components/ui/primitives';
import { Sparkles, Shield, Mail, Calendar } from 'lucide-react';
import { formatDate } from '@/lib/utils';

export default function SettingsPage() {
  const { user } = useAuthStore();

  const { data: profile } = useQuery({
    queryKey: ['profile'],
    queryFn: userApi.me,
    initialData: user || undefined,
  });

  return (
    <AuthGuard>
      <AppShell>
        <PageHeader
          title="Account Settings"
          subtitle="Manage your profile settings, roles, and view AI usage statistics"
        />

        <div className="max-w-2xl space-y-6">
          <Card>
            <h3 className="text-sm font-semibold text-foreground mb-4">User Profile</h3>
            <div className="space-y-4">
              <div className="grid grid-cols-3 text-xs border-b border-border/40 pb-3">
                <span className="text-muted-foreground font-medium flex items-center gap-1.5">
                  Full Name
                </span>
                <span className="col-span-2 text-foreground font-semibold">{profile?.fullName}</span>
              </div>

              <div className="grid grid-cols-3 text-xs border-b border-border/40 pb-3">
                <span className="text-muted-foreground font-medium flex items-center gap-1.5">
                  <Mail className="h-3.5 w-3.5" /> Email Address
                </span>
                <span className="col-span-2 text-foreground font-mono">{profile?.email}</span>
              </div>

              <div className="grid grid-cols-3 text-xs border-b border-border/40 pb-3">
                <span className="text-muted-foreground font-medium flex items-center gap-1.5">
                  <Shield className="h-3.5 w-3.5" /> Account Role
                </span>
                <span className="col-span-2">
                  <Badge variant="default">{profile?.role}</Badge>
                </span>
              </div>

              <div className="grid grid-cols-3 text-xs border-b border-border/40 pb-3">
                <span className="text-muted-foreground font-medium flex items-center gap-1.5">
                  <Calendar className="h-3.5 w-3.5" /> Joined Date
                </span>
                <span className="col-span-2 text-foreground">{formatDate(profile?.createdAt)}</span>
              </div>
            </div>
          </Card>

          <Card>
            <h3 className="text-sm font-semibold text-foreground mb-3 flex items-center gap-1.5">
              <Sparkles className="w-4 h-4 text-amber-400" /> Usage Plan
            </h3>
            <p className="text-xs text-muted-foreground leading-relaxed">
              You are currently on the <strong className="text-foreground">Developer Sandbox Plan</strong> with an allocation of <strong>100 free monthly generation credits</strong>.
            </p>
          </Card>
        </div>
      </AppShell>
    </AuthGuard>
  );
}
