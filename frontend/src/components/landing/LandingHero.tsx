'use client';

import Link from 'next/link';
import { motion } from 'framer-motion';
import { ArrowRight, Sparkles, Database, GitBranch, Share2 } from 'lucide-react';
import { Button } from '@/components/ui/primitives';

const FLOW_STEPS = [
  { icon: '📝', label: 'Plain English', color: 'from-violet-600/20 to-violet-600/5', border: 'border-violet-500/20' },
  { icon: '🤖', label: 'AI Generation', color: 'from-indigo-600/20 to-indigo-600/5', border: 'border-indigo-500/20' },
  { icon: '🗄️', label: 'Normalized Schema', color: 'from-cyan-600/20 to-cyan-600/5', border: 'border-cyan-500/20' },
  { icon: '🔗', label: 'ER Diagram', color: 'from-emerald-600/20 to-emerald-600/5', border: 'border-emerald-500/20' },
  { icon: '⚡', label: 'Production SQL', color: 'from-amber-600/20 to-amber-600/5', border: 'border-amber-500/20' },
];

export function LandingHero() {
  return (
    <section className="relative min-h-screen flex flex-col items-center justify-center pt-14 pb-20 px-4 overflow-hidden">
      {/* Grid background */}
      <div className="absolute inset-0 grid-bg opacity-40" />
      {/* Gradient blobs */}
      <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-violet-600/8 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-indigo-600/8 rounded-full blur-3xl pointer-events-none" />

      <div className="relative z-10 max-w-5xl mx-auto text-center">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <span className="inline-flex items-center gap-2 text-xs font-medium text-primary bg-primary/10 border border-primary/20 rounded-full px-3 py-1 mb-6">
            <Sparkles className="h-3 w-3" />
            AI-Powered Database Engineering
          </span>
        </motion.div>

        <motion.h1
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.1 }}
          className="text-4xl md:text-6xl lg:text-7xl font-bold tracking-tight text-foreground mb-6"
        >
          Transform Plain English Into{' '}
          <span className="bg-gradient-to-r from-violet-400 via-indigo-400 to-cyan-400 bg-clip-text text-transparent">
            Production-Ready
          </span>{' '}
          Database Schemas.
        </motion.h1>

        <motion.p
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.2 }}
          className="text-lg text-muted-foreground max-w-2xl mx-auto mb-8"
        >
          Describe your software system in plain English. SchemaForge AI generates normalized relational
          schemas, interactive ER diagrams, and production SQL — for any database dialect.
        </motion.p>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.3 }}
          className="flex items-center justify-center gap-3 mb-16"
        >
          <Link href="/register">
            <Button size="lg" icon={<ArrowRight className="h-4 w-4" />}>
              Start for free
            </Button>
          </Link>
          <Link href="/login">
            <Button variant="outline" size="lg">
              Sign in
            </Button>
          </Link>
        </motion.div>

        {/* Flow visualization */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.4 }}
          className="flex flex-wrap items-center justify-center gap-2 md:gap-0"
        >
          {FLOW_STEPS.map((step, i) => (
            <div key={i} className="flex items-center gap-2">
              <div
                className={`flex flex-col items-center gap-1.5 px-4 py-3 rounded-xl border ${step.border} bg-gradient-to-b ${step.color} min-w-[100px]`}
              >
                <span className="text-2xl">{step.icon}</span>
                <span className="text-xs font-medium text-muted-foreground">{step.label}</span>
              </div>
              {i < FLOW_STEPS.length - 1 && (
                <ArrowRight className="h-4 w-4 text-muted-foreground/30 hidden md:block mx-1" />
              )}
            </div>
          ))}
        </motion.div>

        {/* Stats */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5, delay: 0.6 }}
          className="flex items-center justify-center gap-8 mt-12 text-center"
        >
          {[
            { value: 'Multi-Dialect', label: 'SQL Export' },
            { value: '3NF / BCNF', label: 'Normalization' },
            { value: 'Real-time', label: 'Collaboration' },
          ].map((s) => (
            <div key={s.value}>
              <p className="text-lg font-semibold text-foreground">{s.value}</p>
              <p className="text-xs text-muted-foreground">{s.label}</p>
            </div>
          ))}
        </motion.div>
      </div>
    </section>
  );
}
