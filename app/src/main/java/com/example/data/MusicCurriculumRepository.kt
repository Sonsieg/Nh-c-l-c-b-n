package com.example.data

import com.example.model.*

object MusicCurriculumRepository {

    val treblePitches: List<StaffPitch> = listOf(
        StaffPitch(ClefType.TREBLE, 0, SolfegeNote.DO, 4, 60, "Dòng kẻ phụ thứ 1 dưới khuông nhạc", 1),
        StaffPitch(ClefType.TREBLE, 1, SolfegeNote.RE, 4, 62, "Khe phụ thứ 1 dưới hàng 1", 1),
        StaffPitch(ClefType.TREBLE, 2, SolfegeNote.MI, 4, 64, "Hàng thứ 1 (dưới cùng)", 1),
        StaffPitch(ClefType.TREBLE, 3, SolfegeNote.FA, 4, 65, "Khe thứ 1 (giữa hàng 1 và 2)", 1),
        StaffPitch(ClefType.TREBLE, 4, SolfegeNote.SOL, 4, 67, "Hàng thứ 2 (Nốt chuẩn Khóa Sol)", 1),
        StaffPitch(ClefType.TREBLE, 5, SolfegeNote.LA, 4, 69, "Khe thứ 2 (giữa hàng 2 và 3)", 2),
        StaffPitch(ClefType.TREBLE, 6, SolfegeNote.SI, 4, 71, "Hàng thứ 3 (đuôi nốt bắt đầu quay xuống)", 2),
        StaffPitch(ClefType.TREBLE, 7, SolfegeNote.DO, 5, 72, "Khe thứ 3 (giữa hàng 3 và 4)", 3),
        StaffPitch(ClefType.TREBLE, 8, SolfegeNote.RE, 5, 74, "Hàng thứ 4 (từ dưới đếm lên)", 3),
        StaffPitch(ClefType.TREBLE, 9, SolfegeNote.MI, 5, 76, "Khe thứ 4 (giữa hàng 4 và 5)", 3),
        StaffPitch(ClefType.TREBLE, 10, SolfegeNote.FA, 5, 77, "Hàng thứ 5 (trên cùng khuông chính)", 3),
        StaffPitch(ClefType.TREBLE, 11, SolfegeNote.SOL, 5, 79, "Khe phụ thứ 1 phía trên hàng 5", 4),
        StaffPitch(ClefType.TREBLE, 12, SolfegeNote.LA, 5, 81, "Hàng phụ thứ 1 phía trên khuông nhạc", 4),
        StaffPitch(ClefType.TREBLE, 13, SolfegeNote.SI, 5, 83, "Khe phụ thứ 2 phía trên khuông nhạc", 4),
        StaffPitch(ClefType.TREBLE, 14, SolfegeNote.DO, 6, 84, "Hàng phụ thứ 2 phía trên khuông nhạc", 4)
    )

    val bassPitches: List<StaffPitch> = listOf(
        StaffPitch(ClefType.BASS, 2, SolfegeNote.SOL, 2, 43, "Hàng thứ 1 (Khóa Fa)", 1),
        StaffPitch(ClefType.BASS, 3, SolfegeNote.LA, 2, 45, "Khe thứ 1 (Khóa Fa)", 1),
        StaffPitch(ClefType.BASS, 4, SolfegeNote.SI, 2, 47, "Hàng thứ 2 (Khóa Fa)", 1),
        StaffPitch(ClefType.BASS, 5, SolfegeNote.DO, 3, 48, "Khe thứ 2 (Đô trầm - Nhóm 1 Khóa Fa)", 1),
        StaffPitch(ClefType.BASS, 6, SolfegeNote.RE, 3, 50, "Hàng thứ 3 (Khóa Fa)", 1),
        StaffPitch(ClefType.BASS, 7, SolfegeNote.MI, 3, 52, "Khe thứ 3 (Khóa Fa)", 1),
        StaffPitch(ClefType.BASS, 8, SolfegeNote.FA, 3, 53, "Hàng thứ 4 (Nốt chuẩn Khóa Fa)", 1),
        StaffPitch(ClefType.BASS, 9, SolfegeNote.SOL, 3, 55, "Khe thứ 4 (Khóa Fa)", 2),
        StaffPitch(ClefType.BASS, 10, SolfegeNote.LA, 3, 57, "Hàng thứ 5 (trên cùng Khóa Fa)", 2),
        StaffPitch(ClefType.BASS, 11, SolfegeNote.SI, 3, 59, "Khe phụ thứ 1 trên hàng 5 (Khóa Fa)", 2),
        StaffPitch(ClefType.BASS, 12, SolfegeNote.DO, 4, 60, "Hàng phụ thứ 1 trên khuông (Đô trung C4)", 2)
    )

    fun getTreblePitch(solfege: SolfegeNote, octave: Int = 4): StaffPitch =
        treblePitches.firstOrNull { it.solfege == solfege && it.octave == octave }
            ?: treblePitches.first { it.solfege == solfege }

    fun getBassPitch(solfege: SolfegeNote, octave: Int = 3): StaffPitch =
        bassPitches.firstOrNull { it.solfege == solfege && it.octave == octave }
            ?: bassPitches.first { it.solfege == solfege }

    private fun note(
        id: String,
        solfege: SolfegeNote,
        octave: Int = 4,
        duration: NoteDuration = NoteDuration.QUARTER,
        accidental: Accidental = Accidental.NONE,
        isRest: Boolean = false,
        isAccented: Boolean = false,
        lyric: String = "",
        chordSymbol: String = "",
        beatLabel: String = "",
        clef: ClefType = ClefType.TREBLE
    ): NoteEvent {
        val pitch = if (clef == ClefType.TREBLE) getTreblePitch(solfege, octave) else getBassPitch(solfege, octave)
        return NoteEvent(
            id = id,
            pitch = pitch,
            duration = duration,
            accidental = accidental,
            isRest = isRest,
            isAccented = isAccented,
            lyric = lyric,
            chordSymbol = chordSymbol,
            beatLabel = beatLabel
        )
    }

