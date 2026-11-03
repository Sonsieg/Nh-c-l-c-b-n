package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.NoteDoColor
import com.example.ui.theme.NoteFaColor
import com.example.ui.theme.NoteLaColor
import com.example.ui.theme.NoteMiColor
import com.example.ui.theme.NoteReColor
import com.example.ui.theme.NoteSiColor
import com.example.ui.theme.NoteSolColor

enum class ClefType(
    val titleVi: String,
    val subtitleVi: String,
    val anchorLineText: String
) {
    TREBLE(
        titleVi = "Khóa Sol (G)",
        subtitleVi = "Dùng cho giọng cao, tay phải Piano, Violon, Guitar, Sáo",
        anchorLineText = "Đặt trên Hàng thứ 2: Nốt nằm trên hàng thứ 2 là nốt Sol (G4)"
    ),
    BASS(
        titleVi = "Khóa Fa (F)",
        subtitleVi = "Dùng cho giọng trầm, tay trái Piano, Cello, Contrebasse",
        anchorLineText = "Đặt trên Hàng thứ 4: Nốt nằm trên hàng thứ 4 là nốt Fa (F3)"
    )
}

enum class SolfegeNote(
    val nameVi: String,
    val letter: String,
    val semitoneOffset: Int,
    val diatonicIndex: Int,
    val color: Color,
    val intervalToNextVi: String
) {
    DO("Đô", "C", 0, 0, NoteDoColor, "Đô → Rê: 1 cung"),
    RE("Rê", "D", 2, 1, NoteReColor, "Rê → Mi: 1 cung"),
    MI("Mi", "E", 4, 2, NoteMiColor, "Mi → Fa: 1/2 cung (Nửa cung tự nhiên)"),
    FA("Fa", "F", 5, 3, NoteFaColor, "Fa → Sol: 1 cung"),
    SOL("Sol", "G", 7, 4, NoteSolColor, "Sol → La: 1 cung"),
    LA("La", "A", 9, 5, NoteLaColor, "La → Si: 1 cung"),
    SI("Si", "B", 11, 6, NoteSiColor, "Si → Đô: 1/2 cung (Nửa cung tự nhiên)")
}

enum class Accidental(
    val symbol: String,
    val nameVi: String,
    val semitoneDelta: Int
) {
    NONE("", "Bình thường", 0),
    SHARP("♯", "Dấu Thăng (+1/2c)", 1),
    FLAT("♭", "Dấu Giáng (-1/2c)", -1),
    NATURAL("♮", "Dấu Bình", 0)
}

enum class NoteDuration(
    val nameVi: String,
    val restNameVi: String,
    val beatsInQuarter: Float,
    val flagCount: Int = 0,
    val isHollow: Boolean = false,
    val hasStem: Boolean = true,
    val isDotted: Boolean = false
) {
    WHOLE("Nốt Tròn (4p)", "Lặng Tròn", 4.0f, isHollow = true, hasStem = false),
    DOTTED_HALF("Trắng Chấm (3p)", "Lặng Trắng Chấm", 3.0f, isHollow = true, hasStem = true, isDotted = true),
    HALF("Nốt Trắng (2p)", "Lặng Trắng", 2.0f, isHollow = true, hasStem = true),
    DOTTED_QUARTER("Đen Chấm (1.5p)", "Lặng Đen Chấm", 1.5f, isHollow = false, hasStem = true, isDotted = true),
    QUARTER("Nốt Đen (1p)", "Lặng Đen", 1.0f, isHollow = false, hasStem = true),
    EIGHTH("Móc Đơn (1/2p)", "Lặng Móc Đơn", 0.5f, flagCount = 1, isHollow = false, hasStem = true),
    SIXTEENTH("Móc Đôi (1/4p)", "Lặng Móc Đôi", 0.25f, flagCount = 2, isHollow = false, hasStem = true)
}

data class StaffPitch(
    val clef: ClefType,
    val staffStep: Int, // 0..14 (step 2 = Hàng 1, step 4 = Hàng 2, step 6 = Hàng 3, step 8 = Hàng 4, step 10 = Hàng 5)
    val solfege: SolfegeNote,
    val octave: Int,
    val midiNote: Int,
    val positionDescriptionVi: String,
    val bookGroup: Int
) {
    val fullNameVi: String
        get() = "${solfege.nameVi}$octave (${solfege.letter}$octave)"

    val stemPointsDown: Boolean
        get() = staffStep >= 6
}

