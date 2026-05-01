"use client"

import Sidebar from '@/components/feed/Sidebar'
import { Bell, Zap, UserPlus, MessageSquare, ArrowRight, ShieldAlert } from 'lucide-react'
import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { notificationsApi } from '@/lib/apiClient'
import { formatDistanceToNow } from 'date-fns'
import toast from 'react-hot-toast'

export default function NotificationsPage() {
    const [notifications, setNotifications] = useState<any[]>([])
    const router = useRouter()

    const normalizeNotif = (n: any) => ({ 
        ...n, 
        isRead: n.isRead ?? n.read ?? n.is_read ?? n.isRead === true ?? false 
    })

    useEffect(() => {
        notificationsApi.getAll().then(({ data }) => {
            if (data) {
                const unreadOnly = data
                    .map(normalizeNotif)
                    .filter((n: any) => !n.isRead)
                setNotifications(unreadOnly)
            }
        })
    }, [])

    const handleMarkAllAsRead = async () => {
        const { error } = await notificationsApi.markAllAsRead()
        if (error) {
            toast.error("Failed to mark all as read")
        } else {
            setNotifications([]) // Clear immediately after marking all read
            toast.success("All notifications marked as read")
        }
    }

    const handleNotificationClick = async (notif: any) => {
        if (!notif.isRead) {
            await notificationsApi.markAsRead(notif.id)
            setNotifications(prev => prev.filter(n => n.id !== notif.id)) // Remove immediately
        }

        if (notif.data?.signalId) {
            router.push(`/signals/${notif.data.signalId}`)
        } else if (notif.type === 'connection') {
            router.push('/network')
        }
    }

    const getIcon = (type: string) => {
        if (type === 'urgent_signal') return <ShieldAlert className="w-5 h-5" />
        if (type === 'signal') return <Zap className="w-5 h-5" />
        if (type === 'connection') return <UserPlus className="w-5 h-5" />
        return <MessageSquare className="w-5 h-5" />
    }

    const getColor = (type: string) => {
        if (type === 'urgent_signal') return 'text-accent-red bg-accent-red/10 border-accent-red/20'
        if (type === 'signal') return 'text-accent-yellow bg-accent-yellow/10 border-accent-yellow/20'
        if (type === 'connection') return 'text-accent-blue bg-accent-blue/10 border-accent-blue/20'
        return 'text-primary bg-primary/10 border-primary/20'
    }

    return (
        <div className="min-h-screen bg-background flex justify-center">
            <div className="max-w-[1400px] w-full flex">
                <Sidebar />

                <main className="flex-1 max-w-[680px] border-r border-border min-h-screen p-6">
                    <header className="mb-10 flex justify-between items-center">
                        <div>
                            <h1 className="text-2xl font-display">Notifications</h1>
                            <p className="text-xs text-text-secondary">Stay updated with your ecosystem activity.</p>
                        </div>
                        <button 
                            onClick={handleMarkAllAsRead}
                            className="text-xs font-bold uppercase text-text-muted hover:text-primary transition-all underline underline-offset-4"
                        >
                            Mark all as read
                        </button>
                    </header>

                    <div className="space-y-4">
                        {notifications.length === 0 ? (
                            <div className="mt-12 p-8 border-2 border-dashed border-border rounded-xl text-center opacity-40">
                                <Bell className="w-10 h-10 mx-auto mb-4 text-text-muted" />
                                <p className="text-sm">No new notifications.</p>
                            </div>
                        ) : (
                            notifications.map((notif) => (
                                <div 
                                    key={notif.id} 
                                    onClick={() => handleNotificationClick(notif)}
                                    className={`bg-white border p-5 rounded-xl flex items-center gap-6 group transition-all cursor-pointer ${
                                        notif.isRead ? 'border-border opacity-70' : 'border-primary shadow-sm'
                                    } hover:border-text-muted`}
                                >
                                    <div className={`w-12 h-12 rounded-full flex items-center justify-center border ${getColor(notif.type)}`}>
                                        {getIcon(notif.type)}
                                    </div>
                                    <div className="flex-1">
                                        <p className="text-sm font-medium leading-relaxed mb-1 text-black">
                                            {notif.title ? <strong>{notif.title}: </strong> : ''}
                                            {notif.body}
                                        </p>
                                        <p className="text-[10px] text-text-muted uppercase tracking-widest font-bold">
                                            {notif.createdAt ? formatDistanceToNow(new Date(notif.createdAt), { addSuffix: true }) : 'Just now'}
                                        </p>
                                    </div>
                                    <div className="opacity-0 group-hover:opacity-100 transition-all">
                                        <ArrowRight className="w-5 h-5 text-text-muted" />
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                </main>
            </div>
        </div>
    )
}
