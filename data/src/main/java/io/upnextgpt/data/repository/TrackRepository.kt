package io.upnextgpt.data.repository

import io.upnextgpt.data.dao.TrackDao
import io.upnextgpt.data.model.Track
import io.upnextgpt.data.model.toApiTrack
import io.upnextgpt.data.model.toDbTrack

class TrackRepository(
    private val trackDao: TrackDao,
) {
    suspend fun getQueueTracks(queueId: String?): List<Track> {
        return trackDao.getQueue(queueId).map { it.toApiTrack() }
    }

    suspend fun save(track: Track) {
        val now = System.currentTimeMillis()
        val saved = trackDao.get(track.id)
        val dbTrack = if (saved != null) {
            track.toDbTrack().copy(addedAt = saved.addedAt, updatedAt = now)
        } else {
            track.toDbTrack().copy(addedAt = now, updatedAt = now)
        }
        return trackDao.save(dbTrack)
    }

    suspend fun delete(id: Long) {
        trackDao.delete(id)
    }

    suspend fun clearQueue(queueId: String?) {
        trackDao.clearQueue(queueId)
    }
}