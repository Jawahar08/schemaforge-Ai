'use client';

import { Handle, Position } from '@xyflow/react';

export function TableNode({ data }: { data: any }) {
  const columns = data.columns || [];

  return (
    <div className="rounded-xl border border-border bg-card shadow-2xl min-w-[200px] overflow-hidden">
      <div className="bg-gradient-to-r from-violet-900/50 to-indigo-900/50 px-4 py-2 border-b border-border flex items-center justify-between">
        <span className="font-bold text-xs font-mono text-primary-foreground">{data.tableName}</span>
        {data.tableCount && (
          <span className="text-[9px] bg-primary/20 text-primary-foreground px-1 py-0.5 rounded font-mono">
            {data.tableCount}
          </span>
        )}
      </div>

      <div className="p-3 space-y-1.5 bg-black/10">
        {columns.map((c: any) => (
          <div key={c.name} className="flex items-center justify-between text-[10px] font-mono gap-4">
            <div className="flex items-center gap-1">
              {c.primaryKey && <span className="text-amber-400 font-bold" title="Primary Key">🔑</span>}
              {c.foreignKey && <span className="text-cyan-400 font-bold" title="Foreign Key">🔗</span>}
              <span className={c.primaryKey ? 'text-amber-200' : 'text-foreground/80'}>{c.name}</span>
            </div>
            <span className="text-muted-foreground text-[9px]">{c.type}</span>
          </div>
        ))}
      </div>

      {/* Connection handles */}
      <Handle type="target" position={Position.Left} className="!bg-primary" />
      <Handle type="source" position={Position.Right} className="!bg-primary" />
    </div>
  );
}
