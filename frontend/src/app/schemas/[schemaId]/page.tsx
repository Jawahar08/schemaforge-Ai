'use client';

import { use, useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { schemasApi, commentsApi, exportsApi } from '@/lib/api-modules';
import { AuthGuard } from '@/components/auth/Guards';
import { AppShell } from '@/components/layout/AppShell';
import { PageHeader, Card, Button, Badge, Spinner, FormField, Textarea, Select } from '@/components/ui/primitives';
import { SchemaFlow } from '@/components/schema/SchemaFlow';
import { useToast } from '@/components/providers/ToastProvider';
import {
  MessageSquare,
  History,
  Download,
  Trash2,
  Undo,
  Code,
  FileCode2,
  TableProperties
} from 'lucide-react';
import Link from 'next/link';
import { dialectLabel, formatDate, normalizationLabel } from '@/lib/utils';
import type { ExportDialect } from '@/types';

export default function SchemaWorkspacePage({ params }: { params: Promise<{ schemaId: string }> }) {
  const resolvedParams = use(params);
  const schemaId = resolvedParams.schemaId;
  const queryClient = useQueryClient();
  const { success, error } = useToast();

  const [activeTab, setActiveTab] = useState<'diagram' | 'tables' | 'sql' | 'versions' | 'comments'>('diagram');
  const [commentContent, setCommentContent] = useState('');
  const [exportDialect, setExportDialect] = useState<ExportDialect>('POSTGRESQL');
  const [sqlContent, setSqlContent] = useState('');

  const { data: schema, isLoading: schemaLoading } = useQuery({
    queryKey: ['schemas', schemaId],
    queryFn: () => schemasApi.get(schemaId),
  });

  const { data: comments, isLoading: commentsLoading } = useQuery({
    queryKey: ['schemas', schemaId, 'comments'],
    queryFn: () => commentsApi.list(schemaId),
  });

  const { data: versions } = useQuery({
    queryKey: ['schemas', schemaId, 'versions'],
    queryFn: () => schemasApi.getVersions(schemaId),
  });

  const addCommentMutation = useMutation({
    mutationFn: () => commentsApi.create(schemaId, { content: commentContent }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['schemas', schemaId, 'comments'] });
      setCommentContent('');
      success('Comment Posted', 'Comment was added successfully.');
    },
    onError: (err: any) => {
      error('Failed to post comment', err.message);
    },
  });

  const createExportMutation = useMutation({
    mutationFn: () => exportsApi.create(schemaId, { dialect: exportDialect }),
    onSuccess: async (exp) => {
      success('SQL Export Generated', 'Download starting now...');
      try {
        const sql = await exportsApi.download(exp.exportId);
        setSqlContent(sql);
        setActiveTab('sql');
      } catch (err: any) {
        error('Failed to load SQL', err.message);
      }
    },
    onError: (err: any) => {
      error('Failed to generate export', err.message);
    },
  });

  const restoreMutation = useMutation({
    mutationFn: (verNum: number) => schemasApi.restoreVersion(schemaId, verNum),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['schemas', schemaId] });
      queryClient.invalidateQueries({ queryKey: ['schemas', schemaId, 'versions'] });
      success('Version Restored', 'The schema has been rolled back successfully.');
    },
    onError: (err: any) => {
      error('Restore Failed', err.message);
    },
  });

  if (schemaLoading) {
    return (
      <AuthGuard>
        <AppShell>
          <div className="flex justify-center py-20"><Spinner /></div>
        </AppShell>
      </AuthGuard>
    );
  }

  if (!schema) {
    return (
      <AuthGuard>
        <AppShell>
          <div className="text-center py-20">Schema Workspace Not Found</div>
        </AppShell>
      </AuthGuard>
    );
  }

  // Convert raw DB JSON list safely to components array mapping
  const tables = schema.tables as any[] || [];
  const relationships = schema.relationships as any[] || [];

  return (
    <AuthGuard>
      <AppShell>
        <PageHeader
          title={schema.systemName}
          subtitle={`Current Version: v${schema.currentVersion} | Target: ${normalizationLabel(schema.normalizationTarget)}`}
          actions={
            <div className="flex items-center gap-2">
              <Select
                value={exportDialect}
                onChange={(e: React.ChangeEvent<HTMLSelectElement>) => setExportDialect(e.target.value as ExportDialect)}
                className="!py-1.5"
              >
                <option value="POSTGRESQL">PostgreSQL</option>
                <option value="MYSQL">MySQL</option>
                <option value="SQLSERVER">SQL Server</option>
                <option value="ORACLE">Oracle</option>
              </Select>
              <Button
                size="sm"
                icon={<Download className="h-4 w-4" />}
                onClick={() => createExportMutation.mutate()}
                loading={createExportMutation.isPending}
              >
                Export SQL
              </Button>
            </div>
          }
        />

        <div className="flex border-b border-border mb-6 gap-4">
          {[
            { id: 'diagram', label: 'Interactive Diagram', icon: TableProperties },
            { id: 'tables', label: 'Table Explorer', icon: Code },
            { id: 'sql', label: 'Generated SQL', icon: FileCode2 },
            { id: 'versions', label: 'Versions', icon: History },
            { id: 'comments', label: 'Comments', icon: MessageSquare }
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={`pb-3 text-xs font-semibold flex items-center gap-1.5 border-b-2 transition-colors ${
                activeTab === tab.id
                  ? 'border-primary text-primary'
                  : 'border-transparent text-muted-foreground hover:text-foreground'
              }`}
            >
              <tab.icon className="h-3.5 w-3.5" />
              {tab.label}
            </button>
          ))}
        </div>

        <div>
          {activeTab === 'diagram' && (
            <SchemaFlow tables={tables} relationships={relationships} />
          )}

          {activeTab === 'tables' && (
            <div className="grid md:grid-cols-2 gap-6">
              {tables.map((t: any) => (
                <Card key={t.name || t.tableName}>
                  <h3 className="font-mono text-sm font-bold text-primary mb-3">{t.name || t.tableName}</h3>
                  <div className="space-y-2">
                    {(t.columns || []).map((col: any) => (
                      <div key={col.name} className="flex justify-between items-center text-xs font-mono border-b border-border/40 pb-1.5">
                        <span className="flex items-center gap-1">
                          {col.primaryKey && <span className="text-amber-400">🔑</span>}
                          {col.foreignKey && <span className="text-cyan-400">🔗</span>}
                          {col.name}
                        </span>
                        <span className="text-muted-foreground">{col.type}</span>
                      </div>
                    ))}
                  </div>
                </Card>
              ))}
            </div>
          )}

          {activeTab === 'sql' && (
            <Card className="font-mono text-xs text-foreground/80 leading-relaxed overflow-x-auto bg-black/30 p-5">
              {sqlContent ? (
                <pre>{sqlContent}</pre>
              ) : (
                <div className="text-center py-12 text-muted-foreground">Select a dialect above and click Export SQL to generate.</div>
              )}
            </Card>
          )}

          {activeTab === 'versions' && (
            <div className="space-y-4 max-w-xl">
              {versions && versions.length > 0 ? (
                versions.map((v) => (
                  <Card key={v.id} className="flex items-center justify-between p-4">
                    <div>
                      <h4 className="font-semibold text-sm">Version {v.versionNumber}</h4>
                      <p className="text-xs text-muted-foreground mt-1">Generated {formatDate(v.createdAt)}</p>
                    </div>
                    {v.versionNumber !== schema.currentVersion && (
                      <Button
                        size="sm"
                        variant="secondary"
                        icon={<Undo className="h-3.5 w-3.5" />}
                        onClick={() => {
                          if (confirm(`Rollback schema back to Version ${v.versionNumber}?`)) {
                            restoreMutation.mutate(v.versionNumber);
                          }
                        }}
                        loading={restoreMutation.isPending}
                      >
                        Restore
                      </Button>
                    )}
                  </Card>
                ))
              ) : (
                <div className="text-muted-foreground text-xs">No version snapshots found.</div>
              )}
            </div>
          )}

          {activeTab === 'comments' && (
            <div className="space-y-6 max-w-2xl">
              <Card>
                <form
                  onSubmit={(e) => {
                    e.preventDefault();
                    if (!commentContent.trim()) return;
                    addCommentMutation.mutate();
                  }}
                  className="space-y-4"
                >
                  <FormField label="Add to Collaboration Thread">
                    <Textarea
                      placeholder="Ask questions or review normalization patterns..."
                      value={commentContent}
                      onChange={(e) => setCommentContent(e.target.value)}
                      rows={3}
                      required
                    />
                  </FormField>
                  <div className="flex justify-end">
                    <Button type="submit" size="sm" loading={addCommentMutation.isPending}>
                      Post Comment
                    </Button>
                  </div>
                </form>
              </Card>

              <div className="space-y-4">
                {commentsLoading ? (
                  <Spinner />
                ) : comments && comments.length > 0 ? (
                  comments.map((c) => (
                    <Card key={c.id} className="p-4">
                      <div className="flex items-center justify-between">
                        <span className="text-xs font-semibold text-foreground">User {c.userId.slice(0, 8)}</span>
                        <span className="text-[10px] text-muted-foreground">{formatDate(c.createdAt)}</span>
                      </div>
                      <p className="text-xs text-muted-foreground mt-2 leading-relaxed whitespace-pre-wrap">{c.content}</p>
                    </Card>
                  ))
                ) : (
                  <div className="text-muted-foreground text-xs text-center py-6">No discussions yet. Start the thread above.</div>
                )}
              </div>
            </div>
          )}
        </div>
      </AppShell>
    </AuthGuard>
  );
}
