/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "./app/**/*.{js,ts,jsx,tsx,mdx}",
        "./components/**/*.{js,ts,jsx,tsx,mdx}",
    ],
    theme: {
        extend: {
            colors: {
                primary: "#0A0A0A",
                background: "#F5F4F0",
                surface: "#FFFFFF",
                "surface-2": "#F0EFEB",
                border: "#E0DFDB",
                "text-primary": "#0A0A0A",
                "text-secondary": "#6B6B6B",
                "text-muted": "#9B9B9B",
                "accent-green": "#0A0A0A",
                "accent-yellow": "#EAB308",
                "accent-blue": "#3B82F6",
                "accent-red": "#EF4444",
            },
            fontFamily: {
                display: ["var(--font-display)", "serif"],
                sans: ["var(--font-body)", "sans-serif"],
                mono: ["var(--font-mono)", "monospace"],
            },
        },
    },
    plugins: [],
}
