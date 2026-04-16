package com.song.fuckvpn.server.util

import jakarta.servlet.http.HttpServletRequest

object IpUtil {
    fun getClientIp(request: HttpServletRequest): String {
        val headers = listOf(
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        )

        for (header in headers) {
            val ip = request.getHeader(header)
            if (!ip.isNullOrBlank() && ip.lowercase() != "unknown") {
                // X-Forwarded-For 可能是多个 IP，第一个才是客户端真实 IP
                return ip.split(",")[0].trim()
            }
        }

        return request.remoteAddr
    }
}