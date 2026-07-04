'use client';

import { use, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { projectsApi, aiApi } from '@/lib/api-modules';
import { AuthGuard } from '@/components/auth/Guards';
import { AppShell } from '@/components/layout/AppShell';
import { PageHeader, Card, Button, FormField, Textarea, Select } from '@/components/ui/primitives';
import { Sparkles, ArrowLeft } from 'lucide-react';
import { useToast } from '@/components/providers/ToastProvider';
import Link from 'next/link';
import type { AiProvider, NormalizationTarget } from '@/types';

const PROMPT_CHIPS = [
  { label: 'E-Commerce Platform', text: 'An e-commerce platform with users, products, categories, orders, order items, payments, reviews, and shopping cart.' },
  { label: 'SaaS Billing System', text: 'A subscription SaaS billing platform with users, organizations, subscription plans, user subscriptions, invoices, payments, and usage logs.' },
  { label: 'Hospital Registry', text: 'A hospital management system with patients, doctors, departments, appointments, medical records, prescriptions, and billing.' },
  { label: 'Online Learning Platform', text: 'A learning management platform with students, instructors, courses, enrollment records, modules, lessons, quizzes, and course completion certificates.' }
];

export default function SchemaGeneratePage({ params }: { params: Promise<{ projectId: string }> }) {
  const resolvedParams = use(params);
  const projectId = resolvedParams.projectId;
  const router = useRouter();
  const queryClient = useQueryClient();
  const { success, error } = useToast();

  const [description, setDescription] = useState('');
  const [normalizationTarget, setNormalizationTarget] = useState<NormalizationTarget>('THREE_NF');
  const [provider, setProvider] = useState<AiProvider>('MOCK');

  const { data: project, isLoading: projectLoading } = useQuery({
    queryKey: ['projects', projectId],
    queryFn: () => projectsApi.get(projectId),
  });

  const generateMutation = useMutation({
    mutationFn: () => aiApi.generate({
      projectId,
      description,
      normalizationTarget,
      provider,
    }),
    onSuccess: (schema) => {
      queryClient.invalidateQueries({ queryKey: ['projects', projectId, 'schemas'] });
      success('Schema Generated', `Successfully generated "${schema.systemName}"`);
      router.push(`/schemas/${schema.id}`);
    },
    onError: (err: any) => {
      error('Generation Failed', err.message || 'Error occurred during generation.');
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (description.length < 10) {
      error('Validation Error', 'System description must be at least 10 characters.');
      return;
    }
    generateMutation.mutate();
  };

  if (projectLoading) {
    return (
      <AuthGuard>
        <AppShell>
          <div className="flex justify-center py-20"><Spinner /></div>
        </AppShell>
      </AuthGuard>
    );
  }

  return (
    <AuthGuard>
      <AppShell>
        <div className="mb-4">
          <Link href={`/projects/${projectId}`} className="text-xs text-muted-foreground hover:text-foreground flex items-center gap-1">
            <ArrowLeft className="h-3 w-3" /> Back to Project
          </Link>
        </div>

        <PageHeader
          title="AI Schema Generation Workspace"
          subtitle={`Create a database schema for project "${project?.name}"`}
        />

        <div className="grid lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-6">
            <Card>
              <form onSubmit={handleSubmit} className="space-y-6">
                <FormField label="Describe Your Software System / Database Requirements" required>
                  <Textarea
                    rows={8}
                    placeholder="Describe entities, properties, actions, and relationship requirements..."
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    disabled={generateMutation.isPending}
                    required
                  />
                </FormField>

                <div className="space-y-2">
                  <span className="text-xs font-medium text-muted-foreground">Template Examples</span>
                  <div className="flex flex-wrap gap-2">
                    {PROMPT_CHIPS.map((chip) => (
                      <button
                        key={chip.label}
                        type="button"
                        onClick={() => setDescription(chip.text)}
                        className="text-xs px-2.5 py-1 rounded bg-secondary hover:bg-secondary/80 text-foreground transition-colors border border-border"
                      >
                        {chip.label}
                      </button>
                    ))}
                  </div>
                </div>

                <div className="grid md:grid-cols-2 gap-4">
                  <FormField label="Normalization Goal">
                    <Select
                      value={normalizationTarget}
                      onChange={(e) => setNormalizationTarget(e.target.value as NormalizationTarget)}
                      disabled={generateMutation.isPending}
                    >
                      <option value="TWO_NF">2NF — First Normal Form & Full Functional Dependence</option>
                      <option value="THREE_NF">3NF — Transitive Dependency Elimination (Recommended)</option>
                      <option value="BCNF">BCNF — Boyce-Codd Normal Form</option>
                    </Select>
                  </FormField>

                  <FormField label="AI Model Provider">
                    <Select
                      value={provider}
                      onChange={(e) => setProvider(e.target.value as AiProvider)}
                      disabled={generateMutation.isPending}
                    >
                      <option value="MOCK">Development Sandbox (Fast / Free)</option>
                      <option value="GEMINI">Google Gemini 2.0 Flash</option>
                      <option value="OPENAI">OpenAI GPT-4o-Mini</option>
                      <option value="CLAUDE">Anthropic Claude Sonnet</option>
                    </Select>
                  </FormField>
                </div>

                <div className="flex justify-end pt-4 border-t border-border">
                  <Button type="submit" loading={generateMutation.isPending} icon={<Sparkles className="h-4 w-4" />}>
                    Generate Schema
                  </Button>
                </div>
              </form>
            </Card>
          </div>

          <div className="space-y-6">
            <Card>
              <h3 className="text-sm font-semibold text-foreground mb-2 flex items-center gap-1.5">
                <Sparkles className="w-4 h-4 text-amber-400" /> AI Workspace Details
              </h3>
              <ul className="text-xs text-muted-foreground space-y-3 leading-relaxed">
                <li>
                  <strong className="text-foreground">Prompt Tuning:</strong> Use clear nouns and specify relationships (e.g. "one-to-many") for optimal database translation.
                </li>
                <li>
                  <strong className="text-foreground">Credit Allocation:</strong> Schema design using real AI models uses credit tokens. Use Sandbox (Mock) for validation tests.
                </li>
                <li>
                  <strong className="text-foreground">Generated Elements:</strong> The tool automatically parses entities into tables, sets primary keys, defines foreign key linkages, and performs architectural validation checks.
                </li>
              </ul>
            </Card>
          </div>
        </div>
      </AppShell>
    </AuthGuard>
  );
}

// Simple Spinner component since it's locally needed if not fully exported
function Spinner() {
  return (
    <div className="animate-spin rounded-full h-8 w-8 border-2 border-primary border-t-transparent" />
  );
}
