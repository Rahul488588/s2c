package com.example.skill2career

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
private val ForestGreen   = Color(0xFF2D5A3D)
private val ForestGreenBg = Color(0xFFD5E8DC)
private val Burgundy      = Color(0xFF7A2A35)
private val BurgundyBg    = Color(0xFFF0D5D5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchDropdown(
    selectedBranch: String,
    onBranchSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val branches = listOf("CSE", "CSA", "CSD", "CSH", "ECE", "ECI")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedBranch,
            onValueChange = {},
            readOnly = true,
            label = { Text("Branch") },
            placeholder = { Text("Select your branch") },
            leadingIcon = { Icon(imageVector = Icons.Outlined.School, contentDescription = null, tint = TextMuted) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NavyDeep, unfocusedBorderColor = DividerLight,
                focusedLabelColor = NavyDeep, unfocusedLabelColor = TextMuted,
                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                focusedLeadingIconColor = NavyDeep, unfocusedLeadingIconColor = TextMuted
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.exposedDropdownSize()) {
            branches.forEach { branch ->
                DropdownMenuItem(
                    text = { Text(branch, color = TextPrimary) },
                    onClick = { onBranchSelected(branch); expanded = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    mainViewModel: MainViewModel,
    onSignUpSuccess: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var branch by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(100); startAnimation = true }

    val passwordsMatch = password == confirmPassword || confirmPassword.isEmpty()
    fun isValidEmail(e: String) = Patterns.EMAIL_ADDRESS.matcher(e).matches()
    fun isValidPhone(p: String) = p.length >= 10 && p.all { it.isDigit() || it == '+' || it.isWhitespace() }
    fun validateForm(): String? = when {
        firstName.isBlank() -> "First name is required"
        lastName.isBlank() -> "Last name is required"
        email.isBlank() -> "Email is required"
        !isValidEmail(email) -> "Please enter a valid email address"
        phoneNumber.isBlank() -> "Phone number is required"
        !isValidPhone(phoneNumber) -> "Please enter a valid phone number"
        branch.isBlank() -> "Please select your branch"
        password.isBlank() -> "Password is required"
        password.length < 6 -> "Password must be at least 6 characters"
        !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
        !password.any { it.isDigit() } -> "Password must contain at least one number"
        confirmPassword.isBlank() -> "Please confirm your password"
        !passwordsMatch -> "Passwords do not match"
        else -> null
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NavyDeep, unfocusedBorderColor = DividerLight,
        focusedLabelColor = NavyDeep, unfocusedLabelColor = TextMuted,
        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
        focusedLeadingIconColor = NavyDeep, unfocusedLeadingIconColor = TextMuted,
        focusedTrailingIconColor = TextMuted, unfocusedTrailingIconColor = TextMuted
    )

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(NavyDeep, NavyMid)))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
                .windowInsetsPadding(WindowInsets.statusBars).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            AnimatedVisibility(visible = startAnimation, enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 3 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Gold), contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, tint = NavyDeep, modifier = Modifier.size(38.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Create Account", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = "Join Skill2Career today", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.65f), modifier = Modifier.padding(top = 6.dp))
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            AnimatedVisibility(visible = startAnimation, enter = fadeIn(tween(600, delayMillis = 100)) + slideInVertically(tween(600, delayMillis = 100)) { it / 4 }) {
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = CardSurface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(24.dp)) {

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(value = firstName, onValueChange = { firstName = it; errorMessage = null }, label = { Text("First Name") }, placeholder = { Text("Rahul") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), singleLine = true, colors = fieldColors)
                            OutlinedTextField(value = lastName, onValueChange = { lastName = it; errorMessage = null }, label = { Text("Last Name") }, placeholder = { Text("Sharma") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), singleLine = true, colors = fieldColors)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ForestGreenBg), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = "Registering as Student", fontWeight = FontWeight.SemiBold, color = ForestGreen, fontSize = 16.sp)
                                    Text(text = "Admin access is invite-only. Contact admin if needed.", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(value = email, onValueChange = { email = it; emailError = !isValidEmail(it) && it.isNotEmpty(); errorMessage = null }, label = { Text("Email address") }, placeholder = { Text("your@email.com") }, isError = emailError, leadingIcon = { Icon(imageVector = Icons.Outlined.Email, contentDescription = null, tint = if (emailError) Burgundy else TextMuted) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true, colors = fieldColors)
                        if (emailError) Text(text = "Please enter a valid email", color = Burgundy, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it; errorMessage = null }, label = { Text("Phone Number") }, placeholder = { Text("+91 98765 43210") }, leadingIcon = { Icon(imageVector = Icons.Outlined.Phone, contentDescription = null, tint = TextMuted) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true, colors = fieldColors)

                        Spacer(modifier = Modifier.height(16.dp))

                        BranchDropdown(selectedBranch = branch, onBranchSelected = { branch = it })

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(value = password, onValueChange = { password = it; errorMessage = null }, label = { Text("Password") }, placeholder = { Text("Min. 6 characters") }, leadingIcon = { Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, tint = TextMuted) }, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff, contentDescription = null, tint = TextMuted) } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true, colors = fieldColors)

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it; errorMessage = null }, label = { Text("Confirm Password") }, placeholder = { Text("Re-enter password") }, isError = !passwordsMatch && confirmPassword.isNotEmpty(), leadingIcon = { Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, tint = if (!passwordsMatch && confirmPassword.isNotEmpty()) Burgundy else TextMuted) }, visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(imageVector = if (confirmPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff, contentDescription = null, tint = TextMuted) } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true, colors = fieldColors)
                        if (!passwordsMatch && confirmPassword.isNotEmpty()) Text(text = "Passwords do not match", color = Burgundy, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))

                        errorMessage?.let {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(colors = CardDefaults.cardColors(containerColor = BurgundyBg), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = Burgundy, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = it, color = Burgundy, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        var buttonPressed by remember { mutableStateOf(false) }
                        val buttonScale by animateFloatAsState(targetValue = if (buttonPressed) 0.97f else 1f, label = "scale")

                        Button(
                            onClick = {
                                buttonPressed = true
                                val err = validateForm()
                                if (err != null) { errorMessage = err; emailError = email.isBlank() || !isValidEmail(email) }
                                else {
                                    isLoading = true; errorMessage = null
                                    val newUser = User(name = "$firstName $lastName", email = email, role = "Student", phoneNumber = phoneNumber, branch = branch)
                                    mainViewModel.signUp(newUser, password, null) { success, error -> isLoading = false; if (success) onSignUpSuccess() else errorMessage = error ?: "Sign up failed" }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp).scale(buttonScale),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = NavyDeep, disabledContainerColor = DividerLight)
                        ) {
                            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                            else Text(text = "Create Account", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerLight)
                            Text(text = "  OR  ", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerLight)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Already have an account? ", color = TextSecondary)
                            TextButton(onClick = onSignUpSuccess) {
                                Text(text = "Sign In", color = Gold, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "By signing up, you agree to our Terms of Service and Privacy Policy", style = MaterialTheme.typography.bodySmall, color = TextMuted, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}