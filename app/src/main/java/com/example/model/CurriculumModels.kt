package com.example.model

enum class CurriculumStage(
    val stageNumber: Int,
    val titleVi: String,
    val subtitleVi: String
) {
    STAGE_1(1, "Chặng 1: Âm Thanh, Tên Nốt & Khuông Nhạc", "Bài 1 – Bài 6: Nền tảng đọc nốt Đô Rê Mi Fa Sol La Si"),
    STAGE_2(2, "Chặng 2: Nhịp, Phách, Dấu Hóa & Trường Độ", "Bài 7 – Bài 14: Số chỉ nhịp, dấu lặng, chấm dôi, khóa Fa & liên ba"),
    STAGE_3(3, "Chặng 3: Phách Mạnh Nhẹ, Đảo Phách & Đánh Nhịp", "Bài 20 – Bài 21: Trọng âm, nhịp lấy đà, đảo phách & nghịch phách"),
    STAGE_4(4, "Chặng 4: Âm Giai, Quãng, Hợp Âm & Sáng Tác", "Bài 15 – Bài 19: Cấu trúc gam, hợp âm, âm thể & phân tích bài hát")
}

data class QuizQuestion(
    val id: String,
    val questionVi: String,
    val optionsVi: List<String>,
    val correctIndex: Int,
    val explanationVi: String
)

data class LessonSection(
    val headingVi: String,
    val bodyVi: String,
    val highlightFormula: String? = null,
    val demoNotes: List<NoteEvent> = emptyList(),
    val demoClef: ClefType = ClefType.TREBLE,
    val demoTimeSignature: TimeSignatureSpec? = null
)

data class MusicLesson(
    val id: Int,
    val lessonNumberLabel: String,
    val titleVi: String,
    val summaryVi: String,
    val stage: CurriculumStage,
    val bookPageStart: Int,
    val sections: List<LessonSection>,
    val songwriterTakeawayVi: String,
    val quizQuestions: List<QuizQuestion>
)

data class SightReadingExercise(
    val id: String,
    val titleVi: String,
    val subtitleVi: String,
    val clef: ClefType,
    val bookGroup: Int,
    val notes: List<NoteEvent>,
    val bookPage: Int
)

data class RhythmPatternExercise(
    val id: String,
    val titleVi: String,
    val categoryVi: String,
    val timeSignature: TimeSignatureSpec,
    val defaultBpm: Int,
    val descriptionVi: String,
    val events: List<NoteEvent>,
    val beatCountingGuideVi: String
)

data class IntervalSpec(
    val nameVi: String,
    val shortCode: String,
    val semitones: Int,
    val distanceTextVi: String,
    val exampleVi: String,
    val lowMidi: Int,
    val highMidi: Int,
    val feelingVi: String
)

data class ScaleDegreeRole(
    val roman: String,
    val degreeNumber: Int,
    val nameVi: String,
    val intlName: String,
    val roleDescriptionVi: String,
    val isPrimary: Boolean
)

data class KeySignatureGuideRow(
    val accidentalType: String,
    val count: Int,
    val accidentalsListVi: String,
    val majorKeyVi: String,
    val majorEndingNoteVi: String,
    val minorKeyVi: String,
    val minorEndingNoteVi: String
)

data class MusicalTermEntry(
    val term: String,
    val categoryVi: String,
    val meaningVi: String,
    val bpmHint: String = ""
)

data class SongAnalysisTemplate(
    val id: String,
    val titleVi: String,
    val composerVi: String,
    val keyTonalityId: String,
    val timeSignature: TimeSignatureSpec,
    val bpm: Int,
    val bookPage: Int,
    val analysisPointsVi: List<String>,
    val chordProgressionSummary: String,
    val melodyEvents: List<NoteEvent>
)
