package com.example.skill2career

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Classical Professional Palette ────────────────────────────────────────────
private val OppNavyDeep      = Color(0xFF1B2A4A)
private val OppNavyMid       = Color(0xFF2E3D5E)
private val OppGold          = Color(0xFFC9A84C)
private val OppGoldLight     = Color(0xFFF0EAD5)
private val OppIvory         = Color(0xFFF5F0E8)
private val OppCardSurface   = Color(0xFFFAF8F4)
private val OppTextPrimary   = Color(0xFF1A1A2E)
private val OppTextSecondary = Color(0xFF4A4F6A)
private val OppTextMuted     = Color(0xFF8B8FA8)
private val OppDividerLight  = Color(0xFFD4C5A9)
private val OppForestGreen   = Color(0xFF2D5A3D)
private val OppForestGreenBg = Color(0xFFD5E8DC)
private val OppBurgundy      = Color(0xFF7A2A35)
private val OppBurgundyBg    = Color(0xFFF0D5D5)
// ───────────────────────────────────────────────────────────────────────────────

enum class OpportunityType {
    @SerializedName("Internship", alternate = ["internship"])
    Internship,
    @SerializedName("Job", alternate = ["job"])
    Job,
    @SerializedName("Scholarship", alternate = ["scholarship"])
    Scholarship;

    companion object {
        fun safeValueOf(value: String?): OpportunityType {
            if (value == null) return Internship
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: Internship
        }
    }
}

data class Opportunity(
    val id: String = "",
    val title: String = "",
    val company: String = "",
    val type: OpportunityType? = OpportunityType.Internship,
    val tags: List<String> = emptyList(),
    val location: String = "",
    val stipendOrSalary: String? = null,
    val date: String = "",
    val minCgpa: Double? = null
) {
    val safeType: OpportunityType get() = type ?: OpportunityType.Internship
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpportunitiesScreen(navController: NavController, initialFilter: String = "All", mainViewModel: MainViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(initialFilter) }
    val filters = listOf("All", "Internship", "Scholarship", "Job")

    var showApplyDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedOpportunity by remember { mutableStateOf<Opportunity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val allOpportunities = mainViewModel.opportunities

    val filteredOpportunities = allOpportunities.filter {
        (selectedFilter == "All" || (it.type?.name ?: "").equals(selectedFilter, ignoreCase = true)) &&
                (it.title.contains(searchQuery, ignoreCase = true) || it.company.contains(searchQuery, ignoreCase = true))
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { Sidebar(navController, drawerState, mainViewModel) }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Opportunities",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = OppGold)
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = OppGold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = OppNavyDeep,
                        titleContentColor = Color.White
                    )
                )
            },
            containerColor = OppIvory,
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = OppForestGreen,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    placeholder = { Text("Search opportunities...", fontSize = 14.sp, color = OppTextMuted) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = OppNavyDeep
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = OppDividerLight,
                        focusedBorderColor = OppNavyDeep,
                        cursorColor = OppNavyDeep,
                        unfocusedContainerColor = OppCardSurface,
                        focusedContainerColor = OppCardSurface,
                        focusedTextColor = OppTextPrimary,
                        unfocusedTextColor = OppTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(filters) { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedFilter = filter },
                            color = if (isSelected) OppNavyDeep else OppCardSurface,
                            border = BorderStroke(1.dp, if (isSelected) OppNavyDeep else OppDividerLight),
                            shadowElevation = if (isSelected) 2.dp else 0.dp
                        ) {
                            Text(
                                text = filter,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = if (isSelected) OppGold else OppTextSecondary,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredOpportunities.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No opportunities found", color = OppTextMuted)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredOpportunities, key = { it.id }) { opp ->
                            var visible by remember { mutableStateOf(false) }
                            LaunchedEffect(opp) { visible = true }

                            AnimatedVisibility(
                                visible = visible,
                                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 20 }
                            ) {
                                OpportunityCard(
                                    opportunity = opp,
                                    mainViewModel = mainViewModel,
                                    onApplyClick = {
                                        selectedOpportunity = opp
                                        showApplyDialog = true
                                    },
                                    onViewDetails = {
                                        selectedOpportunity = opp
                                        showDetailDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (showApplyDialog && selectedOpportunity != null) {
                ApplyFormDialog(
                    opportunity = selectedOpportunity!!,
                    mainViewModel = mainViewModel,
                    onDismiss = { showApplyDialog = false },
                    onApplySubmit = { application, uris ->
                        mainViewModel.applyForOpportunity(application, uris)
                        showApplyDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Application submitted successfully!")
                        }
                    }
                )
            }

            if (showDetailDialog && selectedOpportunity != null) {
                OpportunityDetailDialog(
                    opportunity = selectedOpportunity!!,
                    mainViewModel = mainViewModel,
                    onDismiss = { showDetailDialog = false },
                    onApply = {
                        showDetailDialog = false
                        showApplyDialog = true
                    }
                )
            }
        }
    }
}

