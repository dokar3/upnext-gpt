package io.upnextgpt.data.dao

import io.upnextgpt.Database
import io.upnextgpt.data.db.Track
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackDao(
    private val database: Database,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun getAll(): List<Track> = withContext(dispatcher) {
        database.trackQueries.selectAll().executeAsList()
    }

    suspend fun getQueue(queueId: String?): List<Track> =
        withContext(dispatcher) {
            database.trackQueries.selectQueue(queueId).executeAsList()
        }

    suspend fun get(id: Long): Track? = withContext(dispatcher) {
        database.trackQueries.select(id).executeAsOneOrNull()
    }

    suspend fun exists(id: Long): Boolean = withContext(dispatcher) {
        database.trackQueries.exists(id).executeAsOne()
    }

    suspend fun save(track: Track) = withContext(dispatcher) {
        database.trackQueries.save(track)
    }

    suspend fun delete(id: Long) = withContext(dispatcher) {
        database.trackQueries.delete(id)
    }

    suspend fun delete(ids: Collection<Long>) = withContext(dispatcher) {
        database.trackQueries.deleteAllById(ids)
    }

    suspend fun clearQueue(queueId: String?) = withContext(dispatcher) {
        database.trackQueries.clearQueue(queueId)
    }
}