    val sightReadingExercises: List<SightReadingExercise> = listOf(
        SightReadingExercise(
            id = "sr_treble_g1_1",
            titleVi = "Khóa Sol - Nhóm 1 (Đô4–Sol4)",
            subtitleVi = "5 nốt nền tảng đi liền bậc và quay về Đô4",
            clef = ClefType.TREBLE,
            bookGroup = 1,
            bookPage = 17,
            notes = listOf(
                note("g1_1", SolfegeNote.DO, 4),
                note("g1_2", SolfegeNote.RE, 4),
                note("g1_3", SolfegeNote.MI, 4),
                note("g1_4", SolfegeNote.FA, 4),
                note("g1_5", SolfegeNote.SOL, 4),
                note("g1_6", SolfegeNote.FA, 4),
                note("g1_7", SolfegeNote.MI, 4),
                note("g1_8", SolfegeNote.DO, 4)
            )
        ),
        SightReadingExercise(
            id = "sr_treble_g1_2",
            titleVi = "Khóa Sol - Nhóm 1 (Nhảy Quãng)",
            subtitleVi = "Phân biệt Đô (dòng phụ) - Mi (hàng 1) - Sol (hàng 2)",
            clef = ClefType.TREBLE,
            bookGroup = 1,
            bookPage = 18,
            notes = listOf(
                note("g1b_1", SolfegeNote.DO, 4),
                note("g1b_2", SolfegeNote.MI, 4),
                note("g1b_3", SolfegeNote.SOL, 4),
                note("g1b_4", SolfegeNote.FA, 4),
                note("g1b_5", SolfegeNote.RE, 4),
                note("g1b_6", SolfegeNote.FA, 4),
                note("g1b_7", SolfegeNote.MI, 4),
                note("g1b_8", SolfegeNote.DO, 4)
            )
        ),
        SightReadingExercise(
            id = "sr_treble_g2_1",
            titleVi = "Khóa Sol - Nhóm 2 (Sol4–Rê5)",
            subtitleVi = "Vượt qua hàng thứ 3 (nốt Si đuôi quay xuống)",
            clef = ClefType.TREBLE,
            bookGroup = 2,
            bookPage = 19,
            notes = listOf(
                note("g2_1", SolfegeNote.SOL, 4),
                note("g2_2", SolfegeNote.LA, 4),
                note("g2_3", SolfegeNote.SI, 4),
                note("g2_4", SolfegeNote.DO, 5),
                note("g2_5", SolfegeNote.RE, 5),
                note("g2_6", SolfegeNote.DO, 5),
                note("g2_7", SolfegeNote.SI, 4),
                note("g2_8", SolfegeNote.SOL, 4)
            )
        ),
        SightReadingExercise(
            id = "sr_treble_g3_1",
            titleVi = "Khóa Sol - Nhóm 3 (Đô5–Sol5)",
            subtitleVi = "Nửa trên khuông nhạc: Đô5 (khe 3) đến Sol5 (trên hàng 5)",
            clef = ClefType.TREBLE,
            bookGroup = 3,
            bookPage = 22,
            notes = listOf(
                note("g3_1", SolfegeNote.DO, 5),
                note("g3_2", SolfegeNote.RE, 5),
                note("g3_3", SolfegeNote.MI, 5),
                note("g3_4", SolfegeNote.FA, 5),
                note("g3_5", SolfegeNote.SOL, 5),
                note("g3_6", SolfegeNote.FA, 5),
                note("g3_7", SolfegeNote.RE, 5),
                note("g3_8", SolfegeNote.DO, 5)
            )
        ),
        SightReadingExercise(
            id = "sr_treble_g4_1",
            titleVi = "Khóa Sol - Nhóm 4 (Sol5–Đô6)",
            subtitleVi = "Đọc dòng kẻ phụ phía trên khuông nhạc",
            clef = ClefType.TREBLE,
            bookGroup = 4,
            bookPage = 24,
            notes = listOf(
                note("g4_1", SolfegeNote.SOL, 5),
                note("g4_2", SolfegeNote.LA, 5),
                note("g4_3", SolfegeNote.SI, 5),
                note("g4_4", SolfegeNote.DO, 6),
                note("g4_5", SolfegeNote.SI, 5),
                note("g4_6", SolfegeNote.LA, 5),
                note("g4_7", SolfegeNote.SOL, 5),
                note("g4_8", SolfegeNote.DO, 6)
            )
        ),
        SightReadingExercise(
            id = "sr_bass_g1_1",
            titleVi = "Khóa Fa - Nhóm 1 (Đô3–Sol3)",
            subtitleVi = "Nốt Fa nằm trên hàng thứ 4 (trang 50)",
            clef = ClefType.BASS,
            bookGroup = 1,
            bookPage = 50,
            notes = listOf(
                note("bf1_1", SolfegeNote.DO, 3, clef = ClefType.BASS),
                note("bf1_2", SolfegeNote.RE, 3, clef = ClefType.BASS),
                note("bf1_3", SolfegeNote.MI, 3, clef = ClefType.BASS),
                note("bf1_4", SolfegeNote.FA, 3, clef = ClefType.BASS),
                note("bf1_5", SolfegeNote.SOL, 3, clef = ClefType.BASS),
                note("bf1_6", SolfegeNote.FA, 3, clef = ClefType.BASS),
                note("bf1_7", SolfegeNote.RE, 3, clef = ClefType.BASS),
                note("bf1_8", SolfegeNote.DO, 3, clef = ClefType.BASS)
            )
        ),
        SightReadingExercise(
            id = "sr_bass_g2_1",
            titleVi = "Khóa Fa - Nhóm 2 (Fa3–Đô4)",
            subtitleVi = "Từ hàng thứ 4 (Fa3) lên dòng phụ trên 1 (Đô4)",
            clef = ClefType.BASS,
            bookGroup = 2,
            bookPage = 52,
            notes = listOf(
                note("bf2_1", SolfegeNote.FA, 3, clef = ClefType.BASS),
                note("bf2_2", SolfegeNote.SOL, 3, clef = ClefType.BASS),
                note("bf2_3", SolfegeNote.LA, 3, clef = ClefType.BASS),
                note("bf2_4", SolfegeNote.SI, 3, clef = ClefType.BASS),
                note("bf2_5", SolfegeNote.DO, 4, clef = ClefType.BASS),
                note("bf2_6", SolfegeNote.LA, 3, clef = ClefType.BASS),
                note("bf2_7", SolfegeNote.SOL, 3, clef = ClefType.BASS),
                note("bf2_8", SolfegeNote.FA, 3, clef = ClefType.BASS)
            )
        )
    )

