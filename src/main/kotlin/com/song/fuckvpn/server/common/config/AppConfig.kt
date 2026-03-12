package com.song.fuckvpn.server.common.config

import com.song.fuckvpn.server.service.PluginService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {
    @Bean
    fun pluginService(
    ): PluginService {
        return PluginService()
    }
}