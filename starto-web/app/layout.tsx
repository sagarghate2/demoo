import './globals.css';
import type { Metadata } from 'next';
import Script from 'next/script';
import AuthProvider from '@/components/AuthProvider';
import ErrorBoundary from '@/components/ErrorBoundary';

export const metadata: Metadata = {
    title: 'Starto V3',
    description: 'Unified Growth Ecosystem Platform',
};

export default function RootLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return (
        <html lang="en">
            <body>
                {/* Fix #7: top-level ErrorBoundary prevents blank screen on unhandled render errors */}
                <ErrorBoundary>
                    <AuthProvider>
                        {children}
                    </AuthProvider>
                </ErrorBoundary>
                <Script
                    src={`https://maps.googleapis.com/maps/api/js?key=${process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY}&libraries=geometry,places`}
                    strategy="beforeInteractive"
                />
                <Script
                    src="https://checkout.razorpay.com/v1/checkout.js"
                    strategy="lazyOnload"
                />
            </body>
        </html>
    );
}
