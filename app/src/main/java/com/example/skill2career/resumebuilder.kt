package com.example.skill2career

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.OutputStream

// ─── Classical Professional Palette ────────────────────────────────────────────
private val RbNavyDeep      = Color(0xFF1B2A4A)
private val RbNavyMid       = Color(0xFF2E3D5E)
private val RbGold          = Color(0xFFC9A84C)
private val RbGoldLight     = Color(0xFFF0EAD5)
private val RbIvory         = Color(0xFFF5F0E8)
private val RbCardSurface   = Color(0xFFFAF8F4)
private val RbTextPrimary   = Color(0xFF1A1A2E)
private val RbTextSecondary = Color(0xFF4A4F6A)
private val RbTextMuted     = Color(0xFF8B8FA8)
private val RbDividerLight  = Color(0xFFD4C5A9)
private val RbForestGreen   = Color(0xFF2D5A3D)
// ───────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeBuilderScreen(navController: NavController, mainViewModel: MainViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("resume_draft", Context.MODE_PRIVATE) }

    var fullName by remember { mutableStateOf(prefs.getString("fullName", mainViewModel.currentUser.value?.name ?: "") ?: "") }
    var email by remember { mutableStateOf(prefs.getString("email", mainViewModel.currentUser.value?.email ?: "") ?: "") }
    var phone by remember { mutableStateOf(prefs.getString("phone", "") ?: "") }
    var linkedin by remember { mutableStateOf(prefs.getString("linkedin", "") ?: "") }
    var github by remember { mutableStateOf(prefs.getString("github", "") ?: "") }
    var intro by remember { mutableStateOf(prefs.getString("intro", "") ?: "") }
    var education by remember { mutableStateOf(prefs.getString("education", "") ?: "") }
    var experience by remember { mutableStateOf(prefs.getString("experience", "") ?: "") }
    var skills by remember { mutableStateOf(prefs.getString("skills", "") ?: "") }

    val savedProjects = prefs.getString("projects", "") ?: ""
    val initialProjects = if (savedProjects.isBlank()) listOf("") else savedProjects.split("|||")
    val projectList = remember { mutableStateListOf(*initialProjects.toTypedArray()) }

    val savedAchievements = prefs.getString("achievements", "") ?: ""
    val initialAchievements = if (savedAchievements.isBlank()) listOf("") else savedAchievements.split("|||")
    val achievementList = remember { mutableStateListOf(*initialAchievements.toTypedArray()) }

    var languages by remember { mutableStateOf(prefs.getString("languages", "") ?: "") }

    var isGenerating by remember { mutableStateOf(false) }
    var showResumePreview by remember { mutableStateOf(false) }

    val steps = listOf("Contact", "Profile", "Experience", "Skills", "Finalize")
    val pagerState = rememberPagerState(pageCount = { steps.size })

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { Sidebar(navController, drawerState, mainViewModel) }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "Resume Builder",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                "Step ${pagerState.currentPage + 1} of ${steps.size}: ${steps[pagerState.currentPage]}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFADB8CC)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = RbGold)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            prefs.edit().apply {
                                putString("fullName", fullName); putString("email", email)
                                putString("phone", phone); putString("linkedin", linkedin)
                                putString("github", github); putString("intro", intro)
                                putString("education", education); putString("experience", experience)
                                putString("skills", skills)
                                putString("projects", projectList.filter { it.isNotBlank() }.joinToString("|||"))
                                putString("achievements", achievementList.filter { it.isNotBlank() }.joinToString("|||"))
                                putString("languages", languages)
                                apply()
                            }
                            Toast.makeText(context, "Draft Saved!", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Save, contentDescription = "Save", tint = RbGold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = RbNavyDeep)
                )
            },
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = RbCardSurface
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (pagerState.currentPage > 0) {
                            OutlinedButton(
                                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RbNavyDeep),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RbDividerLight)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Back")
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        if (pagerState.currentPage < steps.size - 1) {
                            Button(
                                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RbNavyDeep)
                            ) {
                                Text("Next Step", color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = RbGold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    scope.launch {
                                        isGenerating = true
                                        delay(2000)
                                        isGenerating = false
                                        showResumePreview = true
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RbForestGreen),
                                enabled = !isGenerating && fullName.isNotBlank() && email.isNotBlank()
                            ) {
                                if (isGenerating) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generate Resume", color = Color.White, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            },
            containerColor = RbIvory
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Step Progress Bar
                LinearProgressIndicator(
                    progress = { (pagerState.currentPage + 1).toFloat() / steps.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = RbGold,
                    trackColor = RbDividerLight
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    userScrollEnabled = false,
                    verticalAlignment = Alignment.Top
                ) { page ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        when (page) {
                            0 -> ContactStep(fullName, { fullName = it }, email, { email = it }, phone, { phone = it }, linkedin, { linkedin = it }, github, { github = it })
                            1 -> ProfileStep(intro, { intro = it }, education, { education = it })
                            2 -> ExperienceStep(experience, { experience = it })
                            3 -> SkillsStep(skills, { skills = it }, projectList)
                            4 -> FinalizeStep(achievementList, languages, { languages = it })
                        }
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }

        if (showResumePreview) {
            ResumePreviewDialog(
                onDismiss = { showResumePreview = false },
                data = ResumeData(
                    fullName, email, phone, linkedin, github, intro, education, experience, skills,
                    projectList.filter { it.isNotBlank() }.joinToString("\n\n"),
                    achievementList.filter { it.isNotBlank() }.joinToString("\n\n"),
                    languages
                )
            )
        }
    }
}

@Composable
fun ContactStep(
    name: String, onName: (String) -> Unit,
    email: String, onEmail: (String) -> Unit,
    phone: String, onPhone: (String) -> Unit,
    li: String, onLi: (String) -> Unit,
    gh: String, onGh: (String) -> Unit
) {
    StepHeader("Contact Information", "Start with your basic details to help recruiters reach you.")
    ResumeTextField(name, onName, "Full Name", Icons.Default.Person)
    ResumeTextField(email, onEmail, "Email Address", Icons.Default.Email)
    ResumeTextField(phone, onPhone, "Phone Number", Icons.Default.Phone)
    ResumeTextField(li, onLi, "LinkedIn Profile URL", Icons.Default.Link)
    ResumeTextField(gh, onGh, "GitHub / Portfolio URL", Icons.Default.Language)
}

@Composable
fun ProfileStep(intro: String, onIntro: (String) -> Unit, edu: String, onEdu: (String) -> Unit) {
    StepHeader("Professional Profile", "Briefly introduce yourself and your academic background.")
    ResumeTextField(intro, onIntro, "About Me / Summary", Icons.Default.Description, singleLine = false, maxLines = 4)
    ResumeTextField(edu, onEdu, "Education (Degree, College, Year)", Icons.Default.School, singleLine = false, maxLines = 3)
}

@Composable
fun ExperienceStep(exp: String, onExp: (String) -> Unit) {
    StepHeader("Work Experience", "List your previous roles, internships, or volunteer work.")
    ResumeTextField(exp, onExp, "Role, Company & Responsibilities", Icons.Default.Work, singleLine = false, maxLines = 8)

    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        colors = CardDefaults.cardColors(containerColor = RbGoldLight),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = RbNavyDeep, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "Tip: Use action verbs like 'Developed', 'Managed', 'Led' to describe your impact.",
                fontSize = 13.sp,
                color = RbTextSecondary
            )
        }
    }
}

