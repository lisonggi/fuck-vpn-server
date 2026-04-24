package com.song.fuckvpn.server.common.config

import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.common.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ResultDto<String>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResultDto("未知的错误", e.message))
    }

    @ExceptionHandler(AppException::class)
    fun handleAppException(e: AppException): ResponseEntity<ResultDto<String>> {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ResultDto("请求失败", e.message))
    }

    @ExceptionHandler(InvalidParameterException::class)
    fun handleInvalidParameterException(e: InvalidParameterException): ResponseEntity<ResultDto<String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResultDto("参数不正确", e.message))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParseException(e: HttpMessageNotReadableException): ResponseEntity<ResultDto<String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResultDto("请求参数格式错误", e.message))
    }

    @ExceptionHandler(PluginNotSupportedException::class)
    fun handlePluginNotSupportedException(e: PluginNotSupportedException): ResponseEntity<ResultDto<String>> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ResultDto("不支持的插件", e.message))
    }

    @ExceptionHandler(NotExistException::class, NoSuchElementException::class)
    fun handleNotFoundException(e: Exception): ResponseEntity<ResultDto<String>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ResultDto("数据不存在", e.message))
    }

    @ExceptionHandler(NotAvailableException::class)
    fun handleNotAvailableException(e: NotAvailableException): ResponseEntity<ResultDto<String>> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ResultDto("服务不可用", e.message))
    }

    @ExceptionHandler(BusyException::class)
    fun handleBusyException(e: BusyException): ResponseEntity<ResultDto<String>> {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ResultDto("请求过于频繁", e.message))
    }

    @ExceptionHandler(AuthException::class)
    fun handleAuthException(e: AuthException): ResponseEntity<ResultDto<String>> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResultDto("认证失败", e.message))
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflictException(e: ConflictException): ResponseEntity<ResultDto<String>> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ResultDto("数据冲突", e.message))
    }
}