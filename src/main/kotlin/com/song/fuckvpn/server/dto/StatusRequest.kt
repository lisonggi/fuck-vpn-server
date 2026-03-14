package com.song.fuckvpn.server.dto

import com.song.fuckvpn.server.enums.RunState


data class StatusRequest(val state: RunState)