fun getFileName(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result
}

@Composable
fun ApplyFormDialog(
    opportunity: Opportunity,
    mainViewModel: MainViewModel,
    onDismiss: () -> Unit,
    onApplySubmit: (Application, Map<String, Uri?>) -> Unit
) {
    var name by remember { mutableStateOf(mainViewModel.currentUser.value?.name ?: "") }
    var email by remember { mutableStateOf(mainViewModel.currentUser.value?.email ?: "") }
    var whyApply by remember { mutableStateOf("") }
    var resumeUri by remember { mutableStateOf<Uri?>(null) }
    var resumeName by remember { mutableStateOf("") }
    var familyIncome by remember { mutableStateOf("") }
    var aadharUri by remember { mutableStateOf<Uri?>(null) }
    var aadharName by remember { mutableStateOf("") }
    var marksheetUri by remember { mutableStateOf<Uri?>(null) }
    var marksheetName by remember { mutableStateOf("") }

    val context = LocalContext.current

    val resumeLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            resumeUri = it
            resumeName = getFileName(context, it) ?: "Resume.pdf"
        }
    }
    val marksheetLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            marksheetUri = it
            marksheetName = getFileName(context, it) ?: "Marksheet.pdf"
        }
    }
    val aadharLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            aadharUri = it
            aadharName = getFileName(context, it) ?: "Aadhar.pdf"
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.94f).wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = OppCardSurface
        ) {
            Column(
                modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Apply for", color = OppTextMuted, style = MaterialTheme.typography.labelLarge)
                Text(
                    opportunity.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = OppTextPrimary
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OppNavyDeep,
                        unfocusedBorderColor = OppDividerLight,
                        focusedContainerColor = OppCardSurface,
                        focusedTextColor = OppTextPrimary,
                        unfocusedTextColor = OppTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OppNavyDeep,
                        unfocusedBorderColor = OppDividerLight,
                        focusedContainerColor = OppCardSurface,
                        focusedTextColor = OppTextPrimary,
                        unfocusedTextColor = OppTextPrimary
                    )
                )

                if (opportunity.safeType == OpportunityType.Scholarship) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = familyIncome,
                        onValueChange = { familyIncome = it },
                        label = { Text("Annual Income") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        prefix = { Text("₹") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OppNavyDeep,
                            unfocusedBorderColor = OppDividerLight,
                            focusedContainerColor = OppCardSurface,
                            focusedTextColor = OppTextPrimary,
                            unfocusedTextColor = OppTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FileUploadButton("Aadhar Card", aadharName) { aadharLauncher.launch("application/pdf") }
                    Spacer(modifier = Modifier.height(12.dp))
                    FileUploadButton("Marksheet", marksheetName) { marksheetLauncher.launch("application/pdf") }
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                    FileUploadButton("Resume", resumeName) { resumeLauncher.launch("application/pdf") }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = whyApply,
                    onValueChange = { whyApply = it },
                    label = { Text("Why should we pick you?") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OppNavyDeep,
                        unfocusedBorderColor = OppDividerLight,
                        focusedContainerColor = OppCardSurface,
                        focusedTextColor = OppTextPrimary,
                        unfocusedTextColor = OppTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OppTextSecondary),
                        border = BorderStroke(1.dp, OppDividerLight)
                    ) { Text("Cancel") }

                    Button(
                        onClick = {
                            if (name.isNotBlank() && email.isNotBlank()) {
                                val uris = mapOf(
                                    "resume" to resumeUri,
                                    "aadhar" to aadharUri,
                                    "marksheet" to marksheetUri
                                )
                                onApplySubmit(
                                    Application(
                                        id = System.currentTimeMillis().toString(),
                                        opportunity = opportunity,
                                        applicantName = name,
                                        applicantEmail = email,
                                        whyApply = whyApply,
                                        resumeFileName = resumeName.ifEmpty { null },
                                        familyIncome = familyIncome.ifEmpty { null },
                                        aadharCardFileName = aadharName.ifEmpty { null },
                                        marksheetFileName = marksheetName.ifEmpty { null },
                                        status = "Pending"
                                    ),
                                    uris
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OppNavyDeep),
                        enabled = name.isNotBlank() && email.isNotBlank() &&
                                (opportunity.safeType != OpportunityType.Scholarship ||
                                        (familyIncome.isNotBlank() && aadharName.isNotBlank() && marksheetName.isNotBlank()))
                    ) { Text("Submit", color = Color.White) }
                }
            }
        }
    }
}

