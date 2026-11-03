package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.MusicCurriculumRepository
import com.example.data.local.SavedCompositionEntity
import com.example.model.*
import com.example.ui.ComposeStudioUiState
import com.example.ui.components.MusicalStaffCanvas
import com.example.ui.components.SolfegePianoPad
import com.example.ui.theme.ConservatoryCrimson
import com.example.ui.theme.JadeHarmony

@Composable
fun SongwritingStudioScreen(
    state: ComposeStudioUiState,
    savedList: List<SavedCompositionEntity>,
    onUpdateTitle: (String) -> Unit,
    onSelectKey: (KeyTonality) -> Unit,
    onSelectTimeSignature: (TimeSignatureSpec) -> Unit,
    onSelectOctave: (Int) -> Unit,
    onSelectDuration: (NoteDuration) -> Unit,
    onSelectAccidental: (Accidental) -> Unit,
    onToggleRestMode: () -> Unit,
    onSelectChord: (String) -> Unit,
    onUpdateLyricDraft: (String) -> Unit,
    onSelectNoteOnStaff: (Int) -> Unit,
    onAddNote: (SolfegeNote) -> Unit,
    onRemoveLastNote: () -> Unit,
    onClearNotes: () -> Unit,
    onGenerateCadence: () -> Unit,
    onLoadTemplate: (SongAnalysisTemplate) -> Unit,
    onLoadSavedComposition: (SavedCompositionEntity) -> Unit,
    onSaveComposition: () -> Unit,
    onDeleteSavedComposition: (Int) -> Unit,
    onPlayComposition: () -> Unit,
    onStopPlayback: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("songwriting_studio_screen"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Key Tonality & Primary Chords
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "1. Chọn Âm Thể (Tông) & Hợp Âm (Bài 15–19)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MusicCurriculumRepository.keyTonalities.forEach { key ->
                            val selected = state.selectedKey.id == key.id
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectKey(key) },
                                label = { Text(key.nameVi, maxLines = 1, softWrap = false) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.height(34.dp).testTag("key_chip_${key.id}")
                            )
                        }
                    }

                    Text(
                        text = "${state.selectedKey.keySignatureTextVi} • ${state.selectedKey.songwritingAdviceVi}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        "Chạm để nghe & gắn Hợp Âm vào nốt đang chọn:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        state.selectedKey.primaryChords.forEach { chord ->
                            AssistChip(
                                onClick = { onSelectChord(chord.symbol) },
                                label = {
                                    Text(
                                        text = "${chord.symbol} (${chord.notesVi})",
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                },
                                modifier = Modifier.height(34.dp).testTag("chord_chip_${chord.symbol}")
                            )
                        }
                    }
                }
            }
        }

        // 2. Staff Canvas & Playback Toolbar
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.title,
                            onValueChange = onUpdateTitle,
                            label = { Text("Tên bản nhạc") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("compose_title_input")
                        )
                        Button(
                            onClick = onSaveComposition,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ConservatoryCrimson, contentColor = Color.White),
                            modifier = Modifier.height(42.dp).testTag("btn_save_composition")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Lưu", maxLines = 1, softWrap = false)
                        }
                    }

                    MusicalStaffCanvas(
                        notes = state.notes,
                        clef = ClefType.TREBLE,
                        timeSignature = state.selectedTimeSignature,
                        keySharps = state.selectedKey.sharpsCount,
                        keyFlats = state.selectedKey.flatsCount,
                        activeNoteIndex = state.activePlaybackIndex,
                        selectedNoteIndex = state.selectedNoteIndex,
                        showNoteNames = true,
                        showLineNumbers = false,
                        showBeatLabels = true,
                        canvasHeight = 220.dp,
                        onNoteTapped = { idx, _ -> onSelectNoteOnStaff(idx) }
                    )

                    Text(
                        text = state.statusMessageVi,
                        style = MaterialTheme.typography.bodySmall,
                        color = JadeHarmony,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = if (state.isPlaying) onStopPlayback else onPlayComposition,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.isPlaying) MaterialTheme.colorScheme.error else ConservatoryCrimson,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("btn_play_composition")
                        ) {
                            Icon(
                                if (state.isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (state.isPlaying) "Dừng Phát" else "Phát Giai Điệu",
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        OutlinedButton(
                            onClick = onGenerateCadence,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("btn_generate_cadence")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Câu Mẫu I-IV-V-I",
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 3. Note Duration, Accidental, Lyric & Solfège Input Pad
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "2. Chọn Hình Nốt (Trường Độ), Dấu Hóa & Lời Ca",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        NoteDuration.entries.forEach { dur ->
                            val selected = state.selectedDuration == dur
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectDuration(dur) },
                                label = { Text(dur.nameVi, maxLines = 1, softWrap = false) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.height(34.dp).testTag("dur_chip_${dur.name}")
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = state.selectedOctave == 4,
                            onClick = { onSelectOctave(4) },
                            label = { Text("Quãng 4", maxLines = 1, softWrap = false) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.height(34.dp)
                        )
                        FilterChip(
                            selected = state.selectedOctave == 5,
                            onClick = { onSelectOctave(5) },
                            label = { Text("Quãng 5", maxLines = 1, softWrap = false) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.height(34.dp)
                        )
                        FilterChip(
                            selected = state.isRestMode,
                            onClick = onToggleRestMode,
                            label = { Text("Dấu Lặng", maxLines = 1, softWrap = false) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.height(34.dp).testTag("chip_rest_mode")
                        )
                        Accidental.entries.forEach { acc ->
                            val selected = state.selectedAccidental == acc
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectAccidental(acc) },
                                label = {
                                    Text(
                                        text = if (acc == Accidental.NONE) "Tự nhiên" else "${acc.symbol} ${acc.nameVi}",
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.height(34.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.lyricDraft,
                            onValueChange = onUpdateLyricDraft,
                            label = { Text("Lời ca dưới nốt đang chọn") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("compose_lyric_input")
                        )
                        IconButton(onClick = onRemoveLastNote, modifier = Modifier.testTag("btn_remove_last_note")) {
                            Icon(Icons.Default.Backspace, contentDescription = "Xóa nốt cuối")
                        }
                        IconButton(onClick = onClearNotes, modifier = Modifier.testTag("btn_clear_composition")) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Xóa tất cả")
                        }
                    }

                    Text(
                        "Chạm phím Đô – Si để thêm nốt vào bản nhạc:",
                        style = MaterialTheme.typography.labelLarge,
                        color = ConservatoryCrimson
                    )
                    SolfegePianoPad(
                        selectedNote = state.selectedKey.tonicSolfege,
                        onNoteSelected = onAddNote,
                        showLetterSubtitle = true,
                        showIntervalBadges = false
                    )
                }
            }
        }

        // 4. Templates & Saved Songs
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "3. Tác Phẩm Mẫu Trong Sách & Bản Lưu Của Bạn",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    MusicCurriculumRepository.songTemplates.forEach { tpl ->
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLoadTemplate(tpl) }
                                .testTag("template_${tpl.id}")
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = tpl.titleVi,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Nạp mẫu →",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = ConservatoryCrimson,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                                Text("Hòa âm: ${tpl.chordProgressionSummary}", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    if (savedList.isNotEmpty()) {
                        HorizontalDivider()
                        Text("Bản Nhạc Đã Lưu:", style = MaterialTheme.typography.labelLarge)
                        savedList.forEach { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onLoadSavedComposition(item) }
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text("Tông: ${item.keyTonalityId} • ${item.bpm} BPM", style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { onDeleteSavedComposition(item.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Xóa")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
