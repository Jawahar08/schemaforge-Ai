'use client';

import Link from 'next/link';
import { ArrowRight, Sparkles } from 'lucide-react';
import { Button } from '@/components/ui/primitives';

export function LandingCTA() {
  return (
    <section className="py-24 px-4 border-t border-border relative overflow-hidden bg-card/20">
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-primary/5 rounded-full blur-3xl pointer-events-none" />
      <div className="max-w-3xl mx-auto text-center relative z-10">
        <span className="inline-flex items-center gap-1.5 text-xs font-semibold text-primary bg-primary/10 px-3 py-1 rounded-full border border-primary/20 mb-6">
          <Sparkles className="h-3 w-3" /> Get Started Today
        </span>
        <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
          Ready to Forge Your Database Schema?
        </h2>
        <p className="text-sm text-muted-foreground mb-8 max-w-xl mx-auto leading-relaxed">
          Skip hours of writing boilerplate DDL scripts and manual ER mapping. Describe your software system and let AI design a perfect, normalized architecture.
        </p>
        <Link href="/register">
          <Button size="lg" icon={<ArrowRight className="h-4 w-4" />}>
            Create Free Account
          </Button>
        </Link>
      </div>
    </section>
  );
}
