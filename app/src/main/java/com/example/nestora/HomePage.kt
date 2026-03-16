package com.example.nestora

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavHostController) {

    var location by remember { mutableStateOf("") }
    var checkIn by remember { mutableStateOf("") }
    var checkOut by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nestora") }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5FAFF))
                .padding(20.dp),

            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Find Hotels",
                fontSize = 24.sp,
                color = Color(0xFF0D47A1),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Text("Location")

            BasicTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Check-in Date")

            BasicTextField(
                value = checkIn,
                onValueChange = { checkIn = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Check-out Date")

            BasicTextField(
                value = checkOut,
                onValueChange = { checkOut = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    searchResult = "Hotels available in $location from $checkIn to $checkOut"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search Hotels")
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (searchResult.isNotEmpty()) {
                Text(
                    text = searchResult,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Sample Hotel Results:", fontSize = 18.sp)

                Text("• Grand City Hotel - £120/night")
                Text("• Blue Sky Resort - £95/night")
                Text("• Nestora Luxury Stay - £150/night")
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    navController.navigate("signin") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0)
                )
            ) {
                Text("Logout")
            }
        }
    }
}