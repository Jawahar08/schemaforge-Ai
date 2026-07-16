'use client';

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { activitiesApi } from '@/lib/api-modules';
import { AuthGuard } from '@/components/auth/Guards';
import { AppShell } from '@/components/layout/AppShell';
import { PageHeader, Card, Button, Badge, Spinner, EmptyState, Dialog, Select } from '@/components/ui/primitives';
import { formatDate } from '@/lib/utils';
import {
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
  Globe,
  Terminal,
  RefreshCw
} from 'lucide-react';
import type { ActivityType } from '@/types';

const ACTIVITY_TYPES: { value: ActivityType | 'ALL'; label: string }[] = [
  { value: 'ALL', label: 'All Activities' },
  { value: 'PROJECT_CREATED', label: 'Project Created' },
  { value: 'PROJECT_UPDATED', label: 'Project Updated' },
  { value: 'PROJECT_ARCHIVED', label: 'Project Archived' },
  { value: 'SCHEMA_GENERATED', label: 'Schema Generated' },
  { value: 'SCHEMA_CREATED', label: 'Schema Created' },
  { value: 'SCHEMA_UPDATED', label: 'Schema Updated' },
  { value: 'SCHEMA_DELETED', label: 'Schema Deleted' },
  { value: 'EXPORT_CREATED', label: 'Export Created' },
  { value: 'EXPORT_FAILED', label: 'Export Failed' },
  { value: 'COMMENT_CREATED', label: 'Comment Created' },
  { value: 'TEAM_CREATED', label: 'Team Created' },
  { value: 'TEAM_MEMBER_ADDED', label: 'Team Member Added' },
  { value: 'USER_LOGIN', label: 'User Login' },
];

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

