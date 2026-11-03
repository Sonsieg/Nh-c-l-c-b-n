package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SolfegeNote

@Composable
fun SolfegePianoPad(
    selectedNote: SolfegeNote?,
    onNoteSelected: (SolfegeNote) -> Unit,
    showLetterSubtitle: Boolean = true,
    showIntervalBadges: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (showIntervalBadges) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val steps = listOf("1 cung", "1 cung", "1/2c", "1 cung", "1 cung", "1 cung")
                steps.forEach { stepLabel ->
                    val isHalf = stepLabel.contains("1/2")
                    Surface(
                        color = if (isHalf) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = stepLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isHalf) Color.White else Color(0xFF111111),
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().height(92.dp).testTag("solfege_piano_pad"),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SolfegeNote.entries.forEach { note ->
                val isSelected = selectedNote == note
                val keyBg = if (isSelected) note.color else Color(0xFFFFFDF9)
                val textColor = if (isSelected) Color.White else Color(0xFF111111)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 10.dp, bottomEnd = 10.dp))
                        .background(keyBg)
                        .border(
                            width = if (isSelected) 2.dp else 1.2.dp,
                            color = if (isSelected) note.color else Color(0xFFC8BBAE),
                            shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 10.dp, bottomEnd = 10.dp)
                        )
                        .clickable { onNoteSelected(note) }
                        .testTag("piano_key_${note.name}")
                        .padding(horizontal = 2.dp, vertical = 6.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White else note.color)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = note.nameVi,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            fontSize = 13.5.sp,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip
                        )
                        if (showLetterSubtitle) {
                            Text(
                                text = note.letter,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White.copy(alpha = 0.92f) else Color(0xFF222222),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }
    }
}
