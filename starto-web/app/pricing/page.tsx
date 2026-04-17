import Sidebar from '@/components/feed/Sidebar'
import { Check, Zap, Rocket } from 'lucide-react'

export default function PricingPage() {
    return (
        <div className="min-h-screen bg-background flex justify-center">
            <div className="max-w-[1400px] w-full flex">
                <Sidebar />

                <main className="flex-1 p-8 md:p-16 overflow-y-auto">
                    <header className="text-center mb-16">
                        <h1 className="text-4xl md:text-5xl font-display mb-4">Choose Your Growth Node</h1>
                        <p className="text-text-secondary text-lg">Scalable intelligence for every stage of your investor journey.</p>
                    </header>

                    <div className="max-w-4xl mx-auto grid grid-cols-1 md:grid-cols-2 gap-8">
                        {/* Free Tier */}
                        <div className="bg-white border border-border p-10 rounded-2xl flex flex-col hover:border-text-muted transition-all">
                            <div className="mb-8">
                                <h3 className="text-xl font-display mb-2">Essential</h3>
                                <p className="text-text-secondary text-sm">Everything needed to start emitting signals.</p>
                            </div>
                            <div className="mb-10">
                                <span className="text-4xl font-mono">₹0</span>
                                <span className="text-text-muted text-sm ml-2">/ forever</span>
                            </div>
                            <ul className="space-y-4 mb-12 flex-1">
                                {['5 Signals / Month', 'Standard Search', 'Direct Messaging', 'Basic Profile'].map(f => (
                                    <li key={f} className="flex gap-3 text-sm text-text-primary">
                                        <Check className="w-4 h-4 text-accent-green shrink-0" /> {f}
                                    </li>
                                ))}
                            </ul>
                            <button className="w-full py-4 border border-border rounded-md font-bold uppercase tracking-widest text-xs hover:bg-surface-2 transition-all">Current Plan</button>
                        </div>

                        {/* Studio Tier */}
                        <div className="bg-primary text-white p-10 rounded-2xl flex flex-col relative overflow-hidden shadow-2xl shadow-primary/20">
                            <div className="absolute -top-4 -right-4 bg-accent-yellow text-primary text-[10px] font-bold px-8 py-2 rotate-45">RECOMMENDED</div>
                            <div className="mb-8">
                                <h3 className="text-xl font-display mb-2 flex items-center gap-2">Studio <Rocket className="w-5 h-5 text-accent-yellow" /></h3>
                                <p className="text-white/60 text-sm">Advanced intelligence for power ecosystem nodes.</p>
                            </div>
                            <div className="mb-10 text-white">
                                <span className="text-4xl font-mono">₹1,499</span>
                                <span className="text-white/40 text-sm ml-2">/ month</span>
                            </div>
                            <ul className="space-y-4 mb-12 flex-1">
                                {['Unlimited Signals', 'AI Market Explore', 'Premium Insights', 'Boost Priority', 'Verified Node Badge'].map(f => (
                                    <li key={f} className="flex gap-3 text-sm">
                                        <Check className="w-4 h-4 text-accent-green shrink-0" /> {f}
                                    </li>
                                ))}
                            </ul>
                            <button className="w-full py-4 bg-white text-primary rounded-md font-bold uppercase tracking-widest text-xs flex items-center justify-center gap-2 hover:opacity-90">
                                Upgrade to Studio <ArrowRight className="w-4 h-4" />
                            </button>
                        </div>
                    </div>
                </main>
            </div>
        </div>
    )
}

function ArrowRight(props: any) {
    return (
        <svg {...props} xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M5 12h14" /><path d="m12 5 7 7-7 7" />
        </svg>
    )
}
