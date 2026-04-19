package com.example.skill2career

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Classical Professional Palette ────────────────────────────────────────────
private val CgNavyDeep      = Color(0xFF1B2A4A)
private val CgNavyMid       = Color(0xFF2E3D5E)
private val CgGold          = Color(0xFFC9A84C)
private val CgGoldLight     = Color(0xFFF0EAD5)
private val CgIvory         = Color(0xFFF5F0E8)
private val CgCardSurface   = Color(0xFFFAF8F4)
private val CgTextPrimary   = Color(0xFF1A1A2E)
private val CgTextSecondary = Color(0xFF4A4F6A)
private val CgTextMuted     = Color(0xFF8B8FA8)
private val CgDividerLight  = Color(0xFFD4C5A9)
private val CgForestGreen   = Color(0xFF2D5A3D)
private val CgForestGreenBg = Color(0xFFD5E8DC)
private val CgBurgundy      = Color(0xFF7A2A35)
private val CgBurgundyBg    = Color(0xFFF0D5D5)
// ───────────────────────────────────────────────────────────────────────────────

data class Course(
    var name: String = "",
    var credits: String = "",
    var gradePoints: String = ""
)

data class Semester(
    val id: Int,
    val courses: MutableList<Course> = mutableStateListOf(Course())
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CGPATrackerScreen(navController: NavController, mainViewModel: MainViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val semesters = remember { mutableStateListOf(Semester(1)) }

    var targetCgpa by remember { mutableStateOf("8.5") }
    var totalSemesters by remember { mutableStateOf("8") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Sidebar(navController, drawerState, mainViewModel)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "CGPA Tracker",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                "Track & plan your GPA goals",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFADB8CC)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = CgGold)
                        }
                    },
                    actions = {
                        IconButton(onClick = { semesters.clear(); semesters.add(Semester(1)) }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = CgGold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = CgNavyDeep)
                )
            },
            containerColor = CgIvory
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                var showSummary by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { showSummary = true }

                AnimatedVisibility(
                    visible = showSummary,
                    enter = fadeIn(tween(800)) + expandVertically(tween(800))
                ) {
                    CGPASummaryCard(semesters, targetCgpa.toFloatOrNull() ?: 0f)
                }

                Spacer(modifier = Modifier.height(16.dp))

                GoalSettingsCard(
                    targetCgpa = targetCgpa,
                    onTargetChange = { targetCgpa = it },
                    totalSemesters = totalSemesters,
                    onTotalSemChange = { totalSemesters = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                GoalAdvisorCard(semesters, targetCgpa.toFloatOrNull() ?: 0f, totalSemesters.toIntOrNull() ?: 8)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Your Semesters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = CgNavyDeep
                )

                Spacer(modifier = Modifier.height(16.dp))

                semesters.forEachIndexed { index, semester ->
                    var itemVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(semester) {
                        delay(index * 100L)
                        itemVisible = true
                    }

                    AnimatedVisibility(
                        visible = itemVisible,
                        enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 20 }
                    ) {
                        SemesterCard(
                            semester = semester,
                            onRemove = if (semesters.size > 1) { { semesters.removeAt(index) } } else null
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                Button(
                    onClick = { semesters.add(Semester(semesters.size + 1)) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CgNavyDeep),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = CgGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Semester", fontWeight = FontWeight.SemiBold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun GoalSettingsCard(
    targetCgpa: String,
    onTargetChange: (String) -> Unit,
    totalSemesters: String,
    onTotalSemChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CgCardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(CgNavyDeep.copy(alpha = 0.09f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = CgNavyDeep, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("Set Your Goals", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = CgTextPrimary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = targetCgpa,
                    onValueChange = onTargetChange,
                    label = { Text("Target CGPA") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CgNavyDeep,
                        unfocusedBorderColor = CgDividerLight,
                        focusedLabelColor = CgNavyDeep,
                        focusedTextColor = CgTextPrimary,
                        unfocusedTextColor = CgTextPrimary
                    )
                )
                OutlinedTextField(
                    value = totalSemesters,
                    onValueChange = onTotalSemChange,
                    label = { Text("Total Semesters") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CgNavyDeep,
                        unfocusedBorderColor = CgDividerLight,
                        focusedLabelColor = CgNavyDeep,
                        focusedTextColor = CgTextPrimary,
                        unfocusedTextColor = CgTextPrimary
                    )
                )
            }
        }
    }
}

@Composable
fun GoalAdvisorCard(semesters: List<Semester>, targetCgpa: Float, totalSems: Int) {
    var totalGradePoints = 0f
    var totalCredits = 0f
    val completedSems = semesters.size

    semesters.forEach { sem ->
        sem.courses.forEach { course ->
            val credits = course.credits.toFloatOrNull() ?: 0f
            val points = course.gradePoints.toFloatOrNull() ?: 0f
            totalGradePoints += points * credits
            totalCredits += credits
        }
    }

    val currentCgpa = if (totalCredits > 0) totalGradePoints / totalCredits else 0f
    val remainingSems = totalSems - completedSems

    val advice: String
    val adviceColor: Color
    val adviceBg: Color

    if (targetCgpa <= 0f) {
        advice = "Set a target CGPA to get reachability advice."
        adviceColor = CgTextMuted
        adviceBg = CgIvory
    } else if (remainingSems <= 0) {
        if (currentCgpa >= targetCgpa) {
            advice = "Congratulations! You reached your goal."
            adviceColor = CgForestGreen
            adviceBg = CgForestGreenBg
        } else {
            advice = "Degree completed. You missed your target by ${(targetCgpa - currentCgpa).format(2)} points."
            adviceColor = CgBurgundy
            adviceBg = CgBurgundyBg
        }
    } else {
        val avgCreditsPerSem = if (completedSems > 0) totalCredits / completedSems else 20f
        val estimatedTotalCredits = totalSems * avgCreditsPerSem
        val pointsNeeded = (targetCgpa * estimatedTotalCredits) - totalGradePoints
        val requiredFutureSgpa = pointsNeeded / (remainingSems * avgCreditsPerSem)

        when {
            requiredFutureSgpa <= 0 -> {
                advice = "You've already surpassed your goal! Keep it up to finish even stronger."
                adviceColor = CgForestGreen
                adviceBg = CgForestGreenBg
            }
            requiredFutureSgpa <= currentCgpa -> {
                advice = "On Track! You need an SGPA of ${requiredFutureSgpa.format(2)} in the next $remainingSems semesters. This is lower than your current performance."
                adviceColor = CgNavyDeep
                adviceBg = CgGoldLight
            }
            requiredFutureSgpa <= 10.0f -> {
                advice = "Push Harder! You need an SGPA of ${requiredFutureSgpa.format(2)} in the next $remainingSems semesters to reach your target of $targetCgpa."
                adviceColor = Color(0xFF7A5A00)
                adviceBg = CgGoldLight
            }
            else -> {
                advice = "Mathematically Impossible: Even with a perfect 10.0 SGPA, you can't reach $targetCgpa. Try adjusting your target to something realistic."
                adviceColor = CgBurgundy
                adviceBg = CgBurgundyBg
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = adviceBg),
        border = BorderStroke(1.dp, adviceColor.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = adviceColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("AI Goal Advisor", fontWeight = FontWeight.SemiBold, color = adviceColor, fontSize = 14.sp)
                Text(advice, style = MaterialTheme.typography.bodyMedium, color = CgTextSecondary, lineHeight = 20.sp)
            }
        }
    }
}

@Composable
fun CGPASummaryCard(semesters: List<Semester>, target: Float) {
    var totalGradePoints = 0f
    var totalCredits = 0f

    semesters.forEach { sem ->
        sem.courses.forEach { course ->
            val credits = course.credits.toFloatOrNull() ?: 0f
            val points = course.gradePoints.toFloatOrNull() ?: 0f
            totalGradePoints += points * credits
            totalCredits += credits
        }
    }

    val cgpa = if (totalCredits > 0) totalGradePoints / totalCredits else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CgNavyDeep),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "OVERALL CGPA",
                color = CgGold.copy(alpha = 0.85f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "%.2f".format(cgpa),
                color = Color.White,
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraBold
            )

            if (target > 0) {
                val progress by animateFloatAsState(
                    targetValue = (cgpa / target).coerceIn(0f, 1f),
                    animationSpec = tween(1000, easing = FastOutSlowInEasing),
                    label = "progress"
                )

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = CgGold,
                    trackColor = Color.White.copy(alpha = 0.15f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Goal: $target  (${(progress * 100).toInt()}% achieved)",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryStat("Total Credits", totalCredits.toInt().toString())
                VerticalDivider(modifier = Modifier.height(30.dp), color = Color.White.copy(alpha = 0.2f))
                SummaryStat("Semesters", semesters.size.toString())
            }
        }
    }
}

