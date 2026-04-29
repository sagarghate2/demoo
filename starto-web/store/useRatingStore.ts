import { create } from 'zustand'
import { reviewsApi } from '@/lib/apiClient'

export interface Rating {
    id: string;
    reviewerId: string;
    reviewerName: string;
    reviewerUsername: string;
    reviewerAvatarUrl?: string;
    rating: number;          // 1-5
    comment?: string;
    createdAt: string;
}

interface RatingState {
    ratings: Rating[];
    summary: { averageRating: number; totalReviews: number } | null;
    isLoading: boolean;
    error: string | null;
    
    addRating: (userId: string, stars: number, comment: string) => Promise<void>;
    fetchRatingsFor: (userId: string) => Promise<void>;
    fetchSummary: (userId: string) => Promise<void>;
}

export const useRatingStore = create<RatingState>((set, get) => ({
    ratings: [],
    summary: null,
    isLoading: false,
    error: null,

    addRating: async (userId, stars, comment) => {
        set({ isLoading: true, error: null });
        try {
            const { error } = await reviewsApi.add(userId, stars, comment);
            if (error) throw new Error(error);
            
            // Refresh data after adding
            await get().fetchRatingsFor(userId);
            await get().fetchSummary(userId);
        } catch (err: any) {
            set({ error: err.message });
            throw err;
        } finally {
            set({ isLoading: false });
        }
    },

    fetchRatingsFor: async (userId) => {
        set({ isLoading: true, error: null });
        try {
            const { data, error } = await reviewsApi.getForUser(userId);
            if (error) throw new Error(error);
            set({ ratings: data || [] });
        } catch (err: any) {
            set({ error: err.message });
        } finally {
            set({ isLoading: false });
        }
    },

    fetchSummary: async (userId) => {
        try {
            const { data } = await reviewsApi.getSummary(userId);
            if (data) set({ summary: data });
        } catch (err) {
            console.error('Failed to fetch summary:', err);
        }
    }
}))
