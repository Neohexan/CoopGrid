package com.example.coopgrid.data.di


object NetworkConstants {
    // 1. Agar local test kar rahe hain toh false, Cloudflare domain ke liye TRUE
    private const val IS_TUNNEL = true

    // 2. IP aur Tunnel Domains
    private const val LOCAL_HOST = "192.168.43.58"
    private const val TUNNEL_DOMAIN = "app-api.neohexane.com" // 👈 Yahan apna mila hua domain daliye (bina http/https aur port ke)

    // 3. Ports (const hatakar sirf val use karenge)
    private val PORT = if (IS_TUNNEL) "" else "8001" // ya "8001" jo bhi server host par ho

    // 4. Schemes (const hatakar sirf val)
    private val HTTP_SCHEME = if (IS_TUNNEL) "https" else "http"
    private val WS_SCHEME = if (IS_TUNNEL) "wss" else "ws"

    private val HOST = if (IS_TUNNEL) TUNNEL_DOMAIN else LOCAL_HOST
    private val AUTHORITY = if (PORT.isEmpty()) HOST else "$HOST:$PORT"

    /** Base HTTP URL for Retrofit (End me '/' zaroori hai Retrofit ke liye) */
    val BASE_URL = "$HTTP_SCHEME://$AUTHORITY/"

    /** Base WebSocket URL (End me '/' MAT rakhiye taaki path easily append ho sake) */
    val BASE_WS_URL = "$WS_SCHEME://$AUTHORITY"

    const val TIMEOUT_SECONDS = 30L

    /** WebSocket Full Connection Route */
    fun getTerminalWsUrl(sessionToken: String): String {
        return "${BASE_WS_URL}ws/v1/terminal?token=$sessionToken"
    }
}