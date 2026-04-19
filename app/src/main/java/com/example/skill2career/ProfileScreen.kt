package com.example.skill2career

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

private val NavyDeep    = Color(0xFF1B2A4A)
private val NavyMid     = Color(0xFF2E3D5E)
private val Gold        = Color(0xFFC9A84C)
private val GoldLight   = Color(0xFFF0EAD5)
private val Ivory       = Color(0xFFF5F0E8)
private val CardSurface = Color(0xFFFAF8F4)
private val TextPrimary   = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF4A4F6A)
private val TextMuted     = Color(0xFF8B8FA8)
private val DividerLight  = Color(0xFFD4C5A9)
private val Burgundy    = Color(0xFF7A2A35)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, mainViewModel: MainViewModel) {
    val user = mainViewModel.currentUser.value
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDeep)
            )
        },
        containerColor = Ivory
    ) { paddingValues ->
        if (user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Not logged in", color = TextSecondary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Profile Picture Section
                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        modifier = Modifier
                            .size(120.dp)
                            .shadow(8.dp, CircleShape),
                        shape = CircleShape,
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(3.dp, Gold)
                    ) {
                        if (profileImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(profileImageUri),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(brush = Brush.linearGradient(listOf(NavyDeep, NavyMid))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.name.take(1).uppercase(),
                                    color = Gold,
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    // Edit/Set Photo Button
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .clickable { photoLauncher.launch("image/*") },
                        shape = CircleShape,
                        color = Gold,
                        shadowElevation = 4.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Set Profile Picture",
                            tint = NavyDeep,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = { photoLauncher.launch("image/*") }) {
                    Text(
                        text = if (profileImageUri == null) "Set Profile Picture" else "Change Picture",
                        color = NavyDeep,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(user.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(user.role, color = TextSecondary, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(32.dp))

                ProfileInfoCard(items = listOf(
                    ProfileInfoItem("Email", user.email, Icons.Default.Email),
                    ProfileInfoItem("Phone", user.phoneNumber, Icons.Default.Phone),
                    ProfileInfoItem("Branch", user.branch, Icons.Default.School),
                    ProfileInfoItem("Role", user.role, Icons.Default.Badge)
                ))

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ProfileStatCard("Applications", mainViewModel.myApplications.size.toString(), Icons.Default.Assignment, Modifier.weight(1f))
                    ProfileStatCard("Saved", mainViewModel.savedOpportunities.size.toString(), Icons.Default.Bookmark, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { 
                        mainViewModel.logout()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } } 
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Burgundy),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Logout", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ProfileInfoCard(items: List<ProfileInfoItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).background(GoldLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(item.icon, contentDescription = null, tint = NavyDeep, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(item.label, style = MaterialTheme.typography.labelMedium, color = TextMuted)
                        Text(item.value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = TextPrimary)
                    }
                }
                if (index < items.size - 1) HorizontalDivider(color = DividerLight.copy(alpha = 0.5f), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun ProfileStatCard(title: String, count: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = Gold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(count, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(title, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        }
    }
}

data class ProfileInfoItem(val label: String, val value: String, val icon: ImageVector)