    val rhythmExercises: List<RhythmPatternExercise> = listOf(
        RhythmPatternExercise(
            id = "rhy_basic_24",
            titleVi = "Nhịp 2/4 Cơ Bản: Đen, Móc Đơn & Trắng",
            categoryVi = "Cơ bản",
            timeSignature = TimeSignatureSpec.TWO_FOUR,
            defaultBpm = 76,
            descriptionVi = "Thí dụ trang 29: Ô 1 gồm 1 nốt Đen + 2 Móc đơn; Ô 2 là 1 nốt Trắng ngân đủ 2 phách.",
            beatCountingGuideVi = "Đếm: [1 (Mạnh)] - [2 và (Nhẹ)] | [1 - 2 (Ngân dài)]",
            events = listOf(
                note("r1_1", SolfegeNote.SOL, 4, NoteDuration.QUARTER, isAccented = true, beatLabel = "Phách 1(M)"),
                note("r1_2", SolfegeNote.LA, 4, NoteDuration.EIGHTH, beatLabel = "Phách 2"),
                note("r1_3", SolfegeNote.SOL, 4, NoteDuration.EIGHTH, beatLabel = "và(N)"),
                note("r1_4", SolfegeNote.MI, 4, NoteDuration.HALF, isAccented = true, beatLabel = "1 - 2"),
                note("r1_5", SolfegeNote.SOL, 4, NoteDuration.QUARTER, isAccented = true, beatLabel = "Phách 1(M)"),
                note("r1_6", SolfegeNote.DO, 4, NoteDuration.QUARTER, beatLabel = "Phách 2(N)")
            )
        ),
        RhythmPatternExercise(
            id = "rhy_waltz_34",
            titleVi = "Nhịp 3/4 (Waltz): Đen Chấm Dôi + Móc Đơn",
            categoryVi = "Chấm dôi",
            timeSignature = TimeSignatureSpec.THREE_FOUR,
            defaultBpm = 84,
            descriptionVi = "Tiết tấu Valse (như bài Silent Night tr. 75): Đen chấm dôi (1.5p) + Móc đơn (0.5p) + Đen (1p).",
            beatCountingGuideVi = "Đếm: [1 - 2] [và] [3] | [1 - 2 - 3 (Trắng chấm)]",
            events = listOf(
                note("r2_1", SolfegeNote.SOL, 4, NoteDuration.DOTTED_QUARTER, isAccented = true, beatLabel = "1-2(M)"),
                note("r2_2", SolfegeNote.LA, 4, NoteDuration.EIGHTH, beatLabel = "và"),
                note("r2_3", SolfegeNote.SOL, 4, NoteDuration.QUARTER, beatLabel = "3(N)"),
                note("r2_4", SolfegeNote.MI, 4, NoteDuration.DOTTED_HALF, isAccented = true, beatLabel = "1-2-3")
            )
        ),
        RhythmPatternExercise(
            id = "rhy_syncope_11",
            titleVi = "Đảo Phách Cân (Tương Quan 1 - 1)",
            categoryVi = "Đảo phách",
            timeSignature = TimeSignatureSpec.TWO_FOUR,
            defaultBpm = 72,
            descriptionVi = "Bài 21 (tr. 86): Móc đơn (nửa phách đầu) + Đen (ngân qua phách sau) + Móc đơn.",
            beatCountingGuideVi = "Nhấn vào nốt Đen ở giữa: [1] - [VÀ-2 (Đảo phách)] - [và]",
            events = listOf(
                note("r3_1", SolfegeNote.MI, 4, NoteDuration.EIGHTH, beatLabel = "1"),
                note("r3_2", SolfegeNote.SOL, 4, NoteDuration.QUARTER, isAccented = true, beatLabel = "và-2(Đảo)"),
                note("r3_3", SolfegeNote.LA, 4, NoteDuration.EIGHTH, beatLabel = "và"),
                note("r3_4", SolfegeNote.SOL, 4, NoteDuration.HALF, isAccented = true, beatLabel = "1 - 2")
            )
        ),
        RhythmPatternExercise(
            id = "rhy_syncope_21",
            titleVi = "Đảo Phách Lệch (Tương Quan 2 - 1)",
            categoryVi = "Đảo phách",
            timeSignature = TimeSignatureSpec.FOUR_FOUR,
            defaultBpm = 76,
            descriptionVi = "Bài 21 (tr. 86): Nốt ở phách nhẹ ngân dài qua phách mạnh kế tiếp.",
            beatCountingGuideVi = "Đếm 4/4: [1] [2 - 3 (Đảo 2-1)] [và] [4]",
            events = listOf(
                note("r4_1", SolfegeNote.DO, 4, NoteDuration.QUARTER, isAccented = true, beatLabel = "1(M)"),
                note("r4_2", SolfegeNote.MI, 4, NoteDuration.DOTTED_QUARTER, isAccented = true, beatLabel = "2-3(Đảo)"),
                note("r4_3", SolfegeNote.SOL, 4, NoteDuration.EIGHTH, beatLabel = "và"),
                note("r4_4", SolfegeNote.DO, 5, NoteDuration.QUARTER, beatLabel = "4(N)")
            )
        ),
        RhythmPatternExercise(
            id = "rhy_contretemps",
            titleVi = "Nghịch Phách (Thay Phách Mạnh Bằng Dấu Lặng)",
            categoryVi = "Nghịch phách",
            timeSignature = TimeSignatureSpec.TWO_FOUR,
            defaultBpm = 72,
            descriptionVi = "Bài 21 (tr. 88): Phách mạnh được thay bằng Dấu Lặng, vào câu ở phách nhẹ.",
            beatCountingGuideVi = "Đếm: [1 (Nghỉ Lặng)] - [2 (Vào nốt)]",
            events = listOf(
                note("r5_1", SolfegeNote.DO, 4, NoteDuration.QUARTER, isRest = true, beatLabel = "1(Lặng M)"),
                note("r5_2", SolfegeNote.MI, 4, NoteDuration.QUARTER, isAccented = true, beatLabel = "2(N)"),
                note("r5_3", SolfegeNote.DO, 4, NoteDuration.EIGHTH, isRest = true, beatLabel = "1(Lặng)"),
                note("r5_4", SolfegeNote.SOL, 4, NoteDuration.EIGHTH, isAccented = true, beatLabel = "và"),
                note("r5_5", SolfegeNote.LA, 4, NoteDuration.QUARTER, beatLabel = "2")
            )
        )
    )

    val scaleDegrees: List<ScaleDegreeRole> = listOf(
        ScaleDegreeRole("I", 1, "Chủ Âm (Tonic)", "Tonique", "Nốt quan trọng nhất, dùng đặt tên cho âm giai, nơi khởi đầu và kết thúc.", true),
        ScaleDegreeRole("II", 2, "Thượng Chủ Âm", "Sus-tonique", "Cao hơn chủ âm 1 bậc, có khuynh hướng hút về Bậc I hoặc sang Bậc V.", false),
        ScaleDegreeRole("III", 3, "Trung Âm (Mediant)", "Médiante", "Nằm giữa Chủ âm (I) và Át âm (V), quyết định màu sắc Trưởng hay Thứ.", true),
        ScaleDegreeRole("IV", 4, "Hạ Át Âm (Subdominant)", "Sous-dominante", "Nằm dưới Át âm, mở rộng không gian hòa âm cho câu hát.", true),
        ScaleDegreeRole("V", 5, "Át Âm (Dominant)", "Dominante", "Quan trọng thứ 2 sau Chủ âm, có sức hút mạnh mẽ đòi hỏi giải quyết về Chủ âm I.", true),
        ScaleDegreeRole("VI", 6, "Thượng Át Âm", "Sus-dominante", "Nằm trên Át âm 1 bậc (bậc chủ của âm giai Thứ tương ứng).", false),
        ScaleDegreeRole("VII", 7, "Cảm Âm (Leading Tone)", "Sensible", "Cách Chủ âm 1/2 cung, rất nhạy cảm, luôn hút mạnh lên Chủ âm VIII.", false)
    )

