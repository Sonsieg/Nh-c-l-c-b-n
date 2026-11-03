package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.InstrumentTimbre
import com.example.audio.MusicSynthesizer
import com.example.data.MusicCurriculumRepository
import com.example.data.local.*
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppTab(val route: String, val labelVi: String) {
    CURRICULUM("curriculum", "Bài Học"),
    NOTES("notes", "Đọc Nốt"),
    RHYTHM("rhythm", "Đọc Phách"),
    COMPOSE("compose", "Sáng Tác"),
    HANDBOOK("handbook", "Cẩm Nang")
}

enum class NotePracticeMode(val titleVi: String) {
    EXPLORE("Khám Phá Khuông"),
    SEQUENCE_DRILL("Đọc Chuỗi 8 Nốt"),
    QUICK_QUIZ("Phản Xạ Nhanh")
}

enum class LearningStageHint(val stageNumber: Int, val labelVi: String) {
    STAGE_1_SHOW_NAMES(1, "GĐ 1: Nhìn nốt + chữ"),
    STAGE_2_HIDE_NAMES(2, "GĐ 2: Che chữ gợi ý"),
    STAGE_3_LEAPS(3, "GĐ 3: Đọc nhảy quãng")
}

data class NoteTrainerUiState(
    val mode: NotePracticeMode = NotePracticeMode.EXPLORE,
    val clef: ClefType = ClefType.TREBLE,
    val selectedBookGroup: Int = 0,
    val learningStage: LearningStageHint = LearningStageHint.STAGE_1_SHOW_NAMES,
    val showLineNumbers: Boolean = true,
    val exploredPitch: StaffPitch = MusicCurriculumRepository.treblePitches[4],
    val selectedExercise: SightReadingExercise = MusicCurriculumRepository.sightReadingExercises.first(),
    val currentSequenceStep: Int = 0,
    val quizTargetPitch: StaffPitch = MusicCurriculumRepository.treblePitches[4],
    val lastSelectedSolfege: SolfegeNote? = null,
    val lastAnswerCorrect: Boolean? = null,
    val feedbackMessageVi: String = "Chạm vào khuông nhạc hoặc phím Đô Rê Mi Fa Sol La Si để nghe và đọc vị trí nốt!",
    val correctCount: Int = 0,
    val totalAttempts: Int = 0,
    val streak: Int = 0,
    val activePlayingIndex: Int = -1
)

data class RhythmTrainerUiState(
    val selectedTimeSignature: TimeSignatureSpec = TimeSignatureSpec.FOUR_FOUR,
    val bpm: Int = 76,
    val isMetronomeRunning: Boolean = false,
    val currentBeatIndex: Int = -1,
    val isRightHandConducting: Boolean = true,
    val selectedExercise: RhythmPatternExercise = MusicCurriculumRepository.rhythmExercises.first(),
    val activeEventIndex: Int = -1,
    val isPlayingPattern: Boolean = false,
    val tapFeedbackVi: String = "Bấm 'Nghe Mẫu Phách' hoặc 'Đếm Nhịp' rồi chạm trống GÕ PHÁCH đúng trọng âm!",
    val tapHitsCount: Int = 0,
    val lastTapStrengthLabel: String = ""
)

data class ComposeStudioUiState(
    val title: String = "Giai Điệu Đầu Tay",
    val selectedKey: KeyTonality = MusicCurriculumRepository.keyTonalities.first(),
    val selectedTimeSignature: TimeSignatureSpec = TimeSignatureSpec.FOUR_FOUR,
    val bpm: Int = 80,
    val selectedOctave: Int = 4,
    val selectedDuration: NoteDuration = NoteDuration.QUARTER,
    val selectedAccidental: Accidental = Accidental.NONE,
    val isRestMode: Boolean = false,
    val selectedChordSymbol: String = "C",
    val lyricDraft: String = "",
    val notes: List<NoteEvent> = MusicCurriculumRepository.songTemplates.first().melodyEvents,
    val selectedNoteIndex: Int = 0,
    val activePlaybackIndex: Int = -1,
    val isPlaying: Boolean = false,
    val statusMessageVi: String = "Chọn nốt Đô–Si để thêm vào khuông, gắn hợp âm và lời ca để sáng tác!"
)

class MusicAppViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MusicAppDatabase.getInstance(application)
    private val localRepo = MusicLocalRepository(database.musicProgressDao())

    val lessonProgressList: StateFlow<List<LessonProgressEntity>> = localRepo.allLessonProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedCompositions: StateFlow<List<SavedCompositionEntity>> = localRepo.allCompositions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(AppTab.CURRICULUM)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _selectedTimbre = MutableStateFlow(InstrumentTimbre.PIANO)
    val selectedTimbre: StateFlow<InstrumentTimbre> = _selectedTimbre.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.update { !it }
    }

    private val _activeLesson = MutableStateFlow<MusicLesson?>(null)
    val activeLesson: StateFlow<MusicLesson?> = _activeLesson.asStateFlow()

    private val _noteState = MutableStateFlow(NoteTrainerUiState())
    val noteState: StateFlow<NoteTrainerUiState> = _noteState.asStateFlow()

    private val _rhythmState = MutableStateFlow(RhythmTrainerUiState())
    val rhythmState: StateFlow<RhythmTrainerUiState> = _rhythmState.asStateFlow()

    private val _composeState = MutableStateFlow(ComposeStudioUiState())
    val composeState: StateFlow<ComposeStudioUiState> = _composeState.asStateFlow()

    private var metronomeJob: Job? = null

    init {
        viewModelScope.launch {
            localRepo.allCompositions.collect { list ->
                if (list.isEmpty()) {
                    val t = MusicCurriculumRepository.songTemplates.first()
                    localRepo.saveComposition(t.titleVi, t.keyTonalityId, t.timeSignature.name, t.bpm, t.melodyEvents)
                }
                return@collect
            }
        }
    }

    fun selectTab(tab: AppTab) {
        if (tab != AppTab.RHYTHM) stopMetronome()
        MusicSynthesizer.stopSequence()
        _currentTab.value = tab
    }

    fun selectTimbre(timbre: InstrumentTimbre) {
        _selectedTimbre.value = timbre
        MusicSynthesizer.playNote(60, durationMs = 450, timbre = timbre)
    }

    fun openLesson(lesson: MusicLesson?) {
        MusicSynthesizer.stopSequence()
        _activeLesson.value = lesson
    }

    fun completeLessonQuiz(lessonId: Int, score: Int, total: Int) {
        viewModelScope.launch { localRepo.saveLessonResult(lessonId, score, total) }
    }

    fun playNoteEventsDemo(events: List<NoteEvent>, bpm: Int = 80, onStep: (Int) -> Unit = {}) {
        val quarterMs = (60_000f / bpm.coerceIn(40, 200)).toInt()
        val seq = events.map { ev ->
            val durMs = (ev.duration.beatsInQuarter * quarterMs).toInt().coerceAtLeast(140)
            Pair(if (ev.isRest) emptyList() else listOf(ev.effectiveMidi), durMs)
        }
        MusicSynthesizer.playSequence(seq, _selectedTimbre.value, onStep)
    }

    // MODULE 2: ĐỌC NỐT
    fun setNotePracticeMode(mode: NotePracticeMode) {
        MusicSynthesizer.stopSequence()
        _noteState.update { s ->
            val pool = getPitchPool(s.clef, s.selectedBookGroup)
            s.copy(
                mode = mode,
                currentSequenceStep = 0,
                quizTargetPitch = pool.random(),
                lastAnswerCorrect = null,
                feedbackMessageVi = when (mode) {
                    NotePracticeMode.EXPLORE -> "Chạm lên khuông nhạc hoặc phím đàn để xem vị trí hàng/khe và nghe cao độ."
                    NotePracticeMode.SEQUENCE_DRILL -> "Đọc lần lượt từng nốt từ trái sang phải trên khuông nhạc!"
                    NotePracticeMode.QUICK_QUIZ -> "Nhìn vị trí nốt trên khuông nhạc và chọn đúng tên nốt!"
                }
            )
        }
    }

    fun setNoteClef(clef: ClefType) {
        _noteState.update { s ->
            val def = if (clef == ClefType.TREBLE) MusicCurriculumRepository.treblePitches[4]
            else MusicCurriculumRepository.bassPitches[6]
            val ex = MusicCurriculumRepository.sightReadingExercises.firstOrNull { it.clef == clef } ?: s.selectedExercise
            s.copy(
                clef = clef, selectedBookGroup = 0, exploredPitch = def, selectedExercise = ex,
                currentSequenceStep = 0, quizTargetPitch = def, lastAnswerCorrect = null,
                feedbackMessageVi = "${clef.titleVi}: ${clef.anchorLineText}"
            )
        }
    }

    fun setNoteBookGroup(group: Int) {
        _noteState.update { s ->
            val pool = getPitchPool(s.clef, group)
            val ex = MusicCurriculumRepository.sightReadingExercises.firstOrNull {
                it.clef == s.clef && (group == 0 || it.bookGroup == group)
            } ?: s.selectedExercise
            s.copy(selectedBookGroup = group, exploredPitch = pool.first(), quizTargetPitch = pool.random(), selectedExercise = ex, currentSequenceStep = 0)
        }
    }

    fun setLearningStage(stage: LearningStageHint) = _noteState.update { it.copy(learningStage = stage) }
    fun toggleShowLineNumbers() = _noteState.update { it.copy(showLineNumbers = !it.showLineNumbers) }

    fun selectSightReadingExercise(exercise: SightReadingExercise) {
        _noteState.update {
            it.copy(
                clef = exercise.clef, selectedExercise = exercise, currentSequenceStep = 0,
                lastAnswerCorrect = null, feedbackMessageVi = "Đọc nốt số 1/${exercise.notes.size} của '${exercise.titleVi}'"
            )
        }
    }

    fun onStaffStepTappedInExplorer(staffStep: Int) {
        val s = _noteState.value
        val pitches = if (s.clef == ClefType.TREBLE) MusicCurriculumRepository.treblePitches else MusicCurriculumRepository.bassPitches
        val found = pitches.minByOrNull { kotlin.math.abs(it.staffStep - staffStep) } ?: return
        MusicSynthesizer.playNote(found.midiNote, 550, timbre = _selectedTimbre.value)
        _noteState.update {
            it.copy(
                exploredPitch = found, lastSelectedSolfege = found.solfege,
                feedbackMessageVi = "${found.fullNameVi} — ${found.positionDescriptionVi} (${if (found.stemPointsDown) "Đuôi quay xuống" else "Đuôi quay lên"})"
            )
        }
    }

    fun onSolfegeKeyPressedInNoteTab(solfege: SolfegeNote) {
        val s = _noteState.value
        when (s.mode) {
            NotePracticeMode.EXPLORE -> {
                val match = getPitchPool(s.clef, s.selectedBookGroup).firstOrNull { it.solfege == solfege }
                    ?: (if (s.clef == ClefType.TREBLE) MusicCurriculumRepository.getTreblePitch(solfege, 4)
                    else MusicCurriculumRepository.getBassPitch(solfege, 3))
                MusicSynthesizer.playNote(match.midiNote, 550, timbre = _selectedTimbre.value)
                _noteState.update {
                    it.copy(
                        exploredPitch = match, lastSelectedSolfege = solfege,
                        feedbackMessageVi = "${match.fullNameVi}: ${match.positionDescriptionVi} • ${solfege.intervalToNextVi}"
                    )
                }
            }
            NotePracticeMode.SEQUENCE_DRILL -> {
                val notes = s.selectedExercise.notes
                if (notes.isEmpty()) return
                val idx = s.currentSequenceStep.coerceIn(0, notes.lastIndex)
                val target = notes[idx]
                val ok = target.pitch.solfege == solfege
                MusicSynthesizer.playNote(if (ok) target.effectiveMidi else 49, if (ok) 500 else 250, timbre = _selectedTimbre.value)
                if (ok) {
                    val next = if (idx + 1 < notes.size) idx + 1 else 0
                    val done = idx + 1 == notes.size
                    _noteState.update {
                        it.copy(
                            currentSequenceStep = next, lastSelectedSolfege = solfege, lastAnswerCorrect = true,
                            correctCount = it.correctCount + 1, totalAttempts = it.totalAttempts + 1, streak = it.streak + 1,
                            feedbackMessageVi = if (done) "Xuất sắc! Bạn đã đọc chính xác cả chuỗi 8 nốt!"
                            else "Chính xác: ${target.pitch.fullNameVi} (${target.pitch.positionDescriptionVi}). Tiếp tục nốt số ${next + 1}!"
                        )
                    }
                } else {
                    _noteState.update {
                        it.copy(
                            lastSelectedSolfege = solfege, lastAnswerCorrect = false,
                            totalAttempts = it.totalAttempts + 1, streak = 0,
                            feedbackMessageVi = "Chưa đúng! Nốt số ${idx + 1} nằm ở '${target.pitch.positionDescriptionVi}' — hãy thử lại!"
                        )
                    }
                }
            }
            NotePracticeMode.QUICK_QUIZ -> {
                val target = s.quizTargetPitch
                val ok = target.solfege == solfege
                MusicSynthesizer.playNote(if (ok) target.midiNote else 49, 500, timbre = _selectedTimbre.value)
                val pool = getPitchPool(s.clef, s.selectedBookGroup)
                val nextPitch = if (ok) pool.filter { it != target }.ifEmpty { pool }.random() else target
                _noteState.update {
                    it.copy(
                        quizTargetPitch = nextPitch, lastSelectedSolfege = solfege, lastAnswerCorrect = ok,
                        correctCount = if (ok) it.correctCount + 1 else it.correctCount,
                        totalAttempts = it.totalAttempts + 1, streak = if (ok) it.streak + 1 else 0,
                        feedbackMessageVi = if (ok) "Chính xác! Đó là nốt ${target.fullNameVi} (${target.positionDescriptionVi})."
                        else "Chưa đúng! Gợi ý: Nốt này nằm ở ${target.positionDescriptionVi}."
                    )
                }
            }
        }
    }

    fun playCurrentSightReadingExercise() {
        playNoteEventsDemo(_noteState.value.selectedExercise.notes, 82) { step ->
            _noteState.update { it.copy(activePlayingIndex = step) }
        }
    }

    private fun getPitchPool(clef: ClefType, group: Int): List<StaffPitch> {
        val all = if (clef == ClefType.TREBLE) MusicCurriculumRepository.treblePitches else MusicCurriculumRepository.bassPitches
        return if (group == 0) all else all.filter { it.bookGroup == group }.ifEmpty { all }
    }

    // MODULE 3: ĐỌC PHÁCH & ĐÁNH NHỊP
    fun selectRhythmTimeSignature(ts: TimeSignatureSpec) {
        stopMetronome()
        val ex = MusicCurriculumRepository.rhythmExercises.firstOrNull { it.timeSignature == ts } ?: _rhythmState.value.selectedExercise
        _rhythmState.update { it.copy(selectedTimeSignature = ts, selectedExercise = ex, currentBeatIndex = -1) }
    }

    fun setRhythmBpm(bpm: Int) = _rhythmState.update { it.copy(bpm = bpm.coerceIn(44, 160)) }
    fun toggleConductingHand() = _rhythmState.update { it.copy(isRightHandConducting = !it.isRightHandConducting) }

    fun selectRhythmExercise(exercise: RhythmPatternExercise) {
        stopMetronome()
        _rhythmState.update {
            it.copy(
                selectedExercise = exercise, selectedTimeSignature = exercise.timeSignature,
                bpm = exercise.defaultBpm, activeEventIndex = -1, tapFeedbackVi = exercise.beatCountingGuideVi
            )
        }
    }

    fun toggleMetronome() {
        if (_rhythmState.value.isMetronomeRunning) stopMetronome() else startMetronome()
    }

    private fun startMetronome() {
        stopMetronome()
        _rhythmState.update { it.copy(isMetronomeRunning = true, currentBeatIndex = 0) }
        metronomeJob = viewModelScope.launch {
            var beat = 0
            while (isActive && _rhythmState.value.isMetronomeRunning) {
                val s = _rhythmState.value
                val strengths = s.selectedTimeSignature.beatStrengths
                val idx = beat % strengths.size
                val st = strengths[idx]
                _rhythmState.update {
                    it.copy(currentBeatIndex = idx, lastTapStrengthLabel = "Phách ${idx + 1}: ${st.labelVi} (${st.code})")
                }
                MusicSynthesizer.playMetronomeClick(idx == 0, st.code == "N" && strengths.size == 6)
                delay((60_000L / s.bpm.coerceIn(44, 160)).coerceAtLeast(250L))
                beat++
            }
        }
    }

    fun stopMetronome() {
        metronomeJob?.cancel()
        metronomeJob = null
        _rhythmState.update { it.copy(isMetronomeRunning = false, currentBeatIndex = -1) }
    }

    fun playRhythmPatternDemo() {
        stopMetronome()
        val s = _rhythmState.value
        val quarterMs = (60_000f / s.bpm.coerceIn(44, 160)).toInt()
        val seq = s.selectedExercise.events.map { ev ->
            Pair(if (ev.isRest) emptyList() else listOf(ev.effectiveMidi), (ev.duration.beatsInQuarter * quarterMs).toInt().coerceAtLeast(140))
        }
        _rhythmState.update { it.copy(isPlayingPattern = true) }
        MusicSynthesizer.playSequence(
            events = seq,
            timbre = _selectedTimbre.value,
            onStepChanged = { idx -> _rhythmState.update { it.copy(activeEventIndex = idx) } },
            onFinished = { _rhythmState.update { it.copy(isPlayingPattern = false, activeEventIndex = -1) } }
        )
    }

    fun onUserTapBeatPad() {
        val s = _rhythmState.value
        val strengths = s.selectedTimeSignature.beatStrengths
        val idx = if (s.currentBeatIndex >= 0) s.currentBeatIndex else s.tapHitsCount % strengths.size
        val st = strengths[idx]
        MusicSynthesizer.playMetronomeClick(idx == 0)
        _rhythmState.update {
            it.copy(
                tapHitsCount = it.tapHitsCount + 1,
                currentBeatIndex = idx,
                lastTapStrengthLabel = "Phách ${idx + 1}/${strengths.size} — ${st.labelVi}",
                tapFeedbackVi = "Gõ phách ${idx + 1} (${st.labelVi}) • Tempo ${s.bpm} BPM"
            )
        }
    }

    // MODULE 4: PHÒNG SÁNG TÁC
    fun updateComposeTitle(newTitle: String) = _composeState.update { it.copy(title = newTitle) }

    fun selectComposeKey(key: KeyTonality) {
        key.primaryChords.firstOrNull()?.let { MusicSynthesizer.playChord(it.midiNotes, 650, timbre = _selectedTimbre.value) }
        _composeState.update {
            it.copy(selectedKey = key, selectedChordSymbol = key.symbol, statusMessageVi = "Đã chọn Âm thể ${key.nameVi}")
        }
    }

    fun selectComposeTimeSignature(ts: TimeSignatureSpec) = _composeState.update { it.copy(selectedTimeSignature = ts) }
    fun setComposeBpm(bpm: Int) = _composeState.update { it.copy(bpm = bpm.coerceIn(48, 160)) }
    fun selectComposeOctave(octave: Int) = _composeState.update { it.copy(selectedOctave = octave.coerceIn(4, 5)) }
    fun selectComposeDuration(duration: NoteDuration) = _composeState.update { it.copy(selectedDuration = duration) }
    fun selectComposeAccidental(accidental: Accidental) = _composeState.update { it.copy(selectedAccidental = accidental) }
    fun toggleComposeRestMode() = _composeState.update { it.copy(isRestMode = !it.isRestMode) }

    fun selectComposeChord(chordSymbol: String) {
        val def = MusicCurriculumRepository.chordsLibrary.firstOrNull { it.symbol == chordSymbol }
        def?.let { MusicSynthesizer.playChord(it.midiNotes, 650, timbre = _selectedTimbre.value) }
        _composeState.update { s ->
            val updated = s.notes.mapIndexed { i, n -> if (i == s.selectedNoteIndex) n.copy(chordSymbol = chordSymbol) else n }
            s.copy(selectedChordSymbol = chordSymbol, notes = updated, statusMessageVi = "Đã gắn hợp âm $chordSymbol vào nốt số ${s.selectedNoteIndex + 1}")
        }
    }

    fun updateComposeLyricDraft(lyric: String) {
        _composeState.update { s ->
            val updated = s.notes.mapIndexed { i, n -> if (i == s.selectedNoteIndex) n.copy(lyric = lyric) else n }
            s.copy(lyricDraft = lyric, notes = updated)
        }
    }

    fun selectNoteInComposition(index: Int) {
        val n = _composeState.value.notes.getOrNull(index) ?: return
        if (!n.isRest) MusicSynthesizer.playNote(n.effectiveMidi, 450, timbre = _selectedTimbre.value)
        _composeState.update {
            it.copy(selectedNoteIndex = index, lyricDraft = n.lyric, selectedDuration = n.duration, selectedAccidental = n.accidental)
        }
    }

    fun addNoteToComposition(solfege: SolfegeNote) {
        val s = _composeState.value
        val autoAcc = when {
            s.selectedAccidental != Accidental.NONE -> s.selectedAccidental
            s.selectedKey.sharpsCount >= 1 && solfege == SolfegeNote.FA -> Accidental.SHARP
            s.selectedKey.flatsCount >= 1 && solfege == SolfegeNote.SI -> Accidental.FLAT
            else -> Accidental.NONE
        }
        val pitch = MusicCurriculumRepository.getTreblePitch(solfege, s.selectedOctave)
        val ev = NoteEvent(
            id = "comp_${System.nanoTime()}", pitch = pitch, duration = s.selectedDuration,
            accidental = autoAcc, isRest = s.isRestMode, lyric = s.lyricDraft.trim(),
            chordSymbol = if (s.notes.isEmpty()) s.selectedChordSymbol else ""
        )
        if (!s.isRestMode) MusicSynthesizer.playNote(ev.effectiveMidi, 480, timbre = _selectedTimbre.value)
        _composeState.update {
            val updated = (it.notes + ev).takeLast(14)
            it.copy(notes = updated, selectedNoteIndex = updated.lastIndex, lyricDraft = "", statusMessageVi = "Đã thêm ${pitch.solfege.nameVi}${autoAcc.symbol} (${s.selectedDuration.nameVi})")
        }
    }

    fun removeLastComposeNote() {
        _composeState.update { s ->
            if (s.notes.isEmpty()) s else s.copy(notes = s.notes.dropLast(1), selectedNoteIndex = (s.notes.size - 2).coerceAtLeast(0))
        }
    }

    fun clearComposition() {
        _composeState.update { it.copy(notes = emptyList(), selectedNoteIndex = 0, lyricDraft = "", statusMessageVi = "Đã làm mới khuông nhạc.") }
    }

    fun generateSuggestedCadence() {
        val key = _composeState.value.selectedKey
        val gen = listOf(
            NoteEvent("g1", MusicCurriculumRepository.getTreblePitch(key.tonicSolfege, 4), NoteDuration.QUARTER, chordSymbol = key.symbol, lyric = "Ngày"),
            NoteEvent("g2", MusicCurriculumRepository.getTreblePitch(SolfegeNote.MI, 4), NoteDuration.QUARTER, lyric = "xanh"),
            NoteEvent("g3", MusicCurriculumRepository.getTreblePitch(SolfegeNote.SOL, 4), NoteDuration.HALF, lyric = "nắng"),
            NoteEvent("g4", MusicCurriculumRepository.getTreblePitch(SolfegeNote.FA, 4), NoteDuration.QUARTER, chordSymbol = "F", lyric = "hát"),
            NoteEvent("g5", MusicCurriculumRepository.getTreblePitch(SolfegeNote.LA, 4), NoteDuration.QUARTER, lyric = "vang"),
            NoteEvent("g6", MusicCurriculumRepository.getTreblePitch(SolfegeNote.SOL, 4), NoteDuration.HALF, chordSymbol = "G7", lyric = "đời"),
            NoteEvent("g7", MusicCurriculumRepository.getTreblePitch(SolfegeNote.SI, 4), NoteDuration.QUARTER, lyric = "tươi"),
            NoteEvent("g8", MusicCurriculumRepository.getTreblePitch(SolfegeNote.DO, 5), NoteDuration.DOTTED_HALF, chordSymbol = "C", lyric = "sáng")
        )
        _composeState.update { it.copy(notes = gen, selectedNoteIndex = 0, statusMessageVi = "Đã tạo câu nhạc mẫu theo vòng I → IV → V7 → I!") }
        playComposition()
    }

    fun loadSongTemplate(t: SongAnalysisTemplate) {
        val key = MusicCurriculumRepository.keyTonalities.firstOrNull { it.id == t.keyTonalityId } ?: MusicCurriculumRepository.keyTonalities.first()
        _composeState.update {
            it.copy(title = t.titleVi, selectedKey = key, selectedTimeSignature = t.timeSignature, bpm = t.bpm, notes = t.melodyEvents, selectedNoteIndex = 0)
        }
    }

    fun loadSavedComposition(e: SavedCompositionEntity) {
        val key = MusicCurriculumRepository.keyTonalities.firstOrNull { it.id == e.keyTonalityId } ?: MusicCurriculumRepository.keyTonalities.first()
        val ts = runCatching { TimeSignatureSpec.valueOf(e.timeSignatureName) }.getOrDefault(TimeSignatureSpec.FOUR_FOUR)
        _composeState.update {
            it.copy(title = e.title, selectedKey = key, selectedTimeSignature = ts, bpm = e.bpm, notes = MusicLocalRepository.deserializeNotes(e.serializedNotes), selectedNoteIndex = 0)
        }
    }

    fun saveCurrentComposition() {
        val s = _composeState.value
        if (s.notes.isEmpty()) return
        viewModelScope.launch {
            localRepo.saveComposition(s.title.ifBlank { "Bản Nhạc Mới" }, s.selectedKey.id, s.selectedTimeSignature.name, s.bpm, s.notes)
            _composeState.update { it.copy(statusMessageVi = "Đã lưu '${s.title}' vào Thư Viện!") }
        }
    }

    fun deleteSavedComposition(id: Int) {
        viewModelScope.launch { localRepo.deleteComposition(id) }
    }

    fun playComposition() {
        val s = _composeState.value
        if (s.notes.isEmpty()) return
        val quarterMs = (60_000f / s.bpm.coerceIn(48, 160)).toInt()
        val seq = s.notes.map { ev ->
            val durMs = (ev.duration.beatsInQuarter * quarterMs).toInt().coerceAtLeast(150)
            val chordDef = MusicCurriculumRepository.chordsLibrary.firstOrNull { it.symbol == ev.chordSymbol }
            val midis = buildList {
                if (!ev.isRest) add(ev.effectiveMidi)
                if (chordDef != null) addAll(chordDef.midiNotes.map { it - 12 })
            }
            Pair(midis, durMs)
        }
        _composeState.update { it.copy(isPlaying = true) }
        MusicSynthesizer.playSequence(
            events = seq,
            timbre = _selectedTimbre.value,
            onStepChanged = { step -> _composeState.update { it.copy(activePlaybackIndex = step) } },
            onFinished = { _composeState.update { it.copy(isPlaying = false, activePlaybackIndex = -1) } }
        )
    }

    fun stopCompositionPlayback() {
        MusicSynthesizer.stopSequence()
        _composeState.update { it.copy(isPlaying = false, activePlaybackIndex = -1) }
    }
}