data class NoteEvent(
    val id: String,
    val pitch: StaffPitch,
    val duration: NoteDuration = NoteDuration.QUARTER,
    val accidental: Accidental = Accidental.NONE,
    val isRest: Boolean = false,
    val tiedToNext: Boolean = false,
    val hasFermata: Boolean = false,
    val isAccented: Boolean = false,
    val lyric: String = "",
    val chordSymbol: String = "",
    val beatLabel: String = ""
) {
    val effectiveMidi: Int
        get() = pitch.midiNote + accidental.semitoneDelta
}

enum class BeatStrength(val code: String, val labelVi: String) {
    STRONG("M", "Phách Mạnh"),
    MEDIUM("KM", "Khá Mạnh"),
    WEAK("N", "Phách Nhẹ")
}

enum class TimeSignatureSpec(
    val numerator: Int,
    val denominator: Int,
    val symbol: String,
    val readNameVi: String,
    val beatUnitVi: String,
    val beatStrengths: List<BeatStrength>,
    val conductingBeats: Int,
    val descriptionVi: String
) {
    TWO_FOUR(
        2, 4, "2/4", "Nhịp Hai - Bốn", "Mỗi phách = 1 nốt Đen",
        listOf(BeatStrength.STRONG, BeatStrength.WEAK), 2,
        "2 phách/ô nhịp: Phách 1 Mạnh (M), Phách 2 Nhẹ (N)."
    ),
    THREE_FOUR(
        3, 4, "3/4", "Nhịp Ba - Bốn (Waltz)", "Mỗi phách = 1 nốt Đen",
        listOf(BeatStrength.STRONG, BeatStrength.WEAK, BeatStrength.WEAK), 3,
        "3 phách/ô nhịp: Phách 1 Mạnh (M), Phách 2 & 3 Nhẹ (N)."
    ),
    FOUR_FOUR(
        4, 4, "4/4 (C)", "Nhịp Bốn - Bốn (C)", "Mỗi phách = 1 nốt Đen",
        listOf(BeatStrength.STRONG, BeatStrength.WEAK, BeatStrength.MEDIUM, BeatStrength.WEAK), 4,
        "4 phách/ô nhịp: 1 Mạnh – 2 Nhẹ – 3 Khá Mạnh – 4 Nhẹ."
    ),
    TWO_TWO(
        2, 2, "2/2 (₵)", "Nhịp Hai - Hai (Xê Chẻ)", "Mỗi phách = 1 nốt Trắng",
        listOf(BeatStrength.STRONG, BeatStrength.WEAK), 2,
        "Viết tắt ₵: 2 phách/ô nhịp, mỗi phách là 1 nốt Trắng (= 2 nốt Đen)."
    ),
    SIX_EIGHT(
        6, 8, "6/8", "Nhịp Sáu - Tám", "Mỗi phách = 1 nốt Móc đơn",
        listOf(
            BeatStrength.STRONG, BeatStrength.WEAK, BeatStrength.WEAK,
            BeatStrength.MEDIUM, BeatStrength.WEAK, BeatStrength.WEAK
        ), 2,
        "6 phách móc đơn/ô nhịp, chia 2 nhóm 3 móc đơn (nhấn phách 1 và 4)."
    )
}

data class ChordDefinition(
    val symbol: String,
    val nameVi: String,
    val categoryVi: String,
    val formulaVi: String,
    val notesVi: String,
    val midiNotes: List<Int>,
    val moodVi: String
)

data class KeyTonality(
    val id: String,
    val symbol: String,
    val nameVi: String,
    val isMajor: Boolean,
    val keySignatureTextVi: String,
    val sharpsCount: Int,
    val flatsCount: Int,
    val tonicSolfege: SolfegeNote,
    val scaleNotesVi: List<String>,
    val scaleMidiNotes: List<Int>,
    val primaryChords: List<ChordDefinition>,
    val songwritingProgression: List<String>,
    val songwritingAdviceVi: String
)
