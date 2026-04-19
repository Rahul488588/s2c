package com.example.skill2career

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skill2career.network.SkillsGapAnalysis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ─── Classical Professional Palette ────────────────────────────────────────────
private val AiNavyDeep      = Color(0xFF1B2A4A)
private val AiNavyMid       = Color(0xFF2E3D5E)
private val AiGold          = Color(0xFFC9A84C)
private val AiGoldLight     = Color(0xFFF0EAD5)
private val AiIvory         = Color(0xFFF5F0E8)
private val AiCardSurface   = Color(0xFFFAF8F4)
private val AiTextPrimary   = Color(0xFF1A1A2E)
private val AiTextSecondary = Color(0xFF4A4F6A)
private val AiTextMuted     = Color(0xFF8B8FA8)
private val AiDividerLight  = Color(0xFFD4C5A9)
private val AiForestGreen   = Color(0xFF2D5A3D)
private val AiForestGreenBg = Color(0xFFD5E8DC)
private val AiBurgundy      = Color(0xFF7A2A35)
private val AiBurgundyBg    = Color(0xFFF0D5D5)
// ───────────────────────────────────────────────────────────────────────────────

enum class AnalysisTab {
    ResumeAnalysis,
    SkillsGap
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIResumeAnalysisScreen(navController: NavController, mainViewModel: MainViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(AnalysisTab.ResumeAnalysis) }

    var resumeText by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var uploadError by remember { mutableStateOf<String?>(null) }

