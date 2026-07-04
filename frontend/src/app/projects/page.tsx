'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { projectsApi } from '@/lib/api-modules';
import { AuthGuard } from '@/components/auth/Guards';
import { AppShell } from '@/components/layout/AppShell';
import { PageHeader, Card, Button, Badge, EmptyState, Spinner, Dialog, FormField, Input, Select } from '@/components/ui/primitives';
import { FolderOpen, Plus, Trash2, ArrowRight } from 'lucide-react';
import { useToast } from '@/components/providers/ToastProvider';
import Link from 'next/link';
import { dialectLabel, formatDate } from '@/lib/utils';
import type { ProjectDialect } from '@/types';

export default function ProjectsPage() {
  const queryClient = useQueryClient();
  const { success, error } = useToast();
  const [createOpen, setCreateOpen] = useState(false);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [dialect, setDialect] = useState<ProjectDialect>('postgresql');
  const [creating, setCreating] = useState(false);

  const { data: projects, isLoading } = useQuery({
    queryKey: ['projects'],
    queryFn: projectsApi.list,
  });

  const createMutation = useMutation({
    mutationFn: () => projectsApi.create({ name, description, dialect }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['projects'] });
      success('Project Created', `Project "${name}" was created successfully.`);
      setName('');
      setDescription('');
      setDialect('postgresql');
      setCreateOpen(false);
    },
    onError: (err: any) => {
      error('Failed to Create Project', err.message || 'Check input fields.');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (projectId: string) => projectsApi.delete(projectId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['projects'] });
      success('Project Deleted', 'Project was removed.');
    },
    onError: (err: any) => {
      error('Failed to Delete Project', err.message || 'Cannot delete at this time.');
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
          title="Projects"
          subtitle="Organize schemas, visualize design layouts, and version database models"
          actions={
            <Button size="sm" icon={<Plus className="h-4 w-4" />} onClick={() => setCreateOpen(true)}>
              New Project
            </Button>
          }
        />

        {isLoading ? (
          <div className="flex justify-center py-12"><Spinner /></div>
        ) : projects && projects.length > 0 ? (
          <div className="grid md:grid-cols-3 gap-6">
            {projects.map((p) => (
              <Card key={p.id} hover className="flex flex-col justify-between h-full relative group">
                <div>
                  <div className="flex items-start justify-between gap-2">
                    <h3 className="font-semibold text-sm text-foreground truncate">{p.name}</h3>
                    <Badge variant="default">{dialectLabel(p.dialect)}</Badge>
                  </div>
                  <p className="text-xs text-muted-foreground line-clamp-3 mt-2">
                    {p.description || 'No description provided.'}
                  </p>
                </div>
                <div className="flex items-center justify-between mt-6 pt-3 border-t border-border/50">
                  <span className="text-[10px] text-muted-foreground">Created {formatDate(p.createdAt)}</span>
                  <div className="flex items-center gap-2">
                    <button
                      onClick={(e) => {
                        e.preventDefault();
                        if (confirm(`Delete project "${p.name}"?`)) {
                          deleteMutation.mutate(p.id);
                        }
                      }}
                      className="p-1 rounded text-muted-foreground hover:text-red-400 transition-colors"
                      title="Delete Project"
                    >
                      <Trash2 className="h-3.5 w-3.5" />
                    </button>
                    <Link href={`/projects/${p.id}`} className="text-primary hover:underline text-xs flex items-center gap-1">
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
              icon={<FolderOpen className="h-10 w-10 text-muted-foreground" />}
              title="No projects created yet"
              description="Create a project to structure schema generation and database migration management."
              action={
                <Button size="sm" icon={<Plus className="h-4 w-4" />} onClick={() => setCreateOpen(true)}>
                  Create First Project
                </Button>
              }
            />
          </Card>
        )}

        <Dialog open={createOpen} onClose={() => setCreateOpen(false)} title="Create New Project">
          <form onSubmit={handleSubmit} className="space-y-4">
            <FormField label="Project Name" required>
              <Input
                placeholder="e.g. SaaS E-Commerce Store"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </FormField>
            <FormField label="Description">
              <Input
                placeholder="Briefly summarize database requirements"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
              />
            </FormField>
            <FormField label="Database Dialect" required>
              <Select value={dialect} onChange={(e) => setDialect(e.target.value as ProjectDialect)}>
                <option value="postgresql">PostgreSQL</option>
                <option value="mysql">MySQL</option>
                <option value="sqlserver">SQL Server</option>
                <option value="oracle">Oracle</option>
              </Select>
            </FormField>
            <div className="flex justify-end gap-2 mt-4 pt-2">
              <Button type="button" variant="secondary" onClick={() => setCreateOpen(false)}>
                Cancel
              </Button>
              <Button type="submit" loading={createMutation.isPending}>
                Create Project
              </Button>
            </div>
          </form>
        </Dialog>
      </AppShell>
    </AuthGuard>
  );
}
