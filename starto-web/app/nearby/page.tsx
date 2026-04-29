"use client"

import { useState, useEffect, useCallback } from 'react'
import Sidebar from '@/components/feed/Sidebar'
import MobileBottomNav from '@/components/feed/MobileBottomNav'
import { Map as MapIcon, Filter, Navigation, Search, Users, ChevronRight, MapPin, Loader2, Zap, LayoutGrid, Map as MapType, Radio, Building } from 'lucide-react'
import Image from 'next/image'
import { useAuthStore } from '@/store/useAuthStore'
import { usersApi, signalsApi, ApiUser, ApiSignal } from '@/lib/apiClient'
import CityAutocomplete from '@/components/CityAutocomplete'
import NearbyMap from '@/components/feed/NearbyMap'
import { AnimatePresence, motion } from 'framer-motion'
import Link from 'next/link'
import VerifiedAvatar from '@/components/feed/VerifiedAvatar'

export default function NearbyEcosystem() {
    const { user, token } = useAuthStore()
    const [nearbyUsers, setNearbyUsers] = useState<ApiUser[]>([])
    const [nearbySignals, setNearbySignals] = useState<ApiSignal[]>([])
    const [nearbySpaces, setNearbySpaces] = useState<any[]>([])
    const [loading, setLoading] = useState(true)
    const [viewMode, setViewMode] = useState<'map' | 'list'>('map')
    
    // Search states
    const [searchCity, setSearchCity] = useState('')
    const [searchLat, setSearchLat] = useState<number | null>(17.3850)
    const [searchLng, setSearchLng] = useState<number | null>(78.4867)
    const [center, setCenter] = useState<{ lat: number, lng: number } | null>({ lat: 17.3850, lng: 78.4867 })
    const [zoom, setZoom] = useState(11)
    const [searchRole, setSearchRole] = useState('')
    const [radius, setRadius] = useState(25)

    const roles = [
        { id: '', label: 'All Roles' },
        { id: 'founder', label: 'Founders' },
        { id: 'mentor', label: 'Mentors' },
        { id: 'investor', label: 'Investors' },
        { id: 'talent', label: 'Talent' }
    ]

    const fetchNearby = useCallback(async () => {
        setLoading(true) // Start loading
        setNearbyUsers([]) // Clear current lists
        setNearbySignals([])
        setNearbySpaces([])
        
        if (!searchLat || !searchLng) {
            setLoading(false)
            return
        }

        if (isNaN(searchLat) || isNaN(searchLng)) {
            console.warn('[Nearby] Skipping fetch: Invalid coordinates', { searchLat, searchLng });
            setLoading(false);
            return;
        }

        try {
            const res = await signalsApi.getNearby(searchLat, searchLng, radius, searchRole || 'all')
            console.log('[DEBUG] Nearby API Response:', res);

            if (res.data) {
                const { users, signals, nearbySpaces } = res.data
                
                // Set signals and spaces directly
                setNearbySignals(Array.isArray(signals) ? signals : [])
                setNearbySpaces(Array.isArray(nearbySpaces) ? nearbySpaces : [])
                
                // The backend already filters users by searchRole correctly.
                setNearbyUsers(Array.isArray(users) ? users : [])
            }
        } catch (error) {
            console.error('Failed to fetch nearby data:', error)
        }
        setLoading(false)
    }, [searchLat, searchLng, radius, searchRole])

    // Initial load from user profile - Forcefully ignore 'mailapurrr'
    useEffect(() => {
        if (user && !searchLat) {
            const city = user.city || '';
            if (city.toLowerCase().includes('mailapurrr')) {
                setSearchCity('');
                setLoading(false); // Stop the spinner
            } else {
                setSearchCity(city);
                setSearchLat(user.lat);
                setSearchLng(user.lng);
            }
        }
    }, [user, searchLat])

    useEffect(() => {
        if (searchLat && searchLng) {
            fetchNearby()
        }
    }, [fetchNearby, searchLat, searchLng, searchRole, radius])

    const handleLocationChange = (name: string, lat?: number, lng?: number) => {
        setSearchCity(name)
        if (lat && lng) {
            setSearchLat(lat)
            setSearchLng(lng)
        }
    }

    return (
        <div className="min-h-screen bg-background text-text-primary flex justify-center">
            <div className="max-w-[1400px] w-full flex flex-col md:flex-row pb-16 md:pb-0">
                <Sidebar />

                <main className="flex-1 flex flex-col min-h-screen border-x border-border overflow-hidden">
                    <header className="p-8 border-b border-border bg-white/80 backdrop-blur-xl sticky top-0 z-50">
                        <div className="flex flex-col lg:flex-row gap-6 justify-between items-start lg:items-center">
                            <div>
                                <h1 className="text-3xl font-display font-medium tracking-tight mb-1">Nearby Ecosystem</h1>
                                <div className="flex items-center gap-2">
                                    <p className="text-xs text-gray-500 uppercase tracking-widest font-bold">
                                        Discover strategic partners within {radius}km
                                    </p>
                                    {searchRole && (
                                        <span className="px-2 py-0.5 bg-primary/10 text-primary text-[10px] font-bold rounded-full border border-primary/20 uppercase tracking-tighter">
                                            Filtered: {searchRole}s
                                        </span>
                                    )}
                                </div>
                            </div>
                            
                            <div className="flex flex-wrap items-center gap-3 w-full lg:w-auto">
                                {/* View Toggle */}
                                <div className="bg-surface-2 p-1 rounded-xl flex gap-1 mr-2 border border-border">
                                    <button 
                                        onClick={() => setViewMode('map')}
                                        className={`px-3 py-2 rounded-lg flex items-center gap-2 transition-all ${viewMode === 'map' ? 'bg-primary text-white' : 'text-text-muted hover:text-text-primary'}`}
                                    >
                                        <MapType className="w-4 h-4" />
                                        <span className="text-[10px] font-bold uppercase tracking-widest">Map</span>
                                    </button>
                                    <button 
                                        onClick={() => setViewMode('list')}
                                        className={`px-3 py-2 rounded-lg flex items-center gap-2 transition-all ${viewMode === 'list' ? 'bg-primary text-white' : 'text-text-muted hover:text-text-primary'}`}
                                    >
                                        <LayoutGrid className="w-4 h-4" />
                                        <span className="text-[10px] font-bold uppercase tracking-widest">List</span>
                                    </button>
                                </div>

                                <div className="w-full sm:w-64">
                                    <CityAutocomplete 
                                        value={searchCity} 
                                        onChange={handleLocationChange} 
                                    />
                                </div>
                                <select 
                                    value={searchRole}
                                    onChange={(e) => setSearchRole(e.target.value)}
                                    className="bg-white border border-border rounded-lg px-4 py-3.5 text-sm focus:outline-none focus:border-primary/50 transition-all cursor-pointer"
                                >
                                    {roles.map(r => (
                                        <option key={r.id} value={r.id} className="bg-white">{r.label}</option>
                                    ))}
                                </select>
                                <button 
                                    onClick={fetchNearby}
                                    className="bg-primary text-white p-3.5 rounded-lg hover:bg-primary-dark transition-all active:scale-95 shadow-lg shadow-primary/20"
                                >
                                    <Search className="w-5 h-5" />
                                </button>
                            </div>
                        </div>
                    </header>

                    <div className="flex-1 overflow-hidden relative">
                        {loading && (
                            <div className="absolute inset-0 z-10 bg-white/60 backdrop-blur-sm flex flex-col items-center justify-center text-text-muted">
                                <Loader2 className="w-10 h-10 animate-spin mb-4 text-primary" />
                                <p className="text-sm font-medium tracking-widest uppercase">Scanning the ecosystem...</p>
                            </div>
                        )}

                        {viewMode === 'map' ? (
                            <div className="w-full h-full p-8">
                                <NearbyMap 
                                    center={{ lat: searchLat || 17.3850, lng: searchLng || 78.4867 }}
                                    users={nearbyUsers}
                                    signals={nearbySignals}
                                    spaces={nearbySpaces}
                                    radius={radius}
                                />
                            </div>
                        ) : (
                            <div className="h-full overflow-y-auto p-8">
                                {nearbyUsers.length > 0 || nearbySignals.length > 0 || nearbySpaces.length > 0 ? (
                                    <div className="space-y-12">
                                        {/* Users Section */}
                                        {nearbyUsers.length > 0 && (
                                            <div>
                                                <div className="flex items-center gap-3 mb-6">
                                                    <div className="p-2 bg-primary/10 rounded-lg border border-primary/20">
                                                        <Users className="w-5 h-5 text-primary" />
                                                    </div>
                                                    <h3 className="font-display text-xl">Nodes ({nearbyUsers.length})</h3>
                                                </div>
                                                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                                    {nearbyUsers.map(u => (
                                                        <UserCard key={u.id} u={u} />
                                                    ))}
                                                </div>
                                            </div>
                                        )}

                                        {/* Signals Section */}
                                        {nearbySignals.length > 0 && (
                                            <div>
                                                <div className="flex items-center gap-3 mb-6">
                                                    <div className="p-2 bg-orange-500/10 rounded-lg border border-orange-500/20">
                                                        <Zap className="w-5 h-5 text-orange-500" />
                                                    </div>
                                                    <h3 className="font-display text-xl">Active Signals ({nearbySignals.length})</h3>
                                                </div>
                                                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                                    {nearbySignals.map(s => (
                                                        <SignalMiniCard key={s.id} s={s} />
                                                    ))}
                                                </div>
                                            </div>
                                        )}
                                        
                                        {/* Spaces Section */}
                                        {nearbySpaces.length > 0 && (
                                            <div>
                                                <div className="flex items-center gap-3 mb-6">
                                                    <div className="p-2 bg-green-500/10 rounded-lg border border-green-500/20">
                                                        <Radio className="w-5 h-5 text-green-500" />
                                                    </div>
                                                    <h3 className="font-display text-xl">Collab Spaces ({nearbySpaces.length})</h3>
                                                </div>
                                                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                                    {nearbySpaces.map(sp => (
                                                        <SpaceCard key={sp.id} sp={sp} />
                                                    ))}
                                                </div>
                                            </div>
                                        )}

                                        {/* Expand Search Button */}
                                        <div className="pt-12 border-t border-border flex justify-center">
                                            <button 
                                                onClick={() => setRadius(r => r + 25)}
                                                className="px-12 py-4 bg-white border border-border text-text-primary rounded-2xl font-bold text-xs uppercase tracking-widest hover:bg-primary hover:text-white transition-all active:scale-95 flex items-center gap-3 shadow-sm"
                                            >
                                                <Navigation className="w-4 h-4" />
                                                Search Further ({radius + 25}km)
                                            </button>
                                        </div>
                                    </div>
                                ) : (
                                    <div className="flex flex-col items-center justify-center h-[400px] border-2 border-dashed border-border rounded-[2rem]">
                                        <div className="bg-surface-2 p-6 rounded-full mb-6 relative">
                                            <MapIcon className="w-12 h-12 text-text-muted" />
                                            <div className="absolute inset-0 bg-primary/5 blur-xl rounded-full" />
                                        </div>
                                        <h3 className="text-xl font-display mb-2">No nodes discovered</h3>
                                        <p className="text-text-muted text-sm mb-8 max-w-xs text-center">
                                            We couldn't find anything within {radius}km of {searchCity}.
                                        </p>
                                        <button 
                                            onClick={() => setRadius(r => r + 25)}
                                            className="px-8 py-3 bg-primary text-white rounded-full font-bold text-xs uppercase tracking-widest hover:scale-105 transition-all active:scale-95"
                                        >
                                            Expand Search to {radius + 25}km
                                        </button>
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                </main>
                <MobileBottomNav />
            </div>
        </div>
    )
}

function UserCard({ u }: { u: ApiUser }) {
    const handleNavigate = () => {
        window.location.href = `/profile/${u.id}`;
    };

    return (
        <div 
            onClick={handleNavigate}
            className="group relative bg-white border border-border rounded-2xl p-6 hover:shadow-xl hover:shadow-primary/5 hover:border-primary/30 transition-all cursor-pointer overflow-hidden shadow-sm"
        >
            <div className="flex items-start gap-5 mb-6">
                <VerifiedAvatar
                    username={u.name || u.username}
                    avatarUrl={u.avatarUrl}
                    plan={u.plan}
                    size="w-16 h-16"
                    badgeSize="w-5 h-5"
                />
                <div className="flex-1">
                    <h4 className="font-display text-lg font-medium text-text-primary group-hover:text-primary transition-colors">{u.name}</h4>
                    <p className="text-xs text-primary font-bold uppercase tracking-wider mb-2">{u.role}</p>
                    <div className="flex items-center gap-1.5 text-text-muted text-[10px] font-bold uppercase tracking-widest">
                        <MapPin className="w-3 h-3" />
                        {u.city || 'Satellite'}
                    </div>
                </div>
            </div>
            <p className="text-sm text-text-secondary line-clamp-2 leading-relaxed h-10 mb-4">{u.bio || `Exploring the ${u.industry || 'ecosystem'}.`}</p>
            <div className="flex items-center justify-between pt-4 border-t border-border">
                <div className="flex flex-col">
                    <span className="text-[10px] text-text-muted uppercase font-bold tracking-widest">Network</span>
                    <span className="text-sm font-display text-text-primary">{u.networkSize || 0} nodes</span>
                </div>
                <div className="flex flex-col items-end">
                    <span className="text-[10px] text-text-muted uppercase font-bold tracking-widest">Trust Store</span>
                    <span className="text-sm font-display text-text-primary">{u.signalCount || 0} signals</span>
                </div>
            </div>
        </div>
    )
}

function SignalMiniCard({ s }: { s: ApiSignal }) {
    return (
        <div 
            onClick={() => window.location.href = `/signals/${s.id}`}
            className="group relative bg-orange-50 border border-orange-100 rounded-2xl p-6 hover:bg-orange-100/50 transition-all cursor-pointer overflow-hidden shadow-sm"
        >
            <div className="flex items-center gap-3 mb-4">
                <div className="p-2 bg-orange-100 rounded-lg">
                    <Zap className="w-4 h-4 text-orange-600" />
                </div>
                <div>
                    <h4 className="font-display text-base font-medium text-text-primary group-hover:text-primary transition-colors">{s.title}</h4>
                    <p className="text-[10px] text-orange-600 font-bold uppercase tracking-widest">{s.category}</p>
                </div>
            </div>
            <p className="text-xs text-text-secondary line-clamp-2 leading-relaxed mb-4">{s.description}</p>
            <div className="flex items-center gap-1.5 text-text-muted text-[9px] font-bold uppercase tracking-widest">
                <MapPin className="w-2.5 h-2.5" />
                {s.city}, {s.state}
            </div>
        </div>
    )
}

function SpaceCard({ sp }: { sp: any }) {
    return (
        <div 
            onClick={() => window.location.href = `/signals/${sp.id}`}
            className="group relative bg-white border border-border rounded-2xl p-6 hover:shadow-xl hover:shadow-primary/5 hover:border-primary/30 transition-all cursor-pointer overflow-hidden flex flex-col h-full shadow-sm"
        >
            <div className="flex items-start justify-between mb-4">
                <div className="flex items-center gap-3">
                    <div className="p-2 bg-green-50 rounded-lg border border-green-100">
                        <Building className="w-4 h-4 text-green-600" />
                    </div>
                    <div>
                        <h4 className="font-display text-base font-medium text-text-primary group-hover:text-primary transition-colors line-clamp-1">{sp.name}</h4>
                        <p className="text-[10px] text-green-600 font-bold uppercase tracking-widest">{sp.type || 'Innovation Hub'}</p>
                    </div>
                </div>
            </div>
            
            <p className="text-xs text-text-secondary line-clamp-2 leading-relaxed mb-4 flex-1">{sp.description || sp.address}</p>
            
            <div className="space-y-2 mb-4">
                <div className="flex items-center gap-1.5 text-text-muted text-[9px] font-bold uppercase tracking-widest">
                    <MapPin className="w-3 h-3 text-primary/60" />
                    <span className="truncate">{sp.city}, {sp.state}</span>
                </div>
            </div>

            <div className="pt-4 border-t border-border flex justify-between items-center mt-auto">
                <span className="text-[10px] font-bold uppercase tracking-[0.2em] text-primary group-hover:translate-x-1 transition-transform">
                    Explore Node →
                </span>
                {sp.website && (
                    <div className="flex items-center gap-2">
                        <div className="w-1 h-1 bg-border rounded-full" />
                        <span className="text-[9px] text-text-muted uppercase font-bold tracking-widest">Verified</span>
                    </div>
                )}
            </div>
        </div>
    )
}

