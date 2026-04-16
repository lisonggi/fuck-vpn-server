package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.dto.TokenDto
import com.song.fuckvpn.server.dto.UserDto
import com.song.fuckvpn.server.model.UserConfig
import com.song.fuckvpn.server.service.AuthService
import com.song.fuckvpn.server.util.hash
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")

class AuthController(val authService: AuthService) {
    val salt = "d498610070174fba9e6512b63a9e2554"

    @PostMapping("/login")
    fun login(@RequestBody userDto: UserDto, response: HttpServletResponse): ResultDto<TokenDto> {
        val token = authService.login(UserConfig(userDto.username, userDto.password.hash(salt)))
        val cookie = Cookie("token", token).apply {
            path = "/"
            isHttpOnly = true
            maxAge = 7 * 24 * 60 * 60 // 7天
        }
        response.addCookie(cookie)
        return ResultDto("ok", TokenDto(authService.userConfig?.username ?: "default", token))
    }

    @DeleteMapping("/logout")
    fun logout(response: HttpServletResponse
    ): ResultDto<Unit> {
        authService.logout()
        val cookie = Cookie("token", "").apply {
            path = "/"          // 必须和设置时一致
            maxAge = 0          // 关键：立即删除
            isHttpOnly = true
        }
        response.addCookie(cookie)
        return ResultDto("ok")
    }

    @GetMapping("/me")
    fun me(
        @CookieValue("token") token: String
    ): ResultDto<TokenDto> {
        return ResultDto("ok", TokenDto(authService.userConfig?.username ?: "default", token))
    }

    @PutMapping("/updateConfig")
    fun updateConfig(
        @RequestBody userDto: UserDto
    ): ResultDto<UserDto> {
        val userConfig = authService.updateConfig(UserConfig(userDto.username, userDto.password.hash(salt)))
        return ResultDto("ok", UserDto(userConfig.username, "password"))
    }
}