package com.example.dialerreplica.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "call_records")
data class CallRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rawNumber: String,
    val formattedNumber: String,
    val location: String,
    val result: String,
    val dialStartedAt: Long,
    val connectedAt: Long?,
    val endedAt: Long,
    val durationMs: Long,
    val recordingUri: String? = null,
    val recordingName: String? = null,
    val recordingDurationMs: Long = 0,
    val recordingSource: String? = null,
)

@Dao
interface CallRecordDao {
    @Query("SELECT * FROM call_records ORDER BY endedAt DESC")
    fun observeAll(): Flow<List<CallRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: CallRecordEntity): Long

    @Query(
        """UPDATE call_records SET
            recordingUri = :uri,
            recordingName = :name,
            recordingDurationMs = :durationMs,
            recordingSource = :source
            WHERE id = :recordId""",
    )
    suspend fun attachRecording(
        recordId: Long,
        uri: String,
        name: String,
        durationMs: Long,
        source: String,
    )

    @Query("DELETE FROM call_records WHERE id = :recordId")
    suspend fun delete(recordId: Long)

    @Query("DELETE FROM call_records WHERE rawNumber = :rawNumber")
    suspend fun deleteByNumber(rawNumber: String)
}

@Database(entities = [CallRecordEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun callRecordDao(): CallRecordDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "dialer-replica.db",
            ).build().also { instance = it }
        }
    }
}
