package com.example.skill2career

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val NavyDeep      = Color(0xFF1B2A4A)
private val NavyMid       = Color(0xFF2E3D5E)
private val Gold          = Color(0xFFC9A84C)
private val GoldLight     = Color(0xFFF0EAD5)
private val CardSurface   = Color(0xFFFAF8F4)
private val TextPrimary   = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF4A4F6A)
private val TextMuted     = Color(0xFF8B8FA8)
private val DividerLight  = Color(0xFFD4C5A9)
private val Burgundy      = Color(0xFF7A2A35)
private val BurgundyBg    = Color(0xFFF0D5D5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    mainViewModel: MainViewModel,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var selectedLoginType by remember { mutableStateOf("Student") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var adminLoginStep by remember { mutableStateOf("email") }

    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
    }

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validateForm(): String? {
        return when {
            email.isBlank() -> "Email is required"
            !isValidEmail(email) -> "Please enter a valid email address"
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }

    val backgroundGradient = Brush.verticalGradient(colors = listOf(NavyDeep, NavyMid))

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .windowInsetsPadding(WindowInsets.statusBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 3 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(Gold.copy(alpha = 0.25f), Color.Transparent))),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier.size(72.dp).clip(CircleShape).background(Gold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.School, contentDescription = null, tint = NavyDeep, modifier = Modifier.size(38.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "Welcome Back", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = "Sign in to continue", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.65f), modifier = Modifier.padding(top = 6.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Select Login Type", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Student" to Icons.Default.Person, "Admin" to Icons.Default.AdminPanelSettings).forEach { (type, icon) ->
                    val selected = selectedLoginType == type
                    val containerColor by animateColorAsState(targetValue = if (selected) Gold else Color.Transparent, label = "container")
                    val contentColor by animateColorAsState(targetValue = if (selected) NavyDeep else Color.White.copy(alpha = 0.65f), label = "content")
                    Surface(
                        onClick = { selectedLoginType = type; email = ""; password = ""; adminLoginStep = "email"; errorMessage = null },
                        shape = RoundedCornerShape(10.dp),
                        color = containerColor,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(modifier = Modifier.height(46.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = type, color = contentColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedLoginType == "Admin") {
                Card(colors = CardDefaults.cardColors(containerColor = GoldLight), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = NavyDeep, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Admin Login", color = NavyDeep, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = if (adminLoginStep == "email") "Enter your email to request access. Super admin will approve and set your password." else "Enter your password to login.",
                                color = TextSecondary, style = MaterialTheme.typography.bodySmall, fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(tween(600, delayMillis = 200)) { it / 5 }
            ) {
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = CardSurface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; emailError = !isValidEmail(it) && it.isNotEmpty(); errorMessage = null },
                            label = { Text("Email address") },
                            isError = emailError || errorMessage != null,
                            leadingIcon = { Icon(imageVector = Icons.Outlined.Email, contentDescription = null, tint = if (emailError) Burgundy else TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, autoCorrectEnabled = false),
                            modifier = Modifier.fillMaxWidth().background(Color.White),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NavyDeep, unfocusedBorderColor = DividerLight,
                                focusedLabelColor = NavyDeep, unfocusedLabelColor = TextMuted,
                                focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                                focusedLeadingIconColor = NavyDeep, unfocusedLeadingIconColor = TextMuted,
                                cursorColor = Color.Black
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black)
                        )
                        if (emailError) {
                            Text(text = "Please enter a valid email address", color = Burgundy, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMessage = null },
                            label = { Text("Password") },
                            leadingIcon = { Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, tint = TextMuted) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff, contentDescription = null, tint = TextMuted)
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth().background(Color.White),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NavyDeep, unfocusedBorderColor = DividerLight,
                                focusedLabelColor = NavyDeep, unfocusedLabelColor = TextMuted,
                                focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                                focusedLeadingIconColor = NavyDeep, unfocusedLeadingIconColor = TextMuted,
                                focusedTrailingIconColor = TextMuted, unfocusedTrailingIconColor = TextMuted,
                                cursorColor = Color.Black
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            TextButton(onClick = { }) {
                                Text("Forgot Password?", color = TextSecondary, fontSize = 13.sp)
                            }
                        }

                        errorMessage?.let {
                            Card(colors = CardDefaults.cardColors(containerColor = BurgundyBg), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = Burgundy, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = it, color = Burgundy, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        var buttonPressed by remember { mutableStateOf(false) }
                        val buttonScale by animateFloatAsState(targetValue = if (buttonPressed) 0.97f else 1f, label = "buttonScale")

                        Button(
                            onClick = {
                                buttonPressed = true
                                val validationError = validateForm()
                                if (validationError != null) {
                                    errorMessage = validationError
                                    emailError = email.isBlank() || !isValidEmail(email)
                                } else {
                                    isLoading = true
                                    errorMessage = null
                                    if (selectedLoginType == "Student") {
                                        mainViewModel.login(email, password) { success, error ->
                                            isLoading = false
                                            if (success) onLoginClick() else errorMessage = error ?: "Invalid credentials"
                                        }
                                    } else {
                                        if (adminLoginStep == "email") {
                                            mainViewModel.adminEmailLogin(email) { result ->
                                                isLoading = false
                                                when (result.status) {
                                                    "requires_password" -> { adminLoginStep = "password"; errorMessage = null }
                                                    "pending_approval" -> errorMessage = "Your admin access request is pending approval."
                                                    "rejected" -> errorMessage = "Your request was rejected. Contact super admin."
                                                    "request_created" -> errorMessage = "Request submitted! Super admin will review. Check back later."
                                                    else -> errorMessage = result.message ?: "Unknown error"
                                                }
                                            }
                                        } else {
                                            mainViewModel.adminPasswordLogin(email, password) { success, error ->
                                                isLoading = false
                                                if (success) onLoginClick() else errorMessage = error ?: "Invalid password"
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp).scale(buttonScale),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = NavyDeep, disabledContainerColor = DividerLight)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                            } else {
                                val buttonText = when {
                                    selectedLoginType == "Student" -> "Sign In as Student"
                                    adminLoginStep == "email" -> "Submit Email for Access"
                                    else -> "Sign In as Admin"
                                }
                                Text(text = buttonText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerLight)
                            Text(text = "  OR  ", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerLight)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Don't have an account? ", color = TextSecondary)
                            TextButton(onClick = onSignUpClick) {
                                Text(text = "Sign Up", color = Gold, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}