package com.song.fuckvpn.server.common.config

import com.song.fuckvpn.server.service.AuthService
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.util.pattern.PathPatternParser

@Configuration
class WebConfig(val authService: AuthService) : WebMvcConfigurer {
    override fun configurePathMatch(configurer: PathMatchConfigurer) {
        val parser = PathPatternParser()
        parser.isCaseSensitive = false
        configurer.patternParser = parser
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("http://localhost:*",
                "http://127.0.0.1:*",
                "http://192.168.*",
                "https://localhost:*",
                "https://127.0.0.1:*",
                "https://192.168.*")
            .allowedMethods("*")
            .allowCredentials(true)
            .allowedHeaders("*")
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(RestAuthInterceptor(authService)).addPathPatterns("/**")
            .excludePathPatterns("/auth/login", "/*/useSub/*")
    }
}