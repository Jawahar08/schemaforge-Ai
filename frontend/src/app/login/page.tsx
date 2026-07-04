'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/store/auth';
import { authApi } from '@/lib/api-modules';
import { useToast } from '@/components/providers/ToastProvider';
import { PublicGuard } from '@/components/auth/Guards';
import { Button, Input, FormField, Card } from '@/components/ui/primitives';
import { Cpu } from 'lucide-react';
import Link from 'next/link';

export default function LoginPage() {
  const router = useRouter();
  const { login } = useAuthStore();
  const { success, error } = useToast();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password) {
      error('Authentication Error', 'Please fill in all fields.');
      return;
    }

    setLoading(true);
    try {
      const data = await authApi.login({ email, password });
      login(data.accessToken, data.user);
      success('Welcome Back!', `Logged in successfully as ${data.user.fullName}`);
      router.push('/dashboard');
    } catch (err: any) {
      error('Login Failed', err.message || 'Check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <PublicGuard>
      <main className="min-h-screen flex items-center justify-center p-4 relative overflow-hidden bg-background">
        <div className="absolute inset-0 grid-bg opacity-30 pointer-events-none" />
        <div className="absolute top-1/4 left-1/2 -translate-x-1/2 w-[500px] h-[500px] bg-violet-600/5 rounded-full blur-3xl pointer-events-none" />

        <div className="relative z-10 w-full max-w-md">
          <div className="flex flex-col items-center mb-8">
            <Link href="/" className="flex items-center gap-2 mb-4">
              <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-violet-600 to-indigo-600 flex items-center justify-center">
                <Cpu className="w-5 h-5 text-white" />
              </div>
              <span className="font-bold text-lg tracking-tight">SchemaForge AI</span>
            </Link>
            <h1 className="text-xl font-bold text-foreground">Sign In to Your Account</h1>
            <p className="text-xs text-muted-foreground mt-1">Transform plain English into production database schemas</p>
          </div>

          <Card>
            <form onSubmit={handleSubmit} className="space-y-4">
              <FormField label="Email Address" required>
                <Input
                  type="email"
                  placeholder="name@company.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  disabled={loading}
                />
              </FormField>

              <FormField label="Password" required>
                <Input
                  type="password"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  disabled={loading}
                />
              </FormField>

              <Button type="submit" className="w-full justify-center mt-2" loading={loading}>
                Sign In
              </Button>
            </form>

            <div className="mt-6 text-center text-xs text-muted-foreground">
              Don't have an account?{' '}
              <Link href="/register" className="text-primary hover:underline font-medium">
                Create an account
              </Link>
            </div>
          </Card>
        </div>
      </main>
    </PublicGuard>
  );
}
