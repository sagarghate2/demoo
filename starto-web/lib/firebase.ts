import { initializeApp, getApps, getApp, FirebaseApp } from 'firebase/app';
import { getAuth, Auth } from 'firebase/auth';

// ── Firebase config ───────────────────────────────────────────────────────────
// All NEXT_PUBLIC_* vars are inlined by Next.js webpack at bundle time.
// They are available on both server and client when present in .env.local.
const firebaseConfig = {
    apiKey:            process.env.NEXT_PUBLIC_FIREBASE_API_KEY,
    authDomain:        process.env.NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN,
    projectId:         process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID,
    storageBucket:     process.env.NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET,
    messagingSenderId: process.env.NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID,
    appId:             process.env.NEXT_PUBLIC_FIREBASE_APP_ID,
    measurementId:     process.env.NEXT_PUBLIC_FIREBASE_MEASUREMENT_ID, // optional
};

// ── Validate required fields ──────────────────────────────────────────────────
const REQUIRED: (keyof typeof firebaseConfig)[] = [
    'apiKey', 'authDomain', 'projectId', 'storageBucket', 'messagingSenderId', 'appId',
];

const missingKeys = REQUIRED.filter((k) => !firebaseConfig[k]);

if (missingKeys.length > 0) {
    const envNames = missingKeys.map(
        (k) => `NEXT_PUBLIC_FIREBASE_${k.replace(/([A-Z])/g, '_$1').toUpperCase()}`
    );
    console.error(
        `[firebase.ts] Missing required Firebase config field(s): ${missingKeys.join(', ')}.\n` +
        `Corresponding env var(s): ${envNames.join(', ')}.\n` +
        `Check starto-web/.env.local and restart the dev server.`
    );
}

// ── App + Auth singleton ──────────────────────────────────────────────────────
// getApps() guard prevents duplicate-app errors during Next.js HMR / SSR.
const app: FirebaseApp = getApps().length
    ? getApp()
    : initializeApp(firebaseConfig);

const auth: Auth = getAuth(app);

/**
 * True when all 6 required Firebase config values were present at module load.
 * Import this in components to conditionally show a config-error UI.
 */
export const firebaseConfigured: boolean = missingKeys.length === 0;

export { app, auth };
