package com.example.nestora

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignInScreen(navController: NavHostController) {

    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showGdprDialog by remember { mutableStateOf(true) }
    var gdprAccepted by remember { mutableStateOf(false) }
    var gdprMessage by remember { mutableStateOf("") }
    var loginMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    if (showGdprDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("GDPR Privacy Consent") },
            text = {
                Text("Nestora collects basic personal data such as your email, booking details, and accommodation preferences to provide hotel booking services. Please accept our privacy consent to continue using the app.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        gdprAccepted = true
                        showGdprDialog = false
                        gdprMessage = "Privacy consent accepted."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text("Accept")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        gdprAccepted = false
                        showGdprDialog = false
                        gdprMessage = "You must accept GDPR consent before using the app."
                    }
                ) {
                    Text("Decline")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D47A1),
                        Color(0xFF42A5F5),
                        Color(0xFFE3F2FD)
                    )
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Nestora", fontSize = 36.sp, color = Color.White)

        Text(
            text = "Sign in to continue",
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (gdprMessage.isNotEmpty()) {
            Text(gdprMessage, fontSize = 14.sp, color = Color.White)
        }

        if (loginMessage.isNotEmpty()) {
            Text(
                text = loginMessage,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        BasicTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (!gdprAccepted) {
                    gdprMessage = "Please accept GDPR consent first."
                    showGdprDialog = true
                    return@Button
                }

                if (email.isBlank() || password.isBlank()) {
                    loginMessage = "Please enter email and password."
                    return@Button
                }

                isLoading = true

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading = false

                        if (task.isSuccessful) {
                            loginMessage = "Login successful."
                            navController.navigate("home")
                        } else {
                            loginMessage = task.exception?.message ?: "Login failed."
                        }
                    }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
            modifier = Modifier.fillMaxWidth(),
            enabled = gdprAccepted && !isLoading
        ) {
            Text(if (isLoading) "Logging in..." else "Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (gdprAccepted) {
                    navController.navigate("signup")
                } else {
                    gdprMessage = "Please accept GDPR consent first."
                    showGdprDialog = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create New Account")
        }
    }
}