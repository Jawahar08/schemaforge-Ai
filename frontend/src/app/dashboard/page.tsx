'use client';

import { useQuery } from '@tanstack/react-query';
import { useAuthStore } from '@/store/auth';
import { projectsApi, notificationsApi, aiApi } from '@/lib/api-modules';
import { AuthGuard } from '@/components/auth/Guards';
import { AppShell } from '@/components/layout/AppShell';
import { PageHeader, Card, Button, Badge, EmptyState, Spinner } from '@/components/ui/primitives';
import { FolderOpen, Sparkles, Bell, Plus, PlusCircle, ArrowRight, FileText } from 'lucide-react';
import Link from 'next/link';
import { dialectLabel, formatDate } from '@/lib/utils';

export default function DashboardPage() {
  const { user } = useAuthStore();

  const { data: projects, isLoading: projectsLoading } = useQuery({
    queryKey: ['projects'],
    queryFn: projectsApi.list,
  });

  const { data: unreadNotifications } = useQuery({
    queryKey: ['notifications', 'unread'],
    queryFn: notificationsApi.listUnread,
  });

  const { data: aiRequests } = useQuery({
    queryKey: ['aiRequests'],
    queryFn: aiApi.listRequests,
  });

  return (
    <AuthGuard>
      <AppShell>
        <PageHeader
          title={`Welcome back, ${user?.fullName || 'Developer'}`}
          subtitle="Manage your projects and design production relational database schemas"
          actions={
            <Link href="/projects">
              <Button size="sm" icon={<Plus className="h-4 w-4" />}>
                New Project
              </Button>
            </Link>
          }
        />

        <div className="grid md:grid-cols-3 gap-6 mb-8">
          <Card>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-muted-foreground font-semibold">ACTIVE PROJECTS</p>
                <p className="text-2xl font-bold mt-1">{projectsLoading ? '...' : projects?.length || 0}</p>
              </div>
              <div className="p-2 bg-violet-500/10 rounded-lg text-violet-400">
                <FolderOpen className="h-5 w-5" />
              </div>
            </div>
          </Card>

          <Card>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-muted-foreground font-semibold">AI GENERATIONS</p>
                <p className="text-2xl font-bold mt-1">{aiRequests ? aiRequests.length : 0}</p>
              </div>
              <div className="p-2 bg-indigo-500/10 rounded-lg text-indigo-400">
                <Sparkles className="h-5 w-5" />
              </div>
            </div>
          </Card>

          <Card>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-muted-foreground font-semibold">UNREAD ALERTS</p>
                <p className="text-2xl font-bold mt-1">{unreadNotifications?.length ?? 0}</p>
              </div>
              <div className="p-2 bg-amber-500/10 rounded-lg text-amber-400">
                <Bell className="h-5 w-5" />
              </div>
            </div>
          </Card>
        </div>

        <div className="grid lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            <h2 className="text-sm font-semibold text-foreground tracking-tight uppercase">Recent Projects</h2>
            {projectsLoading ? (
              <div className="flex justify-center py-12"><Spinner /></div>
            ) : projects && projects.length > 0 ? (
              <div className="grid md:grid-cols-2 gap-4">
                {projects.slice(0, 4).map((project) => (
                  <Link key={project.id} href={`/projects/${project.id}`}>
                    <Card hover className="h-full flex flex-col justify-between">
                      <div>
                        <div className="flex items-start justify-between gap-2">
                          <h3 className="font-semibold text-sm text-foreground truncate">{project.name}</h3>
                          <Badge variant="default">{dialectLabel(project.dialect)}</Badge>
                        </div>
                        <p className="text-xs text-muted-foreground line-clamp-2 mt-2">
                          {project.description || 'No description provided.'}
                        </p>
                      </div>
                      <div className="flex items-center justify-between mt-4 text-[10px] text-muted-foreground border-t border-border/50 pt-3">
                        <span>Created {formatDate(project.createdAt)}</span>
                        <span className="text-primary flex items-center gap-1 hover:underline">
                          View details <ArrowRight className="h-3 w-3" />
                        </span>
                      </div>
                    </Card>
                  </Link>
                ))}
              </div>
            ) : (
              <Card>
                <EmptyState
                  icon={<FolderOpen className="h-8 w-8 text-muted-foreground" />}
                  title="No projects found"
                  description="Create a project to start generating production-ready database schemas."
                  action={
                    <Link href="/projects">
                      <Button variant="outline" size="sm" icon={<PlusCircle className="h-3.5 w-3.5" />}>
                        Create Project
                      </Button>
                    </Link>
                  }
                />
              </Card>
            )}
          </div>

          <div className="space-y-6">
            <h2 className="text-sm font-semibold text-foreground tracking-tight uppercase">AI Usage</h2>
            <Card>
              <div className="space-y-4">
                <div className="flex items-center justify-between pb-3 border-b border-border">
                  <span className="text-xs text-muted-foreground">Current Balance</span>
                  <span className="text-xs font-bold text-foreground flex items-center gap-1">
                    <Sparkles className="w-3.5 h-3.5 text-amber-400" /> {user?.aiCredits ?? 0} Credits
                  </span>
                </div>
                <div>
                  <p className="text-[11px] text-muted-foreground leading-relaxed">
                    AI generation processes cost credit units. Each natural language generation audit log is tracked for usage transparency.
                  </p>
                </div>
              </div>
            </Card>
          </div>
        </div>
      </AppShell>
    </AuthGuard>
  );
}
