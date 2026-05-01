"use client"

import { Home, Zap, BarChart3, Users, MapPin, Settings, LogIn, Bell, Info } from 'lucide-react'
import Link from 'next/link'
import { usePathname } from 'next/navigation'
import Image from 'next/image'
import { useState, useEffect } from 'react'
import { useAuthStore } from '@/store/useAuthStore'
import { useSignalStore } from '@/store/useSignalStore'
import { useNetworkStore } from '@/store/useNetworkStore'
import { signalsApi, notificationsApi } from '@/lib/apiClient'
import VerifiedAvatar from './VerifiedAvatar'

const navItems = [
    { icon: Home, label: 'Home Feed', href: '/feed' },
    { icon: Zap, label: 'My Signals', href: '/feed/my' },
    { icon: Bell, label: 'Notifications', href: '/notifications' },
    { icon: BarChart3, label: 'Starto AI', href: '/explore' },
    { icon: Users, label: 'My Network', href: '/network' },
    { icon: MapPin, label: 'Nearby', href: '/nearby' },
    { icon: Info, label: 'About Us', href: '/about' },
    { icon: Settings, label: 'Settings', href: '/profile' },
]

export default function Sidebar() {
    const pathname = usePathname()
    const { isAuthenticated, user } = useAuthStore()
    const { signals } = useSignalStore()
    const { connections, pendingRequests, offers } = useNetworkStore()
    const [totalSignalCount, setTotalSignalCount] = useState(0)
    const [myNetworkCount, setMyNetworkCount] = useState(0)
    const [unreadNotifCount, setUnreadNotifCount] = useState(0)
    const [backendStatus, setBackendStatus] = useState<'checking' | 'live' | 'offline'>('checking')

    // Fetch true counts from backend
    useEffect(() => {
        if (isAuthenticated && user) {
            signalsApi.getMine().then(({ data }) => {
                if (data) {
                    const sigCount = data.signals?.length || 0;
                    const spaceCount = (data as any).spaces?.length || 0;
                    setTotalSignalCount(sigCount + spaceCount);
                }
            });
            // Also networking count could be fetched here if needed
            setMyNetworkCount(user.networkSize ?? connections.length);

            // Fetch unread notifications
            notificationsApi.getUnreadCount().then(({ data }) => {
                if (data) setUnreadNotifCount(data.count)
            })
        }
    }, [isAuthenticated, user, signals, connections])

    // Ping backend to check connectivity
    useEffect(() => {
        signalsApi.getAll().then(({ error }) => {
            setBackendStatus(error ? 'offline' : 'live')
        })
    }, [])

    const hasNetworkNotifications = user && ((pendingRequests?.length || 0) > 0 || (offers?.length || 0) > 0);

    return (
        <aside className="hidden md:flex w-[240px] sticky top-0 h-screen flex-col border-r border-border bg-background p-4 pt-8 shrink-0">
            {isAuthenticated && user ? (
                <Link href="/profile" className="flex items-center gap-3 mb-10 px-2 group hover:bg-surface-2 p-2 rounded-xl transition-all">
                    <VerifiedAvatar
                        username={user.name || user.username || ''}
                        avatarUrl={user.avatarUrl}
                        plan={user.subscription || user.plan}
                        isVerified={user.isVerified}
                        size="w-10 h-10"
                        badgeSize="w-3.5 h-3.5"
                        fallback={<Users className="w-6 h-6 text-text-muted" />}
                    />
                    <div className="overflow-hidden">
                        <div className="flex items-center gap-1.5">
                            <h3 className="font-medium text-sm truncate group-hover:text-primary transition-colors">{user.name}</h3>
                        </div>
                        <p className="text-[10px] text-text-muted uppercase tracking-wider font-bold">{user.role} • {user.city?.split(',')[0]}</p>
                    </div>
                </Link>
            ) : (
                <Link href="/auth" className="flex items-center gap-3 mb-10 px-2 group hover:bg-surface-2 p-2 rounded-xl transition-all border border-transparent hover:border-border">
                    <div className="w-10 h-10 bg-primary/10 rounded-full overflow-hidden border border-primary/20 relative flex items-center justify-center text-primary group-hover:scale-110 transition-transform">
                        <LogIn className="w-5 h-5" />
                    </div>
                    <div className="overflow-hidden">
                        <h3 className="font-semibold text-sm text-primary">Login / Register</h3>
                        <p className="text-[10px] text-text-muted uppercase tracking-wider font-semibold mt-0.5">Enter Ecosystem</p>
                    </div>
                </Link>
            )}

            <nav className="flex-1 space-y-1">
                {navItems.map((item) => {
                    const Icon = item.icon
                    const isActive = item.href === '/profile' 
                        ? pathname === '/profile' 
                        : pathname === item.href || (item.href !== '/feed' && pathname.startsWith(item.href))

                    return (
                        <Link
                            key={item.label}
                            href={item.href}
                            className={`flex justify-between items-center px-3 py-2.5 rounded-md transition-all ${isActive
                                ? 'bg-primary text-white shadow-sm shadow-black/5'
                                : 'text-text-secondary hover:bg-surface-2 hover:text-primary'
                                }`}
                        >
                            <div className="flex items-center gap-3">
                                <Icon className="w-4 h-4" />
                                <span className="text-sm font-medium">{item.label}</span>
                            </div>
                            {item.label === 'My Network' && hasNetworkNotifications && (
                                <div className={`w-2 h-2 rounded-full ${isActive ? 'bg-white' : 'bg-black'}`} title="New requests or offers" />
                            )}
                            {item.label === 'Notifications' && unreadNotifCount > 0 && (
                                <div className={`px-1.5 py-0.5 rounded-full text-[10px] font-bold ${isActive ? 'bg-white text-primary' : 'bg-accent-red text-white'}`}>
                                    {unreadNotifCount > 99 ? '99+' : unreadNotifCount}
                                </div>
                            )}
                        </Link>
                    )
                })}
            </nav>

            <div className="pt-6 border-t border-border mt-auto">
                <div className="grid grid-cols-3 gap-2 mb-3">
                    <div>
                        <p className="text-[10px] uppercase font-bold text-text-muted mb-1">Signals</p>
                        <p className="text-lg font-mono font-bold">{totalSignalCount}</p>
                    </div>
                    <div>
                        <p className="text-[10px] uppercase font-bold text-text-muted mb-1">Network</p>
                        <p className="text-lg font-mono font-bold">{myNetworkCount}</p>
                    </div>
                    <div>
                        <p className="text-[10px] uppercase font-bold text-text-muted mb-1">Plan</p>
                        <p className="text-xs font-bold truncate text-primary">{user?.subscription || user?.plan || 'Free'}</p>
                    </div>
                </div>

                {/* Backend connection status */}
                <div className={`mb-3 flex items-center gap-1.5 px-2 py-1 rounded-md text-[10px] font-bold uppercase tracking-widest ${
                    backendStatus === 'live'
                        ? 'text-green-600 bg-green-50'
                        : backendStatus === 'offline'
                        ? 'text-orange-500 bg-orange-50'
                        : 'text-text-muted bg-surface-2'
                }`}>
                    <span className={`w-1.5 h-1.5 rounded-full ${
                        backendStatus === 'live' ? 'bg-green-500 animate-pulse'
                        : backendStatus === 'offline' ? 'bg-orange-400'
                        : 'bg-gray-400 animate-pulse'
                    }`} />
                    {backendStatus === 'live' ? 'API Live' : backendStatus === 'offline' ? 'Local Mode' : 'Checking…'}
                </div>

                <div className="flex items-center gap-2 opacity-50 grayscale hover:grayscale-0 transition-all cursor-pointer">
                    <Image src="/logo.png" alt="Starto Logo" width={20} height={20} className="invert" />
                    <span className="text-[10px] font-bold tracking-widest uppercase">Starto V3</span>
                </div>
            </div>
        </aside>
    )
}