    val intervals: List<IntervalSpec> = listOf(
        IntervalSpec("Quãng 2 Thứ", "2m", 1, "1/2 cung", "Mi – Fa, Si – Đô", 64, 65, "Sát nhau, tạo lực dẫn hướng"),
        IntervalSpec("Quãng 2 Trưởng", "2M", 2, "1 cung", "Đô – Rê, Rê – Mi", 60, 62, "Đi liền bậc tự nhiên"),
        IntervalSpec("Quãng 3 Thứ", "3m", 3, "1 cung + 1/2 cung", "Rê – Fa, Mi – Sol, La – Đô", 60, 63, "Mềm mại, trầm buồn (nền tảng Hợp âm Thứ)"),
        IntervalSpec("Quãng 3 Trưởng", "3M", 4, "2 cung", "Đô – Mi, Fa – La, Sol – Si", 60, 64, "Sáng sủa, vui tươi (nền tảng Hợp âm Trưởng)"),
        IntervalSpec("Quãng 4 Đúng", "4P", 5, "2 cung + 1/2 cung", "Đô – Fa, Rê – Sol", 60, 65, "Vững chãi, trang nghiêm"),
        IntervalSpec("Quãng 5 Đúng", "5P", 7, "3 cung + 1/2 cung", "Đô – Sol, Rê – La", 60, 67, "Hài hòa, khung xương của hợp âm"),
        IntervalSpec("Quãng 6 Trưởng", "6M", 9, "4 cung + 1/2 cung", "Đô – La, Rê – Si", 60, 69, "Rộng mở, bay bổng"),
        IntervalSpec("Quãng 7 Thứ", "7m", 10, "4 cung + 2 nửa cung", "Sol – Fa5, Rê – Đô5", 60, 70, "Tạo sức hút của Hợp âm 7 Át"),
        IntervalSpec("Quãng 8 Đúng", "8P", 12, "6 cung", "Đô4 – Đô5", 60, 72, "Lặp lại tên nốt ở tầng cao gấp đôi")
    )

    val chordsLibrary: List<ChordDefinition> = listOf(
        ChordDefinition("C", "Đô Trưởng", "Hợp âm Trưởng", "3 Trưởng + 3 Thứ", "Đô – Mi – Sol", listOf(60, 64, 67), "Trong sáng, ổn định"),
        ChordDefinition("Cm", "Đô Thứ", "Hợp âm Thứ", "3 Thứ + 3 Trưởng", "Đô – Mi♭ – Sol", listOf(60, 63, 67), "Trầm lắng, sâu sắc"),
        ChordDefinition("C7", "Đô Bảy", "Hợp âm 7 (Nghịch)", "C + Si♭ (7m)", "Đô – Mi – Sol – Si♭", listOf(60, 64, 67, 70), "Hút mạnh về Fa Trưởng (F)"),
        ChordDefinition("Dm", "Rê Thứ", "Hợp âm Thứ", "3 Thứ + 3 Trưởng", "Rê – Fa – La", listOf(62, 65, 69), "Mộc mạc, tự sự"),
        ChordDefinition("D", "Rê Trưởng", "Hợp âm Trưởng", "3 Trưởng + 3 Thứ", "Rê – Fa♯ – La", listOf(62, 66, 69), "Rực rỡ, phấn khởi"),
        ChordDefinition("Em", "Mi Thứ", "Hợp âm Thứ", "3 Thứ + 3 Trưởng", "Mi – Sol – Si", listOf(64, 67, 71), "Nhẹ nhàng, hoài niệm"),
        ChordDefinition("E7", "Mi Bảy", "Hợp âm 7 (Nghịch)", "E + Rê (7m)", "Mi – Sol♯ – Si – Rê", listOf(64, 68, 71, 74), "Hút mạnh về La Thứ (Am)"),
        ChordDefinition("F", "Fa Trưởng", "Hợp âm Trưởng", "3 Trưởng + 3 Thứ", "Fa – La – Đô", listOf(65, 69, 72), "Ấm áp, mở rộng (Bậc IV)"),
        ChordDefinition("G", "Sol Trưởng", "Hợp âm Trưởng", "3 Trưởng + 3 Thứ", "Sol – Si – Rê", listOf(67, 71, 74), "Sôi nổi, cao trào (Bậc V)"),
        ChordDefinition("G7", "Sol Bảy", "Hợp âm 7 (Nghịch)", "G + Fa (7m)", "Sol – Si – Rê – Fa", listOf(67, 71, 74, 77), "Át 7 hút mạnh về Đô Trưởng (C)"),
        ChordDefinition("Am", "La Thứ", "Hợp âm Thứ", "3 Thứ + 3 Trưởng", "La – Đô – Mi", listOf(69, 72, 76), "Trữ tình, gần gũi"),
        ChordDefinition("Bb", "Si♭ Trưởng", "Hợp âm Trưởng", "3 Trưởng + 3 Thứ", "Si♭ – Rê – Fa", listOf(58, 62, 65), "Hào hùng, trang trọng")
    )

    val keyTonalities: List<KeyTonality> = listOf(
        KeyTonality(
            id = "C_MAJOR", symbol = "C", nameVi = "Đô Trưởng (C)", isMajor = true,
            keySignatureTextVi = "Không có dấu Thăng/Giáng theo khóa", sharpsCount = 0, flatsCount = 0,
            tonicSolfege = SolfegeNote.DO,
            scaleNotesVi = listOf("Đô(I)", "Rê(II)", "Mi(III)", "Fa(IV)", "Sol(V)", "La(VI)", "Si(VII)", "Đô(VIII)"),
            scaleMidiNotes = listOf(60, 62, 64, 65, 67, 69, 71, 72),
            primaryChords = chordsLibrary.filter { it.symbol in listOf("C", "Dm", "Em", "F", "G", "G7", "Am") },
            songwritingProgression = listOf("C", "Am", "F", "G7", "C"),
            songwritingAdviceVi = "Khởi đầu và kết thúc ở nốt Đô (hợp âm C), dùng G7 cuối câu để hút về C."
        ),
        KeyTonality(
            id = "A_MINOR", symbol = "Am", nameVi = "La Thứ (Am)", isMajor = false,
            keySignatureTextVi = "Không có dấu hóa theo khóa (Bậc VII tăng 1/2c thành Sol♯)", sharpsCount = 0, flatsCount = 0,
            tonicSolfege = SolfegeNote.LA,
            scaleNotesVi = listOf("La(I)", "Si(II)", "Đô(III)", "Rê(IV)", "Mi(V)", "Fa(VI)", "Sol♯(VII)", "La(VIII)"),
            scaleMidiNotes = listOf(57, 59, 60, 62, 64, 65, 68, 69),
            primaryChords = chordsLibrary.filter { it.symbol in listOf("Am", "Dm", "E7", "C", "F", "G") },
            songwritingProgression = listOf("Am", "Dm", "G", "C", "E7", "Am"),
            songwritingAdviceVi = "Rất hợp viết Ballad: Dùng E7 (có Sol♯) để tạo lực hút dẫn về chủ âm Am."
        ),
        KeyTonality(
            id = "G_MAJOR", symbol = "G", nameVi = "Sol Trưởng (G)", isMajor = true,
            keySignatureTextVi = "1 dấu Thăng theo khóa: Fa♯", sharpsCount = 1, flatsCount = 0,
            tonicSolfege = SolfegeNote.SOL,
            scaleNotesVi = listOf("Sol(I)", "La(II)", "Si(III)", "Đô(IV)", "Rê(V)", "Mi(VI)", "Fa♯(VII)", "Sol(VIII)"),
            scaleMidiNotes = listOf(55, 57, 59, 60, 62, 64, 66, 67),
            primaryChords = chordsLibrary.filter { it.symbol in listOf("G", "Am", "C", "D", "Em") },
            songwritingProgression = listOf("G", "Em", "C", "D", "G"),
            songwritingAdviceVi = "Màu sắc vui tươi (như bài 'Ơi Cuộc Sống Mến Thương'). Mọi nốt Fa đều là Fa♯."
        ),
        KeyTonality(
            id = "D_MINOR", symbol = "Dm", nameVi = "Rê Thứ (Dm)", isMajor = false,
            keySignatureTextVi = "1 dấu Giáng theo khóa: Si♭", sharpsCount = 0, flatsCount = 1,
            tonicSolfege = SolfegeNote.RE,
            scaleNotesVi = listOf("Rê(I)", "Mi(II)", "Fa(III)", "Sol(IV)", "La(V)", "Si♭(VI)", "Đô♯(VII)", "Rê(VIII)"),
            scaleMidiNotes = listOf(62, 64, 65, 67, 69, 70, 73, 74),
            primaryChords = chordsLibrary.filter { it.symbol in listOf("Dm", "F", "Bb", "C") },
            songwritingProgression = listOf("Dm", "Bb", "F", "C", "Dm"),
            songwritingAdviceVi = "Âm hưởng quê hương sâu lắng (như bài 'Chuyến Đò Quê Hương')."
        ),
        KeyTonality(
            id = "F_MAJOR", symbol = "F", nameVi = "Fa Trưởng (F)", isMajor = true,
            keySignatureTextVi = "1 dấu Giáng theo khóa: Si♭", sharpsCount = 0, flatsCount = 1,
            tonicSolfege = SolfegeNote.FA,
            scaleNotesVi = listOf("Fa(I)", "Sol(II)", "La(III)", "Si♭(IV)", "Đô(V)", "Rê(VI)", "Mi(VII)", "Fa(VIII)"),
            scaleMidiNotes = listOf(65, 67, 69, 70, 72, 74, 76, 77),
            primaryChords = chordsLibrary.filter { it.symbol in listOf("F", "Bb", "C", "C7", "Dm") },
            songwritingProgression = listOf("F", "Dm", "Bb", "C7", "F"),
            songwritingAdviceVi = "Ấm áp, vừa tầm giọng hát. C7 là hợp âm Át (V7) hút về Fa Trưởng."
        )
    )

