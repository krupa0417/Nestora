package com.example.nestora

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import android.net.Uri

data class Hotel(
    val name: String,
    val location: String,
    val price: String,
    val rating: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavHostController) {

    var location by remember { mutableStateOf("") }
    var checkIn by remember { mutableStateOf("") }
    var checkOut by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val allHotels = listOf(
        Hotel("Grand Palace Hotel", "London", "£120/night", "4.5"),
        Hotel("Blue Sky Resort", "Manchester", "£95/night", "4.2"),
        Hotel("City Comfort Inn", "London", "£80/night", "4.0"),
        Hotel("Nestora Luxury Stay", "Birmingham", "£150/night", "4.8"),
        Hotel("Sunset Suites", "Liverpool", "£110/night", "4.3")
    )

    val filteredHotels = remember { mutableStateListOf<Hotel>() }

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
                .padding(16.dp)
        ) {

            Text(
                text = "Find Your Stay",
                fontSize = 26.sp,
                color = Color(0xFF0D47A1)
            )

            Text(
                text = "Search hotels by location and dates",
                fontSize = 15.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            Text(text = "Location", color = Color(0xFF0D47A1))
            BasicTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(14.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Check-in Date", color = Color(0xFF0D47A1))
            BasicTextField(
                value = checkIn,
                onValueChange = { checkIn = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(14.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Check-out Date", color = Color(0xFF0D47A1))
            BasicTextField(
                value = checkOut,
                onValueChange = { checkOut = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    filteredHotels.clear()

                    if (location.isBlank()) {
                        message = "Please enter a location."
                    } else {
                        val results = allHotels.filter {
                            it.location.contains(location, ignoreCase = true)
                        }

                        filteredHotels.addAll(results)

                        message = if (results.isEmpty()) {
                            "No hotels found for $location"
                        } else {
                            "Hotels available in $location from $checkIn to $checkOut"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0)
                )
            ) {
                Text("Search Hotels")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(filteredHotels) { hotel ->
                    HotelCard(
                        hotel = hotel,
                        onBookClick = {
                            navController.navigate(
                                "hotelDetails/${Uri.encode(hotel.name)}/${Uri.encode(hotel.location)}/${Uri.encode(hotel.price)}/${Uri.encode(hotel.rating)}"
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Button(
                onClick = {
                    navController.navigate("signin") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0)
                )
            ) {
                Text("Logout")
            }
        }
    }
}

@Composable
fun HotelCard(
    hotel: Hotel,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = hotel.name,
                fontSize = 20.sp,
                color = Color(0xFF0D47A1)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Location: ${hotel.location}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = "Price: ${hotel.price}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = "Rating: ${hotel.rating} / 5",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onBookClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    )
                ) {
                    Text("Book Now")
                }
            }
        }
    }
}