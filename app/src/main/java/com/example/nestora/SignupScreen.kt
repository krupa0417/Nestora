package com.example.nestora

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import com.google.firebase.auth.UserProfileChangeRequest

@Composable
fun SignupScreen(navController: NavHostController) {

    val auth = FirebaseAuth.getInstance()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var signupMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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

        Text(
            text = "Create Account",
            fontSize = 32.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (signupMessage.isNotEmpty()) {
            Text(
                text = signupMessage,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        BasicTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                    signupMessage = "Please fill all fields."
                    return@Button
                }

                if (password.length < 6) {
                    signupMessage = "Password must be at least 6 characters."
                    return@Button
                }

                isLoading = true

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading = false

                        if (task.isSuccessful) {
                            val user = auth.currentUser

                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build()

                            user?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener {
                                    signupMessage = "Account created successfully."
                                    navController.navigate("home")
                                }
                        } else {
                            signupMessage = task.exception?.message ?: "Signup failed."
                        }
                    }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1565C0)
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Creating Account..." else "Sign Up")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF64B5F6)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Sign In")
        }
    }
}