    var analysisResult by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }

    var targetRole by remember { mutableStateOf("") }
    var skillsGapResult by remember { mutableStateOf<SkillsGapAnalysis?>(null) }
    var isAnalyzingGap by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileName(context, it) ?: "resume.pdf"
            selectedFileName = fileName
            uploadError = null

            scope.launch {
                isUploading = true
                try {
                    val extracted = withContext(Dispatchers.IO) {
                        extractTextFromUri(context, it, fileName)
                    }
                    if (extracted != null) {
                        resumeText = extracted
                        uploadError = null
                    } else {
                        uploadError = "Could not read file. Try a .txt file or paste your resume text below."
                    }
                } catch (e: Exception) {
                    uploadError = "Error reading file: ${e.message}"
                } finally {
                    isUploading = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AI Resume Analysis",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AiGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AiNavyDeep,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = AiIvory
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth(),
                containerColor = AiNavyDeep,
                contentColor = AiGold,
                divider = { HorizontalDivider(color = AiNavyMid) }
            ) {
                Tab(
                    selected = selectedTab == AnalysisTab.ResumeAnalysis,
                    onClick = { selectedTab = AnalysisTab.ResumeAnalysis },
                    text = {
                        Text(
                            "Resume Analysis",
                            fontWeight = if (selectedTab == AnalysisTab.ResumeAnalysis) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTab == AnalysisTab.ResumeAnalysis) AiGold else Color(0xFF8BA3CC)
                        )
                    },
                    selectedContentColor = AiGold,
                    unselectedContentColor = Color(0xFF8BA3CC)
                )
                Tab(
                    selected = selectedTab == AnalysisTab.SkillsGap,
                    onClick = { selectedTab = AnalysisTab.SkillsGap },
                    text = {
                        Text(
                            "Skills Gap",
                            fontWeight = if (selectedTab == AnalysisTab.SkillsGap) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTab == AnalysisTab.SkillsGap) AiGold else Color(0xFF8BA3CC)
                        )
                    },
                    selectedContentColor = AiGold,
                    unselectedContentColor = Color(0xFF8BA3CC)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AiCardSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(AiNavyDeep.copy(alpha = 0.09f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = AiNavyDeep
                            )
                        }
                        Column {
                            Text(
                                text = "Upload Resume",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AiTextPrimary
                            )
                            Text(
                                text = "TXT files work best. PDF/DOCX: paste text below.",
                                fontSize = 12.sp,
                                color = AiTextMuted
                            )
                        }
                    }

                    HorizontalDivider(color = AiDividerLight)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clickable(enabled = !isUploading) { filePickerLauncher.launch("*/*") },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedFileName != null) AiForestGreenBg else AiIvory,
                        border = BorderStroke(
                            2.dp,
                            if (selectedFileName != null) AiForestGreen else AiNavyDeep.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            when {
                                isUploading -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        color = AiNavyDeep,
                                        strokeWidth = 3.dp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Reading file...", fontSize = 13.sp, color = AiTextSecondary)
                                }
                                selectedFileName != null -> {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = AiForestGreen
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = selectedFileName!!,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AiForestGreen
                                    )
                                    Text(text = "Tap to change", fontSize = 11.sp, color = AiTextMuted)
                                }
                                else -> {
                                    Icon(
                                        imageVector = Icons.Default.AttachFile,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = AiNavyDeep
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Tap to select file",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AiTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    if (uploadError != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AiBurgundyBg),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = uploadError!!,
                                fontSize = 13.sp,
                                color = AiBurgundy,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            if (selectedTab == AnalysisTab.ResumeAnalysis) {
                Button(
                    onClick = {
                        if (resumeText.isNotBlank()) {
                            isAnalyzing = true
                            mainViewModel.analyzeResume(resumeText) { success, result ->
                                isAnalyzing = false
                                if (success && result != null) {
                                    analysisResult = buildString {
                                        appendLine("Summary:")
                                        appendLine(result.summary)
                                        appendLine()
                                        appendLine("Strengths:")
                                        result.strengths.forEach { appendLine("• $it") }
                                        appendLine()
                                        appendLine("Areas for Improvement:")
                                        result.improvements.forEach { appendLine("• $it") }
                                        appendLine()
                                        appendLine("Recommended Careers:")
                                        result.recommendedCareers.forEach { appendLine("• $it") }
                                    }.trimEnd()
                                } else {
                                    analysisResult = "Failed to analyze resume. Please check your internet connection and try again."
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AiNavyDeep),
                    enabled = resumeText.isNotBlank() && !isAnalyzing
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyzing...", color = Color.White)
                    } else {
                        Text("Analyze Resume", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }

                if (analysisResult.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AiCardSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Analysis Results",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AiNavyDeep
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AiDividerLight)
                            Text(
                                text = analysisResult,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = AiTextSecondary
                            )
                        }
                    }
                }
            }

            if (selectedTab == AnalysisTab.SkillsGap) {
                OutlinedTextField(
                    value = targetRole,
                    onValueChange = { targetRole = it },
                    label = { Text("Target Role") },
                    placeholder = { Text("e.g. Senior Software Engineer, Data Analyst...", color = AiTextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isAnalyzingGap,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AiNavyDeep,
                        unfocusedBorderColor = AiDividerLight,
                        focusedLabelColor = AiNavyDeep,
                        focusedTextColor = AiTextPrimary,
                        unfocusedTextColor = AiTextPrimary
                    )
                )

                Button(
                    onClick = {
                        if (resumeText.isNotBlank() && targetRole.isNotBlank()) {
                            isAnalyzingGap = true
                            mainViewModel.analyzeSkillsGap(resumeText, targetRole) { success, result ->
                                isAnalyzingGap = false
                                if (success) {
                                    skillsGapResult = result
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AiNavyDeep),
                    enabled = resumeText.isNotBlank() && targetRole.isNotBlank() && !isAnalyzingGap
                ) {
                    if (isAnalyzingGap) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyzing Gap...", color = Color.White)
                    } else {
                        Text("Analyze Skills Gap", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }

                skillsGapResult?.let { result ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AiCardSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Skills Gap: ${result.targetRole}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AiNavyDeep
                            )
                            HorizontalDivider(color = AiDividerLight)

                            if (result.currentSkills.isNotEmpty()) {
                                Text(
                                    text = "Your Current Skills:",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AiForestGreen
                                )
                                Text(
                                    text = result.currentSkills.joinToString(", "),
                                    fontSize = 13.sp,
                                    color = AiTextSecondary
                                )
                            }

                            if (result.missingSkills.isNotEmpty()) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = AiBurgundyBg),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Skills to Learn:",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = AiBurgundy
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        result.missingSkills.forEach { skill ->
                                            Text(text = "• $skill", fontSize = 13.sp, color = AiBurgundy)
                                        }
                                    }
                                }
                            }

                            if (result.recommendations.isNotEmpty()) {
                                Text(
                                    text = "Recommendations:",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AiTextPrimary
                                )
                                result.recommendations.forEach { rec ->
                                    Text(text = "• $rec", fontSize = 13.sp, color = AiTextSecondary)
                                }
                            }

                            if (result.learningResources.isNotEmpty()) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = AiGoldLight),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Learning Resources:",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = AiNavyDeep
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        result.learningResources.forEach { resource ->
                                            Text(text = "• $resource", fontSize = 13.sp, color = AiTextSecondary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun extractTextFromUri(
    context: android.content.Context,
    uri: Uri,
    fileName: String
): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val text = inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        inputStream.close()
        if (text.isBlank()) null else text
    } catch (e: Exception) {
        null
    }
}