    val keySignatureGuideRows: List<KeySignatureGuideRow> = listOf(
        KeySignatureGuideRow("0", 0, "Không có dấu Hóa", "Đô Trưởng (C)", "Đô", "La Thứ (Am)", "La"),
        KeySignatureGuideRow("#", 1, "Fa♯", "Sol Trưởng (G)", "Sol", "Mi Thứ (Em)", "Mi"),
        KeySignatureGuideRow("#", 2, "Fa♯, Đô♯", "Rê Trưởng (D)", "Rê", "Si Thứ (Bm)", "Si"),
        KeySignatureGuideRow("#", 3, "Fa♯, Đô♯, Sol♯", "La Trưởng (A)", "La", "Fa♯ Thứ (F♯m)", "Fa♯"),
        KeySignatureGuideRow("#", 4, "Fa♯, Đô♯, Sol♯, Rê♯", "Mi Trưởng (E)", "Mi", "Đô♯ Thứ (C♯m)", "Đô♯"),
        KeySignatureGuideRow("b", 1, "Si♭", "Fa Trưởng (F)", "Fa", "Rê Thứ (Dm)", "Rê"),
        KeySignatureGuideRow("b", 2, "Si♭, Mi♭", "Si♭ Trưởng (B♭)", "Si♭", "Sol Thứ (Gm)", "Sol"),
        KeySignatureGuideRow("b", 3, "Si♭, Mi♭, La♭", "Mi♭ Trưởng (E♭)", "Mi♭", "Đô Thứ (Cm)", "Đô"),
        KeySignatureGuideRow("b", 4, "Si♭, Mi♭, La♭, Rê♭", "La♭ Trưởng (A♭)", "La♭", "Fa Thứ (Fm)", "Fa")
    )

    val songTemplates: List<SongAnalysisTemplate> = listOf(
        SongAnalysisTemplate(
            id = "silent_night",
            titleVi = "Silent Night (Đêm Thánh Vô Cùng)",
            composerVi = "F. Gruber (Bài 19, tr. 75)",
            keyTonalityId = "C_MAJOR",
            timeSignature = TimeSignatureSpec.THREE_FOUR,
            bpm = 78,
            bookPage = 75,
            chordProgressionSummary = "C → G7 → C → F → C → G7 → C",
            analysisPointsVi = listOf(
                "Thuộc Âm thể Đô Trưởng (C): Khởi đầu và kết thúc về hợp âm C.",
                "Nhịp 3/4 (Waltz): Mỗi ô nhịp 3 phách Đen, dùng Đen chấm dôi + Móc đơn.",
                "Sức hút hòa âm rõ rệt: G7 → C và F → C."
            ),
            melodyEvents = listOf(
                note("sn_1", SolfegeNote.SOL, 4, NoteDuration.DOTTED_QUARTER, chordSymbol = "C", lyric = "Đêm", beatLabel = "1-2"),
                note("sn_2", SolfegeNote.LA, 4, NoteDuration.EIGHTH, lyric = "thánh", beatLabel = "và"),
                note("sn_3", SolfegeNote.SOL, 4, NoteDuration.QUARTER, lyric = "vô", beatLabel = "3"),
                note("sn_4", SolfegeNote.MI, 4, NoteDuration.DOTTED_HALF, lyric = "cùng", beatLabel = "1-2-3"),
                note("sn_5", SolfegeNote.RE, 5, NoteDuration.HALF, chordSymbol = "G7", lyric = "Đất", beatLabel = "1-2"),
                note("sn_6", SolfegeNote.RE, 5, NoteDuration.QUARTER, lyric = "với", beatLabel = "3"),
                note("sn_7", SolfegeNote.SI, 4, NoteDuration.DOTTED_HALF, lyric = "trời", beatLabel = "1-2-3"),
                note("sn_8", SolfegeNote.DO, 5, NoteDuration.DOTTED_HALF, chordSymbol = "C", lyric = "đồng", beatLabel = "1-2-3")
            )
        ),
        SongAnalysisTemplate(
            id = "oi_cuoc_song",
            titleVi = "Ơi Cuộc Sống Mến Thương (Trích)",
            composerVi = "Nguyễn Ngọc Thiện (tr. 98)",
            keyTonalityId = "G_MAJOR",
            timeSignature = TimeSignatureSpec.TWO_FOUR,
            bpm = 100,
            bookPage = 98,
            chordProgressionSummary = "G → Em → C → D7 → G",
            analysisPointsVi = listOf(
                "Hóa biểu 1 dấu Fa♯, kết thúc ở nốt Sol → Âm thể Sol Trưởng (G).",
                "Nhịp 2/4 dùng tiết tấu Đảo phách cân (Móc đơn - Đen - Móc đơn) tươi vui."
            ),
            melodyEvents = listOf(
                note("ocs_1", SolfegeNote.SI, 4, NoteDuration.EIGHTH, chordSymbol = "G", lyric = "Có", beatLabel = "1"),
                note("ocs_2", SolfegeNote.RE, 5, NoteDuration.QUARTER, isAccented = true, lyric = "chú", beatLabel = "và-2"),
                note("ocs_3", SolfegeNote.SI, 4, NoteDuration.EIGHTH, lyric = "chim", beatLabel = "và"),
                note("ocs_4", SolfegeNote.LA, 4, NoteDuration.EIGHTH, chordSymbol = "Em", lyric = "non", beatLabel = "1"),
                note("ocs_5", SolfegeNote.SOL, 4, NoteDuration.EIGHTH, lyric = "nho", beatLabel = "và"),
                note("ocs_6", SolfegeNote.MI, 4, NoteDuration.HALF, lyric = "nhỏ", beatLabel = "2")
            )
        )
    )

