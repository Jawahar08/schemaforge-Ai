'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { teamsApi } from '@/lib/api-modules';
import { AuthGuard } from '@/components/auth/Guards';
import { AppShell } from '@/components/layout/AppShell';
import { PageHeader, Card, Button, Badge, Spinner, EmptyState, Dialog, FormField, Input } from '@/components/ui/primitives';
import { Users, Plus, ArrowRight, Trash2 } from 'lucide-react';
import { useToast } from '@/components/providers/ToastProvider';
import Link from 'next/link';

export default function TeamsPage() {
  const queryClient = useQueryClient();
  const { success, error } = useToast();
  const [createOpen, setCreateOpen] = useState(false);
  const [name, setName] = useState('');

  const { data: teams, isLoading } = useQuery({
    queryKey: ['teams'],
    queryFn: teamsApi.list,
  });

  const createMutation = useMutation({
    mutationFn: () => teamsApi.create({ name }),
    onSuccess: (newTeam) => {
      queryClient.invalidateQueries({ queryKey: ['teams'] });
      success('Team Created', `Workspace "${newTeam.name}" has been created.`);
      setName('');
      setCreateOpen(false);
    },
    onError: (err: any) => {
      error('Failed to Create Team', err.message);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => teamsApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teams'] });
      success('Deleted', 'Team workspace deleted.');
    },
    onError: (err: any) => {
      error('Failed to Delete', err.message);
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name) return;
    createMutation.mutate();
  };

  return (
    <AuthGuard>
      <AppShell>
        <PageHeader
          title="Teams"
          subtitle="Collaborate on database design structures with organization members"
          actions={
            <Button size="sm" icon={<Plus className="h-4 w-4" />} onClick={() => setCreateOpen(true)}>
              New Team
            </Button>
          }
        />

        {isLoading ? (
          <div className="flex justify-center py-12"><Spinner /></div>
        ) : teams && teams.length > 0 ? (
          <div className="grid md:grid-cols-3 gap-6">
            {teams.map((t) => (
              <Card key={t.id} hover className="flex flex-col justify-between h-full relative group">
                <div>
                  <div className="flex items-start justify-between gap-2">
                    <h3 className="font-semibold text-sm text-foreground truncate">{t.name}</h3>
                    <Badge variant="default">{t.plan}</Badge>
                  </div>
                  <p className="text-xs text-muted-foreground mt-2">Slug: {t.slug}</p>
                </div>
                <div className="flex items-center justify-between mt-6 pt-3 border-t border-border/50">
                  <span className="text-[10px] text-muted-foreground">Workspace ID: {t.id.slice(0, 8)}</span>
                  <div className="flex items-center gap-2">
                    <button
                      onClick={(e) => {
                        e.preventDefault();
                        if (confirm(`Delete team "${t.name}"?`)) {
                          deleteMutation.mutate(t.id);
                        }
                      }}
                      className="p-1 rounded text-muted-foreground hover:text-red-400 transition-colors"
                      title="Delete Team"
                    >
                      <Trash2 className="h-3.5 w-3.5" />
                    </button>
                    <Link href={`/teams/${t.id}`} className="text-primary hover:underline text-xs flex items-center gap-1 font-medium">
                      Manage <ArrowRight className="h-3 w-3" />
                    </Link>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        ) : (
          <Card>
            <EmptyState
              icon={<Users className="h-10 w-10 text-muted-foreground" />}
              title="No teams active"
              description="Create a team workspace to invite designers, developers, and product owners."
              action={
                <Button size="sm" icon={<Plus className="h-4 w-4" />} onClick={() => setCreateOpen(true)}>
                  Create Team
                </Button>
              }
            />
          </Card>
        )}

        <Dialog open={createOpen} onClose={() => setCreateOpen(false)} title="Create New Team Workspace">
          <form onSubmit={handleSubmit} className="space-y-4">
            <FormField label="Team Workspace Name" required>
              <Input
                placeholder="e.g. Engineering Core"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </FormField>
            <div className="flex justify-end gap-2 mt-4 pt-2">
              <Button type="button" variant="secondary" onClick={() => setCreateOpen(false)}>
                Cancel
              </Button>
              <Button type="submit" loading={createMutation.isPending}>
                Create Team
              </Button>
            </div>
          </form>
        </Dialog>
      </AppShell>
    </AuthGuard>
  );
}
