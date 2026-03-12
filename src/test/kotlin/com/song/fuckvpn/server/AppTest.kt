package com.song.fuckvpn.server

import com.song.fuckvpn.server.service.PluginService
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
@SpringBootTest
class AppTest {
    @Test
    fun testApp() {
        val a = PluginService()
    }
}