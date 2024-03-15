package ru.kpfu.itis.gr201.ponomarev.mapper

import ru.kpfu.itis.gr201.ponomarev.model.TrackModel
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack
import se.michaelthelin.spotify.model_objects.specification.SavedTrack
import se.michaelthelin.spotify.model_objects.specification.Track

object TrackMapper {
    fun apiTrackToTrackModel(track: Track): TrackModel = TrackModel(
        name = track.name,
        artists = track.artists.map { it.name }.toMutableList(),
        albumName = track.album.name,
        albumType = track.album.type.type,
        duration = track.durationMs,
        explicit = track.isExplicit,
        previewUrl = track.previewUrl,
        imageUrl = track.album.images.getOrNull(0)?.url,
    )

    fun playlistTrackToTrack(track: PlaylistTrack): Track? = track.track as? Track
    fun savedTrackToTrack(track: SavedTrack): Track = track.track
}