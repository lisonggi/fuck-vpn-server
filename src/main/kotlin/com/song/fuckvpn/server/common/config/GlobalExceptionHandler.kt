package com.song.fuckvpn.server.common.config

import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.common.exception.AppException
import com.song.fuckvpn.server.common.exception.RepeatedOperationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.resource.NoResourceFoundException

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ResultDto> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResultDto("未知的错误", e.message))
    }

    @ExceptionHandler(RepeatedOperationException::class)
    fun handleRepeatedOperationException(e: RepeatedOperationException): ResponseEntity<ResultDto> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResultDto("重复的操作错误", e.message))
    }

    @ExceptionHandler(AppException::class)
    fun handleAppException(e: AppException): ResponseEntity<ResultDto> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResultDto("服务错误", e.message))
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(e: NoResourceFoundException): ResponseEntity<ResultDto> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResultDto("请求的路径错误", e.message))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(e: NoSuchElementException): ResponseEntity<ResultDto> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResultDto("指定的资源不存在", e.message))
    }
}