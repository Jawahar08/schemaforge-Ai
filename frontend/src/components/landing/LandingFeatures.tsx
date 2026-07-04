'use client';

import { motion } from 'framer-motion';
import { Sparkles, GitBranch, DownloadCloud, MessageSquare, Users, Shield, Zap, Database } from 'lucide-react';

const FEATURES = [
  {
    icon: Sparkles,
    title: 'AI Schema Generation',
    description: 'Describe your system in plain English. Claude, GPT-4o, or Gemini generate normalized schemas with tables, relationships, and analysis.',
    color: 'text-violet-400',
    bg: 'bg-violet-500/10',
  },
  {
    icon: Database,
    title: 'Interactive ER Diagrams',
    description: 'Explore your schema visually with interactive entity-relationship diagrams. Pan, zoom, inspect relationships.',
    color: 'text-cyan-400',
    bg: 'bg-cyan-500/10',
  },
  {
    icon: GitBranch,
    title: 'Schema Versioning',
    description: 'Every schema change creates a version snapshot. Browse history, compare versions, and restore any point in time.',
    color: 'text-indigo-400',
    bg: 'bg-indigo-500/10',
  },
  {
    icon: DownloadCloud,
    title: 'Multi-Dialect SQL Export',
    description: 'Export production-ready DDL scripts for PostgreSQL, MySQL, SQL Server, and Oracle with a single click.',
    color: 'text-emerald-400',
    bg: 'bg-emerald-500/10',
  },
  {
    icon: MessageSquare,
    title: 'Schema Comments',
    description: 'Collaborate directly on schemas with threaded comments. Reference specific tables and columns.',
    color: 'text-amber-400',
    bg: 'bg-amber-500/10',
  },
  {
    icon: Users,
    title: 'Team Collaboration',
    description: 'Invite team members with role-based access control. Owner, admin, member, and viewer roles.',
    color: 'text-sky-400',
    bg: 'bg-sky-500/10',
  },
  {
    icon: Shield,
    title: 'Normalization Analysis',
    description: 'Specify 2NF, 3NF, or BCNF targets. AI generates normalization notes and analysis recommendations.',
    color: 'text-rose-400',
    bg: 'bg-rose-500/10',
  },
  {
    icon: Zap,
    title: 'AI Credit System',
    description: 'Track AI generation usage with a credits system. Each generation is audited for full transparency.',
    color: 'text-orange-400',
    bg: 'bg-orange-500/10',
  },
];

export function LandingFeatures() {
  return (
    <section id="features" className="py-20 px-4 border-t border-border">
      <div className="max-w-5xl mx-auto">
        <div className="text-center mb-12">
          <span className="text-xs font-medium text-primary uppercase tracking-widest">Platform Features</span>
          <h2 className="text-3xl font-bold text-foreground mt-2">
            Everything a Database Engineer Needs
          </h2>
          <p className="text-sm text-muted-foreground mt-2 max-w-xl mx-auto">
            A complete collaborative database engineering platform — powered by AI, built for production.
          </p>
        </div>

        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {FEATURES.map((f, i) => (
            <motion.div
              key={f.title}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ delay: i * 0.05 }}
              className="rounded-xl border border-border bg-card p-5 hover:border-primary/20 transition-all duration-200 group"
            >
              <div className={`w-9 h-9 rounded-lg ${f.bg} flex items-center justify-center mb-4`}>
                <f.icon className={`h-4.5 w-4.5 ${f.color} h-[18px] w-[18px]`} />
              </div>
              <h3 className="text-sm font-semibold text-foreground mb-1.5">{f.title}</h3>
              <p className="text-xs text-muted-foreground leading-relaxed">{f.description}</p>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}
