package ru.kpfu.itis.gr201.ponomarev.service

import io.ktor.http.*
import ru.kpfu.itis.gr201.ponomarev.model.TrackModel
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials
import se.michaelthelin.spotify.model_objects.specification.User

interface SpotifyService {
    suspend fun getFromPlaylist(playlistId: String, limit: Int? = 50): List<TrackModel>
    suspend fun getSaved(limit: Int? = 50): List<TrackModel>

    suspend fun currentUserProfile(): User

    suspend fun authCodeUrl(): Url
    suspend fun authCode(code: String): AuthorizationCodeCredentials
    suspend fun refreshToken(refreshToken: String): AuthorizationCodeCredentials

    companion object {
        private var _instance: SpotifyServiceImpl? = null
            get() {
                if (field == null) {
                    field = SpotifyServiceImpl()
                }
                return field
            }

        fun getInstance(): SpotifyServiceImpl = _instance!!

        fun withAccessToken(accessToken: String): SpotifyService = SpotifyServiceImpl(accessToken)
    }
}