import { create } from 'zustand'
import { persist, createJSONStorage } from 'zustand/middleware'

export interface Response {
    id: string;
    signalId: string;
    signalTitle: string;
    signalUsername: string;   // the person who raised the signal
    signalCategory: string;
    respondedAt: number;
}

interface ResponseState {
    responses: Response[];
    addResponse: (r: Omit<Response, 'id' | 'respondedAt'>) => void;
    hasResponded: (signalId: string) => boolean;
    clearAll: () => void;
}

export const useResponseStore = create<ResponseState>((set, get) => ({
    responses: [],
    addResponse: (r) => set((state) => {
        if (state.responses.some(existing => existing.signalId === r.signalId)) return state;
        return {
            responses: [{
                ...r,
                id: Date.now().toString(),
                respondedAt: Date.now(),
            }, ...state.responses]
        }
    }),
    hasResponded: (signalId) => get().responses.some(r => r.signalId === signalId),
    clearAll: () => set({ responses: [] }),
}))
