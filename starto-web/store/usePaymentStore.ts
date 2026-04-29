import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

export interface PaymentRecord {
    id: string;
    planName: string;
    amount: number;
    currency: string;
    dateTime: string;
    status: 'Successful' | 'Failed' | 'Pending';
}

interface PaymentState {
    records: PaymentRecord[];
    addRecord: (record: Omit<PaymentRecord, 'id'>) => void;
    clearHistory: () => void;
}

export const usePaymentStore = create<PaymentState>((set) => ({
    records: [],
    addRecord: (record) => set((state) => ({
        records: [
            { ...record, id: `pay_${Date.now()}` },
            ...state.records
        ]
    })),
    clearHistory: () => set({ records: [] })
}));
