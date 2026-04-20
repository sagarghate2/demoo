package com.starto.constants

import com.starto.BuildConfig

/**
 * App-wide constants.
 *
 * Fix #10: BASE_URL is read from BuildConfig.API_BASE_URL which is injected at
 * build time via the buildConfigField declared in app/build.gradle.kts.
 *
 * To configure:
 *   Local dev (emulator): add `API_BASE_URL=http://10.0.2.2:8080` to starto-android/local.properties
 *   CI / release:         set environment variable API_BASE_URL=https://api.starto.in before build
 *
 * NEVER hardcode the URL as a string literal here.
 */
object Constants {

    /** Backend REST API base URL — set via API_BASE_URL env var / local.properties */
    val BASE_URL: String = BuildConfig.API_BASE_URL
        .trimEnd('/') + "/"

    // ── Endpoint paths ────────────────────────────────────────────────────────

    const val ENDPOINT_SIGNALS       = "api/signals"
    const val ENDPOINT_USERS         = "api/users"
    const val ENDPOINT_AUTH          = "api/auth"
    const val ENDPOINT_SUBSCRIPTIONS = "api/subscriptions"
    const val ENDPOINT_CONNECTIONS   = "api/connections"
    const val ENDPOINT_NOTIFICATIONS = "api/notifications"
    const val ENDPOINT_SEARCH        = "api/search"

    // ── Timeouts (ms) ─────────────────────────────────────────────────────────

    const val CONNECT_TIMEOUT_MS = 30_000L
    const val READ_TIMEOUT_MS    = 30_000L

    // ── Presence heartbeat interval ───────────────────────────────────────────

    const val HEARTBEAT_INTERVAL_MS = 30_000L
}
