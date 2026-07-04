'use client';

import { useMemo } from 'react';
import { ReactFlow, Controls, Background, MiniMap } from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { TableNode } from './TableNode';

const NODE_TYPES = {
  tableNode: TableNode,
};

interface SchemaFlowProps {
  tables: any[];
  relationships: any[];
}

export function SchemaFlow({ tables, relationships }: SchemaFlowProps) {
  const nodes = useMemo(() => {
    return tables.map((t: any, index: number) => {
      // Calculate a basic grid position for visual layout
      const row = Math.floor(index / 3);
      const col = index % 3;
      return {
        id: t.name || t.tableName,
        type: 'tableNode',
        data: {
          tableName: t.name || t.tableName,
          columns: t.columns || [],
        },
        position: { x: col * 260 + 50, y: row * 220 + 50 },
      };
    });
  }, [tables]);

  const edges = useMemo(() => {
    return relationships.map((r: any, idx: number) => {
      const source = r.fromTable || r.sourceTable;
      const target = r.toTable || r.targetTable;
      return {
        id: `e-${source}-${target}-${idx}`,
        source,
        target,
        animated: true,
        style: { stroke: 'hsl(var(--primary))', strokeWidth: 1.5 },
      };
    });
  }, [relationships]);

  return (
    <div className="h-[500px] border border-border rounded-xl bg-card/20 overflow-hidden relative">
      <ReactFlow
        nodes={nodes}
        edges={edges}
        nodeTypes={NODE_TYPES}
        fitView
        colorMode="dark"
      >
        <Background gap={12} size={1} className="opacity-20" />
        <Controls className="!bg-card !border-border !text-foreground" />
        <MiniMap
          style={{ height: 100, width: 150 }}
          zoomable
          pannable
          className="!bg-card !border-border"
        />
      </ReactFlow>
    </div>
  );
}