@Composable
fun SkillsStep(skills: String, onSkills: (String) -> Unit, projectList: MutableList<String>) {
    StepHeader("Skills & Projects", "Highlight your technical expertise and key projects.")
    ResumeTextField(skills, onSkills, "Technical Skills (comma separated)", Icons.Default.Star)

    Spacer(modifier = Modifier.height(16.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Projects", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = RbTextPrimary)
        TextButton(
            onClick = { projectList.add("") },
            colors = ButtonDefaults.textButtonColors(contentColor = RbNavyDeep)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Text("Add Project")
        }
    }

    projectList.forEachIndexed { index, project ->
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            ResumeTextField(project, { projectList[index] = it }, "Project ${index + 1}", Icons.Default.Code, modifier = Modifier.weight(1f), singleLine = false, maxLines = 3)
            if (projectList.size > 1) {
                IconButton(onClick = { projectList.removeAt(index) }) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFF7A2A35).copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
fun FinalizeStep(achievementList: MutableList<String>, languages: String, onLang: (String) -> Unit) {
    StepHeader("Final Touches", "Add achievements and languages to stand out.")

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Achievements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = RbTextPrimary)
        TextButton(
            onClick = { achievementList.add("") },
            colors = ButtonDefaults.textButtonColors(contentColor = RbNavyDeep)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Text("Add")
        }
    }

    achievementList.forEachIndexed { index, achievement ->
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            ResumeTextField(achievement, { achievementList[index] = it }, "Achievement ${index + 1}", Icons.Default.EmojiEvents, modifier = Modifier.weight(1f))
            if (achievementList.size > 1) {
                IconButton(onClick = { achievementList.removeAt(index) }) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFF7A2A35).copy(alpha = 0.7f))
                }
            }
        }
    }

    ResumeTextField(languages, onLang, "Languages (e.g. English, Hindi)", Icons.Default.Translate)
}

