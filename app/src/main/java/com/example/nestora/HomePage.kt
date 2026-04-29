package com.example.nestora

// Android
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri

// Compose Core
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

// Material Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Place

// Material3
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults

// Navigation
import androidx.navigation.NavHostController

// Permissions
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

// Firebase Firestore
import com.google.firebase.firestore.FirebaseFirestore

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Hotel(
    val name: String = "",
    val location: String = "",
    val price: String = "",
    val rating: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavHostController) {

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var location by remember { mutableStateOf("") }
    var checkIn by remember { mutableStateOf("") }
    var checkOut by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val filteredHotels = remember { mutableStateListOf<Hotel>() }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            message = if (isGranted) {
                "Location permission granted."
            } else {
                "Location permission denied."
            }
        }

    fun requestLocationPermission() {
        val permissionStatus = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            message = "Location permission already granted."
        } else {
            showPermissionDialog = true
        }
    }



    fun searchHotelsFromFirestore() {
        filteredHotels.clear()

        if (location.isBlank()) {
            message = "Please enter a location to search."
            return
        }

        isLoading = true

        db.collection("hotels")
            .get()
            .addOnSuccessListener { result ->
                isLoading = false

                val hotels = result.documents.mapNotNull { document ->
                    document.toObject(Hotel::class.java)
                }

                val results = hotels.filter {
                    it.location.contains(location, ignoreCase = true)
                }

                filteredHotels.addAll(results)

                message = if (results.isEmpty()) {
                    "No hotels found in $location"
                } else {
                    "Showing hotels in $location"
                }
            }
            .addOnFailureListener { exception ->
                isLoading = false
                message = exception.message ?: "Failed to load hotels."
            }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = {
                showPermissionDialog = false
            },
            title = {
                Text("Location Permission")
            },
            text = {
                Text("Nestora needs location permission to help find hotels near your current area.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        locationPermissionLauncher.launch(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0)
                    )
                ) {
                    Text("Allow")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        message = "Location permission was not accepted."
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

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
                    onLocationClick = {
                        requestLocationPermission()
                    },
                    onSearchClick = {
                        searchHotelsFromFirestore()
                    }
                )
            }



            if (isLoading) {
                item {
                    Text(
                        text = "Loading hotels...",
                        color = Color(0xFF546E7A),
                        fontSize = 14.sp
                    )
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCard(
    location: String,
    onLocationChange: (String) -> Unit,
    checkIn: String,
    onCheckInChange: (String) -> Unit,
    checkOut: String,
    onCheckOutChange: (String) -> Unit,
    onLocationClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    var showCheckInPicker by remember { mutableStateOf(false) }
    var showCheckOutPicker by remember { mutableStateOf(false) }

    val checkInDatePickerState = rememberDatePickerState()
    val checkOutDatePickerState = rememberDatePickerState()

    fun formatDate(milliseconds: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(Date(milliseconds))
    }

    if (showCheckInPicker) {
        DatePickerDialog(
            onDismissRequest = { showCheckInPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        checkInDatePickerState.selectedDateMillis?.let {
                            onCheckInChange(formatDate(it))
                        }
                        showCheckInPicker = false
                    }
                ) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckInPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = checkInDatePickerState)
        }
    }

    if (showCheckOutPicker) {
        DatePickerDialog(
            onDismissRequest = { showCheckOutPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        checkOutDatePickerState.selectedDateMillis?.let {
                            onCheckOutChange(formatDate(it))
                        }
                        showCheckOutPicker = false
                    }
                ) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckOutPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = checkOutDatePickerState)
        }
    }

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
                    IconButton(onClick = onLocationClick) {
                        Icon(
                            imageVector = Icons.Outlined.Place,
                            contentDescription = "Location Permission"
                        )
                    }
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
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                label = { Text("Check-in Date") },
                placeholder = { Text("Select check-in date") },
                leadingIcon = {
                    IconButton(onClick = { showCheckInPicker = true }) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = "Select Check-in Date"
                        )
                    }
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
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                label = { Text("Check-out Date") },
                placeholder = { Text("Select check-out date") },
                leadingIcon = {
                    IconButton(onClick = { showCheckOutPicker = true }) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = "Select Check-out Date"
                        )
                    }
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