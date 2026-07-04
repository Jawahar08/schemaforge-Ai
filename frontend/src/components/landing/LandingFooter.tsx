'use client';

import { Cpu } from 'lucide-react';
import Link from 'next/link';

export function LandingFooter() {
  return (
    <footer className="border-t border-border bg-card/30 py-12 px-4">
      <div className="max-w-5xl mx-auto flex flex-col md:flex-row items-center justify-between gap-6">
        <div className="flex items-center gap-2">
          <div className="w-6 h-6 rounded bg-gradient-to-br from-violet-600 to-indigo-600 flex items-center justify-center">
            <Cpu className="w-3.5 h-3.5 text-white" />
          </div>
          <span className="text-xs font-semibold text-foreground">SchemaForge AI</span>
        </div>
        <p className="text-[11px] text-muted-foreground">
          © {new Date().getFullYear()} SchemaForge AI. Built for modern database architectures.
        </p>
        <div className="flex items-center gap-4 text-xs text-muted-foreground">
          <Link href="#" className="hover:text-foreground transition-colors">Privacy</Link>
          <Link href="#" className="hover:text-foreground transition-colors">Terms</Link>
          <Link href="#" className="hover:text-foreground transition-colors">GitHub</Link>
        </div>
      </div>
    </footer>
  );
}
