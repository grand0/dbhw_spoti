package ru.kpfu.itis.gr201.ponomarev.server

import io.ktor.server.auth.*

data class SpotifyCredentials(
    val displayName: String,
    val accessToken: String,
    val refreshToken: String,
    val tokenExpireTimestamp: Long,
) : Principal
