package com.song.fuckvpn.server.common.config

import com.song.fuckvpn.server.service.AuthService
import com.song.fuckvpn.server.store.ConfigStore
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.util.pattern.PathPatternParser

@Configuration
class WebConfig(val authService: AuthService) : WebMvcConfigurer {
    val configStore = ConfigStore(
        "OriginPatternsConfig.json",
        ListSerializer(String.serializer())
    ) { emptyList() }

    val originPatternsConfig = configStore.load(true)

    override fun configurePathMatch(configurer: PathMatchConfigurer) {
        val parser = PathPatternParser()
        parser.isCaseSensitive = false
        configurer.patternParser = parser
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns(
                *originPatternsConfig.toTypedArray()
            )
            .allowCredentials(true)
            .allowedMethods("*")
            .allowedHeaders("*")
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(RestAuthInterceptor(authService)).addPathPatterns("/**")
            .excludePathPatterns("/auth/login", "/*/useSub/*")
    }
}