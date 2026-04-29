"use client"
import { useEffect, ReactNode } from 'react'
import { useAuthStore } from '@/store/useAuthStore'
import { usersApi } from '@/lib/apiClient'
import { auth, firebaseConfigured } from '@/lib/firebase'
import { onAuthStateChanged } from 'firebase/auth'

export default function AuthProvider({ children }: { children: ReactNode }) {
    const { token, isAuthenticated, user, clearAuth, setAuth, setLoading, setInitialized } = useAuthStore()

    // 1. Listen for Firebase Auth changes to sync store.
    //    Guard: if Firebase is not configured (missing env vars) we skip the
    //    onAuthStateChanged call entirely — calling it on the {} stub throws
    //    "onAuthStateChanged is not a function" and crashes the whole app.
    useEffect(() => {
        if (!firebaseConfigured) {
            // Firebase not ready — treat as unauthenticated, stop loading spinner
            setLoading(false)
            return
        }

        const unsubscribe = onAuthStateChanged(auth, async (firebaseUser) => {
            if (firebaseUser) {
                // Firebase user present — refresh profile from backend to ensure up-to-date data
                const token = await firebaseUser.getIdToken();
                try {
                    const { data: profile } = await usersApi.getMe(token);
                    if (profile) {
                        setAuth(firebaseUser, token, profile as any);
                    }
                } catch (err) {
                    console.error('Failed to refresh profile on auth change', err);
                }
            } else {
                // Firebase user gone (sign-out or session expired) — clear store
                if (isAuthenticated) clearAuth()
            }
            setInitialized(true)
            setLoading(false)
        })

        return () => unsubscribe()
    }, [isAuthenticated, clearAuth, setAuth, setLoading])

    // 2. Heartbeat every 30 seconds to update presence
    useEffect(() => {
        if (!isAuthenticated) return

        const sendHeartbeat = async () => {
            try {
                await usersApi.heartbeat()
            } catch (error) {
                console.error('Heartbeat failed', error)
            }
        }

        sendHeartbeat()
        const interval = setInterval(sendHeartbeat, 30000)
        return () => clearInterval(interval)
    }, [isAuthenticated])

    return <>{children}</>
}
