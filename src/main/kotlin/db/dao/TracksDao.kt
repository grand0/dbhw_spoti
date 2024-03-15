package ru.kpfu.itis.gr201.ponomarev.db.dao

import ru.kpfu.itis.gr201.ponomarev.model.TrackModel

interface TracksDao {
    suspend fun getAll(limit: Int? = null, page: Int? = null): List<TrackModel>
    suspend fun countAll(): Long
    suspend fun addTrack(track: TrackModel, skipExisting: Boolean = false)
    suspend fun addAll(tracks: List<TrackModel>, skipExisting: Boolean = true)
    suspend fun search(query: String): List<TrackModel>
}