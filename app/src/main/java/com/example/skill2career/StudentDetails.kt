package com.example.skill2career

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val NavyDeep      = Color(0xFF1B2A4A)
private val Gold          = Color(0xFFC9A84C)
private val GoldLight     = Color(0xFFF0EAD5)
private val Ivory         = Color(0xFFF5F0E8)
private val CardSurface   = Color(0xFFFAF8F4)
private val TextPrimary   = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF4A4F6A)
private val TextMuted     = Color(0xFF8B8FA8)
private val DividerLight  = Color(0xFFD4C5A9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetails(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var cgpa by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NavyDeep,
        unfocusedBorderColor = DividerLight,
        focusedLabelColor = NavyDeep,
        unfocusedLabelColor = TextMuted,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedLeadingIconColor = NavyDeep,
        unfocusedLeadingIconColor = TextMuted
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Student Details",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Gold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDeep)
            )
        },
        containerColor = Ivory
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = GoldLight),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Outlined.Info, contentDescription = null, tint = NavyDeep, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Fill in your details to complete your profile.",
                        color = NavyDeep,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            @Composable
            fun DetailField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector, keyboardType: KeyboardType = KeyboardType.Text) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(label) },
                    leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = label != "Address",
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    colors = fieldColors
                )
            }

            DetailField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                icon = Icons.Outlined.Person
            )

            DetailField(
                value = mobile,
                onValueChange = { mobile = it },
                label = "Mobile Number",
                icon = Icons.Outlined.Phone,
                keyboardType = KeyboardType.Phone
            )

            DetailField(
                value = address,
                onValueChange = { address = it },
                label = "Address",
                icon = Icons.Outlined.LocationOn
            )

            DetailField(
                value = cgpa,
                onValueChange = { cgpa = it },
                label = "CGPA",
                icon = Icons.Outlined.School,
                keyboardType = KeyboardType.Decimal
            )

            DetailField(
                value = skills,
                onValueChange = { skills = it },
                label = "Skills (comma-separated)",
                icon = Icons.Outlined.Star
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("studentScreen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyDeep)
            ) {
                Text(
                    text = "Save & Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}
