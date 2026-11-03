package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.MusicCurriculumRepository
import com.example.ui.AppTab
import com.example.ui.MusicAppViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: MusicAppViewModel = viewModel()
            val isDarkMode by vm.isDarkMode.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = isDarkMode) {
                NhacLyCanBanApp(viewModel = vm, isDarkMode = isDarkMode)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NhacLyCanBanApp(
    viewModel: MusicAppViewModel = viewModel(),
    isDarkMode: Boolean = false
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val selectedTimbre by viewModel.selectedTimbre.collectAsStateWithLifecycle()
    val activeLesson by viewModel.activeLesson.collectAsStateWithLifecycle()
    val lessonProgress by viewModel.lessonProgressList.collectAsStateWithLifecycle()
    val savedCompositions by viewModel.savedCompositions.collectAsStateWithLifecycle()
    val noteState by viewModel.noteState.collectAsStateWithLifecycle()
    val rhythmState by viewModel.rhythmState.collectAsStateWithLifecycle()
    val composeState by viewModel.composeState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "Nhạc Lý Căn Bản",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Đọc Nốt • Đọc Phách • Sáng Tác",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    OutlinedButton(
                        onClick = { viewModel.toggleDarkMode() },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .height(34.dp)
                            .testTag("btn_toggle_theme")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Chuyển giao diện Sáng / Tối",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isDarkMode) "Sáng" else "Tối",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                AppTab.entries.forEach { tab ->
                    val selected = currentTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = tabIcon(tab),
                                contentDescription = tab.labelVi
                            )
                        },
                        label = {
                            Text(
                                text = tab.labelVi,
                                fontSize = 11.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.CURRICULUM -> CurriculumScreen(
                    lessons = MusicCurriculumRepository.lessons,
                    progressList = lessonProgress,
                    activeLesson = activeLesson,
                    selectedTimbre = selectedTimbre,
                    onSelectTimbre = viewModel::selectTimbre,
                    onOpenLesson = viewModel::openLesson,
                    onPlayDemoNotes = { notes -> viewModel.playNoteEventsDemo(notes) },
                    onCompleteQuiz = viewModel::completeLessonQuiz,
                    onNavigateToPracticeTab = { idx ->
                        val target = AppTab.entries.getOrElse(idx) { AppTab.NOTES }
                        viewModel.selectTab(target)
                    }
                )

                AppTab.NOTES -> NoteReadingScreen(
                    state = noteState,
                    onSelectMode = viewModel::setNotePracticeMode,
                    onSelectClef = viewModel::setNoteClef,
                    onSelectBookGroup = viewModel::setNoteBookGroup,
                    onSelectLearningStage = viewModel::setLearningStage,
                    onToggleShowLineNumbers = viewModel::toggleShowLineNumbers,
                    onSelectExercise = viewModel::selectSightReadingExercise,
                    onStaffStepTapped = viewModel::onStaffStepTappedInExplorer,
                    onSolfegeKeyPressed = viewModel::onSolfegeKeyPressedInNoteTab,
                    onPlayExerciseAudio = viewModel::playCurrentSightReadingExercise
                )

                AppTab.RHYTHM -> RhythmTrainerScreen(
                    state = rhythmState,
                    onSelectTimeSignature = viewModel::selectRhythmTimeSignature,
                    onSetBpm = viewModel::setRhythmBpm,
                    onToggleConductingHand = viewModel::toggleConductingHand,
                    onSelectExercise = viewModel::selectRhythmExercise,
                    onToggleMetronome = viewModel::toggleMetronome,
                    onPlayPatternDemo = viewModel::playRhythmPatternDemo,
                    onTapBeatPad = viewModel::onUserTapBeatPad
                )

                AppTab.COMPOSE -> SongwritingStudioScreen(
                    state = composeState,
                    savedList = savedCompositions,
                    onUpdateTitle = viewModel::updateComposeTitle,
                    onSelectKey = viewModel::selectComposeKey,
                    onSelectTimeSignature = viewModel::selectComposeTimeSignature,
                    onSelectOctave = viewModel::selectComposeOctave,
                    onSelectDuration = viewModel::selectComposeDuration,
                    onSelectAccidental = viewModel::selectComposeAccidental,
                    onToggleRestMode = viewModel::toggleComposeRestMode,
                    onSelectChord = viewModel::selectComposeChord,
                    onUpdateLyricDraft = viewModel::updateComposeLyricDraft,
                    onSelectNoteOnStaff = viewModel::selectNoteInComposition,
                    onAddNote = viewModel::addNoteToComposition,
                    onRemoveLastNote = viewModel::removeLastComposeNote,
                    onClearNotes = viewModel::clearComposition,
                    onGenerateCadence = viewModel::generateSuggestedCadence,
                    onLoadTemplate = viewModel::loadSongTemplate,
                    onLoadSavedComposition = viewModel::loadSavedComposition,
                    onSaveComposition = viewModel::saveCurrentComposition,
                    onDeleteSavedComposition = viewModel::deleteSavedComposition,
                    onPlayComposition = viewModel::playComposition,
                    onStopPlayback = viewModel::stopCompositionPlayback
                )

                AppTab.HANDBOOK -> HandbookScreen()
            }
        }
    }
}

private fun tabIcon(tab: AppTab): ImageVector = when (tab) {
    AppTab.CURRICULUM -> Icons.AutoMirrored.Filled.MenuBook
    AppTab.NOTES -> Icons.Default.MusicNote
    AppTab.RHYTHM -> Icons.Default.GraphicEq
    AppTab.COMPOSE -> Icons.Default.EditNote
    AppTab.HANDBOOK -> Icons.Default.LibraryMusic
}
