package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun MusicalStaffCanvas(
    notes: List<NoteEvent>,
    clef: ClefType = ClefType.TREBLE,
    timeSignature: TimeSignatureSpec? = null,
    keySharps: Int = 0,
    keyFlats: Int = 0,
    activeNoteIndex: Int = -1,
    selectedNoteIndex: Int = -1,
    showNoteNames: Boolean = true,
    showLineNumbers: Boolean = false,
    showBeatLabels: Boolean = true,
    colorCodeNotes: Boolean = true,
    canvasHeight: Dp = 220.dp,
    modifier: Modifier = Modifier,
    onNoteTapped: ((Int, NoteEvent) -> Unit)? = null,
    onStaffStepTapped: ((Int) -> Unit)? = null
) {
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(canvasHeight)
            .clip(RoundedCornerShape(16.dp))
            .background(ParchmentBackground)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("musical_staff_canvas")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(notes, clef, onNoteTapped, onStaffStepTapped) {
                    detectTapGestures { tapOffset ->
                        val h = size.height.toFloat()
                        val w = size.width.toFloat()
                        val lineSpacing = (h * 0.40f) / 4f
                        val line1Y = h * 0.67f
                        val stepHeight = lineSpacing / 2f

                        if (onStaffStepTapped != null) {
                            val stepFloat = 2f + (line1Y - tapOffset.y) / stepHeight
                            onStaffStepTapped(stepFloat.toInt().coerceIn(0, 14))
                        } else if (onNoteTapped != null && notes.isNotEmpty()) {
                            val headerReserve = 90f + (if (timeSignature != null) 30f else 0f)
                            val usableW = (w - headerReserve - 20f).coerceAtLeast(100f)
                            val spacing = usableW / notes.size.coerceAtLeast(1)
                            val idx = ((tapOffset.x - headerReserve) / spacing).toInt().coerceIn(0, notes.lastIndex)
                            onNoteTapped(idx, notes[idx])
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height
            val lineSpacing = (h * 0.40f) / 4f
            val stepHeight = lineSpacing / 2f
            val line1Y = h * 0.67f
            val line5Y = line1Y - 4 * lineSpacing

            fun yForStaffStep(step: Int): Float = line1Y - (step - 2) * stepHeight

            // 1. 5 horizontal lines (Hàng 1..5 từ dưới lên)
            for (line in 1..5) {
                val y = line1Y - (line - 1) * lineSpacing
                val isAnchor = (clef == ClefType.TREBLE && line == 2) || (clef == ClefType.BASS && line == 4)
                drawLine(
                    color = if (isAnchor && showLineNumbers) ConservatoryCrimson.copy(alpha = 0.65f) else StaffLineInk,
                    start = Offset(14f, y),
                    end = Offset(w - 14f, y),
                    strokeWidth = if (isAnchor && showLineNumbers) 2.5f else 1.8f
                )
                if (showLineNumbers) {
                    drawNativeText("H$line", 18f, y + 5f, 20f, if (isAnchor) ConservatoryCrimson else StaffLineInk, isAnchor, false)
                }
            }

            // Double barline at end
            drawLine(StaffLineInk, Offset(14f, line5Y), Offset(14f, line1Y), 2.4f)
            drawLine(StaffLineInk, Offset(w - 21f, line5Y), Offset(w - 21f, line1Y), 1.8f)
            drawLine(StaffLineInk, Offset(w - 15f, line5Y), Offset(w - 15f, line1Y), 4.8f)

            var cursorX = if (showLineNumbers) 54f else 34f
            drawClefSymbol(clef, cursorX, line1Y, lineSpacing)
            cursorX += 40f

            if (keySharps > 0) {
                val sharpSteps = listOf(10, 7, 11, 8, 5)
                for (i in 0 until keySharps.coerceAtMost(sharpSteps.size)) {
                    drawNativeText("♯", cursorX, yForStaffStep(sharpSteps[i]) + 8f, lineSpacing * 1.4f, ConservatoryCrimson, true)
                    cursorX += 13f
                }
            } else if (keyFlats > 0) {
                val flatSteps = listOf(6, 9, 5, 8, 4)
                for (i in 0 until keyFlats.coerceAtMost(flatSteps.size)) {
                    drawNativeText("♭", cursorX, yForStaffStep(flatSteps[i]) + 8f, lineSpacing * 1.4f, ConservatoryCrimson, true)
                    cursorX += 13f
                }
            }

            if (timeSignature != null) {
                cursorX += 8f
                if (timeSignature == TimeSignatureSpec.TWO_TWO) {
                    drawNativeText("₵", cursorX + 5f, line1Y - 1.3f * lineSpacing, lineSpacing * 2.1f, StaffLineInk, true)
                } else {
                    drawNativeText(timeSignature.numerator.toString(), cursorX + 5f, line1Y - 2.3f * lineSpacing, lineSpacing * 1.6f, StaffLineInk, true)
                    drawNativeText(timeSignature.denominator.toString(), cursorX + 5f, line1Y - 0.3f * lineSpacing, lineSpacing * 1.6f, StaffLineInk, true)
                }
                cursorX += 24f
            }

            if (notes.isEmpty()) return@Canvas
            val availableW = (w - cursorX - 26f).coerceAtLeast(80f)
            val noteSpacing = availableW / notes.size.coerceAtLeast(1)
            val rx = (lineSpacing * 0.52f).coerceIn(7f, 14f)
            val ry = (lineSpacing * 0.39f).coerceIn(5.5f, 11f)

            notes.forEachIndexed { index, event ->
                val noteX = cursorX + index * noteSpacing + noteSpacing * 0.5f
                val noteY = yForStaffStep(event.pitch.staffStep)
                val isActive = index == activeNoteIndex
                val isSelected = index == selectedNoteIndex

                if (isActive || isSelected) {
                    drawRoundRect(
                        color = if (isActive) BrassGold.copy(alpha = 0.24f) else ConservatoryCrimson.copy(alpha = 0.14f),
                        topLeft = Offset(noteX - noteSpacing * 0.42f, 6f),
                        size = Size(noteSpacing * 0.84f, h - 12f),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                }

                if (event.chordSymbol.isNotBlank()) {
                    drawNativeText(event.chordSymbol, noteX, line5Y - lineSpacing * 1.3f, 25f, ConservatoryCrimson, true)
                }

                if (event.isRest) {
                    drawMusicalRest(event.duration, noteX, line1Y, lineSpacing)
                } else {
                    if (event.pitch.staffStep <= 0) {
                        for (s in 0 downTo event.pitch.staffStep step 2) {
                            val ly = yForStaffStep(s)
                            drawLine(StaffLineInk, Offset(noteX - rx * 1.6f, ly), Offset(noteX + rx * 1.6f, ly), 2.2f)
                        }
                    }
                    if (event.pitch.staffStep >= 12) {
                        for (s in 12..event.pitch.staffStep step 2) {
                            val ly = yForStaffStep(s)
                            drawLine(StaffLineInk, Offset(noteX - rx * 1.6f, ly), Offset(noteX + rx * 1.6f, ly), 2.2f)
                        }
                    }

                    val noteColor = when {
                        isActive -> ConservatoryCrimson
                        colorCodeNotes -> event.pitch.solfege.color
                        else -> StaffLineInk
                    }

                    if (event.accidental != Accidental.NONE) {
                        drawNativeText(event.accidental.symbol, noteX - rx * 1.9f, noteY + ry * 0.8f, lineSpacing * 1.35f, ConservatoryCrimson, true)
                    }

                    if (event.duration.isHollow) {
                        drawOval(noteColor, Offset(noteX - rx, noteY - ry), Size(rx * 2f, ry * 2f), style = Stroke(3.2f))
                    } else {
                        drawOval(noteColor, Offset(noteX - rx, noteY - ry), Size(rx * 2f, ry * 2f))
                    }

                    if (event.duration.hasStem) {
                        val stemLen = lineSpacing * 3.0f
                        val down = event.pitch.stemPointsDown
                        val stemX = if (down) noteX - rx + 1.5f else noteX + rx - 1.5f
                        val stemEndY = if (down) noteY + stemLen else noteY - stemLen
                        drawLine(noteColor, Offset(stemX, noteY), Offset(stemX, stemEndY), 2.4f, StrokeCap.Round)

                        for (f in 0 until event.duration.flagCount) {
                            val off = f * (lineSpacing * 0.6f)
                            val sy = if (down) stemEndY - off else stemEndY + off
                            val ty = if (down) sy - lineSpacing * 0.85f else sy + lineSpacing * 0.85f
                            val path = Path().apply {
                                moveTo(stemX, sy)
                                quadraticTo(stemX + rx * 1.5f, (sy + ty) / 2f, stemX + rx, ty)
                            }
                            drawPath(path, noteColor, style = Stroke(2.4f, cap = StrokeCap.Round))
                        }
                    }

                    if (event.duration.isDotted) {
                        drawCircle(noteColor, 3.5f, Offset(noteX + rx * 1.55f, noteY - 2f))
                    }
                }

                if (showBeatLabels && event.beatLabel.isNotBlank()) {
                    drawNativeText(event.beatLabel, noteX, h - 29f, 19f, StaffLineInk.copy(alpha = 0.8f), false)
                }

                val caption = when {
                    event.lyric.isNotBlank() -> event.lyric
                    showNoteNames && !event.isRest -> "${event.pitch.solfege.nameVi}${event.accidental.symbol}"
                    showNoteNames && event.isRest -> "Nghỉ"
                    else -> ""
                }
                if (caption.isNotBlank()) {
                    drawNativeText(caption, noteX, h - 8f, 23f, if (isActive) ConservatoryCrimson else JadeHarmony, true)
                }
            }
        }
    }
}

private fun DrawScope.drawClefSymbol(clef: ClefType, x: Float, line1Y: Float, lineSpacing: Float) {
    if (clef == ClefType.TREBLE) {
        val line2Y = line1Y - lineSpacing
        val line5Y = line1Y - 4 * lineSpacing
        val path = Path().apply {
            moveTo(x - 2f, line1Y + lineSpacing * 0.8f)
            lineTo(x + 2f, line5Y - lineSpacing * 0.7f)
            cubicTo(x + 11f, line5Y, x + 9f, line5Y + lineSpacing * 1.3f, x - 2f, line1Y - 2f * lineSpacing)
            cubicTo(x - 15f, line1Y - lineSpacing, x - 13f, line1Y + lineSpacing * 0.2f, x + 2f, line1Y + lineSpacing * 0.1f)
            cubicTo(x + 14f, line1Y - lineSpacing * 0.1f, x + 12f, line2Y - lineSpacing * 0.6f, x, line2Y)
        }
        drawPath(path, StaffLineInk, style = Stroke(3.0f, cap = StrokeCap.Round))
        drawCircle(ConservatoryCrimson, 4.2f, Offset(x, line2Y))
    } else {
        val line4Y = line1Y - 3 * lineSpacing
        drawCircle(ConservatoryCrimson, 4.8f, Offset(x - 5f, line4Y))
        val path = Path().apply {
            moveTo(x - 5f, line4Y)
            cubicTo(x - 5f, line4Y - lineSpacing, x + 11f, line4Y - lineSpacing, x + 10f, line4Y + lineSpacing * 0.3f)
            cubicTo(x + 9f, line4Y + lineSpacing * 1.4f, x - 2f, line1Y - lineSpacing * 0.3f, x - 9f, line1Y)
        }
        drawPath(path, StaffLineInk, style = Stroke(3.2f, cap = StrokeCap.Round))
        drawCircle(StaffLineInk, 2.8f, Offset(x + 16f, line4Y - lineSpacing * 0.4f))
        drawCircle(StaffLineInk, 2.8f, Offset(x + 16f, line4Y + lineSpacing * 0.4f))
    }
}

private fun DrawScope.drawMusicalRest(duration: NoteDuration, x: Float, line1Y: Float, lineSpacing: Float) {
    val line3Y = line1Y - 2f * lineSpacing
    val line4Y = line1Y - 3f * lineSpacing
    when (duration) {
        NoteDuration.WHOLE -> drawRect(StaffLineInk, Offset(x - 10f, line4Y + 1f), Size(20f, lineSpacing * 0.45f))
        NoteDuration.HALF, NoteDuration.DOTTED_HALF -> {
            val bh = lineSpacing * 0.45f
            drawRect(StaffLineInk, Offset(x - 10f, line3Y - bh), Size(20f, bh))
        }
        else -> {
            drawLine(StaffLineInk, Offset(x - 3f, line4Y), Offset(x + 4f, line3Y), 3.0f)
            drawLine(StaffLineInk, Offset(x + 4f, line3Y), Offset(x - 3f, line1Y - lineSpacing * 0.6f), 3.0f)
        }
    }
}

private fun DrawScope.drawNativeText(
    text: String,
    x: Float,
    y: Float,
    textSizePx: Float,
    color: Color,
    bold: Boolean = false,
    alignCenter: Boolean = true
) {
    val paint = android.graphics.Paint().apply {
        isAntiAlias = true
        textSize = textSizePx
        this.color = android.graphics.Color.argb(
            (color.alpha * 255).toInt(),
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt()
        )
        isFakeBoldText = bold
        textAlign = if (alignCenter) android.graphics.Paint.Align.CENTER else android.graphics.Paint.Align.LEFT
    }
    drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)
}
