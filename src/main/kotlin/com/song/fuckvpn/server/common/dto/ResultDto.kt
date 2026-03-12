package com.song.fuckvpn.server.common.dto


data class ResultDto(val message: String, var body: Any? = null) {
    init {
        if (body == null) {
            this.body = emptyMap<String, Any>()
        }
    }
}