@Composable
fun SummaryStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(label, color = CgGold.copy(alpha = 0.8f), fontSize = 12.sp)
    }
}

@Composable
fun SemesterCard(semester: Semester, onRemove: (() -> Unit)?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CgCardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, CgDividerLight)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp, 24.dp)
                            .background(CgGold, RoundedCornerShape(3.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Semester ${semester.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = CgTextPrimary
                    )
                }
                if (onRemove != null) {
                    IconButton(onClick = onRemove) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove Semester",
                            tint = CgBurgundy.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            semester.courses.forEachIndexed { index, course ->
                CourseRow(
                    course = course,
                    onUpdate = { updatedCourse -> semester.courses[index] = updatedCourse },
                    onDelete = if (semester.courses.size > 1) { { semester.courses.removeAt(index) } } else null
                )
            }

            TextButton(
                onClick = { semester.courses.add(Course()) },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.textButtonColors(contentColor = CgNavyDeep)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Course", fontWeight = FontWeight.SemiBold)
            }

            var semPoints = 0f
            var semCredits = 0f
            semester.courses.forEach {
                val c = it.credits.toFloatOrNull() ?: 0f
                val g = it.gradePoints.toFloatOrNull() ?: 0f
                semPoints += c * g
                semCredits += c
            }
            val sgpa = if (semCredits > 0) semPoints / semCredits else 0f

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CgDividerLight)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Semester SGPA: ", style = MaterialTheme.typography.bodyMedium, color = CgTextMuted)
                Text(
                    text = "%.2f".format(sgpa),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = CgNavyDeep
                )
            }
        }
    }
}

