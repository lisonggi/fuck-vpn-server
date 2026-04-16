package com.song.fuckvpn.server.common.config

import com.song.fuckvpn.server.common.exception.AuthException
import com.song.fuckvpn.server.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor

class RestAuthInterceptor(val authService: AuthService) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (request.method == "OPTIONS") {
            response.status = HttpServletResponse.SC_OK
            return true
        }
        val token = request.cookies?.firstOrNull { it.name == "token" }?.value
        if (token != null && authService.checkToken(token)) {
            return true
        }
        throw AuthException("未登录或token失效")
    }
}