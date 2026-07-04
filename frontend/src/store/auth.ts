'use client';

import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import type { UserResponse } from '@/types';
import { setToken, removeToken } from '@/lib/api';

interface AuthState {
  user: UserResponse | null;
  token: string | null;
  isAuthenticated: boolean;
  isHydrated: boolean;
  login: (token: string, user: UserResponse) => void;
  logout: () => void;
  setUser: (user: UserResponse) => void;
  setHydrated: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      isAuthenticated: false,
      isHydrated: false,

      login: (token, user) => {
        setToken(token);
        set({ token, user, isAuthenticated: true });
      },

      logout: () => {
        removeToken();
        set({ token: null, user: null, isAuthenticated: false });
      },

      setUser: (user) => set({ user }),

      setHydrated: () => set({ isHydrated: true }),
    }),
    {
      name: 'sf-auth',
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({ token: state.token, user: state.user }),
      onRehydrateStorage: () => (state) => {
        if (state) {
          state.isAuthenticated = !!state.token;
          state.setHydrated();
          if (state.token) {
            setToken(state.token);
          }
        }
      },
    }
  )
);
