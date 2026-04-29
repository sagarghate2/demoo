package com.starto.filter;

import io.github.bucket4j.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fix #5: Tiered rate limiting.
 *
 * Auth endpoints (/api/auth/register, /api/auth/forgot-password, /api/auth/reset-password):
 *   - Unauthenticated (by IP): 5 requests / 10 minutes (brute-force protection)
 *   - Authenticated (by token hash): 10 requests / 10 minutes
 *
 * All other endpoints:
 *   - Unauthenticated (by IP): 30 requests / minute
 *   - Authenticated (by token hash): 60 requests / minute
 *
 * Fix #4 (related): password operations go through Firebase — no local credential store
 * to brute-force. Rate limiting auth routes still protects against Firebase quota abuse.
 */
@Component
public class RateLimitFilter implements Filter {

    /** Auth-specific paths that get the strictest limits */
    private static final Set<String> AUTH_PATHS = Set.of(
        "/api/auth/register",
        "/api/auth/forgot-password",
        "/api/auth/reset-password"
    );

    // Buckets per identifier
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    // ── bucket factories ──────────────────────────────────────────────────────

    /** Strict: 5 per 10 min — auth endpoints, unauthenticated callers */
    private Bucket authIpBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.simple(5, Duration.ofMinutes(10)))
                .build();
    }

    /** Relaxed: 10 per 10 min — auth endpoints, authenticated callers */
    private Bucket authUserBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.simple(10, Duration.ofMinutes(10)))
                .build();
    }

    /** Standard: 30 per min — general, unauthenticated */
    private Bucket generalIpBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.simple(30, Duration.ofMinutes(1)))
                .build();
    }

    /** Standard: 60 per min — general, authenticated */
    private Bucket generalUserBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.simple(150, Duration.ofMinutes(1)))
                .build();
    }

    // ── filter logic ──────────────────────────────────────────────────────────

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getServletPath();
        String authHeader = req.getHeader("Authorization");
        boolean isAuthenticated = authHeader != null && authHeader.startsWith("Bearer ");
        boolean isAuthPath = AUTH_PATHS.contains(path);

        // Build a bucket key that encodes: path-tier + auth-tier + identity
        String identity = isAuthenticated
                ? "user_" + Math.abs(authHeader.substring(7).hashCode())
                : "ip_" + req.getRemoteAddr();

        String bucketKey = (isAuthPath ? "auth:" : "gen:") + identity;

        Bucket bucket = buckets.computeIfAbsent(bucketKey, k -> {
            if (isAuthPath) {
                return isAuthenticated ? authUserBucket() : authIpBucket();
            } else {
                return isAuthenticated ? generalUserBucket() : generalIpBucket();
            }
        });

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse res = (HttpServletResponse) response;
            res.setStatus(429);
            res.setContentType("application/json");
            res.setHeader("Retry-After", isAuthPath ? "600" : "60");
            res.getWriter().write("{\"error\": \"Too many requests. Please slow down.\"}");
        }
    }
}