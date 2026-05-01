'use client';

import Link from 'next/link';
import Image from 'next/image';
import { motion } from 'framer-motion';
import { ArrowRight, Zap, Shield, Globe, Cpu, Heart, Twitter, Linkedin, Github, Mail } from 'lucide-react';

const team = [
    { name: 'Krishna Murthi', role: 'Founder & CEO', image: '/team/krishna.png' },
    { name: 'Akshay Hangargi', role: 'Co-Founder', image: '/team/akshay.png' },
    { name: 'Sagar Ghate', role: 'Backend Developer', image: '/team/sagar.png' },
    { name: 'Guru Hugar', role: 'Frontend Developer', image: '/team/guru.png' },
    { name: 'Kartik Malipatil', role: 'AI Engineer', image: '/team/kartik.png' },
    { name: 'Aziz M Nadaf', role: 'Android Developer', image: '/team/aziz.png' }
]

export default function LandingPage() {
    return (
        <div className="min-h-screen bg-[#0A0A0A] text-white selection:bg-white selection:text-black">
            
            {/* Navigation */}
            <nav className="fixed top-0 w-full z-50 px-6 py-8 flex justify-between items-center bg-[#0A0A0A]/80 backdrop-blur-md">
                <div className="flex items-center gap-2">
                    <Image src="/about-logo.png" alt="Starto" width={32} height={32} />
                    <span className="text-xl font-bold tracking-tighter uppercase font-display">STARTO</span>
                </div>
                <div className="flex items-center gap-10">
                    <div className="hidden md:flex gap-10 text-[11px] font-bold tracking-widest uppercase">
                        <Link href="/about" className="hover:opacity-60 transition-opacity">ABOUT</Link>
                        <Link href="/careers" className="hover:opacity-60 transition-opacity">CAREERS</Link>
                        <Link href="/feed" className="opacity-40 hover:opacity-100 transition-opacity">LOGIN</Link>
                    </div>
                    <Link href="/feed" className="bg-white text-black px-8 py-3 rounded-full text-[11px] font-bold tracking-widest uppercase hover:bg-gray-200 transition-all">
                        GET STARTED
                    </Link>
                </div>
            </nav>

            <main className="pt-52 pb-32 px-6 max-w-7xl mx-auto">
                {/* Hero with Quotes */}
                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="text-center mb-60"
                >
                    <p className="text-[#C5D941] font-mono text-[10px] tracking-[0.5em] uppercase mb-8">Ecosystem V3</p>
                    <h1 className="text-5xl md:text-8xl font-bold tracking-tighter mb-10 leading-[0.9] uppercase font-display">
                        Build where it<br />
                        <span className="text-gray-600 italic font-medium lowercase">actually works.</span>
                    </h1>
                    <p className="text-gray-400 text-xl max-w-2xl mx-auto mb-16 leading-relaxed italic">
                        "Connecting visionaries in Tier-2 and Tier-3 cities to the resources they need to thrive."
                    </p>
                    <Link href="/feed" className="inline-flex items-center gap-3 bg-white text-black px-12 py-5 rounded-2xl font-bold text-sm uppercase tracking-widest hover:scale-105 transition-all shadow-2xl shadow-white/5">
                        Enter Ecosystem <ArrowRight className="w-4 h-4" />
                    </Link>
                </motion.div>

                {/* Feature Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-60">
                    <FeatureCard icon={<Zap />} title="Real-time Help" description="Instant ecosystem support." />
                    <FeatureCard icon={<Cpu />} title="AI Market Analysis" description="Data-driven feasibility." />
                    <FeatureCard icon={<Globe />} title="Geospatial Map" description="Visualize local nodes." />
                    <FeatureCard icon={<Shield />} title="Verified Trust" description="Secure collaboration." />
                </div>

                {/* Team Section */}
                <section className="mb-60">
                    <div className="text-center mb-24">
                        <h2 className="text-4xl md:text-6xl font-bold uppercase tracking-tight font-display">Meet the Builders</h2>
                        <div className="w-16 h-px bg-[#C5D941] mx-auto mt-6" />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-12">
                        {team.map((member, idx) => (
                            <motion.div
                                key={member.name}
                                initial={{ opacity: 0, y: 20 }}
                                whileInView={{ opacity: 1, y: 0 }}
                                viewport={{ once: true }}
                                transition={{ delay: idx * 0.1 }}
                                className="flex flex-col group"
                            >
                                <div className="rounded-[2.5rem] overflow-hidden shadow-2xl transition-all duration-500 hover:scale-[1.02]">
                                    <div className="w-full aspect-[4/5] relative">
                                        <Image src={member.image} alt={member.name} fill className="object-contain" />
                                    </div>
                                </div>
                                <div className="mt-8 px-4">
                                    <h4 className="text-2xl font-bold text-white mb-2">{member.name}</h4>
                                    <p className="text-xs text-gray-500 font-bold uppercase tracking-[0.2em]">{member.role}</p>
                                </div>
                            </motion.div>
                        ))}
                    </div>
                </section>
            </main>

            {/* Footer */}
            <footer className="bg-[#050505] pt-24 pb-12 px-6 md:px-12 border-t border-white/5">
                <div className="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-4 gap-16 mb-24 text-center md:text-left">
                    <div className="col-span-1 md:col-span-2">
                        <div className="flex items-center gap-3 mb-8 justify-center md:justify-start">
                            <Image src="/about-logo.png" alt="Starto" width={40} height={40} />
                            <span className="text-2xl font-bold tracking-tighter uppercase font-display">STARTO</span>
                        </div>
                        <p className="text-gray-500 text-lg max-w-sm leading-relaxed mb-10 mx-auto md:mx-0">
                            A unified growth ecosystem built for the next generation of Indian entrepreneurs.
                        </p>
                        <div className="flex gap-6 justify-center md:justify-start">
                            <Twitter className="w-5 h-5 text-gray-600 hover:text-white cursor-pointer" />
                            <Linkedin className="w-5 h-5 text-gray-600 hover:text-white cursor-pointer" />
                            <Mail className="w-5 h-5 text-gray-600 hover:text-white cursor-pointer" />
                        </div>
                    </div>
                    
                    <div>
                        <h4 className="text-[10px] font-bold text-white uppercase tracking-[0.4em] mb-10">Platform</h4>
                        <ul className="space-y-4 text-gray-500 text-sm font-medium">
                            <li className="hover:text-white cursor-pointer">Help Signals</li>
                            <li className="hover:text-white cursor-pointer">AI Reports</li>
                            <li className="hover:text-white cursor-pointer">Map View</li>
                        </ul>
                    </div>

                    <div>
                        <h4 className="text-[10px] font-bold text-white uppercase tracking-[0.4em] mb-10">Connect</h4>
                        <ul className="space-y-4 text-gray-500 text-sm font-medium">
                            <li><Link href="/about" className="hover:text-white">Team</Link></li>
                            <li className="hover:text-white cursor-pointer">Contact</li>
                            <li className="hover:text-white cursor-pointer">Support</li>
                        </ul>
                    </div>
                </div>

                <div className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-center border-t border-white/5 pt-12 gap-6">
                    <p className="text-gray-700 font-mono text-[9px] tracking-[0.3em] uppercase">
                        &copy; 2026 STARTO. ALL RIGHTS RESERVED.
                    </p>
                    <div className="flex items-center gap-2 text-gray-700 font-mono text-[9px] tracking-[0.3em] uppercase">
                        <Heart className="w-4 h-4 text-accent-red" />
                        <span>BHARAT</span>
                    </div>
                </div>
            </footer>
        </div>
    );
}

function FeatureCard({ icon, title, description }: { icon: any, title: string, description: string }) {
    return (
        <div className="p-10 rounded-3xl border border-white/5 bg-white/[0.02] hover:bg-white/[0.05] transition-all group">
            <div className="w-10 h-10 mb-8 flex items-center justify-center text-[#C5D941] group-hover:scale-110 transition-transform">{icon}</div>
            <h3 className="text-lg font-bold uppercase tracking-widest mb-4">{title}</h3>
            <p className="text-gray-500 text-xs leading-relaxed">{description}</p>
        </div>
    );
}
