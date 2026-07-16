'use client';

import { use, useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { teamsApi, activitiesApi } from '@/lib/api-modules';
import { AuthGuard } from '@/components/auth/Guards';
import { AppShell } from '@/components/layout/AppShell';
import { PageHeader, Card, Button, Badge, Spinner, EmptyState, FormField, Input, Select, Dialog } from '@/components/ui/primitives';
import {
  Users,
  Mail,
  Plus,
  Trash2,
  ArrowLeft,
  History,
  ChevronLeft,
  ChevronRight,
  User,
  Calendar,
  Terminal,
  FolderPlus,
  Code2,
  FileJson,
  MessageSquare,
  Users2,
  Activity,
} from 'lucide-react';
import { useToast } from '@/components/providers/ToastProvider';
import Link from 'next/link';
import type { TeamMemberRole, ActivityType } from '@/types';
import { formatDate } from '@/lib/utils';

function getActivityMeta(type: ActivityType) {
  if (type.startsWith('PROJECT_')) {
    return { icon: FolderPlus, color: 'info' as const, category: 'Projects' };
  }
  if (type.startsWith('SCHEMA_')) {
    return { icon: Code2, color: type.includes('FAILED') ? ('danger' as const) : ('default' as const), category: 'Schemas' };
  }
  if (type.startsWith('EXPORT_')) {
    return { icon: FileJson, color: type.includes('FAILED') ? ('danger' as const) : ('success' as const), category: 'Exports' };
  }
  if (type.startsWith('COMMENT_')) {
    return { icon: MessageSquare, color: 'warning' as const, category: 'Comments' };
  }
  if (type.startsWith('TEAM_') || type.startsWith('INVITATION_')) {
    return { icon: Users2, color: 'success' as const, category: 'Teams' };
  }
  if (type === 'USER_LOGIN') {
    return { icon: User, color: 'info' as const, category: 'Access' };
  }
  return { icon: Activity, color: 'muted' as const, category: 'System' };
}

export default function TeamDetailPage({ params }: { params: Promise<{ teamId: string }> }) {
  const resolvedParams = use(params);
  const teamId = resolvedParams.teamId;
  const queryClient = useQueryClient();
  const { success, error } = useToast();

  const [inviteEmail, setInviteEmail] = useState('');
  const [inviteRole, setInviteRole] = useState<TeamMemberRole>('MEMBER');

  const [activityPage, setActivityPage] = useState(0);
  const [selectedActivityId, setSelectedActivityId] = useState<string | null>(null);

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

  const { data: activityPageData, isLoading: activityLoading } = useQuery({
    queryKey: ['teams', teamId, 'activities', activityPage],
    queryFn: () => activitiesApi.getTeamActivities(teamId, { page: activityPage, size: 6 }),
  });

  const { data: detailData, isLoading: detailLoading } = useQuery({
    queryKey: ['activities', selectedActivityId],
    queryFn: () => activitiesApi.getActivityById(selectedActivityId!),
    enabled: !!selectedActivityId,
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

            {/* Team Activity Log */}
            <div>
              <div className="flex items-center justify-between mb-3">
                <h2 className="text-xs font-semibold text-foreground uppercase tracking-wider">Team Activities</h2>
                {activityPageData && activityPageData.totalPages > 1 && (
                  <div className="flex items-center gap-2">
                    <span className="text-xs text-muted-foreground mr-1">
                      Page {activityPage + 1} of {activityPageData.totalPages}
                    </span>
                    <Button
                      size="sm"
                      variant="secondary"
                      onClick={() => setActivityPage((p) => Math.max(0, p - 1))}
                      disabled={activityPage === 0}
                    >
                      <ChevronLeft className="h-3.5 w-3.5" />
                    </Button>
                    <Button
                      size="sm"
                      variant="secondary"
                      onClick={() => setActivityPage((p) => Math.min(activityPageData.totalPages - 1, p + 1))}
                      disabled={activityPage >= activityPageData.totalPages - 1}
                    >
                      <ChevronRight className="h-3.5 w-3.5" />
                    </Button>
                  </div>
                )}
              </div>

              {activityLoading ? (
                <Spinner />
              ) : activityPageData && activityPageData.content.length > 0 ? (
                <div className="relative border-l border-border/80 ml-4 pl-6 space-y-4">
                  {activityPageData.content.map((item) => {
                    const meta = getActivityMeta(item.activityType);
                    const Icon = meta.icon;
                    return (
                      <div key={item.id} className="relative group">
                        <div className="absolute -left-[35px] mt-1.5 w-3 h-3 rounded-full border-2 border-background bg-card flex items-center justify-center text-primary group-hover:border-primary/50 transition-colors">
                          <div className="w-1 h-1 rounded-full bg-muted-foreground/60 group-hover:bg-primary transition-colors" />
                        </div>

                        <Card className="hover:border-primary/20 hover:bg-card/50 transition-all p-4">
                          <div className="flex items-center justify-between gap-3">
                            <div className="flex items-center gap-3">
                              <div className="p-1.5 bg-secondary rounded text-muted-foreground shrink-0">
                                <Icon className="h-3.5 w-3.5" />
                              </div>
                              <div>
                                <span className="font-semibold text-xs text-foreground block">{item.title}</span>
                                <span className="text-[10px] text-muted-foreground block mt-0.5">
                                  by <strong className="text-foreground">{item.actorName}</strong>
                                </span>
                              </div>
                            </div>
                            <div className="flex items-center gap-3 shrink-0">
                              <span className="text-[10px] text-muted-foreground">{formatDate(item.createdAt)}</span>
                              <Button size="sm" variant="ghost" className="h-7 py-1 px-2 text-[10px]" onClick={() => setSelectedActivityId(item.id)}>
                                Details
                              </Button>
                            </div>
                          </div>
                        </Card>
                      </div>
                    );
                  })}
                </div>
              ) : (
                <div className="text-xs text-muted-foreground">No team activity logs recorded.</div>
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

        {/* Detailed View Dialog */}
        <Dialog
          open={!!selectedActivityId}
          onClose={() => setSelectedActivityId(null)}
          title="Team Activity Details"
        >
          {detailLoading ? (
            <div className="flex justify-center py-10">
              <Spinner />
            </div>
          ) : detailData ? (
            <div className="space-y-4">
              <div>
                <h3 className="text-sm font-bold text-foreground mb-1">{detailData.title}</h3>
                <div className="flex items-center gap-2 mt-1.5">
                  <Badge variant={getActivityMeta(detailData.activityType).color}>
                    {getActivityMeta(detailData.activityType).category}
                  </Badge>
                  <span className="text-[10px] text-muted-foreground">{detailData.activityType}</span>
                </div>
              </div>

              {detailData.description && (
                <div className="bg-secondary/40 border border-border/60 rounded-lg p-3 text-xs text-foreground leading-relaxed">
                  {detailData.description}
                </div>
              )}

              <div className="space-y-2.5">
                <div className="grid grid-cols-3 text-xs border-b border-border/40 pb-2">
                  <span className="text-muted-foreground font-medium flex items-center gap-1.5">
                    <User className="h-3.5 w-3.5" /> Actor
                  </span>
                  <span className="col-span-2 text-foreground font-semibold">
                    {detailData.actorName} ({detailData.actorUserId.slice(0, 8)})
                  </span>
                </div>

                <div className="grid grid-cols-3 text-xs border-b border-border/40 pb-2">
                  <span className="text-muted-foreground font-medium flex items-center gap-1.5">
                    <Calendar className="h-3.5 w-3.5" /> Date & Time
                  </span>
                  <span className="col-span-2 text-foreground">
                    {new Date(detailData.createdAt).toLocaleString()}
                  </span>
                </div>

                {detailData.projectId && (
                  <div className="grid grid-cols-3 text-xs border-b border-border/40 pb-2">
                    <span className="text-muted-foreground font-medium">Project ID</span>
                    <span className="col-span-2 text-foreground font-mono">{detailData.projectId}</span>
                  </div>
                )}

                {detailData.metadata && Object.keys(detailData.metadata).length > 0 && (
                  <div className="space-y-1.5 pt-1">
                    <span className="text-xs text-muted-foreground font-medium flex items-center gap-1.5">
                      <Terminal className="h-3.5 w-3.5" /> Metadata
                    </span>
                    <pre className="text-[10px] text-muted-foreground bg-secondary/80 rounded-lg p-3 overflow-x-auto font-mono max-h-48 border border-border/60">
                      {JSON.stringify(detailData.metadata, null, 2)}
                    </pre>
                  </div>
                )}
              </div>

              <div className="pt-2 flex justify-end">
                <Button size="sm" variant="secondary" onClick={() => setSelectedActivityId(null)}>
                  Close
                </Button>
              </div>
            </div>
          ) : (
            <p className="text-xs text-muted-foreground text-center py-4">Failed to load activity details.</p>
          )}
        </Dialog>
      </AppShell>
    </AuthGuard>
  );
}
