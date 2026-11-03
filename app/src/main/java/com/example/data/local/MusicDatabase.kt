package com.example.data.local

import android.content.Context
import androidx.room.*
import com.example.data.MusicCurriculumRepository
import com.example.model.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val lessonId: Int,
    val isCompleted: Boolean = false,
    val quizScore: Int = 0,
    val quizTotal: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_compositions")
data class SavedCompositionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val keyTonalityId: String,
    val timeSignatureName: String,
    val bpm: Int,
    val serializedNotes: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface MusicProgressDao {
    @Query("SELECT * FROM lesson_progress")
    fun getAllLessonProgress(): Flow<List<LessonProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLessonProgress(progress: LessonProgressEntity)

    @Query("SELECT * FROM saved_compositions ORDER BY createdAt DESC")
    fun getAllCompositions(): Flow<List<SavedCompositionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComposition(composition: SavedCompositionEntity)

    @Query("DELETE FROM saved_compositions WHERE id = :id")
    suspend fun deleteCompositionById(id: Int)
}

@Database(
    entities = [LessonProgressEntity::class, SavedCompositionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MusicAppDatabase : RoomDatabase() {
    abstract fun musicProgressDao(): MusicProgressDao

    companion object {
        @Volatile
        private var INSTANCE: MusicAppDatabase? = null

        fun getInstance(context: Context): MusicAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicAppDatabase::class.java,
                    "nhac_ly_can_ban_db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class MusicLocalRepository(private val dao: MusicProgressDao) {
    val allLessonProgress: Flow<List<LessonProgressEntity>> = dao.getAllLessonProgress()
    val allCompositions: Flow<List<SavedCompositionEntity>> = dao.getAllCompositions()

    suspend fun saveLessonResult(lessonId: Int, score: Int, total: Int) {
        dao.upsertLessonProgress(
            LessonProgressEntity(lessonId = lessonId, isCompleted = true, quizScore = score, quizTotal = total)
        )
    }

    suspend fun saveComposition(
        title: String,
        keyTonalityId: String,
        timeSignatureName: String,
        bpm: Int,
        notes: List<NoteEvent>
    ) {
        dao.insertComposition(
            SavedCompositionEntity(
                title = title,
                keyTonalityId = keyTonalityId,
                timeSignatureName = timeSignatureName,
                bpm = bpm,
                serializedNotes = serializeNotes(notes)
            )
        )
    }

    suspend fun deleteComposition(id: Int) = dao.deleteCompositionById(id)

    companion object {
        fun serializeNotes(notes: List<NoteEvent>): String =
            notes.joinToString(";") { n ->
                listOf(
                    n.pitch.clef.name,
                    n.pitch.solfege.name,
                    n.pitch.octave.toString(),
                    n.duration.name,
                    n.accidental.name,
                    n.isRest.toString(),
                    n.lyric.replace("|", " ").replace(";", " "),
                    n.chordSymbol.replace("|", " ").replace(";", " ")
                ).joinToString("|")
            }

        fun deserializeNotes(serialized: String): List<NoteEvent> {
            if (serialized.isBlank()) return emptyList()
            return serialized.split(";").mapIndexedNotNull { idx, token ->
                val p = token.split("|")
                if (p.size < 8) return@mapIndexedNotNull null
                val clef = runCatching { ClefType.valueOf(p[0]) }.getOrDefault(ClefType.TREBLE)
                val sol = runCatching { SolfegeNote.valueOf(p[1]) }.getOrDefault(SolfegeNote.DO)
                val oct = p[2].toIntOrNull() ?: 4
                val dur = runCatching { NoteDuration.valueOf(p[3]) }.getOrDefault(NoteDuration.QUARTER)
                val acc = runCatching { Accidental.valueOf(p[4]) }.getOrDefault(Accidental.NONE)
                val rest = p[5].toBooleanStrictOrNull() ?: false
                val pitch = if (clef == ClefType.TREBLE) MusicCurriculumRepository.getTreblePitch(sol, oct)
                else MusicCurriculumRepository.getBassPitch(sol, oct)
                NoteEvent("saved_$idx", pitch, dur, acc, rest, lyric = p[6], chordSymbol = p[7])
            }
        }
    }
}
