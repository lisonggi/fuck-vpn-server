package com.song.fuckvpn.server.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.util.pattern.PathPatternParser

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun configurePathMatch(configurer: PathMatchConfigurer) {
        val parser = PathPatternParser()
        parser.isCaseSensitive = false
        configurer.patternParser = parser
    }
}