@Composable
fun StepHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = RbNavyDeep
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = RbTextMuted)
    }
}

@Composable
fun ResumeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = RbNavyDeep, modifier = Modifier.size(20.dp)) },
        modifier = modifier.fillMaxWidth().padding(bottom = 14.dp),
        shape = RoundedCornerShape(12.dp),
        singleLine = singleLine,
        maxLines = maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = RbNavyDeep,
            unfocusedBorderColor = RbDividerLight,
            focusedLabelColor = RbNavyDeep,
            focusedTextColor = RbTextPrimary,
            unfocusedTextColor = RbTextPrimary
        )
    )
}

data class ResumeData(
    val name: String,
    val email: String,
    val phone: String,
    val linkedin: String,
    val github: String,
    val intro: String,
    val education: String,
    val experience: String,
    val skills: String,
    val projects: String,
    val achievements: String,
    val languages: String
)

@Composable
fun ResumePreviewDialog(onDismiss: () -> Unit, data: ResumeData) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = RbCardSurface
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Live Preview", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = RbTextPrimary)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = RbTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .border(1.dp, RbDividerLight, RoundedCornerShape(12.dp))
                        .padding(20.dp)
                ) {
                    Text(
                        data.name.uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = RbTextPrimary
                    )
                    Text(
                        "${data.email} | ${data.phone}",
                        fontSize = 12.sp,
                        color = RbTextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp, color = RbNavyDeep)

                    PreviewSection("SUMMARY", data.intro)
                    PreviewSection("EDUCATION", data.education)
                    PreviewSection("EXPERIENCE", data.experience)
                    PreviewSection("PROJECTS", data.projects)
                    PreviewSection("SKILLS", data.skills)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { generatePDF(context, data) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RbNavyDeep)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, tint = RbGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download Professional PDF", fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PreviewSection(title: String, content: String) {
    if (content.isNotBlank()) {
        Column(modifier = Modifier.padding(bottom = 12.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = RbNavyDeep)
            Text(content, style = MaterialTheme.typography.bodySmall, color = RbTextSecondary, lineHeight = 18.sp)
        }
    }
}

fun generatePDF(context: Context, data: ResumeData) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()

    var y = 60f
    val margin = 50f
    val pageWidth = 595f

    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 22f
    paint.textAlign = Paint.Align.CENTER
    canvas.drawText(data.name.uppercase(), pageWidth / 2, y, paint)
    y += 25f

    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.textSize = 10f
    canvas.drawText("${data.email} | ${data.phone}", pageWidth / 2, y, paint)
    y += 15f
    canvas.drawText("LinkedIn: ${data.linkedin} | GitHub: ${data.github}", pageWidth / 2, y, paint)
    y += 20f

    paint.strokeWidth = 1f
    canvas.drawLine(margin, y, pageWidth - margin, y, paint)
    y += 30f

    paint.textAlign = Paint.Align.LEFT
    val sections = listOf(
        "SUMMARY" to data.intro,
        "EDUCATION" to data.education,
        "WORK EXPERIENCE" to data.experience,
        "PROJECTS" to data.projects,
        "ACHIEVEMENTS" to data.achievements,
        "SKILLS" to data.skills
    )

    for ((title, content) in sections) {
        if (content.isNotBlank()) {
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 12f
            paint.color = android.graphics.Color.parseColor("#1B2A4A")
            canvas.drawText(title, margin, y, paint)
            y += 18f

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 10f
            paint.color = android.graphics.Color.parseColor("#4A4F6A")
            val words = content.split(" ")
            var line = ""
            val maxWidth = pageWidth - 2 * margin
            for (word in words) {
                val testLine = if (line.isEmpty()) word else "$line $word"
                if (paint.measureText(testLine) > maxWidth) {
                    canvas.drawText(line, margin, y, paint)
                    y += 14f
                    line = word
                } else {
                    line = testLine
                }
                if (y > 800f) break
            }
            if (line.isNotEmpty()) {
                canvas.drawText(line, margin, y, paint)
                y += 14f
            }
            y += 10f
            if (y > 800f) break
        }
    }

    pdfDocument.finishPage(page)

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "${data.name.replace(" ", "_")}_Resume.pdf")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    pdfDocument.writeTo(stream)
                }
            }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "${data.name.replace(" ", "_")}_Resume.pdf")
            pdfDocument.writeTo(file.outputStream())
        }
        Toast.makeText(context, "Resume saved to Downloads!", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    } finally {
        pdfDocument.close()
    }
}