export default function ActivitiesPage() {
  const [page, setPage] = useState(0);
  const [typeFilter, setTypeFilter] = useState<ActivityType | 'ALL'>('ALL');
  const [selectedActivityId, setSelectedActivityId] = useState<string | null>(null);

  const { data: pageData, isLoading, refetch, isRefetching } = useQuery({
    queryKey: ['activities', page, typeFilter],
    queryFn: () =>
      activitiesApi.getMyActivities({
        page,
        size: 10,
        activityType: typeFilter === 'ALL' ? undefined : typeFilter,
      }),
  });

  const { data: detailData, isLoading: detailLoading } = useQuery({
    queryKey: ['activities', selectedActivityId],
    queryFn: () => activitiesApi.getActivityById(selectedActivityId!),
    enabled: !!selectedActivityId,
  });

  const handleNextPage = () => {
    if (pageData && page < pageData.totalPages - 1) {
      setPage((p) => p + 1);
    }
  };

  const handlePrevPage = () => {
    if (page > 0) {
      setPage((p) => p - 1);
    }
  };

  const handleFilterChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setTypeFilter(e.target.value as ActivityType | 'ALL');
    setPage(0);
  };

  return (
    <AuthGuard>
      <AppShell>
        <PageHeader
          title="Activity Log"
          subtitle="View an immutable history of repository updates, AI generation tasks, and member operations"
          actions={
            <Button
              size="sm"
              variant="outline"
              onClick={() => refetch()}
              loading={isRefetching}
              icon={<RefreshCw className="h-3.5 w-3.5" />}
            >
              Refresh
            </Button>
          }
        />

        <div className="flex flex-col md:flex-row gap-4 items-center justify-between mb-6">
          <div className="w-full md:w-64">
            <Select value={typeFilter} onChange={handleFilterChange}>
              {ACTIVITY_TYPES.map((t) => (
                <option key={t.value} value={t.value}>
                  {t.label}
                </option>
              ))}
            </Select>
          </div>

          {pageData && pageData.totalPages > 1 && (
            <div className="flex items-center gap-2">
              <span className="text-xs text-muted-foreground mr-2">
                Page {page + 1} of {pageData.totalPages}
              </span>
              <Button size="sm" variant="secondary" onClick={handlePrevPage} disabled={page === 0}>
                <ChevronLeft className="h-4 w-4" /> Prev
              </Button>
              <Button
                size="sm"
                variant="secondary"
                onClick={handleNextPage}
                disabled={page >= pageData.totalPages - 1}
              >
                Next <ChevronRight className="h-4 w-4" />
              </Button>
            </div>
          )}
        </div>

        {isLoading ? (
          <div className="flex justify-center py-20">
            <Spinner size="lg" />
          </div>
        ) : pageData && pageData.content.length > 0 ? (
          <div className="relative border-l border-border/80 ml-4 pl-6 space-y-6">
            {pageData.content.map((item) => {
              const meta = getActivityMeta(item.activityType);
              const Icon = meta.icon;
              return (
                <div key={item.id} className="relative group">
                  {/* Timeline dot */}
                  <div className="absolute -left-[35px] mt-1 w-4 h-4 rounded-full border-2 border-background bg-card flex items-center justify-center text-primary group-hover:border-primary/50 transition-colors">
                    <div className="w-1.5 h-1.5 rounded-full bg-muted-foreground/60 group-hover:bg-primary transition-colors" />
                  </div>

                  <Card className="hover:border-primary/20 hover:bg-card/50 transition-all">
                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                      <div className="flex items-start gap-3">
                        <div className="p-2 bg-secondary rounded-lg text-muted-foreground shrink-0 mt-0.5">
                          <Icon className="h-4 w-4" />
                        </div>
                        <div>
                          <div className="flex flex-wrap items-center gap-2">
                            <span className="font-semibold text-sm text-foreground">{item.title}</span>
                            <Badge variant={meta.color}>{meta.category}</Badge>
                          </div>
                          <p className="text-xs text-muted-foreground mt-1">
                            Triggered by <strong className="text-foreground">{item.actorName}</strong>
                          </p>
                        </div>
                      </div>

                      <div className="flex items-center gap-3 self-end sm:self-center shrink-0">
                        <span className="text-[10px] text-muted-foreground">{formatDate(item.createdAt)}</span>
                        <Button size="sm" variant="ghost" onClick={() => setSelectedActivityId(item.id)}>
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
              icon={<History className="h-10 w-10 text-muted-foreground" />}
              title="No activities recorded"
              description="No audit logs matched your current filters."
            />
          </Card>
        )}

        {/* Detailed View Dialog */}
        <Dialog
          open={!!selectedActivityId}
          onClose={() => setSelectedActivityId(null)}
          title="Activity Details"
        >
          {detailLoading ? (
            <div className="flex justify-center py-10">
              <Spinner />
            </div>
          ) : detailData ? (
            <div className="space-y-5">
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

              <div className="space-y-3">
                <div className="grid grid-cols-3 text-xs border-b border-border/40 pb-2.5">
                  <span className="text-muted-foreground font-medium flex items-center gap-1.5">
                    <User className="h-3.5 w-3.5" /> Actor
                  </span>
                  <span className="col-span-2 text-foreground font-semibold">
                    {detailData.actorName} ({detailData.actorUserId.slice(0, 8)})
                  </span>
                </div>

                <div className="grid grid-cols-3 text-xs border-b border-border/40 pb-2.5">
                  <span className="text-muted-foreground font-medium flex items-center gap-1.5">
                    <Calendar className="h-3.5 w-3.5" /> Date & Time
                  </span>
                  <span className="col-span-2 text-foreground">
                    {new Date(detailData.createdAt).toLocaleString()}
                  </span>
                </div>

                {detailData.projectId && (
                  <div className="grid grid-cols-3 text-xs border-b border-border/40 pb-2.5">
                    <span className="text-muted-foreground font-medium">Project ID</span>
                    <span className="col-span-2 text-foreground font-mono">{detailData.projectId}</span>
                  </div>
                )}

                {detailData.teamId && (
                  <div className="grid grid-cols-3 text-xs border-b border-border/40 pb-2.5">
                    <span className="text-muted-foreground font-medium">Team ID</span>
                    <span className="col-span-2 text-foreground font-mono">{detailData.teamId}</span>
                  </div>
                )}

                {detailData.schemaId && (
                  <div className="grid grid-cols-3 text-xs border-b border-border/40 pb-2.5">
                    <span className="text-muted-foreground font-medium">Schema ID</span>
                    <span className="col-span-2 text-foreground font-mono">{detailData.schemaId}</span>
                  </div>
                )}

                {detailData.metadata && Object.keys(detailData.metadata).length > 0 && (
                  <div className="space-y-1.5 pt-1">
                    <span className="text-xs text-muted-foreground font-medium flex items-center gap-1.5">
                      <Terminal className="h-3.5 w-3.5" /> Event Metadata
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
