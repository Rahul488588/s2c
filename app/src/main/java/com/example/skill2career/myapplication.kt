package com.example.skill2career

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

private val NavyDeep      = Color(0xFF1B2A4A)
private val Ivory         = Color(0xFFF5F0E8)
private val CardSurface   = Color(0xFFFAF8F4)
private val TextPrimary   = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF4A4F6A)
private val TextMuted     = Color(0xFF8B8FA8)
private val DividerLight  = Color(0xFFD4C5A9)
private val Gold          = Color(0xFFC9A84C)
private val GoldLight     = Color(0xFFF0EAD5)
private val ForestGreen   = Color(0xFF2D5A3D)
private val ForestGreenBg = Color(0xFFD5E8DC)
private val Burgundy      = Color(0xFF7A2A35)
private val BurgundyBg    = Color(0xFFF0D5D5)

data class Application(
    val id: String = "",
    val opportunity: Opportunity = Opportunity(),
    val applicantName: String = "",
    val applicantEmail: String = "",
    val whyApply: String = "",
    val resumeFileName: String? = null,
    val familyIncome: String? = null,
    val aadharCardFileName: String? = null,
    val marksheetFileName: String? = null,
    var status: String = "Pending"
)

data class User(
    val name: String = "",
    val email: String = "",
    val role: String = "Student",
    val phoneNumber: String = "",
    val branch: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplicationsScreen(navController: NavController, mainViewModel: MainViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val appliedApplicationsList = mainViewModel.myApplications

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = { Sidebar(navController, drawerState, mainViewModel) }) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Applications", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDeep)
                )
            },
            containerColor = Ivory
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Application Status", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(16.dp))

                if (appliedApplicationsList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("You haven't applied for anything yet", color = TextMuted)
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
                        items(appliedApplicationsList) { app -> ApplicationStatusCard(app) }
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationStatusCard(application: Application) {
    val statusBg = when (application.status) { "Pending" -> GoldLight; "Accepted" -> ForestGreenBg; else -> BurgundyBg }
    val statusColor = when (application.status) { "Pending" -> Gold; "Accepted" -> ForestGreen; else -> Burgundy }

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardSurface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), border = androidx.compose.foundation.BorderStroke(1.dp, DividerLight)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = application.opportunity.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Surface(color = statusBg, shape = MaterialTheme.shapes.small) {
                    Text(text = application.status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.Bold)
                }
            }
            Text(text = application.opportunity.company, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerLight)
            Text(text = "Applied as: ${application.applicantName}", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
            Text(text = "Email: ${application.applicantEmail}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            if (application.resumeFileName != null) Text(text = "Resume: ${application.resumeFileName}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            if (application.opportunity.safeType == OpportunityType.Scholarship) {
                Text(text = "Income: ${application.familyIncome}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                if (application.aadharCardFileName != null) Text(text = "Aadhar Card: ${application.aadharCardFileName}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                if (application.marksheetFileName != null) Text(text = "Marksheet: ${application.marksheetFileName}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}