"use client"

import { motion } from 'framer-motion'
import Image from 'next/image'
import Link from 'next/link'
import { Briefcase, ArrowLeft } from 'lucide-react'

export default function CareersPage() {
    return (
        <div className="min-h-screen bg-[#0A0A0A] text-white selection:bg-white selection:text-black flex flex-col items-center justify-center px-6">
            
            {/* Simple Nav */}
            <nav className="fixed top-0 w-full z-50 px-6 md:px-12 py-8 flex justify-between items-center bg-black/80 backdrop-blur-md">
                <Link href="/">
                    <Image src="/about-logo.png" alt="Starto Logo" width={32} height={32} />
                </Link>
                <Link href="/" className="flex items-center gap-2 text-[11px] font-bold tracking-widest uppercase hover:opacity-60 transition-opacity">
                    <ArrowLeft className="w-4 h-4" /> Back Home
                </Link>
            </nav>

            <motion.div 
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                className="text-center max-w-2xl"
            >
                <div className="w-20 h-20 bg-white/5 rounded-3xl flex items-center justify-center mx-auto mb-10 border border-white/10 shadow-2xl">
                    <Briefcase className="w-8 h-8 text-[#C5D941]" />
                </div>
                
                <h1 className="text-4xl md:text-6xl font-display uppercase tracking-tighter mb-6">
                    Join the <span className="text-gray-500">Ecosystem</span>
                </h1>
                
                <div className="w-16 h-px bg-[#C5D941] mx-auto mb-10" />
                
                <p className="text-gray-400 text-xl leading-relaxed mb-12">
                    We are currently not hiring at the moment, but we are always looking for passionate builders to join the Starto vision.
                </p>
                
                <div className="p-8 rounded-[2rem] bg-white/5 border border-white/10 text-sm font-mono tracking-wider text-gray-500 uppercase">
                    No active positions available right now. <br />
                    Stay tuned for future opportunities.
                </div>
            </motion.div>

            {/* Footer Background Decor */}
            <div className="absolute bottom-0 left-1/2 -translate-x-1/2 w-full h-[50vh] bg-gradient-to-t from-white/[0.02] to-transparent -z-10" />
        </div>
    )
}
