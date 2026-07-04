import Link from 'next/link';
import type { Metadata } from 'next';
import { LandingNav } from '@/components/landing/LandingNav';
import { LandingHero } from '@/components/landing/LandingHero';
import { LandingFeatures } from '@/components/landing/LandingFeatures';
import { LandingDemo } from '@/components/landing/LandingDemo';
import { LandingExport } from '@/components/landing/LandingExport';
import { LandingCTA } from '@/components/landing/LandingCTA';
import { LandingFooter } from '@/components/landing/LandingFooter';

export const metadata: Metadata = {
  title: 'SchemaForge AI — Transform Plain English Into Production-Ready Database Schemas',
};

export default function HomePage() {
  return (
    <div className="min-h-screen bg-background text-foreground overflow-x-hidden">
      <LandingNav />
      <LandingHero />
      <LandingDemo />
      <LandingFeatures />
      <LandingExport />
      <LandingCTA />
      <LandingFooter />
    </div>
  );
}