    val lessons: List<MusicLesson> = listOf(
        MusicLesson(
            id = 1, lessonNumberLabel = "Bài 1 & 2",
            titleVi = "Âm Thanh & 7 Tên Nốt Nhạc (Đô Rê Mi Fa Sol La Si)",
            summaryVi = "4 thuộc tính của âm thanh (Cao độ, Cường độ, Trường độ, Âm sắc) và đọc xuôi/ngược 7 tên nốt.",
            stage = CurriculumStage.STAGE_1, bookPageStart = 8,
            sections = listOf(
                LessonSection(
                    headingVi = "1. Bốn thuộc tính âm thanh & 7 tên nốt",
                    bodyVi = "• Cao độ (trầm/bổng), Cường độ (mạnh/nhẹ), Trường độ (dài/ngắn), Âm sắc (màu âm nhạc cụ).\n" +
                        "• 7 tên nốt từ thấp lên cao: ĐÔ – RÊ – MI – FA – SOL – LA – SI.\n" +
                        "• Đọc ngược từ cao xuống thấp: ĐÔ – SI – LA – SOL – FA – MI – RÊ – ĐÔ.",
                    highlightFormula = "Xuôi: Đô Rê Mi Fa Sol La Si Đô | Ngược: Đô Si La Sol Fa Mi Rê Đô",
                    demoNotes = listOf(
                        note("b1_1", SolfegeNote.DO, 4, lyric = "Đô"),
                        note("b1_2", SolfegeNote.RE, 4, lyric = "Rê"),
                        note("b1_3", SolfegeNote.MI, 4, lyric = "Mi"),
                        note("b1_4", SolfegeNote.FA, 4, lyric = "Fa"),
                        note("b1_5", SolfegeNote.SOL, 4, lyric = "Sol"),
                        note("b1_6", SolfegeNote.LA, 4, lyric = "La"),
                        note("b1_7", SolfegeNote.SI, 4, lyric = "Si"),
                        note("b1_8", SolfegeNote.DO, 5, lyric = "Đô5")
                    )
                )
            ),
            songwriterTakeawayVi = "Cao độ tạo nên Giai điệu (Melody), Trường độ + Cường độ tạo nên Tiết tấu (Rhythm) của bài hát.",
            quizQuestions = listOf(
                QuizQuestion("q1", "Yếu tố nào giúp phân biệt tiếng đàn Tranh với tiếng kèn Trompette khi cùng đánh 1 nốt?", listOf("Cao độ", "Trường độ", "Âm sắc", "Cường độ"), 2, "Theo Bài 1 (tr. 8): Sự khác nhau đó là Âm sắc."),
                QuizQuestion("q2", "Khi đọc ngược từ cao xuống thấp, ngay sau nốt SOL là nốt nào?", listOf("La", "Fa", "Mi", "Rê"), 1, "Đô - Si - La - Sol - Fa - Mi - Rê - Đô.")
            )
        ),
        MusicLesson(
            id = 3, lessonNumberLabel = "Bài 3 & 6",
            titleVi = "Khuông Nhạc, Khóa Sol & Vị Trí Các Nốt",
            summaryVi = "5 hàng, 4 khe (đếm từ dưới lên), dòng kẻ phụ và cách xác định vị trí nốt trên Khóa Sol.",
            stage = CurriculumStage.STAGE_1, bookPageStart = 10,
            sections = listOf(
                LessonSection(
                    headingVi = "1. Khuông nhạc 5 hàng 4 khe & Khóa Sol",
                    bodyVi = "• Khuông nhạc gồm 5 hàng và 4 khe đếm từ DƯỚI LÊN TRÊN.\n" +
                        "• Khóa Sol đặt trên HÀNG THỨ 2: Nốt nằm trên hàng thứ 2 là nốt SOL.\n" +
                        "• 5 Hàng: Mi(1) - Sol(2) - Si(3) - Rê(4) - Fa(5).\n" +
                        "• 4 Khe: Fa(1) - La(2) - Đô(3) - Mi(4).",
                    highlightFormula = "Khóa Sol: Hàng 2 = Nốt Sol | Khóa Fa: Hàng 4 = Nốt Fa",
                    demoNotes = treblePitches.take(7).mapIndexed { i, p -> NoteEvent("b3_$i", p, lyric = p.solfege.nameVi) }
                )
            ),
            songwriterTakeawayVi = "Nắm chắc vị trí Hàng và Khe giúp bạn nhìn bản nhạc là đọc ra giai điệu ngay lập tức.",
            quizQuestions = listOf(
                QuizQuestion("q3_1", "Thứ tự các hàng và khe của khuông nhạc chính được đếm như thế nào?", listOf("Từ trên xuống", "Từ dưới đếm lên", "Từ giữa đếm ra", "Tùy ý"), 1, "Luôn đếm từ dưới lên (Hàng 1 đến Hàng 5)."),
                QuizQuestion("q3_2", "Trên khuông nhạc Khóa Sol, 4 khe chính từ dưới lên lần lượt là:", listOf("Mi-Sol-Si-Rê", "Fa-La-Đô-Mi", "Đô-Rê-Mi-Fa", "Sol-Si-Rê-Fa"), 1, "Khe 1 = Fa, Khe 2 = La, Khe 3 = Đô5, Khe 4 = Mi5.")
            )
        ),
        MusicLesson(
            id = 4, lessonNumberLabel = "Bài 4 & 5",
            titleVi = "Hình Nốt, Trường Độ & Cung / Nửa Cung",
            summaryVi = "7 hình nốt (Tròn, Trắng, Đen, Móc...), quy tắc quay đuôi nốt ở hàng 3 và khoảng cách Cung/Nửa cung.",
            stage = CurriculumStage.STAGE_1, bookPageStart = 12,
            sections = listOf(
                LessonSection(
                    headingVi = "1. Giá trị hình nốt & Cung / Nửa cung",
                    bodyVi = "• 1 Tròn = 2 Trắng = 4 Đen = 8 Móc đơn = 16 Móc đôi.\n" +
                        "• Từ hàng thứ 3 trở lên đuôi nốt quay xuống; dưới hàng 3 đuôi quay lên.\n" +
                        "• Khoảng cách 1 cung: Đô-Rê, Rê-Mi, Fa-Sol, Sol-La, La-Si.\n" +
                        "• Khoảng cách 1/2 cung (Nửa cung tự nhiên): MI–FA và SI–ĐÔ.",
                    highlightFormula = "Đô (1c) Rê (1c) Mi [1/2c] Fa (1c) Sol (1c) La (1c) Si [1/2c] Đô",
                    demoNotes = listOf(
                        note("b4_1", SolfegeNote.MI, 4, NoteDuration.HALF, lyric = "Mi"),
                        note("b4_2", SolfegeNote.FA, 4, NoteDuration.HALF, lyric = "Fa (1/2c)"),
                        note("b4_3", SolfegeNote.SI, 4, NoteDuration.HALF, lyric = "Si"),
                        note("b4_4", SolfegeNote.DO, 5, NoteDuration.WHOLE, lyric = "Đô (1/2c)")
                    )
                )
            ),
            songwriterTakeawayVi = "Hai cặp nửa cung Mi–Fa và Si–Đô là nơi tạo ra lực hút cảm xúc mạnh nhất trong giai điệu.",
            quizQuestions = listOf(
                QuizQuestion("q4_1", "Hai cặp nốt tự nhiên nào liền kề nhau có khoảng cách là 1/2 cung?", listOf("Đô–Rê và Fa–Sol", "Mi–Fa và Si–Đô", "Rê–Mi và La–Si", "Sol–La và Si–Đô"), 1, "Mi–Fa và Si–Đô là 2 nửa cung tự nhiên duy nhất.")
            )
        ),
        MusicLesson(
            id = 8, lessonNumberLabel = "Bài 7, 8 & 9",
            titleVi = "Nhịp, Phách, Số Chỉ Nhịp & Dấu Hóa (♯, ♭, ♮)",
            summaryVi = "Ý nghĩa Tử số & Mẫu số của Số chỉ nhịp (2/4, 3/4, 4/4, 2/2, 6/8) và tác dụng của Dấu hóa.",
            stage = CurriculumStage.STAGE_2, bookPageStart = 26,
            sections = listOf(
                LessonSection(
                    headingVi = "1. Số chỉ nhịp & Dấu hóa",
                    bodyVi = "• Tử số = Số phách trong mỗi ô nhịp.\n" +
                        "• Mẫu số = Giá trị hình nốt của 1 phách (Mẫu 2 = Trắng, Mẫu 4 = Đen, Mẫu 8 = Móc đơn).\n" +
                        "• Dấu Thăng (♯) nâng 1/2 cung; Dấu Giáng (♭) giảm 1/2 cung; Dấu Bình (♮) trả về bình thường.\n" +
                        "• Dấu hóa theo khóa (Hóa biểu) ảnh hưởng toàn bài; Dấu hóa bất thường chỉ ảnh hưởng trong 1 ô nhịp.",
                    highlightFormula = "Mẫu 2 = Nốt Trắng | Mẫu 4 = Nốt Đen | Mẫu 8 = Nốt Móc đơn",
                    demoTimeSignature = TimeSignatureSpec.FOUR_FOUR,
                    demoNotes = listOf(
                        note("b8_1", SolfegeNote.DO, 4, lyric = "Phách 1"),
                        note("b8_2", SolfegeNote.FA, 4, accidental = Accidental.SHARP, lyric = "Fa♯ (2)"),
                        note("b8_3", SolfegeNote.SOL, 4, lyric = "Phách 3"),
                        note("b8_4", SolfegeNote.DO, 5, lyric = "Phách 4")
                    )
                )
            ),
            songwriterTakeawayVi = "Chọn nhịp 2/4 cho bài hát sôi nổi, 3/4 cho Valse nhẹ nhàng, 4/4 cho Pop/Ballad cân đối.",
            quizQuestions = listOf(
                QuizQuestion("q8_1", "Ở nhịp 2/2 (₵), 2 nốt Đen có giá trị bằng mấy phách?", listOf("4 phách", "2 phách", "1 phách", "1/2 phách"), 2, "Ở nhịp 2/2, 1 phách = 1 nốt Trắng (= 2 nốt Đen).")
            )
        ),
        MusicLesson(
            id = 10, lessonNumberLabel = "Bài 10 – 14",
            titleVi = "Dấu Lặng, Chấm Dôi, Dấu Nối, Khóa Fa & Liên Ba",
            summaryVi = "Vị trí 7 dấu lặng, cách tính Chấm dôi (+1/2 giá trị), đọc nốt Khóa Fa và nhóm Liên Ba.",
            stage = CurriculumStage.STAGE_2, bookPageStart = 41,
            sections = listOf(
                LessonSection(
                    headingVi = "1. Dấu Lặng, Chấm dôi & Liên ba",
                    bodyVi = "• Lặng Tròn nằm DƯỚI hàng 4 (nghỉ 4 phách hoặc trọn 1 ô nhịp mọi loại nhịp).\n" +
                        "• Lặng Trắng nằm TRÊN hàng 3 (nghỉ 2 phách).\n" +
                        "• Dấu Chấm Dôi đặt sau nốt làm tăng thêm 1/2 trường độ nốt đó (Trắng chấm = 3 phách).\n" +
                        "• Liên Ba chia hình nốt làm 3 phần bằng nhau nhưng giữ nguyên tổng trường độ (3 móc đơn liên ba = 1 nốt Đen).",
                    highlightFormula = "Nốt chấm dôi = 1.5 × Nốt gốc | 3 Móc đơn Liên Ba = 1 Nốt Đen",
                    demoClef = ClefType.BASS,
                    demoNotes = bassPitches.subList(3, 8).mapIndexed { i, p -> NoteEvent("b10_$i", p, lyric = p.solfege.nameVi) }
                )
            ),
            songwriterTakeawayVi = "Dấu lặng tạo nhịp lấy hơi tự nhiên cho ca sĩ; Nốt chấm dôi tạo độ ngân uyển chuyển đầu câu.",
            quizQuestions = listOf(
                QuizQuestion("q10_1", "Để nghỉ trọn 1 ô nhịp ở bất kỳ loại nhịp nào, ta đặt dấu Lặng gì?", listOf("Lặng Trắng", "Lặng Tròn (dưới hàng 4)", "Lặng Đen", "Chấm ngân"), 1, "Theo trang 42: Nghỉ trọn 1 ô nhịp dùng dấu Lặng Tròn.")
            )
        ),
        MusicLesson(
            id = 20, lessonNumberLabel = "Bài 20 & 21",
            titleVi = "Phách Mạnh - Nhẹ, Nhịp Lấy Đà, Đảo Phách & Nghịch Phách",
            summaryVi = "Trọng âm ô nhịp, nguyên tắc Nhịp lấy đà, kỹ thuật Đảo phách (Syncope) và Nghịch phách.",
            stage = CurriculumStage.STAGE_3, bookPageStart = 81,
            sections = listOf(
                LessonSection(
                    headingVi = "1. Phách Mạnh/Nhẹ, Đảo phách & Nghịch phách",
                    bodyVi = "• Phách 1 sau vạch nhịp luôn là Phách Mạnh nhất.\n" +
                        "• Nhịp lấy đà: Ô nhịp đầu không đủ số phách (không ghi hợp âm ở ô nhịp lấy đà).\n" +
                        "• Đảo phách: Âm thanh từ Phách Nhẹ (hoặc phần nhẹ) ngân dài qua Phách Mạnh (hoặc phần mạnh) kế tiếp.\n" +
                        "• Nghịch phách: Phách mạnh bị thay thế bằng Dấu Lặng.",
                    highlightFormula = "Đảo phách = Nhẹ ngân sang Mạnh | Nghịch phách = Lặng ở Phách Mạnh",
                    demoTimeSignature = TimeSignatureSpec.TWO_FOUR,
                    demoNotes = listOf(
                        note("b20_1", SolfegeNote.MI, 4, NoteDuration.EIGHTH, lyric = "Nhẹ"),
                        note("b20_2", SolfegeNote.SOL, 4, NoteDuration.QUARTER, isAccented = true, lyric = "Đảo(1-1)"),
                        note("b20_3", SolfegeNote.LA, 4, NoteDuration.EIGHTH, lyric = "Nhẹ")
                    )
                )
            ),
            songwriterTakeawayVi = "Đảo phách giúp câu hát tiếng Việt trẻ trung, hiện đại và giàu sức bật thay vì đều đều cứng nhắc.",
            quizQuestions = listOf(
                QuizQuestion("q20_1", "Khi âm thanh từ Phách Nhẹ ngân dài sang Phách Mạnh tiếp theo, ta gọi là gì?", listOf("Nghịch phách", "Đảo phách (Syncope)", "Nhịp lấy đà", "Liên ba"), 1, "Theo Bài 21 (tr. 85): Nhẹ ngân sang Mạnh là Đảo phách.")
            )
        ),
        MusicLesson(
            id = 15, lessonNumberLabel = "Bài 15 – 19",
            titleVi = "Âm Giai, Quãng, Hợp Âm (A–G) & Âm Thể Sáng Tác",
            summaryVi = "Cấu tạo Gam Trưởng/Thứ, 7 bậc âm giai, Hợp âm (C, Dm, Em, F, G7, Am) và 3 quy luật Âm thể.",
            stage = CurriculumStage.STAGE_4, bookPageStart = 58,
            sections = listOf(
                LessonSection(
                    headingVi = "1. Âm giai, Hợp âm A–G & Âm thể",
                    bodyVi = "• Âm giai Trưởng: 1c - 1c - 1/2c - 1c - 1c - 1c - 1/2c.\n" +
                        "• Âm giai Thứ: 1c - 1/2c - 1c - 1c - 1/2c - 1c - 1c (Thứ hòa âm tăng bậc VII lên 1/2 cung).\n" +
                        "• Chữ cái Hợp âm: A=La, B=Si, C=Đô, D=Rê, E=Mi, F=Fa, G=Sol (Thêm 'm' là Thứ, '7' là Bảy).\n" +
                        "• 3 nhiệm vụ của Hợp âm chủ (Âm thể): Khởi đầu & kết thúc bài, xuất hiện nhiều lần, và thu hút các hợp âm khác (như G7 → C, E7 → Am).",
                    highlightFormula = "Vòng hòa âm kinh điển: Chủ âm (I) → Hạ Át (IV) → Át âm (V7) → Chủ âm (I)",
                    demoNotes = listOf(
                        note("b15_1", SolfegeNote.DO, 4, NoteDuration.HALF, chordSymbol = "C(I)", lyric = "Chủ"),
                        note("b15_2", SolfegeNote.FA, 4, NoteDuration.HALF, chordSymbol = "F(IV)", lyric = "Hạ át"),
                        note("b15_3", SolfegeNote.SOL, 4, NoteDuration.HALF, chordSymbol = "G7(V7)", lyric = "Át 7"),
                        note("b15_4", SolfegeNote.DO, 5, NoteDuration.WHOLE, chordSymbol = "C(I)", lyric = "Kết")
                    )
                )
            ),
            songwriterTakeawayVi = "Để viết một bài hát hoàn chỉnh: Chọn Âm thể (C hoặc Am), đặt vòng hợp âm I–IV–V7–I và viết giai điệu bám vào các nốt của hợp âm ở phách mạnh!",
            quizQuestions = listOf(
                QuizQuestion("q15_1", "Ký hiệu hợp âm 'F♯m' có nghĩa là gì?", listOf("Fa Thăng Trưởng", "Fa Thăng Thứ", "Fa Giáng Thứ", "Fa Thứ Bảy"), 1, "F = Fa, ♯ = Thăng, m = Thứ → Fa Thăng Thứ.")
            )
        )
    )

