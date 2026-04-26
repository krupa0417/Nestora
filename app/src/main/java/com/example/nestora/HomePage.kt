package com.example.nestora

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

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
        containerColor = Color(0xFFF4F8FF),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nestora",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("signin") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFF0D47A1)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF4F8FF))
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                HomeHeaderSection()
            }

            item {
                SearchCard(
                    location = location,
                    onLocationChange = { location = it },
                    checkIn = checkIn,
                    onCheckInChange = { checkIn = it },
                    checkOut = checkOut,
                    onCheckOutChange = { checkOut = it },
                    onSearchClick = {
                        filteredHotels.clear()

                        if (location.isBlank()) {
                            message = "Please enter a location to search."
                        } else {
                            val results = allHotels.filter {
                                it.location.contains(location, ignoreCase = true)
                            }

                            filteredHotels.addAll(results)

                            message = if (results.isEmpty()) {
                                "No hotels found in $location"
                            } else {
                                "Showing hotels in $location"
                            }
                        }
                    }
                )
            }

            if (message.isNotEmpty()) {
                item {
                    Text(
                        text = message,
                        color = Color(0xFF546E7A),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            if (filteredHotels.isNotEmpty()) {
                item {
                    Text(
                        text = "Available Hotels",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                }
            }

            items(filteredHotels) { hotel ->
                HotelCard(
                    hotel = hotel,
                    onBookClick = {
                        navController.navigate(
                            "hotelDetails/${Uri.encode(hotel.name)}/${Uri.encode(hotel.location)}/${Uri.encode(hotel.price)}/${Uri.encode(hotel.rating)}"
                        )
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun HomeHeaderSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1565C0),
                            Color(0xFF42A5F5),
                            Color(0xFF90CAF9)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Find your perfect stay",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Search hotels by city, dates, and preferences with Nestora.",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.95f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FeatureBadge("Easy Booking")
                    Spacer(modifier = Modifier.width(8.dp))
                    FeatureBadge("Top Hotels")
                }
            }
        }
    }
}

@Composable
fun FeatureBadge(text: String) {
    Box(
        modifier = Modifier
            .background(
                color = Color.White.copy(alpha = 0.22f),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SearchCard(
    location: String,
    onLocationChange: (String) -> Unit,
    checkIn: String,
    onCheckInChange: (String) -> Unit,
    checkOut: String,
    onCheckOutChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Search Hotels",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1)
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = location,
                onValueChange = onLocationChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Location") },
                placeholder = { Text("Enter city name") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Place,
                        contentDescription = "Location"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = checkIn,
                onValueChange = onCheckInChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Check-in Date") },
                placeholder = { Text("DD/MM/YYYY") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = "Check-in"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = checkOut,
                onValueChange = onCheckOutChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Check-out Date") },
                placeholder = { Text("DD/MM/YYYY") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = "Check-out"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSearchClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Search Hotels",
                    fontSize = 16.sp
                )
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
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFBBDEFB),
                                Color(0xFFE3F2FD)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Hotel",
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(42.dp)
                )
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = hotel.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D47A1)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Place,
                                contentDescription = "Place",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = hotel.location,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    RatingBadge(hotel.rating)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = hotel.price,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )

                Text(
                    text = "Includes standard room and basic facilities",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBookClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
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

@Composable
fun RatingBadge(rating: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            tint = Color(0xFFFFB300),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = rating,
            color = Color(0xFF2E7D32),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}