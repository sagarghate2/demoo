import { create } from 'zustand'
import { persist, createJSONStorage } from 'zustand/middleware'

interface OnboardingState {
    role: string
    industry: string
    subIndustry: string
    city: string
    address: string
    lat: number | null
    lng: number | null
    name: string
    username: string
    bio: string
    avatarUrl: string | null
    coverUrl: string | null
    linkedin: string
    twitter: string
    github: string
    website: string
    subscription: 'Free' | 'Pro' | 'Founder'
    isVerified: boolean

    setRole: (role: string) => void
    setIndustry: (industry: string, subIndustry?: string) => void
    setLocation: (city: string, lat: number, lng: number, address?: string) => void
    setProfile: (profile: Partial<OnboardingState>) => void
    setSubscription: (plan: 'Free' | 'Pro' | 'Founder') => void
    setAvatar: (url: string | null) => void
}

export const useOnboardingStore = create<OnboardingState>()(
    persist(
        (set) => ({
            role: '',
            industry: '',
            subIndustry: '',
            city: '',
            address: '',
            lat: null,
            lng: null,
            name: '',
            username: '',
            bio: '',
            avatarUrl: null,
            coverUrl: null,
            linkedin: '',
            twitter: '',
            github: '',
            website: '',
            subscription: 'Free',
            isVerified: false,

            setRole: (role) => set({ role }),
            setIndustry: (industry, subIndustry = '') => set({ industry, subIndustry }),
            setLocation: (city, lat, lng, address = '') => set({ city, lat, lng, address }),
            setProfile: (profile) => set((state) => ({ ...state, ...profile })),
            setSubscription: (plan) => set({ subscription: plan, isVerified: plan !== 'Free' }),
            setAvatar: (avatarUrl) => set({ avatarUrl }),
        }),
        {
            name: 'starto-onboarding-storage',
            storage: createJSONStorage(() => localStorage),
        }
    )
)