@Composable
fun FileUploadButton(label: String, fileName: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onClick() },
        color = OppIvory,
        border = BorderStroke(1.dp, if (fileName.isNotEmpty()) OppForestGreen else OppDividerLight)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (fileName.isEmpty()) Icons.Default.UploadFile else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (fileName.isEmpty()) OppTextMuted else OppForestGreen
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                if (fileName.isEmpty()) "Upload $label" else fileName,
                color = if (fileName.isEmpty()) OppTextMuted else OppForestGreen,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun OpportunityCard(
    opportunity: Opportunity,
    mainViewModel: MainViewModel,
    onApplyClick: () -> Unit,
    onViewDetails: () -> Unit = {}
) {
    val isSaved = mainViewModel.savedOpportunities.any { it.id == opportunity.id }
    val type = opportunity.safeType
    val (badgeBg, badgeFg) = when (type) {
        OpportunityType.Internship  -> Pair(OppNavyDeep.copy(alpha = 0.10f), OppNavyDeep)
        OpportunityType.Scholarship -> Pair(OppForestGreenBg, OppForestGreen)
        OpportunityType.Job         -> Pair(OppGoldLight, OppGold)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = OppCardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(color = badgeBg, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        type.name,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeFg,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(
                    onClick = { mainViewModel.toggleSaveOpportunity(opportunity) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        tint = if (isSaved) OppGold else OppTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth().clickable { onViewDetails() }
            ) {
                Text(
                    opportunity.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OppTextPrimary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    opportunity.company,
                    color = OppTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoItem(Icons.Default.LocationOn, opportunity.location)
                    opportunity.stipendOrSalary?.let { InfoItem(Icons.Default.Payments, it) }
                }

                if (opportunity.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        opportunity.tags.take(3).forEach { tag ->
                            Surface(color = OppGoldLight, shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    tag,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OppTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onApplyClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OppNavyDeep)
            ) {
                Text("Apply Now", fontWeight = FontWeight.Medium, color = Color.White)
            }
        }
    }
}

@Composable
fun OpportunityDetailDialog(
    opportunity: Opportunity,
    mainViewModel: MainViewModel,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    val isSaved = mainViewModel.savedOpportunities.any { it.id == opportunity.id }
    val type = opportunity.safeType

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.94f).wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = OppCardSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val (badgeBg, badgeFg) = when (type) {
                            OpportunityType.Internship  -> Pair(OppNavyDeep.copy(alpha = 0.10f), OppNavyDeep)
                            OpportunityType.Scholarship -> Pair(OppForestGreenBg, OppForestGreen)
                            OpportunityType.Job         -> Pair(OppGoldLight, OppGold)
                        }
                        Surface(color = badgeBg, shape = RoundedCornerShape(6.dp)) {
                            Text(
                                type.name,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = badgeFg,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(opportunity.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = OppTextPrimary)
                        Text(opportunity.company, style = MaterialTheme.typography.bodyMedium, color = OppTextSecondary)
                    }
                    Row {
                        IconButton(onClick = { mainViewModel.toggleSaveOpportunity(opportunity) }) {
                            Icon(
                                if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                tint = if (isSaved) OppGold else OppTextMuted
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = OppTextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = OppDividerLight)
                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    InfoItem(Icons.Default.LocationOn, opportunity.location)
                    opportunity.stipendOrSalary?.let { InfoItem(Icons.Default.Payments, it) }
                }

                if (opportunity.minCgpa != null && opportunity.minCgpa > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoItem(Icons.Default.School, "Min CGPA: ${opportunity.minCgpa}")
                }

                if (opportunity.date.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoItem(Icons.Default.CalendarToday, "Deadline: ${opportunity.date}")
                }

                if (opportunity.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Skills Required", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = OppTextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        opportunity.tags.forEach { tag ->
                            Surface(color = OppGoldLight, shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    tag,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = OppTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onApply,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OppNavyDeep)
                ) {
                    Text("Apply Now", fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = OppTextMuted, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = OppTextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
