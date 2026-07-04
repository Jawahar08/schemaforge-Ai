import type { Metadata } from 'next';
import './globals.css';
import { ReactQueryProvider } from '@/components/providers/QueryProvider';
import { ToastProvider } from '@/components/providers/ToastProvider';

export const metadata: Metadata = {
  title: {
    default: 'SchemaForge AI — Transform Plain English Into Production-Ready Database Schemas',
    template: '%s | SchemaForge AI',
  },
  description:
    'AI-powered database design platform. Describe your system in plain English and get normalized schemas, SQL scripts, ER diagrams, and documentation instantly.',
  keywords: ['database design', 'AI', 'schema generator', 'SQL', 'ER diagram', 'PostgreSQL'],
  openGraph: {
    title: 'SchemaForge AI',
    description: 'Transform Plain English Into Production-Ready Database Schemas',
    type: 'website',
  },
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" suppressHydrationWarning className="dark">
      <body className="antialiased">
        <ReactQueryProvider>
          <ToastProvider>
            {children}
          </ToastProvider>
        </ReactQueryProvider>
      </body>
    </html>
  );
}
