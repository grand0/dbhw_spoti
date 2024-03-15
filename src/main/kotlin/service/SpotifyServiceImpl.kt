package ru.kpfu.itis.gr201.ponomarev.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import kotlinx.coroutines.delay
import ru.kpfu.itis.gr201.ponomarev.model.TrackModel
import ru.kpfu.itis.gr201.ponomarev.mapper.TrackMapper
import se.michaelthelin.spotify.SpotifyApi
import se.michaelthelin.spotify.enums.AuthorizationScope
import se.michaelthelin.spotify.exceptions.detailed.TooManyRequestsException
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials
import se.michaelthelin.spotify.model_objects.specification.Paging
import se.michaelthelin.spotify.model_objects.specification.Track
import se.michaelthelin.spotify.model_objects.specification.User
import java.net.URI

class SpotifyServiceImpl(
    accessToken: String? = null
) : SpotifyService {

    private val api = apiBuilder
        .setAccessToken(accessToken)
        .build()

    override suspend fun getFromPlaylist(playlistId: String, limit: Int?): List<TrackModel> {
        return getTracks(
            initialRequest = {
                api.getPlaylistsItems(playlistId)
                    .limit(limit ?: MAX_LIMIT)
                    .build()
                    .execute()
            },
            paginatedRequest = { offset ->
                api.getPlaylistsItems(playlistId)
                    .offset(offset)
                    .limit(MAX_LIMIT)
                    .build()
                    .execute()
            },
            trackMapper = TrackMapper::playlistTrackToTrack,
        )
    }

    override suspend fun getSaved(limit: Int?): List<TrackModel> {
        return getTracks(
            initialRequest = {
                api.usersSavedTracks
                    .limit(limit ?: MAX_LIMIT)
                    .build()
                    .execute()
            },
            paginatedRequest = { offset ->
                api.usersSavedTracks
                    .offset(offset)
                    .limit(MAX_LIMIT)
                    .build()
                    .execute()
            },
            trackMapper = TrackMapper::savedTrackToTrack,
        )
    }

    override suspend fun currentUserProfile(): User = api.currentUsersProfile
        .build().execute()

    override suspend fun authCodeUrl() = Url(
        api.authorizationCodeUri()
            .scope(AuthorizationScope.USER_LIBRARY_READ, AuthorizationScope.PLAYLIST_READ_PRIVATE)
            .build()
            .execute()
    )

    override suspend fun authCode(code: String): AuthorizationCodeCredentials =
        api.authorizationCode(code)
            .build()
            .execute()

    override suspend fun refreshToken(refreshToken: String): AuthorizationCodeCredentials =
        api.authorizationCodeRefresh()
            .refresh_token(refreshToken)
            .build()
            .execute()

    private suspend fun <T> getTracks(
        initialRequest: suspend () -> Paging<T>,
        paginatedRequest: suspend (Int) -> Paging<T>,
        trackMapper: (T) -> Track?,
    ): List<TrackModel> {
        val paging = initialRequest()
        val items = paging.items.map(trackMapper).filterNotNull()
        if (items.size == paging.total) {
            return items.map { TrackMapper.apiTrackToTrackModel(it); }
        } else {
            val total = paging.total
            val tracks = items.map { TrackMapper.apiTrackToTrackModel(it) }.toMutableList()
            var offset = tracks.size
            while (offset < total) {
                KotlinLogging.logger {}
                    .info { "offset: $offset, total: $total" }
                try {
                    tracks.addAll(
                        paginatedRequest(offset)
                            .items
                            .map(trackMapper)
                            .filterNotNull()
                            .map { TrackMapper.apiTrackToTrackModel(it) }
                    )
                    offset += MAX_LIMIT
                } catch (e: TooManyRequestsException) {
                    KotlinLogging.logger {}
                        .info { "rate limited, delaying" }
                    delay(RATE_LIMIT_DELAY_MS)
                }
            }
            return tracks
        }
    }

    companion object {
        private const val MAX_LIMIT = 50
        private const val RATE_LIMIT_DELAY_MS = 30_000L

        private val CLIENT_ID = System.getenv("SPOTIFY_API_CLIENT_ID")
        private val CLIENT_SECRET = System.getenv("SPOTIFY_API_CLIENT_SECRET")
        private val APP_REDIRECT_URI = System.getenv("SPOTIFY_API_REDIRECT_URL")

        private val apiBuilder = SpotifyApi.Builder()
            .setClientId(CLIENT_ID)
            .setClientSecret(CLIENT_SECRET)
            .setRedirectUri(URI(APP_REDIRECT_URI))
    }
}