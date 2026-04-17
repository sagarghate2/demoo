'use client';

import Link from 'next/link';
import { motion } from 'framer-motion';
import { ArrowRight, Zap, Shield, Globe, Cpu } from 'lucide-react';

export default function LandingPage() {
    return (
        <div className="min-h-screen bg-[#0A0A0A] text-white selection:bg-white selection:text-black">
            {/* Navigation */}
            <nav className="fixed top-0 w-full z-50 px-4 md:px-6 py-4 md:py-8 flex justify-between items-center bg-[#0A0A0A]/80 backdrop-blur-md">
                <div className="text-lg md:text-xl font-bold tracking-tighter">STARTO V2</div>
                <div className="flex gap-4 md:gap-8 text-xs md:text-sm font-medium items-center">
                    <Link href="/feed" className="hidden sm:block hover:text-gray-400 transition-colors">LOGIN</Link>
                    <Link href="/feed" className="bg-white text-black px-4 md:px-6 py-2 rounded-full font-bold hover:bg-gray-200 transition-all transform hover:scale-105">
                        GET STARTED
                    </Link>
                </div>
            </nav>

            {/* Hero Section */}
            <main className="pt-40 pb-20 px-6 max-w-7xl mx-auto">
                <div className="relative">
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.8 }}
                        className="text-center"
                    >
                        <div className="inline-block px-4 py-1 rounded-full border border-white/10 text-xs font-mono mb-8 bg-white/5">
                            PHASE 4: LIVE IN TIER-2 & TIER-3 CITIES
                        </div>
                        <h1 className="text-5xl sm:text-6xl md:text-8xl font-bold tracking-tighter mb-8 leading-[0.9]">
                            REAL-TIME ECOSYSTEM<br />
                            <span className="text-transparent bg-clip-text bg-gradient-to-b from-white to-white/40">INTELLIGENCE</span>
                        </h1>
                        <p className="text-xl text-gray-400 max-w-2xl mx-auto mb-12">
                            A structured signal exchange for India's emerging investor hubs.
                            Build connections, get instant help, and access market intelligence without the noise.
                        </p>
                        <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
                            <Link href="/feed" className="group relative bg-white text-black px-12 py-5 rounded-xl font-bold text-lg flex items-center gap-3 overflow-hidden transition-all hover:scale-105 active:scale-95">
                                <span className="relative z-10 transition-colors group-hover:text-black">Enter Ecosystem</span>
                                <ArrowRight className="w-5 h-5 relative z-10 group-hover:translate-x-1 transition-transform" />
                            </Link>
                        </div>
                    </motion.div>
                </div>

                {/* Features Grid */}
                <div className="mt-40 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                    <FeatureCard
                        icon={<Zap className="w-6 h-6" />}
                        title="Instant Help"
                        description="Urgency-driven help signals broadcasted to nearby nodes within minutes."
                    />
                    <FeatureCard
                        icon={<Cpu className="w-6 h-6" />}
                        title="AI Explore"
                        description="Market demand and risk analysis powered by GPT-4o and Gemini integration."
                    />
                    <FeatureCard
                        icon={<Globe className="w-6 h-6" />}
                        title="Geospatial Map"
                        description="Visualize your local investor ecosystem nodes within a 25km radius."
                    />
                    <FeatureCard
                        icon={<Shield className="w-6 h-6" />}
                        title="Verified Nodes"
                        description="Secure, verified identity for founders, mentors, and investors."
                    />
                </div>
            </main>

            {/* Footer */}
            <footer className="border-t border-white/5 py-20 px-6">
                <div className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-center gap-8">
                    <div className="text-gray-500 text-xs md:text-sm font-mono text-center md:text-left mb-4 md:mb-0">&copy; 2026 STARTO V2. ALL RIGHTS RESERVED.</div>
                    <div className="flex gap-8 text-xs font-mono text-gray-500">
                        <Link href="#">TERMS</Link>
                        <Link href="#">PRIVACY</Link>
                        <Link href="#">DOCS</Link>
                    </div>
                </div>
            </footer>
        </div>
    );
}

function FeatureCard({ icon, title, description }: { icon: React.ReactNode, title: string, description: string }) {
    return (
        <div className="p-8 rounded-2xl border border-white/5 bg-white/[0.02] hover:bg-white/[0.04] transition-colors group">
            <div className="w-12 h-12 rounded-xl bg-white/5 flex items-center justify-center mb-6 group-hover:scale-110 transition-transform text-white">
                {icon}
            </div>
            <h3 className="text-xl font-bold mb-4">{title}</h3>
            <p className="text-gray-400 text-sm leading-relaxed">{description}</p>
        </div>
    );
}