    val musicalTerms: List<MusicalTermEntry> = listOf(
        MusicalTermEntry("Grave", "Tốc độ (Tempo)", "Nghiêm trang, rất chậm", "♩ = 44"),
        MusicalTermEntry("Largo", "Tốc độ (Tempo)", "Chậm rãi, rộng rãi", "♩ = 48"),
        MusicalTermEntry("Adagio", "Tốc độ (Tempo)", "Thong thả, khoan thai", "♩ = 54"),
        MusicalTermEntry("Andante", "Tốc độ (Tempo)", "Thư thái, không vội", "♩ = 60"),
        MusicalTermEntry("Moderato", "Tốc độ (Tempo)", "Vừa phải, chuẩn mực", "♩ = 80"),
        MusicalTermEntry("Allegretto", "Tốc độ (Tempo)", "Hơi vui, linh hoạt", "♩ = 100"),
        MusicalTermEntry("Allegro", "Tốc độ (Tempo)", "Vui tươi, nhanh", "♩ = 116"),
        MusicalTermEntry("Presto", "Tốc độ (Tempo)", "Rất nhanh", "♩ = 144"),
        MusicalTermEntry("Rall. / Rit.", "Thay đổi tốc độ", "Chậm dần lại", ""),
        MusicalTermEntry("A Tempo", "Thay đổi tốc độ", "Trở lại tốc độ ban đầu", ""),
        MusicalTermEntry("p (Piano) / pp", "Sắc thái", "Êm nhẹ / Rất khẽ", ""),
        MusicalTermEntry("f (Forte) / ff", "Sắc thái", "Mạnh / Rất mạnh", ""),
        MusicalTermEntry("Cresc. (<) / Decresc. (>)", "Sắc thái", "Mạnh dần lên / Nhẹ dần", ""),
        MusicalTermEntry("Legato / Staccato", "Diễn tấu", "Đàn hát liền tiếng / Ngắt âm gọn", ""),
        MusicalTermEntry("Syncope / Contre-temps", "Thuật ngữ", "Đảo phách / Nghịch phách", "")
    )
}
