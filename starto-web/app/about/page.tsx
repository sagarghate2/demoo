"use client"

import { motion } from 'framer-motion'
import Image from 'next/image'
import Link from 'next/link'
import { Zap, Shield, Globe, Cpu, Heart, Twitter, Linkedin, Github, Mail, ArrowRight } from 'lucide-react'

const allTeam = [
    { name: 'Krishna Murthi', role: 'Founder & CEO', image: '/team/krishna.png' },
    { name: 'Akshay Hangargi', role: 'Co-Founder', image: '/team/akshay.png' },
    { name: 'Sagar Ghate', role: 'Backend Developer', image: '/team/sagar.png' },
    { name: 'Guru Hugar', role: 'Frontend Developer', image: '/team/guru.png' },
    { name: 'Kartik Malipatil', role: 'AI Engineer', image: '/team/kartik.png' },
    { name: 'Aziz M Nadaf', role: 'Android Developer', image: '/team/aziz.png' }
]

export default function AboutPage() {
    return (
        <div className="min-h-screen bg-[#0A0A0A] text-white selection:bg-white selection:text-black">
            
            {/* Mockup Navigation */}
            <nav className="fixed top-0 w-full z-50 px-6 md:px-12 py-8 flex justify-between items-center bg-black/80 backdrop-blur-md">
                <Link href="/">
                    <Image src="/about-logo.png" alt="Starto Logo" width={32} height={32} className="w-8 h-8" />
                </Link>
                <div className="flex items-center gap-10">
                    <div className="hidden md:flex gap-10 text-[11px] font-bold tracking-widest uppercase">
                        <Link href="/about" className="border-b-2 border-white pb-1">ABOUT</Link>
                        <Link href="/careers" className="hover:opacity-60 transition-opacity">CAREERS</Link>
                        <Link href="/feed" className="opacity-40 hover:opacity-100 transition-opacity">LOGIN</Link>
                    </div>
                    <Link href="/feed" className="bg-white text-black px-8 py-3 rounded-full text-[11px] font-bold tracking-widest uppercase hover:bg-gray-200 transition-all">
                        GET STARTED
                    </Link>
                </div>
            </nav>

            <main className="pt-52 pb-32 px-6 max-w-[1400px] mx-auto">
                
                {/* Intro Quotes Section */}
                <section className="mb-40 text-center relative">
                    <motion.div
                        initial={{ opacity: 0, y: 30 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 1 }}
                    >
                        <p className="text-[#C5D941] font-mono text-[10px] tracking-[0.5em] uppercase mb-10">The Mission</p>
                        <h2 className="text-4xl md:text-7xl font-display uppercase tracking-tighter leading-[0.9] mb-12">
                            Innovation is not about an idea.<br />
                            It's about the <span className="text-gray-500">Ecosystem.</span>
                        </h2>
                        <div className="w-24 h-px bg-white/20 mx-auto mb-12" />
                        <p className="text-gray-400 text-xl max-w-3xl mx-auto italic leading-relaxed">
                            "Starto was built on the belief that the next generation of Indian giants will emerge 
                            from Tier-2 and Tier-3 cities. We don't just provide networking; we provide the ground 
                            where vision actually finds its way to work."
                        </p>
                    </motion.div>
                    <div className="absolute -top-20 left-1/2 -translate-x-1/2 w-96 h-96 bg-white/5 blur-[120px] rounded-full -z-10" />
                </section>

                {/* Team Grid */}
                <section className="mb-60">
                    <div className="text-center mb-24">
                        <h2 className="text-4xl md:text-6xl font-display uppercase tracking-tight">The Builders</h2>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-12">
                        {allTeam.map((member, idx) => (
                            <motion.div
                                key={member.name}
                                initial={{ opacity: 0, scale: 0.95 }}
                                whileInView={{ opacity: 1, scale: 1 }}
                                viewport={{ once: true }}
                                transition={{ delay: idx * 0.1 }}
                                className="flex flex-col group"
                            >
                                <div className="rounded-[2.5rem] overflow-hidden shadow-2xl transition-all duration-500 group-hover:shadow-[#C5D941]/5">
                                    <div className="w-full aspect-[4/5] relative">
                                        <Image 
                                            src={member.image} 
                                            alt={member.name}
                                            fill
                                            className="object-contain"
                                            priority={idx < 3}
                                        />
                                    </div>
                                </div>
                                <div className="mt-8 px-4">
                                    <h4 className="text-2xl font-bold text-white mb-2">{member.name}</h4>
                                    <p className="text-sm text-gray-500 font-medium uppercase tracking-widest">{member.role}</p>
                                </div>
                            </motion.div>
                        ))}
                    </div>
                </section>

                {/* Closing Vision Section */}
                <section className="py-40 border-t border-white/5 text-center">
                    <Zap className="w-12 h-12 text-[#C5D941] fill-[#C5D941] mx-auto mb-10" />
                    <h3 className="text-3xl md:text-5xl font-display uppercase tracking-tight mb-8">
                        Building for the <br /> 500 Million.
                    </h3>
                    <p className="text-gray-400 text-lg max-w-2xl mx-auto leading-relaxed mb-12">
                        Starto V3 is the culmination of years of ecosystem research. 
                        We are bridging the intelligence gap for founders who are 
                        building the future of Bharat.
                    </p>
                    <Link href="/feed" className="inline-flex items-center gap-3 bg-white text-black px-12 py-5 rounded-full font-bold text-sm uppercase tracking-widest hover:scale-105 transition-all">
                        Join the Ecosystem <ArrowRight className="w-4 h-4" />
                    </Link>
                </section>

            </main>

            {/* Comprehensive Footer */}
            <footer className="bg-[#050505] pt-24 pb-12 px-6 md:px-12 border-t border-white/5">
                <div className="max-w-[1400px] mx-auto grid grid-cols-1 md:grid-cols-4 gap-16 mb-24">
                    <div className="col-span-1 md:col-span-2">
                        <div className="flex items-center gap-3 mb-8">
                            <Image src="/about-logo.png" alt="Starto" width={40} height={40} />
                            <span className="text-2xl font-bold tracking-tighter uppercase font-display">STARTO V3</span>
                        </div>
                        <p className="text-gray-500 text-lg max-w-sm leading-relaxed mb-10">
                            A unified growth ecosystem for Tier-2 & Tier-3 investor hubs. 
                            Built to empower the next generation of Indian entrepreneurs.
                        </p>
                        <div className="flex gap-6">
                            <Twitter className="w-5 h-5 text-gray-600 hover:text-white cursor-pointer transition-colors" />
                            <Linkedin className="w-5 h-5 text-gray-600 hover:text-white cursor-pointer transition-colors" />
                            <Github className="w-5 h-5 text-gray-600 hover:text-white cursor-pointer transition-colors" />
                            <Mail className="w-5 h-5 text-gray-600 hover:text-white cursor-pointer transition-colors" />
                        </div>
                    </div>
                    
                    <div>
                        <h4 className="text-[10px] font-bold text-white uppercase tracking-[0.4em] mb-10">Ecosystem</h4>
                        <ul className="space-y-4 text-gray-500 text-sm font-medium">
                            <li className="hover:text-white transition-colors cursor-pointer">Real-time Signals</li>
                            <li className="hover:text-white transition-colors cursor-pointer">AI Market Explore</li>
                            <li className="hover:text-white transition-colors cursor-pointer">Geospatial Nodes</li>
                            <li className="hover:text-white transition-colors cursor-pointer">Verified Connections</li>
                        </ul>
                    </div>

                    <div>
                        <h4 className="text-[10px] font-bold text-white uppercase tracking-[0.4em] mb-10">Company</h4>
                        <ul className="space-y-4 text-gray-500 text-sm font-medium">
                            <li><Link href="/about" className="hover:text-white transition-colors">About Team</Link></li>
                            <li className="hover:text-white transition-colors cursor-pointer">Careers</li>
                            <li className="hover:text-white transition-colors cursor-pointer">Press Kit</li>
                            <li className="hover:text-white transition-colors cursor-pointer">Contact</li>
                        </ul>
                    </div>
                </div>

                <div className="max-w-[1400px] mx-auto flex flex-col md:flex-row justify-between items-center border-t border-white/5 pt-12 gap-6">
                    <p className="text-gray-700 font-mono text-[9px] tracking-[0.3em] uppercase">
                        &copy; 2026 STARTO ECOSYSTEM PVT LTD. ALL RIGHTS RESERVED.
                    </p>
                    <div className="flex gap-10">
                        <Heart className="w-4 h-4 text-accent-red animate-pulse" />
                        <span className="text-gray-700 font-mono text-[9px] tracking-[0.3em] uppercase">MADE IN BHARAT</span>
                    </div>
                </div>
            </footer>
        </div>
    )
}
