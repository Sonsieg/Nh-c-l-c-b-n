package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.audio.MusicSynthesizer
import com.example.data.MusicCurriculumRepository
import com.example.ui.theme.ConservatoryCrimson
import com.example.ui.theme.JadeHarmony
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun HandbookScreen(modifier: Modifier = Modifier) {
    var selectedSection by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize().testTag("handbook_screen")) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val tabs = listOf("Hóa Biểu & Quãng", "Từ Điển & Tốc Độ", "Sách Gốc (126 Trang)")
            tabs.forEachIndexed { idx, title ->
                val selected = selectedSection == idx
                FilterChip(
                    selected = selected,
                    onClick = { selectedSection = idx },
                    label = {
                        Text(
                            text = title,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.height(34.dp).testTag("handbook_tab_$idx")
                )
            }
        }

        when (selectedSection) {
            0 -> KeySignatureAndIntervalsSection()
            1 -> MusicalTermsDictionarySection()
            2 -> OriginalPdfBookSection()
        }
    }
}

@Composable
private fun KeySignatureAndIntervalsSection() {
    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                "Máy Nghe Quãng (Intervals - Bài 16, tr. 65–66)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Chạm vào từng Quãng để nghe 2 nốt vang lên và cảm nhận màu sắc hòa âm:",
                style = MaterialTheme.typography.bodySmall
            )
        }

        items(MusicCurriculumRepository.intervals) { interval ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .clickable {
                        MusicSynthesizer.playChord(listOf(interval.lowMidi, interval.highMidi), 850)
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(12.dp).fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "${interval.nameVi} (${interval.shortCode}) • ${interval.distanceTextVi}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Thí dụ: ${interval.exampleVi}", style = MaterialTheme.typography.labelLarge, color = ConservatoryCrimson)
                        Text(interval.feelingVi, style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(Icons.Default.PlayArrow, contentDescription = "Nghe quãng", tint = ConservatoryCrimson)
                }
            }
        }

        item {
            Spacer(Modifier.height(6.dp))
            Text(
                "Bảng Tra Hóa Biểu (♯ và ♭) & Nốt Kết Thúc (Bài 19, tr. 77–80)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(MusicCurriculumRepository.keySignatureGuideRows) { row ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (row.count == 0) "Bộ khóa Không có dấu Hóa"
                        else "${row.count} dấu ${if (row.accidentalType == "#") "Thăng (♯)" else "Giáng (♭)"}: ${row.accidentalsListVi}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ConservatoryCrimson
                    )
                    Text("• Trưởng: ${row.majorKeyVi} (Nốt kết: ${row.majorEndingNoteVi})", style = MaterialTheme.typography.bodyMedium)
                    Text("• Thứ: ${row.minorKeyVi} (Nốt kết: ${row.minorEndingNoteVi})", style = MaterialTheme.typography.bodyMedium, color = JadeHarmony)
                }
            }
        }
    }
}

