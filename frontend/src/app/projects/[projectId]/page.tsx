'use client';

import { use, useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { projectsApi, schemasApi, activitiesApi } from '@/lib/api-modules';
import { AuthGuard } from '@/components/auth/Guards';
import { AppShell } from '@/components/layout/AppShell';
import { PageHeader, Card, Button, Badge, EmptyState, Spinner, Dialog } from '@/components/ui/primitives';
import {
  Sparkles,
  Database,
  Plus,
  Trash2,
  ArrowRight,
  GitBranch,
  History,
  ChevronLeft,
  ChevronRight,
  User,
  FolderPlus,
  Code2,
  FileJson,
  MessageSquare,
  Users2,
  Activity,
  Calendar,
  Terminal,
} from 'lucide-react';
import { useToast } from '@/components/providers/ToastProvider';
import Link from 'next/link';
import { dialectLabel, formatDate } from '@/lib/utils';
import type { ActivityType } from '@/types';

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

export default function ProjectDetailPage({ params }: { params: Promise<{ projectId: string }> }) {
  const resolvedParams = use(params);
  const projectId = resolvedParams.projectId;
  const queryClient = useQueryClient();
  const { success, error } = useToast();

  const [activeTab, setActiveTab] = useState<'schemas' | 'activity'>('schemas');
  const [activityPage, setActivityPage] = useState(0);
  const [selectedActivityId, setSelectedActivityId] = useState<string | null>(null);

  const { data: project, isLoading: projectLoading } = useQuery({
    queryKey: ['projects', projectId],
    queryFn: () => projectsApi.get(projectId),
  });

  const { data: schemas, isLoading: schemasLoading } = useQuery({
    queryKey: ['projects', projectId, 'schemas'],
    queryFn: () => schemasApi.listForProject(projectId),
  });

  const { data: activityPageData, isLoading: activityLoading } = useQuery({
    queryKey: ['projects', projectId, 'activities', activityPage],
    queryFn: () => activitiesApi.getProjectActivities(projectId, { page: activityPage, size: 8 }),
    enabled: activeTab === 'activity',
  });

  const { data: detailData, isLoading: detailLoading } = useQuery({
    queryKey: ['activities', selectedActivityId],
    queryFn: () => activitiesApi.getActivityById(selectedActivityId!),
    enabled: !!selectedActivityId,
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

        {/* Tabs switcher */}
        <div className="flex border-b border-border/60 mb-6 gap-6">
          <button
            onClick={() => setActiveTab('schemas')}
            className={`pb-3 text-xs font-semibold uppercase tracking-wider transition-all relative ${
              activeTab === 'schemas' ? 'text-primary' : 'text-muted-foreground hover:text-foreground'
            }`}
          >
            Schemas
            {activeTab === 'schemas' && (
              <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-primary rounded-full" />
            )}
          </button>
          <button
            onClick={() => setActiveTab('activity')}
            className={`pb-3 text-xs font-semibold uppercase tracking-wider transition-all relative ${
              activeTab === 'activity' ? 'text-primary' : 'text-muted-foreground hover:text-foreground'
            }`}
          >
            Activity Log
            {activeTab === 'activity' && (
              <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-primary rounded-full" />
            )}
          </button>
        </div>

        <div className="space-y-6">
          {activeTab === 'schemas' ? (
            <>
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
            </>
          ) : (
            <>
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-xs font-semibold text-foreground uppercase tracking-wider">Project Audit Trail</h3>
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
                <div className="flex justify-center py-12"><Spinner /></div>
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
                <Card>
                  <EmptyState
                    icon={<History className="h-8 w-8 text-muted-foreground" />}
                    title="No project activity logs"
                    description="Activities related to this project will be documented here."
                  />
                </Card>
              )}
            </>
          )}
        </div>

        {/* Detailed View Dialog */}
        <Dialog
          open={!!selectedActivityId}
          onClose={() => setSelectedActivityId(null)}
          title="Project Activity Details"
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

                {detailData.schemaId && (
                  <div className="grid grid-cols-3 text-xs border-b border-border/40 pb-2">
                    <span className="text-muted-foreground font-medium">Schema ID</span>
                    <span className="col-span-2 text-foreground font-mono">{detailData.schemaId}</span>
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