@Composable
fun CourseRow(course: Course, onUpdate: (Course) -> Unit, onDelete: (() -> Unit)?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = course.name,
            onValueChange = { onUpdate(course.copy(name = it)) },
            label = { Text("Course", fontSize = 11.sp) },
            modifier = Modifier.weight(2.2f),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CgNavyDeep,
                unfocusedBorderColor = CgDividerLight,
                focusedLabelColor = CgNavyDeep,
                focusedTextColor = CgTextPrimary,
                unfocusedTextColor = CgTextPrimary
            )
        )
        OutlinedTextField(
            value = course.credits,
            onValueChange = { onUpdate(course.copy(credits = it)) },
            label = { Text("Credits", fontSize = 11.sp) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CgNavyDeep,
                unfocusedBorderColor = CgDividerLight,
                focusedLabelColor = CgNavyDeep,
                focusedTextColor = CgTextPrimary,
                unfocusedTextColor = CgTextPrimary
            )
        )
        OutlinedTextField(
            value = course.gradePoints,
            onValueChange = { onUpdate(course.copy(gradePoints = it)) },
            label = { Text("Grade", fontSize = 11.sp) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CgNavyDeep,
                unfocusedBorderColor = CgDividerLight,
                focusedLabelColor = CgNavyDeep,
                focusedTextColor = CgTextPrimary,
                unfocusedTextColor = CgTextPrimary
            )
        )
        if (onDelete != null) {
            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove Course",
                    tint = CgTextMuted
                )
            }
        }
    }
}

fun Float.format(digits: Int) = "%.${digits}f".format(this)
