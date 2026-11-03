package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.audio.MusicSynthesizer
import com.example.data.MusicCurriculumRepository
import com.example.model.*
import com.example.ui.LearningStageHint
import com.example.ui.NotePracticeMode
import com.example.ui.NoteTrainerUiState
import com.example.ui.components.MusicalStaffCanvas
import com.example.ui.components.SolfegePianoPad
import com.example.ui.theme.ConservatoryCrimson
import com.example.ui.theme.JadeHarmony

@Composable
fun NoteReadingScreen(
    state: NoteTrainerUiState,
    onSelectMode: (NotePracticeMode) -> Unit,
    onSelectClef: (ClefType) -> Unit,
    onSelectBookGroup: (Int) -> Unit,
    onSelectLearningStage: (LearningStageHint) -> Unit,
    onToggleShowLineNumbers: () -> Unit,
    onSelectExercise: (SightReadingExercise) -> Unit,
    onStaffStepTapped: (Int) -> Unit,
    onSolfegeKeyPressed: (SolfegeNote) -> Unit,
    onPlayExerciseAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("note_reading_screen"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Mode Switcher (Horizontal scroll + single-line non-wrapping compact buttons)
        item {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NotePracticeMode.entries.forEach { mode ->
                    val selected = state.mode == mode
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectMode(mode) },
                        label = {
                            Text(
                                text = mode.titleVi,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.height(34.dp).testTag("note_mode_${mode.name}")
                    )
                }
            }
        }

        // 2. Clef Switcher & Line Numbers Toggle
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
            ) {
                ClefType.entries.forEach { clef ->
                    val selected = state.clef == clef
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectClef(clef) },
                        label = {
                            Text(
                                text = clef.titleVi,
                                maxLines = 1,
                                softWrap = false
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.height(34.dp).testTag("clef_chip_${clef.name}")
                    )
                }
                FilterChip(
                    selected = state.showLineNumbers,
                    onClick = onToggleShowLineNumbers,
                    label = {
                        Text(
                            text = if (state.showLineNumbers) "Hiện Hàng 1-5" else "Ẩn số Hàng",
                            maxLines = 1,
                            softWrap = false
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.height(34.dp).testTag("toggle_line_numbers")
                )
            }
        }

        // 3. Mode-Specific Controls
        item {
            when (state.mode) {
                NotePracticeMode.EXPLORE -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Sắp xếp các nốt trên ${state.clef.titleVi}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${state.clef.anchorLineText} • Chạm trực tiếp vào khuông nhạc hoặc phím đàn bên dưới để nghe và xem vị trí Hàng/Khe.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                NotePracticeMode.SEQUENCE_DRILL -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        val exercisesForClef = MusicCurriculumRepository.sightReadingExercises.filter { it.clef == state.clef }
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            exercisesForClef.forEach { ex ->
                                val selected = state.selectedExercise.id == ex.id
                                FilterChip(
                                    selected = selected,
                                    onClick = { onSelectExercise(ex) },
                                    label = { Text(ex.titleVi, maxLines = 1, softWrap = false) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.height(34.dp).testTag("sr_exercise_${ex.id}")
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            LearningStageHint.entries.forEach { stage ->
                                val selected = state.learningStage == stage
                                FilterChip(
                                    selected = selected,
                                    onClick = { onSelectLearningStage(stage) },
                                    label = { Text(stage.labelVi, maxLines = 1, softWrap = false) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.height(34.dp).testTag("learning_stage_${stage.stageNumber}")
                                )
                            }
                        }
                    }
                }
                NotePracticeMode.QUICK_QUIZ -> {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val maxG = if (state.clef == ClefType.TREBLE) 4 else 2
                        FilterChip(
                            selected = state.selectedBookGroup == 0,
                            onClick = { onSelectBookGroup(0) },
                            label = { Text("Tất cả", maxLines = 1, softWrap = false) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.height(34.dp)
                        )
                        for (g in 1..maxG) {
                            FilterChip(
                                selected = state.selectedBookGroup == g,
                                onClick = { onSelectBookGroup(g) },
                                label = { Text("Nhóm $g", maxLines = 1, softWrap = false) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.height(34.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Musical Staff Canvas
        item {
            when (state.mode) {
                NotePracticeMode.EXPLORE -> {
                    val pitches = if (state.clef == ClefType.TREBLE) MusicCurriculumRepository.treblePitches.take(11)
                    else MusicCurriculumRepository.bassPitches
                    val events = pitches.mapIndexed { i, p -> NoteEvent("exp_$i", p) }
                    val selIdx = pitches.indexOfFirst { it.staffStep == state.exploredPitch.staffStep }
                    MusicalStaffCanvas(
                        notes = events,
                        clef = state.clef,
                        activeNoteIndex = selIdx,
                        showNoteNames = true,
                        showLineNumbers = state.showLineNumbers,
                        showBeatLabels = false,
                        canvasHeight = 220.dp,
                        onNoteTapped = { _, ev -> onStaffStepTapped(ev.pitch.staffStep) },
                        onStaffStepTapped = onStaffStepTapped
                    )
                }
                NotePracticeMode.SEQUENCE_DRILL -> {
                    MusicalStaffCanvas(
                        notes = state.selectedExercise.notes,
                        clef = state.clef,
                        activeNoteIndex = if (state.activePlayingIndex >= 0) state.activePlayingIndex else state.currentSequenceStep,
                        showNoteNames = state.learningStage == LearningStageHint.STAGE_1_SHOW_NAMES,
                        showLineNumbers = state.showLineNumbers,
                        showBeatLabels = false,
                        canvasHeight = 220.dp,
                        onNoteTapped = { _, ev -> MusicSynthesizer.playNote(ev.effectiveMidi, 500) }
                    )
                }
                NotePracticeMode.QUICK_QUIZ -> {
                    MusicalStaffCanvas(
                        notes = listOf(NoteEvent("quiz", state.quizTargetPitch, beatLabel = "Nốt gì?")),
                        clef = state.clef,
                        activeNoteIndex = 0,
                        showNoteNames = false,
                        showLineNumbers = state.showLineNumbers,
                        showBeatLabels = true,
                        canvasHeight = 210.dp
                    )
                }
            }
        }

        // 5. Status & Audio Button
        item {
            val activePitch = when (state.mode) {
                NotePracticeMode.EXPLORE -> state.exploredPitch
                NotePracticeMode.SEQUENCE_DRILL ->
                    state.selectedExercise.notes.getOrNull(state.currentSequenceStep)?.pitch ?: state.exploredPitch
                NotePracticeMode.QUICK_QUIZ -> state.quizTargetPitch
            }
            val freqHz = MusicSynthesizer.midiToFrequency(activePitch.midiNote).toInt()

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (state.lastAnswerCorrect) {
                        true -> JadeHarmony.copy(alpha = 0.12f)
                        false -> MaterialTheme.colorScheme.primaryContainer
                        null -> MaterialTheme.colorScheme.surface
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (state.mode == NotePracticeMode.EXPLORE) {
                                "Đang chọn: ${activePitch.fullNameVi} • $freqHz Hz"
                            } else {
                                "Đúng: ${state.correctCount}/${state.totalAttempts} • Chuỗi: ${state.streak}"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        if (state.mode == NotePracticeMode.SEQUENCE_DRILL) {
                            Button(
                                onClick = onPlayExerciseAudio,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ConservatoryCrimson, contentColor = Color.White),
                                modifier = Modifier.height(34.dp).testTag("btn_play_sr_sequence")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Nghe Cả Câu", style = MaterialTheme.typography.labelMedium, maxLines = 1, softWrap = false)
                            }
                        }
                    }
                    Text(state.feedbackMessageVi, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // 6. 7-Note Solfège Pad
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (state.mode == NotePracticeMode.EXPLORE) "Bàn Phím 7 Nốt" else "Chọn Đúng Tên Nốt:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Đô Rê Mi Fa Sol La Si",
                            style = MaterialTheme.typography.labelMedium,
                            color = ConservatoryCrimson,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                    SolfegePianoPad(
                        selectedNote = state.lastSelectedSolfege ?: state.exploredPitch.solfege,
                        onNoteSelected = onSolfegeKeyPressed,
                        showLetterSubtitle = true,
                        showIntervalBadges = state.mode == NotePracticeMode.EXPLORE
                    )
                }
            }
        }
    }
}
