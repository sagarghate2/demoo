"use client"

import { motion } from 'framer-motion'
import Image from 'next/image'
import Link from 'next/link'
import { Briefcase, ArrowLeft, Twitter, Linkedin, Github, Mail, Heart, Zap } from 'lucide-react'

export default function CareersPage() {
    return (
        <div className="min-h-screen bg-background text-primary selection:bg-black selection:text-white">
            
            {/* Navigation */}
            <nav className="fixed top-0 w-full z-50 px-6 md:px-12 py-8 flex justify-between items-center bg-background/80 backdrop-blur-md border-b border-black/5">
                <Link href="/">
                    <Image src="/about-logo.png" alt="Starto Logo" width={32} height={32} className="w-8 h-8 invert" />
                </Link>
                <div className="flex items-center gap-10">
                    <div className="hidden md:flex gap-10 text-[11px] font-bold tracking-widest uppercase">
                        <Link href="/about" className="hover:opacity-60 transition-opacity">ABOUT</Link>
                        <Link href="/careers" className="border-b-2 border-black pb-1">CAREERS</Link>
                        <Link href="/auth" className="opacity-40 hover:opacity-100 transition-opacity">LOGIN</Link>
                    </div>
                    <Link href="/auth" className="bg-black text-white px-8 py-3 rounded-full text-[11px] font-bold tracking-widest uppercase hover:bg-black/80 transition-all">
                        GET STARTED
                    </Link>
                </div>
            </nav>

            <main className="pt-52 pb-32 px-6 flex flex-col items-center justify-center">
                <motion.div 
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                    className="text-center max-w-2xl"
                >
                    <div className="w-20 h-20 bg-white rounded-3xl flex items-center justify-center mx-auto mb-10 border border-black/5 shadow-xl">
                        <Briefcase className="w-8 h-8 text-primary" />
                    </div>
                    
                    <h1 className="text-4xl md:text-6xl font-display uppercase tracking-tighter mb-6">
                        Join the <span className="text-text-muted">Ecosystem</span>
                    </h1>
                    
                    <div className="w-16 h-px bg-black mx-auto mb-10" />
                    
                    <p className="text-text-secondary text-xl leading-relaxed mb-12 italic">
                        "We are currently not hiring at the moment, but we are always looking for passionate builders to join the Starto vision."
                    </p>
                    
                    <div className="p-8 rounded-[2rem] bg-white border border-black/5 text-sm font-mono tracking-wider text-text-muted uppercase shadow-sm">
                        No active positions available right now. <br />
                        Stay tuned for future opportunities.
                    </div>
                </motion.div>
            </main>

            {/* Comprehensive Footer */}
            <footer className="bg-surface-2 pt-24 pb-12 px-6 md:px-12 border-t border-black/5 mt-32">
                <div className="max-w-[1400px] mx-auto grid grid-cols-1 md:grid-cols-4 gap-16 mb-24">
                    <div className="col-span-1 md:col-span-2">
                        <div className="flex items-center gap-3 mb-8">
                            <Image src="/about-logo.png" alt="Starto" width={40} height={40} className="invert" />
                            <span className="text-2xl font-bold tracking-tighter uppercase font-display">STARTO V3</span>
                        </div>
                        <p className="text-text-secondary text-lg max-w-sm leading-relaxed mb-10">
                            A unified growth ecosystem for Tier-2 & Tier-3 investor hubs. 
                            Built to empower the next generation of Indian entrepreneurs.
                        </p>
                        <div className="flex gap-6">
                            <a href="https://x.com/Startoindia" target="_blank" rel="noopener noreferrer">
                                <Twitter className="w-5 h-5 text-text-muted hover:text-black cursor-pointer transition-colors" />
                            </a>
                            <a href="https://www.linkedin.com/company/startoindia/" target="_blank" rel="noopener noreferrer">
                                <Linkedin className="w-5 h-5 text-text-muted hover:text-black cursor-pointer transition-colors" />
                            </a>
                            <Github className="w-5 h-5 text-text-muted hover:text-black cursor-pointer transition-colors" />
                            <a href="mailto:startoindiaofficial@gmail.com">
                                <Mail className="w-5 h-5 text-text-muted hover:text-black cursor-pointer transition-colors" />
                            </a>
                        </div>
                    </div>
                    
                    <div>
                        <h4 className="text-[10px] font-bold text-primary uppercase tracking-[0.4em] mb-10">Ecosystem</h4>
                        <ul className="space-y-4 text-text-secondary text-sm font-medium">
                            <li className="hover:text-black transition-colors cursor-pointer">Real-time Signals</li>
                            <li className="hover:text-black transition-colors cursor-pointer">AI Market Explore</li>
                            <li className="hover:text-black transition-colors cursor-pointer">Geospatial Nodes</li>
                            <li className="hover:text-black transition-colors cursor-pointer">Verified Connections</li>
                        </ul>
                    </div>

                    <div>
                        <h4 className="text-[10px] font-bold text-primary uppercase tracking-[0.4em] mb-10">Company</h4>
                        <ul className="space-y-4 text-text-secondary text-sm font-medium">
                            <li><Link href="/about" className="hover:text-black transition-colors">About Us</Link></li>
                            <li><Link href="/careers" className="hover:text-black transition-colors">Careers</Link></li>
                            <li><Link href="/contact" className="hover:text-black transition-colors">Contact</Link></li>
                            <li><Link href="/terms" className="hover:text-black transition-colors">Terms of Service</Link></li>
                            <li><Link href="/privacy" className="hover:text-black transition-colors">Privacy Policy</Link></li>
                        </ul>
                    </div>
                </div>

                <div className="max-w-[1400px] mx-auto flex flex-col md:flex-row justify-between items-center border-t border-black/5 pt-12 gap-6">
                    <p className="text-text-muted font-mono text-[9px] tracking-[0.3em] uppercase">
                        &copy; 2026 STARTO ECOSYSTEM PVT LTD. ALL RIGHTS RESERVED.
                    </p>
                    <div className="flex gap-10">
                        <Heart className="w-4 h-4 text-accent-red animate-pulse" />
                        <span className="text-text-muted font-mono text-[9px] tracking-[0.3em] uppercase">MADE IN BHARAT</span>
                    </div>
                </div>
            </footer>
        </div>
    )
}
