'use client';

import { use, useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { teamsApi } from '@/lib/api-modules';
import { AuthGuard } from '@/components/auth/Guards';
import { AppShell } from '@/components/layout/AppShell';
import { PageHeader, Card, Button, Badge, Spinner, EmptyState, FormField, Input, Select } from '@/components/ui/primitives';
import { Users, Mail, Plus, Trash2, ArrowLeft } from 'lucide-react';
import { useToast } from '@/components/providers/ToastProvider';
import Link from 'next/link';
import type { TeamMemberRole } from '@/types';

export default function TeamDetailPage({ params }: { params: Promise<{ teamId: string }> }) {
  const resolvedParams = use(params);
  const teamId = resolvedParams.teamId;
  const queryClient = useQueryClient();
  const { success, error } = useToast();

  const [inviteEmail, setInviteEmail] = useState('');
  const [inviteRole, setInviteRole] = useState<TeamMemberRole>('MEMBER');

  const { data: team, isLoading: teamLoading } = useQuery({
    queryKey: ['teams', teamId],
    queryFn: () => teamsApi.get(teamId),
  });

  const { data: members, isLoading: membersLoading } = useQuery({
    queryKey: ['teams', teamId, 'members'],
    queryFn: () => teamsApi.getMembers(teamId),
  });

  const { data: invitations, isLoading: invitesLoading } = useQuery({
    queryKey: ['teams', teamId, 'invitations'],
    queryFn: () => teamsApi.listInvitations(teamId),
  });

  const inviteMutation = useMutation({
    mutationFn: () => teamsApi.invite(teamId, { email: inviteEmail, role: inviteRole }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teams', teamId, 'invitations'] });
      success('Invitation Sent', `Invitation was successfully sent to ${inviteEmail}`);
      setInviteEmail('');
    },
    onError: (err: any) => {
      error('Failed to Invite Member', err.message);
    },
  });

  const removeMemberMutation = useMutation({
    mutationFn: (userId: string) => teamsApi.removeMember(teamId, userId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teams', teamId, 'members'] });
      success('Member Removed', 'User has been removed from the team.');
    },
    onError: (err: any) => {
      error('Action Failed', err.message);
    },
  });

  if (teamLoading) {
    return (
      <AuthGuard>
        <AppShell>
          <div className="flex justify-center py-20"><Spinner /></div>
        </AppShell>
      </AuthGuard>
    );
  }

  if (!team) {
    return (
      <AuthGuard>
        <AppShell>
          <EmptyState title="Team Not Found" description="This team workspace does not exist or has been deleted." />
        </AppShell>
      </AuthGuard>
    );
  }

  return (
    <AuthGuard>
      <AppShell>
        <div className="mb-4">
          <Link href="/teams" className="text-xs text-muted-foreground hover:text-foreground flex items-center gap-1">
            <ArrowLeft className="h-3 w-3" /> Back to Teams
          </Link>
        </div>

        <PageHeader
          title={team.name}
          subtitle={`Plan level: ${team.plan} | Owner ID: ${team.ownerId}`}
        />

        <div className="grid lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-6">
            <div>
              <h2 className="text-xs font-semibold text-foreground uppercase tracking-wider mb-3">Members</h2>
              {membersLoading ? (
                <Spinner />
              ) : members && members.length > 0 ? (
                <div className="space-y-3">
                  {members.map((m) => (
                    <Card key={m.id} className="flex items-center justify-between p-4">
                      <div>
                        <span className="text-xs font-semibold text-foreground">User {m.userId.slice(0, 8)}</span>
                        <p className="text-[10px] text-muted-foreground mt-0.5">Joined at {new Date(m.joinedAt).toLocaleDateString()}</p>
                      </div>
                      <div className="flex items-center gap-3">
                        <Badge variant="default">{m.role}</Badge>
                        {m.role !== 'OWNER' && (
                          <button
                            onClick={() => {
                              if (confirm('Remove member from workspace?')) {
                                removeMemberMutation.mutate(m.userId);
                              }
                            }}
                            className="p-1 rounded text-muted-foreground hover:text-red-400 transition-colors"
                          >
                            <Trash2 className="h-4 w-4" />
                          </button>
                        )}
                      </div>
                    </Card>
                  ))}
                </div>
              ) : (
                <div className="text-xs text-muted-foreground">No active members found.</div>
              )}
            </div>

            <div>
              <h2 className="text-xs font-semibold text-foreground uppercase tracking-wider mb-3">Sent Invitations</h2>
              {invitesLoading ? (
                <Spinner />
              ) : invitations && invitations.length > 0 ? (
                <div className="space-y-3">
                  {invitations.map((i) => (
                    <Card key={i.id} className="flex items-center justify-between p-4">
                      <div>
                        <span className="text-xs font-semibold text-foreground">{i.email}</span>
                        <p className="text-[10px] text-muted-foreground mt-0.5">Role: {i.role}</p>
                      </div>
                      <Badge variant={i.status === 'PENDING' ? 'warning' : 'success'}>{i.status}</Badge>
                    </Card>
                  ))}
                </div>
              ) : (
                <div className="text-xs text-muted-foreground">No pending invitations.</div>
              )}
            </div>
          </div>

          <div>
            <Card>
              <h3 className="text-sm font-semibold text-foreground mb-4 flex items-center gap-1.5">
                <Mail className="h-4 w-4 text-primary" /> Invite Team Member
              </h3>
              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  if (!inviteEmail) return;
                  inviteMutation.mutate();
                }}
                className="space-y-4"
              >
                <FormField label="Email Address" required>
                  <Input
                    type="email"
                    placeholder="colleague@company.com"
                    value={inviteEmail}
                    onChange={(e) => setInviteEmail(e.target.value)}
                    required
                  />
                </FormField>
                <FormField label="Workspace Role" required>
                  <Select value={inviteRole} onChange={(e) => setInviteRole(e.target.value as TeamMemberRole)}>
                    <option value="MEMBER">Member (Can edit schemas)</option>
                    <option value="VIEWER">Viewer (Read-only)</option>
                    <option value="ADMIN">Admin (Manage settings)</option>
                  </Select>
                </FormField>
                <Button type="submit" className="w-full justify-center" loading={inviteMutation.isPending}>
                  Send Invite
                </Button>
              </form>
            </Card>
          </div>
        </div>
      </AppShell>
    </AuthGuard>
  );
}
