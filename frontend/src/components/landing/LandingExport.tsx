'use client';

import { motion } from 'framer-motion';
import { Badge, Card, Button } from '@/components/ui/primitives';
import { Check, Copy } from 'lucide-react';
import { useState } from 'react';

const DIALECT_DIFFERENCES = {
  POSTGRESQL: `CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email VARCHAR(255) NOT NULL UNIQUE,
  created_at TIMESTAMPTZ DEFAULT NOW()
);`,
  MYSQL: `CREATE TABLE users (
  id CHAR(36) PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);`,
  SQLSERVER: `CREATE TABLE users (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  email VARCHAR(255) NOT NULL UNIQUE,
  created_at DATETIMEOFFSET DEFAULT SYSDATETIMEOFFSET()
);`,
  ORACLE: `CREATE TABLE users (
  id VARCHAR2(36) PRIMARY KEY,
  email VARCHAR2(255) NOT NULL UNIQUE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);`
};

export function LandingExport() {
  const [selected, setSelected] = useState<keyof typeof DIALECT_DIFFERENCES>('POSTGRESQL');
  const [copied, setCopied] = useState(false);

  const handleCopy = () => {
    navigator.clipboard.writeText(DIALECT_DIFFERENCES[selected]);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <section id="export" className="py-20 px-4 border-t border-border">
      <div className="max-w-5xl mx-auto">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          <div>
            <span className="text-xs font-medium text-primary uppercase tracking-widest">Multi-Dialect Export</span>
            <h2 className="text-3xl font-bold text-foreground mt-2">
              Generate Pure SQL for Any Database
            </h2>
            <p className="text-sm text-muted-foreground mt-4 leading-relaxed">
              No locked-in proprietary formats. SchemaForge AI automatically translates database schemas, references, data types, and primary/foreign key mappings to clean, native SQL dialect structure.
            </p>
            <div className="flex flex-wrap gap-2 mt-6">
              {(Object.keys(DIALECT_DIFFERENCES) as Array<keyof typeof DIALECT_DIFFERENCES>).map((dialect) => (
                <button
                  key={dialect}
                  onClick={() => setSelected(dialect)}
                  className={`px-3 py-1.5 rounded-lg text-xs font-semibold border transition-all ${
                    selected === dialect
                      ? 'bg-primary/10 border-primary text-primary'
                      : 'border-border text-muted-foreground hover:text-foreground hover:bg-secondary/50'
                  }`}
                >
                  {dialect === 'SQLSERVER' ? 'SQL Server' : dialect.charAt(0) + dialect.slice(1).toLowerCase()}
                </button>
              ))}
            </div>
          </div>

          <div className="rounded-xl border border-border bg-card overflow-hidden">
            <div className="flex items-center justify-between px-4 py-2.5 border-b border-border bg-secondary/30">
              <span className="text-xs text-muted-foreground font-mono">schema.sql</span>
              <Button
                variant="ghost"
                size="sm"
                onClick={handleCopy}
                icon={copied ? <Check className="h-3.5 w-3.5 text-emerald-400" /> : <Copy className="h-3.5 w-3.5" />}
              >
                {copied ? 'Copied' : 'Copy'}
              </Button>
            </div>
            <div className="p-5 font-mono text-xs text-foreground/80 leading-relaxed overflow-x-auto bg-black/20">
              <pre>{DIALECT_DIFFERENCES[selected]}</pre>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
