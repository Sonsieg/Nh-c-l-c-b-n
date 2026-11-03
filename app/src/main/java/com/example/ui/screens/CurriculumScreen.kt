package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.audio.InstrumentTimbre
import com.example.data.local.LessonProgressEntity
import com.example.model.CurriculumStage
import com.example.model.MusicLesson
import com.example.model.NoteEvent
import com.example.ui.components.MusicalStaffCanvas
import com.example.ui.theme.ConservatoryCrimson
import com.example.ui.theme.JadeHarmony

@Composable
fun CurriculumScreen(
    lessons: List<MusicLesson>,
    progressList: List<LessonProgressEntity>,
    activeLesson: MusicLesson?,
    selectedTimbre: InstrumentTimbre,
    onSelectTimbre: (InstrumentTimbre) -> Unit,
    onOpenLesson: (MusicLesson?) -> Unit,
    onPlayDemoNotes: (List<NoteEvent>) -> Unit,
    onCompleteQuiz: (Int, Int, Int) -> Unit,
    onNavigateToPracticeTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeLesson != null) {
        LessonDetailContent(
            lesson = activeLesson,
            onBack = { onOpenLesson(null) },
            onPlayDemoNotes = onPlayDemoNotes,
            onCompleteQuiz = { score, total -> onCompleteQuiz(activeLesson.id, score, total) },
            modifier = modifier
        )
        return
    }

    var selectedStage by remember { mutableStateOf<CurriculumStage?>(null) }
    val filtered = remember(lessons, selectedStage) {
        if (selectedStage == null) lessons else lessons.filter { it.stage == selectedStage }
    }
    val completedCount = progressList.count { it.isCompleted }

    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("curriculum_list"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFFC62828), Color(0xFF8E1414))))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "GIÁO TRÌNH NGÔ NGỌC THẮNG • HOÀN THÀNH $completedCount/${lessons.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFFFF8F0),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Nhạc Lý Căn Bản & Sáng Tác",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                    Text(
                        text = "Đọc nốt Đô Rê Mi Fa Sol La Si, nắm vững phách nhịp và tự tay phối hợp âm sáng tác bài hát.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFFFF3E8)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { onNavigateToPracticeTab(1) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFFDF9),
                                contentColor = Color(0xFF111111)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("hero_btn_read_notes")
                        ) {
                            Text(
                                text = "Luyện Đọc Nốt",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Button(
                            onClick = { onNavigateToPracticeTab(2) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFEBEE),
                                contentColor = Color(0xFF111111)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("hero_btn_read_beats")
                        ) {
                            Text(
                                text = "Luyện Đọc Phách",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                InstrumentTimbre.entries.forEach { timbre ->
                    val selected = timbre == selectedTimbre
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectTimbre(timbre) },
                        label = {
                            Text(
                                text = "Âm sắc: ${timbre.labelVi.substringBefore(" (")}",
                                maxLines = 1,
                                softWrap = false
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.height(34.dp).testTag("timbre_chip_${timbre.name}")
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedStage == null,
                    onClick = { selectedStage = null },
                    label = { Text("Tất cả", maxLines = 1, softWrap = false) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.height(34.dp).testTag("stage_filter_all")
                )
                CurriculumStage.entries.forEach { stage ->
                    val selected = selectedStage == stage
                    FilterChip(
                        selected = selected,
                        onClick = { selectedStage = if (selected) null else stage },
                        label = { Text("Chặng ${stage.stageNumber}", maxLines = 1, softWrap = false) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.height(34.dp).testTag("stage_filter_${stage.stageNumber}")
                    )
                }
            }
        }

        items(filtered, key = { it.id }) { lesson ->
            val prog = progressList.firstOrNull { it.lessonId == lesson.id }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                    .clickable { onOpenLesson(lesson) }
                    .testTag("lesson_card_${lesson.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${lesson.lessonNumberLabel} • Trang ${lesson.bookPageStart}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        if (prog?.isCompleted == true) {
                            Text(
                                text = "✓ Đạt ${prog.quizScore}/${prog.quizTotal}",
                                style = MaterialTheme.typography.labelMedium,
                                color = JadeHarmony,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = lesson.titleVi,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = lesson.summaryVi,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonDetailContent(
    lesson: MusicLesson,
    onBack: () -> Unit,
    onPlayDemoNotes: (List<NoteEvent>) -> Unit,
    onCompleteQuiz: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val selectedAnswers = remember(lesson.id) { mutableStateMapOf<String, Int>() }
    var submitted by remember(lesson.id) { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("lesson_detail_screen"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_from_lesson")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                }
                Column {
                    Text(
                        "${lesson.lessonNumberLabel} • Sách trang ${lesson.bookPageStart}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        lesson.titleVi,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        items(lesson.sections) { sec ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(sec.headingVi, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Text(sec.bodyVi, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    if (sec.highlightFormula != null) {
                        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)) {
                            Text(
                                sec.highlightFormula,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                    if (sec.demoNotes.isNotEmpty()) {
                        MusicalStaffCanvas(
                            notes = sec.demoNotes,
                            clef = sec.demoClef,
                            timeSignature = sec.demoTimeSignature,
                            showNoteNames = true,
                            showLineNumbers = true,
                            canvasHeight = 195.dp,
                            onNoteTapped = { _, n -> onPlayDemoNotes(listOf(n)) }
                        )
                        Button(
                            onClick = { onPlayDemoNotes(sec.demoNotes) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ConservatoryCrimson, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(38.dp).testTag("btn_play_lesson_demo")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Nghe Minh Họa Khuông Nhạc",
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Ứng Dụng Sáng Tác Ca Khúc",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        lesson.songwriterTakeawayVi,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        if (lesson.quizQuestions.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Bài Tập Thực Hành Trắc Nghiệm", style = MaterialTheme.typography.titleLarge)
                        lesson.quizQuestions.forEachIndexed { qIdx, q ->
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Câu ${qIdx + 1}: ${q.questionVi}", style = MaterialTheme.typography.titleMedium)
                                q.optionsVi.forEachIndexed { oIdx, opt ->
                                    val chosen = selectedAnswers[q.id] == oIdx
                                    val borderCol = when {
                                        submitted && oIdx == q.correctIndex -> JadeHarmony
                                        submitted && chosen -> MaterialTheme.colorScheme.error
                                        chosen -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.outlineVariant
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (chosen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background)
                                            .border(1.5.dp, borderCol, RoundedCornerShape(8.dp))
                                            .clickable { selectedAnswers[q.id] = oIdx }
                                            .padding(horizontal = 10.dp, vertical = 8.dp)
                                            .testTag("quiz_opt_${q.id}_$oIdx")
                                    ) {
                                        Text("${('a' + oIdx)}. $opt", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                                if (submitted) {
                                    Text(q.explanationVi, style = MaterialTheme.typography.bodySmall, color = JadeHarmony)
                                }
                            }
                        }
                        Button(
                            onClick = {
                                submitted = true
                                val score = lesson.quizQuestions.count { selectedAnswers[it.id] == it.correctIndex }
                                onCompleteQuiz(score, lesson.quizQuestions.size)
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(38.dp).testTag("btn_submit_lesson_quiz")
                        ) {
                            Text(
                                text = if (submitted) "Đã Chấm Điểm & Lưu Tiến Độ" else "Kiểm Tra Đáp Án",
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
