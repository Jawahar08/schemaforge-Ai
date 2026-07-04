'use client';

import { use, useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { projectsApi, schemasApi } from '@/lib/api-modules';
import { AuthGuard } from '@/components/auth/Guards';
import { AppShell } from '@/components/layout/AppShell';
import { PageHeader, Card, Button, Badge, EmptyState, Spinner, Dialog } from '@/components/ui/primitives';
import { Sparkles, Database, Plus, Trash2, ArrowRight, GitBranch } from 'lucide-react';
import { useToast } from '@/components/providers/ToastProvider';
import Link from 'next/link';
import { dialectLabel, formatDate, normalizationLabel } from '@/lib/utils';

export default function ProjectDetailPage({ params }: { params: Promise<{ projectId: string }> }) {
  const resolvedParams = use(params);
  const projectId = resolvedParams.projectId;
  const queryClient = useQueryClient();
  const { success, error } = useToast();

  const { data: project, isLoading: projectLoading } = useQuery({
    queryKey: ['projects', projectId],
    queryFn: () => projectsApi.get(projectId),
  });

  const { data: schemas, isLoading: schemasLoading } = useQuery({
    queryKey: ['projects', projectId, 'schemas'],
    queryFn: () => schemasApi.listForProject(projectId),
  });

  const deleteSchemaMutation = useMutation({
    mutationFn: (schemaId: string) => schemasApi.delete(schemaId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['projects', projectId, 'schemas'] });
      success('Schema Deleted', 'Schema was deleted successfully.');
    },
    onError: (err: any) => {
      error('Failed to Delete Schema', err.message || 'Cannot delete schema.');
    },
  });

  if (projectLoading) {
    return (
      <AuthGuard>
        <AppShell>
          <div className="flex justify-center py-20"><Spinner /></div>
        </AppShell>
      </AuthGuard>
    );
  }

  if (!project) {
    return (
      <AuthGuard>
        <AppShell>
          <EmptyState title="Project Not Found" description="This project does not exist or has been deleted." />
        </AppShell>
      </AuthGuard>
    );
  }

  return (
    <AuthGuard>
      <AppShell>
        <PageHeader
          title={project.name}
          subtitle={`Dialect: ${dialectLabel(project.dialect)} — ${project.description || 'No description provided.'}`}
          actions={
            <Link href={`/projects/${projectId}/generate`}>
              <Button size="sm" icon={<Sparkles className="h-4 w-4" />}>
                Generate Schema
              </Button>
            </Link>
          }
        />

        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <h2 className="text-sm font-semibold text-foreground uppercase tracking-wider">Schemas</h2>
          </div>

          {schemasLoading ? (
            <div className="flex justify-center py-12"><Spinner /></div>
          ) : schemas && schemas.length > 0 ? (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {schemas.map((s) => (
                <Card key={s.id} hover className="flex flex-col justify-between h-full group">
                  <div>
                    <div className="flex items-start justify-between gap-2">
                      <h3 className="font-semibold text-sm text-foreground truncate">{s.systemName}</h3>
                      <Badge variant="info">v{s.currentVersion}</Badge>
                    </div>
                    <div className="flex items-center gap-4 mt-3 text-xs text-muted-foreground">
                      <span>Tables: {s.tableCount}</span>
                      <span>Status: {s.status}</span>
                    </div>
                  </div>
                  <div className="flex items-center justify-between mt-6 pt-3 border-t border-border/50">
                    <span className="text-[10px] text-muted-foreground">Created {formatDate(s.createdAt)}</span>
                    <div className="flex items-center gap-2">
                      <button
                        onClick={(e) => {
                          e.preventDefault();
                          if (confirm(`Are you sure you want to delete schema "${s.systemName}"?`)) {
                            deleteSchemaMutation.mutate(s.id);
                          }
                        }}
                        className="p-1 rounded text-muted-foreground hover:text-red-400 transition-colors"
                        title="Delete Schema"
                      >
                        <Trash2 className="h-3.5 w-3.5" />
                      </button>
                      <Link href={`/schemas/${s.id}`} className="text-primary hover:underline text-xs flex items-center gap-1 font-medium">
                        Open Workspace <ArrowRight className="h-3 w-3" />
                      </Link>
                    </div>
                  </div>
                </Card>
              ))}
            </div>
          ) : (
            <Card>
              <EmptyState
                icon={<Database className="h-8 w-8 text-muted-foreground" />}
                title="No database schemas"
                description="Describe your application requirements using AI Workspace to generate a structured schema."
                action={
                  <Link href={`/projects/${projectId}/generate`}>
                    <Button variant="outline" size="sm" icon={<Sparkles className="h-3.5 w-3.5" />}>
                      Generate with AI
                    </Button>
                  </Link>
                }
              />
            </Card>
          )}
        </div>
      </AppShell>
    </AuthGuard>
  );
}
