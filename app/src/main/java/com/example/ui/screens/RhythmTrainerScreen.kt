package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.MusicCurriculumRepository
import com.example.model.BeatStrength
import com.example.model.RhythmPatternExercise
import com.example.model.TimeSignatureSpec
import com.example.ui.RhythmTrainerUiState
import com.example.ui.components.MusicalStaffCanvas
import com.example.ui.theme.BrassGold
import com.example.ui.theme.ConservatoryCrimson
import com.example.ui.theme.JadeHarmony
import com.example.ui.theme.StaffLineInk

@Composable
fun RhythmTrainerScreen(
    state: RhythmTrainerUiState,
    onSelectTimeSignature: (TimeSignatureSpec) -> Unit,
    onSetBpm: (Int) -> Unit,
    onToggleConductingHand: () -> Unit,
    onSelectExercise: (RhythmPatternExercise) -> Unit,
    onToggleMetronome: () -> Unit,
    onPlayPatternDemo: () -> Unit,
    onTapBeatPad: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("rhythm_trainer_screen"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Time Signature Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Số Chỉ Nhịp & Cấu Trúc Phách (Bài 7 & 8)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TimeSignatureSpec.entries.forEach { ts ->
                        val selected = state.selectedTimeSignature == ts
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectTimeSignature(ts) },
                            label = { Text(ts.symbol, maxLines = 1, softWrap = false) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.height(34.dp).testTag("ts_chip_${ts.name}")
                        )
                    }
                }
            }
        }

        // 2. Beat Strength Visualizer & Conducting Diagram
        item {
            val ts = state.selectedTimeSignature
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(ts.readNameVi, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                            Text(ts.beatUnitVi, style = MaterialTheme.typography.labelLarge)
                        }
                        FilterChip(
                            selected = state.isRightHandConducting,
                            onClick = onToggleConductingHand,
                            label = {
                                Text(
                                    text = if (state.isRightHandConducting) "Tay Phải" else "Tay Trái",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.height(34.dp).testTag("btn_toggle_hand")
                        )
                    }

                    Text(ts.descriptionVi, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    // Beat Strength Indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ts.beatStrengths.forEachIndexed { idx, strength ->
                            val isActive = state.currentBeatIndex == idx
                            val baseColor = when (strength) {
                                BeatStrength.STRONG -> ConservatoryCrimson
                                BeatStrength.MEDIUM -> BrassGold
                                BeatStrength.WEAK -> JadeHarmony
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isActive) baseColor else baseColor.copy(alpha = 0.12f))
                                    .border(1.2.dp, baseColor, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 2.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "P.${idx + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isActive) Color.White else baseColor,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Text(
                                    text = strength.code,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActive) Color.White else baseColor,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    // Conducting Diagram + Tempo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ConductingDiagramCanvas(
                            beats = ts.conductingBeats,
                            isRightHand = state.isRightHandConducting,
                            activeBeatIndex = if (state.currentBeatIndex >= 0) state.currentBeatIndex % ts.conductingBeats else 0,
                            modifier = Modifier.size(106.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Tốc độ: ${state.bpm} BPM",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Slider(
                                value = state.bpm.toFloat(),
                                onValueChange = { onSetBpm(it.toInt()) },
                                valueRange = 48f..144f
                            )
                            Button(
                                onClick = onToggleMetronome,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (state.isMetronomeRunning) MaterialTheme.colorScheme.error else ConservatoryCrimson,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth().height(38.dp).testTag("btn_toggle_metronome")
                            ) {
                                Icon(
                                    if (state.isMetronomeRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = if (state.isMetronomeRunning) "Dừng Đếm Phách" else "Bật Đếm Phách",
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Rhythm Pattern Exercises
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Bài Tập Tiết Tấu & Đảo Phách Trên Khuông",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MusicCurriculumRepository.rhythmExercises.forEach { ex ->
                        val selected = state.selectedExercise.id == ex.id
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectExercise(ex) },
                            label = {
                                Text(
                                    text = "${ex.categoryVi}: ${ex.timeSignature.symbol}",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.height(34.dp).testTag("rhythm_ex_${ex.id}")
                        )
                    }
                }
            }
        }

        // 4. Staff Canvas & Action Buttons
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(state.selectedExercise.titleVi, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(state.selectedExercise.descriptionVi, style = MaterialTheme.typography.bodySmall)

                    MusicalStaffCanvas(
                        notes = state.selectedExercise.events,
                        timeSignature = state.selectedExercise.timeSignature,
                        activeNoteIndex = state.activeEventIndex,
                        showNoteNames = true,
                        showBeatLabels = true,
                        canvasHeight = 205.dp
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.tapFeedbackVi,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = onPlayPatternDemo,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ConservatoryCrimson, contentColor = Color.White),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("btn_play_rhythm_pattern")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Nghe Mẫu Phách",
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = onTapBeatPad,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = JadeHarmony, contentColor = Color.White),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("btn_tap_beat_pad")
                        ) {
                            Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "GÕ PHÁCH (${state.tapHitsCount})",
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConductingDiagramCanvas(
    beats: Int,
    isRightHand: Boolean,
    activeBeatIndex: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFDF9))
            .border(1.2.dp, Color(0xFFDCD3C6), RoundedCornerShape(12.dp))
            .padding(6.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f

            val points: List<Offset> = when (beats) {
                2 -> listOf(Offset(cx, h * 0.84f), Offset(cx, h * 0.18f))
                3 -> {
                    val leftX = w * 0.20f
                    val rightX = w * 0.80f
                    listOf(
                        Offset(if (isRightHand) leftX + 10f else rightX - 10f, h * 0.82f),
                        Offset(if (isRightHand) rightX else leftX, h * 0.78f),
                        Offset(cx, h * 0.16f)
                    )
                }
                else -> {
                    val leftX = w * 0.16f
                    val rightX = w * 0.84f
                    listOf(
                        Offset(cx, h * 0.86f),
                        Offset(if (isRightHand) leftX else rightX, h * 0.56f),
                        Offset(if (isRightHand) rightX else leftX, h * 0.52f),
                        Offset(cx, h * 0.14f)
                    )
                }
            }

            for (i in points.indices) {
                val p1 = points[i]
                val p2 = points[(i + 1) % points.size]
                drawLine(StaffLineInk.copy(alpha = 0.45f), p1, p2, 3f, StrokeCap.Round)
            }

            points.forEachIndexed { idx, pt ->
                val active = idx == activeBeatIndex
                drawCircle(if (active) ConservatoryCrimson else Color(0xFFE6DAC8), if (active) 14f else 10f, pt)
                val paint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    textSize = 20f
                    isFakeBoldText = true
                    color = if (active) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawContext.canvas.nativeCanvas.drawText("${idx + 1}", pt.x, pt.y + 7f, paint)
            }
        }
    }
}
