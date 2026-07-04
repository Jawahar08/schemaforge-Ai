'use client';

import { motion } from 'framer-motion';

const DEMO_INPUT = `I need an e-commerce platform with users, products, 
categories, orders, order items, payments, reviews, 
and a shopping cart system.`;

const DEMO_OUTPUT = [
  {
    table: 'users',
    pk: 'id UUID',
    columns: ['email VARCHAR(255)', 'full_name VARCHAR(255)', 'created_at TIMESTAMPTZ'],
  },
  {
    table: 'products',
    pk: 'id UUID',
    columns: ['name VARCHAR(255)', 'price DECIMAL(10,2)', 'category_id UUID FK', 'stock_qty INT'],
  },
  {
    table: 'orders',
    pk: 'id UUID',
    columns: ['user_id UUID FK', 'status VARCHAR(50)', 'total DECIMAL(10,2)', 'created_at TIMESTAMPTZ'],
  },
  {
    table: 'order_items',
    pk: 'id UUID',
    columns: ['order_id UUID FK', 'product_id UUID FK', 'quantity INT', 'unit_price DECIMAL(10,2)'],
  },
];

export function LandingDemo() {
  return (
    <section id="demo" className="py-20 px-4">
      <div className="max-w-5xl mx-auto">
        <div className="text-center mb-12">
          <span className="text-xs font-medium text-primary uppercase tracking-widest">Live Demo</span>
          <h2 className="text-3xl font-bold text-foreground mt-2">
            From English to Schema in Seconds
          </h2>
          <p className="text-sm text-muted-foreground mt-2 max-w-xl mx-auto">
            Watch how a natural language description becomes a production-ready database schema.
          </p>
        </div>

        <div className="grid lg:grid-cols-2 gap-4">
          {/* Input panel */}
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true }}
            className="rounded-xl border border-border bg-card overflow-hidden"
          >
            <div className="flex items-center gap-2 px-4 py-2.5 border-b border-border bg-secondary/30">
              <div className="flex gap-1.5">
                <span className="w-2.5 h-2.5 rounded-full bg-red-500/60" />
                <span className="w-2.5 h-2.5 rounded-full bg-amber-500/60" />
                <span className="w-2.5 h-2.5 rounded-full bg-emerald-500/60" />
              </div>
              <span className="text-xs text-muted-foreground ml-1">System description</span>
            </div>
            <div className="p-5">
              <pre className="text-sm text-foreground/80 font-mono whitespace-pre-wrap leading-relaxed">{DEMO_INPUT}</pre>
              <div className="mt-4 flex items-center gap-2">
                <span className="text-xs text-muted-foreground">Provider:</span>
                <span className="text-xs font-medium text-primary bg-primary/10 px-2 py-0.5 rounded">Claude</span>
                <span className="text-xs text-muted-foreground ml-2">Target:</span>
                <span className="text-xs font-medium text-cyan-400 bg-cyan-500/10 px-2 py-0.5 rounded">3NF</span>
              </div>
            </div>
          </motion.div>

          {/* Output panel */}
          <motion.div
            initial={{ opacity: 0, x: 20 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true }}
            transition={{ delay: 0.1 }}
            className="rounded-xl border border-border bg-card overflow-hidden"
          >
            <div className="flex items-center gap-2 px-4 py-2.5 border-b border-border bg-secondary/30">
              <div className="flex gap-1.5">
                <span className="w-2.5 h-2.5 rounded-full bg-red-500/60" />
                <span className="w-2.5 h-2.5 rounded-full bg-amber-500/60" />
                <span className="w-2.5 h-2.5 rounded-full bg-emerald-500/60" />
              </div>
              <span className="text-xs text-muted-foreground ml-1">Generated schema — 8 tables</span>
              <span className="ml-auto text-xs text-emerald-400">✓ 3NF normalized</span>
            </div>
            <div className="p-4 space-y-2.5 max-h-80 overflow-y-auto">
              {DEMO_OUTPUT.map((t) => (
                <div
                  key={t.table}
                  className="rounded-lg border border-border bg-secondary/30 px-3 py-2.5"
                >
                  <div className="flex items-center gap-2 mb-1.5">
                    <span className="font-mono text-xs font-bold text-primary">{t.table}</span>
                    <span className="text-[10px] text-muted-foreground ml-auto font-mono">{t.pk} PK</span>
                  </div>
                  <div className="space-y-0.5">
                    {t.columns.map((col) => (
                      <p key={col} className="font-mono text-[10px] text-muted-foreground leading-relaxed">
                        {col}
                      </p>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </motion.div>
        </div>
      </div>
    </section>
  );
}