@Composable
private fun MusicalTermsDictionarySection() {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) MusicCurriculumRepository.musicalTerms
        else MusicCurriculumRepository.musicalTerms.filter {
            it.term.contains(searchQuery, ignoreCase = true) ||
                it.meaningVi.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Tìm thuật ngữ (Allegro, Legato, Syncope...)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("dictionary_search_input")
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 14.dp)
        ) {
            items(filtered) { entry ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(12.dp).fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.term, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ConservatoryCrimson)
                            Text(entry.meaningVi, style = MaterialTheme.typography.bodyMedium)
                            Text(entry.categoryVi, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (entry.bpmHint.isNotBlank()) {
                            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    entry.bpmHint,
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OriginalPdfBookSection() {
    val context = LocalContext.current
    var currentPage by remember { mutableIntStateOf(1) }
    var totalPages by remember { mutableIntStateOf(126) }
    var pageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Zoom and pan state
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var pfd by remember { mutableStateOf<ParcelFileDescriptor?>(null) }
    val renderMutex = remember { Mutex() }

    val jumpBookmarks = listOf(
        "Bìa & Tựa" to 1,
        "Lời mở đầu" to 3,
        "Bài 1: Âm thanh" to 5,
        "Bài 2: Tên nốt" to 6,
        "Bài 3: Khuông & Khóa" to 7,
        "Bài 4: Hình nốt" to 9,
        "Bài 5: Cung & Nửa cung" to 11,
        "Bài 6: Vị trí Khóa Sol" to 13,
        "Bài 7: Nhịp - Phách" to 23,
        "Bài 8: Số chỉ nhịp" to 24,
        "Bài 9: Dấu hóa" to 32,
        "Bài 10: Dấu lặng" to 38,
        "Bài 11: Dấu nối & Chấm" to 42,
        "Bài 12: Dấu hồi tấu" to 44,
        "Bài 13: Vị trí Khóa Fa" to 46,
        "Bài 14: Nhóm bất thường" to 51,
        "Bài 15: Âm giai (Gam)" to 55,
        "Bài 16: Quãng" to 62,
        "Bài 17: Hợp âm" to 64,
        "Bài 18: Tên viết tắt H.Â" to 67,
        "Bài 19: Âm thể" to 71,
        "Bài 20: Phách mạnh/nhẹ" to 78,
        "Bài 21: Đảo phách" to 82,
        "Phân tích tác phẩm" to 90,
        "Cách đánh nhịp" to 98,
        "Ký hiệu & Danh từ" to 100,
        "Mục lục" to 123
    )

    // Load PDF from APK assets into cache directory once, then initialize PdfRenderer
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                isLoading = true
                errorMessage = null
                val cacheFile = File(context.cacheDir, "nhac_ly_can_ban.pdf")

                // If not cached or size seems wrong, extract from assets or fallback file
                if (!cacheFile.exists() || cacheFile.length() < 100_000L) {
                    var copied = false
                    try {
                        context.assets.open("nhac_ly_can_ban.pdf").use { input ->
                            FileOutputStream(cacheFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        copied = true
                    } catch (_: Throwable) {
                        // Asset open failed, check host filesystem candidates
                    }

                    if (!copied) {
                        val candidates = listOf(
                            File("public/assets/aistudio/Nha_c-Ly_-Ca_n-Ba_n-Ngo_-Ngo_c-Tha__ng_ca350052.pdf"),
                            File("/app/applet/public/assets/aistudio/Nha_c-Ly_-Ca_n-Ba_n-Ngo_-Ngo_c-Tha__ng_ca350052.pdf"),
                            File("/app/applet/app/src/main/assets/nhac_ly_can_ban.pdf")
                        )
                        val hostFile = candidates.firstOrNull { it.exists() && it.length() > 0 }
                        if (hostFile != null) {
                            hostFile.inputStream().use { input ->
                                FileOutputStream(cacheFile).use { output ->
                                    input.copyTo(output)
                                }
                            }
                        }
                    }
                }

                if (!cacheFile.exists() || cacheFile.length() == 0L) {
                    throw IllegalStateException("Không tìm thấy tệp tài liệu sách gốc.")
                }

                val descriptor = ParcelFileDescriptor.open(cacheFile, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(descriptor)
                pfd = descriptor
                pdfRenderer = renderer
                totalPages = renderer.pageCount
            } catch (e: Throwable) {
                errorMessage = "Không thể tải tệp PDF: ${e.localizedMessage ?: e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Clean up PdfRenderer when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            try {
                pdfRenderer?.close()
            } catch (_: Throwable) {}
            try {
                pfd?.close()
            } catch (_: Throwable) {}
        }
    }

    // Render current page whenever currentPage or pdfRenderer updates
    LaunchedEffect(currentPage, pdfRenderer) {
        val renderer = pdfRenderer ?: return@LaunchedEffect
        // Reset zoom offset on page change
        scale = 1f
        offsetX = 0f
        offsetY = 0f
        isLoading = true

        withContext(Dispatchers.IO) {
            renderMutex.withLock {
                try {
                    val pageIndex = (currentPage - 1).coerceIn(0, totalPages - 1)
                    val page = renderer.openPage(pageIndex)
                    // High quality render scale
                    val renderScale = 2.0f
                    val targetW = (page.width * renderScale).toInt().coerceIn(600, 2400)
                    val targetH = (page.height * renderScale).toInt().coerceIn(800, 3200)

                    val bmp = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888)
                    bmp.eraseColor(android.graphics.Color.WHITE)
                    page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()

                    withContext(Dispatchers.Main) {
                        pageBitmap = bmp
                        isLoading = false
                        errorMessage = null
                    }
                } catch (e: Throwable) {
                    withContext(Dispatchers.Main) {
                        errorMessage = "Lỗi hiển thị trang: ${e.localizedMessage ?: e.message}"
                        isLoading = false
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Quick chapter bookmarks
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            jumpBookmarks.forEach { (label, pageNum) ->
                val isSelected = currentPage == pageNum
                FilterChip(
                    selected = isSelected,
                    onClick = { currentPage = pageNum },
                    label = { Text("$label (Tr.$pageNum)", maxLines = 1, softWrap = false) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        // Navigation and Zoom controls bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = { if (currentPage > 1) currentPage-- },
                    enabled = currentPage > 1,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Trước", maxLines = 1, softWrap = false)
                }

                OutlinedButton(
                    onClick = { if (currentPage < totalPages) currentPage++ },
                    enabled = currentPage < totalPages,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Sau", maxLines = 1, softWrap = false)
                    Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }

            Text(
                "Trang $currentPage / $totalPages",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false
            )

            // Zoom In / Out / Reset
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                IconButton(
                    onClick = { scale = (scale * 1.25f).coerceAtMost(3.5f) },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "Phóng to", modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = {
                        scale = (scale / 1.25f).coerceAtLeast(1f)
                        if (scale == 1f) {
                            offsetX = 0f
                            offsetY = 0f
                        }
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "Thu nhỏ", modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = {
                        scale = 1f
                        offsetX = 0f
                        offsetY = 0f
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = "Đặt lại", modifier = Modifier.size(18.dp))
                }
            }
        }

        // Main Viewer Box with Pinch-to-zoom and Pan
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 4f)
                        if (scale > 1f) {
                            offsetX += pan.x
                            offsetY += pan.y
                        } else {
                            offsetX = 0f
                            offsetY = 0f
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            val bmp = pageBitmap
            if (bmp != null) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Trang sách $currentPage",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        )
                )
            } else if (isLoading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    CircularProgressIndicator(color = ConservatoryCrimson, modifier = Modifier.size(36.dp))
                    Text(
                        "Đang tải trang sách $currentPage...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF111111)
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = errorMessage ?: "Không thể hiển thị trang sách",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ConservatoryCrimson
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Giáo trình Nhạc Lý Căn Bản Thực Hành (Ngô Ngọc Thắng) gồm 126 trang.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF222222)
                    )
                }
            }
        }
    }
}
