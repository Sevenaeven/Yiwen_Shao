package com.hw9.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hw9.app.ui.theme.HW9Theme
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox

import androidx.compose.material3.Icon
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.alpha

import androidx.compose.foundation.*
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.input.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.json.JSONArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.Serializable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import kotlinx.serialization.builtins.ListSerializer
import java.net.URLEncoder
import java.net.URLDecoder;

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.text.font.FontStyle
import coil.compose.rememberImagePainter

import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.TabRowDefaults
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.lifecycle.ViewModel
import com.google.gson.Gson

import android.content.SharedPreferences
import com.google.gson.reflect.TypeToken

class MainActivity : ComponentActivity() {
    private val eventViewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(
            savedInstanceState
        )

        System.setProperty("http.proxyHost", "10.0.2.2")
        setContent {
            HW9Theme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(navController,eventViewModel)
                        }

                        composable("eventDetail/{eventResponse}"){
                            backStackEntry ->
                            val responseParam = backStackEntry.arguments?.getString("eventResponse")
                            val responseString = URLDecoder.decode(responseParam, "utf-8")
                            IsLoading(responseString,navController,eventViewModel)
                        }
                    }
                }
            }
        }
    }
}


@Serializable
data class EventSearch(
    val localDate: String,
    val localTime: String,
    val icon: String,
    val event: String,
    val genre: String,
    val venue: String,
    val id: String
)

@Serializable
data class EventDetail(
    val id: String,
    val detailname: String,
    val detailLocalDate: String,
    val detailLocalTime: String,
    val detailTicketStatus: String,
    val artistTitle: String,
    val artistList: String,
    val detailVenue: String,
    val detailGenre: String,
    val detailPrice: String,
    val detailTicketUrl: String,
    val detailSeatMap: String,
    val currency : String
)

@Serializable
data class ArtistGenre(
    val artistName: String,
    val musicRelated: String
)

@Serializable
data class ArtistDetail(
    val name: String,
    val followers: String,
    val popularity: String,
    val spotifyLink: String,
    val album: String,
    val image: String
)

@Serializable
data class VenueDetail(
    val venueName: String,
    val longitude: String,
    val latitude: String,
    val venueAddress: String,
    val cityName: String,
    val phone: String,
    val openHoure: String,
    val childRule: String,
    val generalRule: String
)


class EventViewModel(context: Context) : ViewModel() {

    constructor() : this(context = MyApplication.context)

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    val favoriteEvents = mutableStateListOf<EventSearch>()

    init {
        favoriteEvents.addAll(loadFavoriteEvents())
    }

    fun addToFavorites(event: EventSearch) {
        favoriteEvents.add(event)
        saveFavoriteEvents()
    }

    fun deleteFavorites(event: EventSearch) {
        favoriteEvents.remove(event)
        saveFavoriteEvents()
    }

    private fun saveFavoriteEvents() {
        val json = gson.toJson(favoriteEvents)
        sharedPreferences.edit().putString("favoriteEvents", json).apply()
    }

    private fun loadFavoriteEvents(): MutableList<EventSearch> {
        val json = sharedPreferences.getString("favoriteEvents", null)
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<MutableList<EventSearch>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    var allSearchResult by mutableStateOf("")

    fun addSearchResult(result: String){
        allSearchResult = result
    }

    fun deleteSearchResult(){
        allSearchResult = ""
    }

    var showAutoMap by mutableStateOf(true)

    fun disableAutoMap(){
        showAutoMap = false
    }
}

enum class Category(val displayName: String) {
    All("All"),
    Music("Music"),
    Sports("Sports"),
    ArtsAndTheatre("Arts & Theatre"),
    Film("Film"),
    Miscellaneous("Miscellaneous")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController,eventViewModel: EventViewModel = viewModel()){

    var selectedTabIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    if(eventViewModel.showAutoMap){
        LaunchedEffect(true) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val locationAuto = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (locationAuto != null) {

                }
            } else {
                ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
        eventViewModel.disableAutoMap()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        //EventFinder
        Box(
            modifier = Modifier
                .background(Color(0xFF222221))
                .fillMaxWidth()
                .height(50.dp)
                .padding(8.dp)
        ) {
            Text(
                text = "EventFinder",
                color = Color(102, 201, 0),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        color = Color(102, 201, 0),
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                    )
                },
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedTabIndex = 0
                    },
                    content = {
                        Row(verticalAlignment = Alignment.CenterVertically)
                        {
                            Spacer(modifier = Modifier.widthIn(4.dp))
                            Text(text = "SEARCH")
                        }
                    },
                    modifier = Modifier
                        .background(Color.Black)
                        .height(50.dp),
                    selectedContentColor = Color(102, 201, 0),
                    unselectedContentColor = Color.White
                )

                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        selectedTabIndex = 1
                    },
                    content = {
                        Row(verticalAlignment = Alignment.CenterVertically)
                        {
                            Spacer(modifier = Modifier.widthIn(4.dp))
                            Text(text = "FAVORITES")
                        }
                    },
                    modifier = Modifier
                        .background(Color.Black)
                        .height(50.dp),
                    selectedContentColor = Color(102, 201, 0),
                    unselectedContentColor = Color.White
                )
            }
        }

        var detaiResponse by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var newPage by remember { mutableStateOf(false) }
        var eventapiUrl by remember { mutableStateOf("") }
        var apiUrlStatus by remember { mutableStateOf(false) }
        var detailStatus by remember { mutableStateOf(false) }

        when (selectedTabIndex) {
            0 -> {
                Box(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .padding(top = 20.dp)
                ) {
                   // SearchForm(navController)
                    //****
                    var keyword by remember { mutableStateOf("") }
                    var distance by remember { mutableStateOf("10") }
                    var category by remember { mutableStateOf(Category.All) }
                    var location by remember { mutableStateOf("") }
                    var autoDetectLocation by remember { mutableStateOf("")}
                    var autoDetect by remember { mutableStateOf(false)}
                    var geoInput by remember { mutableStateOf("") }
                    var locationInput by remember { mutableStateOf("") }
                    var clearText by remember { mutableStateOf(false) }

                    //In this block, some code is adapted from https://www.boltuix.com/2021/12/snackbar-host_25.html
                    //The block is from 117-125 lines
                    val snackState = remember { SnackbarHostState()}
                    val scope = rememberCoroutineScope()
                    var isSnack by remember { mutableStateOf(false)}

                    var selectedOption by remember { mutableStateOf("") }

                    fun Clear(){
                        keyword = ""
                        distance = "10"
                        category = Category.All
                        location = ""
                        autoDetectLocation = ""
                        autoDetect = false
                        geoInput = ""
                        locationInput= ""
                        detaiResponse = ""
                        isLoading = false
                        newPage = false
                        eventapiUrl = ""
                        apiUrlStatus = false
                        detailStatus = false
                        selectedOption = ""
                        clearText = true
                        eventViewModel.deleteSearchResult()
                    }

                    fun launchSnackbar(message: String, actionLabel : String?=null, duration: SnackbarDuration = SnackbarDuration.Short){
                        scope.launch {
                            snackState.showSnackbar(
                                message = "Please fill all fields",
                                duration=duration
                            )
                        }
                    }

                    LaunchedEffect(autoDetect) {
                        if (autoDetect) {
                            sendDataToGeoAPI(
                                context = context,
                                apiUrl = "https://ipinfo.io/json?token=f8d287f84a5f3c"
                            ){  apiResponse ->
                                val jsonObject = JSONObject(apiResponse)
                                autoDetectLocation = jsonObject.getString("loc")
                            }
                        }
                    }

                    LaunchedEffect(isLoading) {
                        if (isLoading) {

                            newPage =true

                            if(!autoDetect){
                                geoInput =  location.replace(" ", "+").replace(",", "+").filter { it != '.' }

                                sendDataToGeoAPI(
                                    context = context,
                                    apiUrl = "https://maps.googleapis.com/maps/api/geocode/json?address="+geoInput+"&key=AIzaSyDqkDt5Z3gLGpSnsx35ZnBQx3wM1tte6eM"
                                ){  apiResponse ->
                                    var geoResponse = apiResponse
                                    val jsonObject = JSONObject(geoResponse)
                                    val resultsArray = jsonObject.getJSONArray("results")
                                    val firstResult = resultsArray.getJSONObject(0)
                                    val geometry = firstResult.getJSONObject("geometry")
                                    val location = geometry.getJSONObject("location")
                                    val lat = location.getDouble("lat")
                                    val lng = location.getDouble("lng")
                                    locationInput = lat.toString()+","+lng.toString()

                                    eventapiUrl = "https://nodejs-backend-384105.wl.r.appspot.com/search?keyword=$keyword&distance=$distance&category=$category&location=$locationInput"

                                    isLoading = false
                                    apiUrlStatus = true
                                }
                            }else{
                                locationInput = autoDetectLocation

                                eventapiUrl = "https://nodejs-backend-384105.wl.r.appspot.com/search?keyword=$keyword&distance=$distance&category=$category&location=$locationInput"
                                isLoading = false
                                apiUrlStatus = true
                            }
                        }
                    }

                    LaunchedEffect(apiUrlStatus) {
                        if(apiUrlStatus){
                            sendDataToSearchAPI(
                                context = context,
                                apiUrl = eventapiUrl
                            ){  apiResponse ->
                                detaiResponse = apiResponse
                                eventViewModel.addSearchResult(detaiResponse)
                                detailStatus = true
                            }

                        }
                    }

                    if(!newPage && !isLoading && !detailStatus && eventViewModel.allSearchResult == ""){
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray)
                        ) {

                            //submit form
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                            ) {
                                //keyword
                                Column(
                                    modifier = Modifier
                                        .background(Color.Black, RoundedCornerShape(16.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "Keyword*",
                                        color = Color(102, 201, 0),
                                    )

                                    keyword = AutoCompleteTextField(onOptionSelected = {  option ->
                                        selectedOption = option }, clearText)
                                    if(keyword == ""){
                                        clearText = false
                                    }

                                    Divider(
                                        color = Color(102, 201, 0),
                                        modifier = Modifier
                                            .height(0.8.dp)
                                            .fillMaxHeight()
                                            .fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    //distance
                                    Text(
                                        text = "Distance(Miles)*",
                                        color = Color(102, 201, 0),
                                    )
                                    TextField(
                                        value = distance,
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        onValueChange = { distance = it },
                                        textStyle = TextStyle(color = Color.White),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            containerColor = Color.Black
                                        )
                                    )

                                    Divider(
                                        color = Color(102, 201, 0),
                                        modifier = Modifier
                                            .height(0.8.dp)
                                            .fillMaxHeight()
                                            .fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    //category
                                    Text(
                                        text = "Category*",
                                        color = Color(102, 201, 0),
                                    )
                                    category = CategoryDropDownList(
                                        selectedCategory = category,
                                        onCategorySelected = { category = it }
                                    )

                                    //location
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Location*",
                                            color = Color(102, 201, 0),
                                        )
                                        SwitchButton(autoDetect) { newValue ->
                                            autoDetect = newValue
                                        }
                                    }

                                    if (!autoDetect) {
                                        TextField(
                                            value = location,
                                            placeholder = { Text("Enter the Location") },
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            onValueChange = { location = it },
                                            textStyle = TextStyle(color = Color.White),
                                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                                containerColor = Color.Black
                                            )
                                        )
                                        Divider(
                                            color = Color(102, 201, 0),
                                            modifier = Modifier
                                                .height(0.8.dp)
                                                .fillMaxHeight()
                                                .fillMaxWidth()
                                        )
                                    } else {
                                        location = ""
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    //submit button
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            Button(
                                                onClick = {
                                                    if(keyword == "" || (autoDetect == false && location == "")){
                                                        isSnack = true
                                                        launchSnackbar(
                                                            message = "",
                                                            duration = SnackbarDuration.Short
                                                        )
                                                    }else{
                                                        isLoading = true
                                                    }
                                                },
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .width(150.dp),
                                                colors = ButtonDefaults.buttonColors(Color(102, 201, 0)),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("Search", color = Color.White, fontSize = 20.sp)
                                            }


                                            Button(
                                                onClick = {
                                                    Clear()
                                                          },
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .width(150.dp),
                                                colors = ButtonDefaults.buttonColors(Color(0xFFFFA500)),
                                                shape = RoundedCornerShape(10.dp)

                                            ) {
                                                Text("Clear", color = Color.White, fontSize = 20.sp)
                                            }
                                        }
                                    }

                                }

                            }
                        }
                        if(isSnack == true){
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                Alignment.BottomCenter
                            ) {
                                SnackbarHost(hostState = snackState)
                            }
                        }

                    }else if(detailStatus || eventViewModel.allSearchResult != ""){

                        Box(
                            modifier = Modifier.fillMaxSize(),
                        ){
                            Column() {
                                Box(){
                                    IconButton(
                                        onClick = {
                                            Clear()
                                        },
                                        modifier = Modifier
                                            .width(200.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically){
                                            Icon(
                                                painter = painterResource(id = R.drawable.green_back_btn),
                                                contentDescription = null,
                                                tint = Color.Black,
                                            )
                                            Text(
                                                text = "Back to Search",
                                                color = Color.Black,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier
                                                    .padding(start=5.dp)
                                            )
                                        }
                                    }
                                }
                                if(detaiResponse == "[]"){
                                    Box (
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Transparent)
                                            .padding(40.dp)
                                    ){
                                        Box(
                                            modifier = Modifier
                                                .background(Color.Black, RoundedCornerShape(16.dp))
                                                .height(40.dp)
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "No events found",
                                                color = Color(102, 201, 0),
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }else{
                                    SearchResultScreen(eventViewModel.allSearchResult,navController,eventViewModel)
                                }
                            }
                        }

                    }else{
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    //****
                }
            }
            1 ->{

                val favoriteEvents = eventViewModel.favoriteEvents

                if(favoriteEvents.size != 0){

                    LazyColumn {
                        items(favoriteEvents) { searchResult ->
                            SearchResultBlock(navController,searchResult,eventViewModel)
                        }
                    }

                }else{

                    Box (
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                            .padding(40.dp)
                    ){
                        Box(
                            modifier = Modifier
                                .background(Color.Black, RoundedCornerShape(16.dp))
                                .height(40.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No favorites available",
                                color = Color(102, 201, 0),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteTextField(
    onOptionSelected: (String) -> Unit, clearText: Boolean
):String{
    var textFieldValue by remember { mutableStateOf("") }
    var filteredOptions by remember { mutableStateOf(emptyList<String>()) }
    val context = LocalContext.current

    var showOptions by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    if (clearText) {
        textFieldValue = ""
        filteredOptions = emptyList()
        showOptions = false
    }

    LaunchedEffect(textFieldValue) {
        getOptionsFromBackend(context, textFieldValue) { options ->
            if(!selected){
                filteredOptions = options
                showOptions = textFieldValue.isNotBlank()
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = { showOptions = false })
    ) {
        Column {

            TextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    scope.launch {
                        getOptionsFromBackend(context, textFieldValue) { options ->
                            if(!selected){
                                filteredOptions = options
                                showOptions = textFieldValue.isNotBlank()
                            }
                        }
                    }
                    selected = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        showOptions = true
                    }
                    ),
                placeholder = { Text("Enter the Keyword") },
                textStyle = TextStyle(color = Color.White),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        showOptions = false
                    }
                )
            )
            if (showOptions) {
                LazyColumn {
                    items(filteredOptions) { option ->
                        Text(
                            text = option,
                            modifier = Modifier
                                .clickable {
                                    onOptionSelected(option)
                                    textFieldValue = option
                                    showOptions = false
                                    selected = true
                                }
                                .padding(16.dp),
                            color = Color(102, 201, 0),
                        )
                    }
                }
            }
        }
    }

    return textFieldValue
}

suspend fun getOptionsFromBackend(context: Context, input: String, onSuccess: (List<String>) -> Unit) {

    val apiUrl = "https://nodejs-backend-384105.wl.r.appspot.com/autoSearch?search="+input
    val options = mutableListOf<String>()

    val requestQueue = Volley.newRequestQueue(context)

    val stringRequest = StringRequest(
        Request.Method.GET,
        apiUrl,
        { response ->

            val jsonArray = JSONArray(response)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                var event = jsonObject.getString("name")
                options.add(event)
            }
            onSuccess(options)
        },
        { error ->
            Log.e("Error", error.toString())
        }
    )
    requestQueue.add(stringRequest)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropDownList(
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit): Category{
    val category = listOf(Category.All, Category.Music, Category.Sports, Category.ArtsAndTheatre, Category.Film, Category.Miscellaneous)
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
    ) {
        //In this block, some code is adapted from https://alexzh.com/jetpack-compose-dropdownmenu/
        //The block is from 287-290, 296-298 lines
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            TextField(
                value = selectedCategory.displayName,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    TrailingIcon(
                        expanded = expanded,
                        color = Color(102, 201, 0)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.Black,
                    textColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            //In this block, some code is adapted from https://alexzh.com/jetpack-compose-dropdownmenu/
            //The block is from 320-329 lines
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
            ) {
                category.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item.displayName,
                                color = Color.White,
                                modifier = Modifier
                                    .background(Color.Black)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        },
                        onClick = {
                            onCategorySelected(item)
                            expanded = false
                            //Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.background(Color.Black)
                    )
                }
            }
        }
    }
    return selectedCategory
}

@Composable
private fun TrailingIcon(
    expanded: Boolean,
    color: Color
) {
    Icon(
        imageVector = Icons.Filled.ArrowDropDown,
        contentDescription = "",
        tint = color,
        modifier = Modifier
            .size(24.dp)
            .rotate(if (expanded) 180f else 0f)
            .padding()
    )
}

@Composable
private fun SwitchButton(selected: Boolean, onSelectedChange: (Boolean) -> Unit) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Auto-Detect",
            color = Color.White
        )

        //This block of code is adapted from https://semicolonspace.com/jetpack-compose-switch-button/
        //The block is from 391-397
        Switch(
            checked = selected,
            onCheckedChange = onSelectedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(102, 201, 0),
                checkedTrackColor = Color(37, 49, 37, 255)
            ),
            modifier = Modifier
                .scale(scale = 1f)
                .padding(start = 10.dp)
        )
    }
}

@Composable
fun SearchResultScreen(responseString: String,navController: NavHostController,eventViewModel: EventViewModel = viewModel()) {
    var eventSearch = mutableListOf<EventSearch>()
    val jsonArray = JSONArray(responseString)
    for (i in 0 until jsonArray.length()) {
        val jsonObject = jsonArray.getJSONObject(i)
        val searchResult = EventSearch(
            localDate = jsonObject.getString("LocalDate"),
            localTime = jsonObject.getString("LocalTime"),
            icon = jsonObject.getString("Icon"),
            event = jsonObject.getString("Event"),
            genre = jsonObject.getString("Genre"),
            venue = jsonObject.getString("Venue"),
            id = jsonObject.getString("id")
        )
        eventSearch.add(searchResult)
    }


    LazyColumn {
        items(eventSearch) { searchResult ->
            SearchResultBlock(navController,searchResult,eventViewModel)
        }
    }
}


@Composable
fun ShowIcon(url:String) {
    val painter = rememberImagePainter(data = url)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .width(90.dp)
            .height(90.dp)
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchResultBlock(navController: NavHostController,searchResult:EventSearch,eventViewModel: EventViewModel = viewModel()) {

    fun formatDate(dateInput: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateInput)
        return outputFormat.format(date)
    }

    fun formatTime(timeInput: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
        val time = inputFormat.parse(timeInput)
        return outputFormat.format(time)
    }


    fun ExistStatus(eventId:String):Boolean{

        var isClick = false
        val favoriteEvents = eventViewModel.favoriteEvents

        if(favoriteEvents.size != 0){
            for (i in 0 until favoriteEvents.size){
                if(favoriteEvents[i].id == eventId){
                    isClick = true
                    break
                }
            }
        }
        return isClick
    }

    fun changeFavorites(event: EventSearch) {
        if(!ExistStatus(event.id)){
            eventViewModel.addToFavorites(event)

        }else{
            eventViewModel.deleteFavorites(event)
        }
    }

    val gson = Gson()
    val searchResultJson = gson.toJson(searchResult)
    val encodedResponse = URLEncoder.encode(searchResultJson, "utf-8")

    val snackState = remember { SnackbarHostState()}
    val scope = rememberCoroutineScope()
    fun launchSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.Short){
        scope.launch {
            snackState.showSnackbar(
                message = message,
                duration = duration
            )
        }
    }

    Column() {
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .clickable(onClick = {
                    navController.navigate("eventDetail/$encodedResponse")
                })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black, shape = RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (searchResult.icon != "") {
                        ShowIcon(searchResult.icon)
                    }

                    Column(
                        modifier = Modifier
                            .width(150.dp)
                            .padding(start = 10.dp)
                    ) {

                        if (searchResult.event != "") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = searchResult.event,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .basicMarquee(),
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        if (searchResult.venue != "") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = searchResult.venue,
                                    fontSize = 17.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .basicMarquee(),
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        if (searchResult.genre != "") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = searchResult.genre,
                                    fontSize = 17.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .basicMarquee(),
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    Column() {

                        if (searchResult.localDate != "") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(30.dp)
                                    .horizontalScroll(rememberScrollState()),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text(
                                    text = formatDate(searchResult.localDate),
                                    fontSize = 14.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.Gray
                                )
                            }
                        }

                        if (searchResult.localTime != "") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(30.dp)
                                    .horizontalScroll(rememberScrollState()),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text(
                                    text = formatTime(searchResult.localTime),
                                    fontSize = 14.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.Gray
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            IconButton(
                                onClick = {
                                    if (!ExistStatus(searchResult.id)) {
                                        launchSnackbar(
                                            message = searchResult.event + " added to favorites",
                                            duration = SnackbarDuration.Short
                                        )
                                    }else {
                                        launchSnackbar(
                                            message = searchResult.event + " removed from favorites",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                    changeFavorites(searchResult)
                                }
                            ) {
                                if (!ExistStatus(searchResult.id)) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_favorite_border_24),
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_favorite_fill),
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize(),
            Alignment.BottomCenter
        ) {
            SnackbarHost(
                hostState = snackState
            )
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IsLoading(searchResult: String,navController:NavHostController,eventViewModel: EventViewModel = viewModel()){

    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    val jsonObject = JSONObject(searchResult)
    val eventSearch = EventSearch(
        localDate = jsonObject.getString("localDate"),
        localTime = jsonObject.getString("localTime"),
        icon = jsonObject.getString("icon"),
        event = jsonObject.getString("event"),
        genre = jsonObject.getString("genre"),
        venue = jsonObject.getString("venue"),
        id = jsonObject.getString("id")
    )
    var detailResponse by remember { mutableStateOf("") }

    if (isLoading) {
        var apiUrl = "https://nodejs-backend-384105.wl.r.appspot.com/detail?id=" + eventSearch.id

        LaunchedEffect(Unit){
            sendDataToDetailAPI(
                context,
                apiUrl
            ){  apiResponse ->
                detailResponse = apiResponse
                isLoading = false
            }
        }
    }

    fun ExistStatus(eventId:String):Boolean{

        var isClick = false
        val favoriteEvents = eventViewModel.favoriteEvents

        if(favoriteEvents.size != 0){
            for (i in 0 until favoriteEvents.size){
                if(favoriteEvents[i].id == eventId){
                    isClick = true
                    break
                }
            }
        }
        return isClick
    }

    if(isLoading){
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .background(Color(0xFF222221))
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                    ) {
                        IconButton(
                            onClick = { navController.navigate("home") }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.green_back_btn),
                                contentDescription = null,
                                tint = Color(102, 201, 0)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(180.dp)
                    ) {
                        Text(
                            text = eventSearch.event,
                            color = Color(102, 201, 0),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .basicMarquee()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(20.dp),
                        contentAlignment = Alignment.Center
                    ) {

                    }

                    Box(
                        modifier = Modifier
                            .width(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.facebook),
                            contentDescription = null

                        )
                        IconButton(
                            onClick = {}
                        ) {
                        }
                    }
                    Box(
                        modifier = Modifier
                            .width(6.dp),
                        contentAlignment = Alignment.Center
                    ) {

                    }

                    Box(
                        modifier = Modifier
                            .width(36.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.twitter),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(CircleShape)
                        )

                        IconButton(
                            onClick = {}
                        ) {

                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(6.dp),
                        contentAlignment = Alignment.Center
                    ) {

                    }

                    Box(
                        modifier = Modifier
                            .width(45.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        IconButton(
                            onClick = {}
                        ) {
                            if (!ExistStatus(eventSearch.id)) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_favorite_border_24),
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_favorite_fill),
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }else{
        EventDetailTab(searchResult,detailResponse,navController,eventViewModel)
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventDetailTab(eventResponse: String,detailResponse: String, navController:NavHostController,eventViewModel: EventViewModel = viewModel()){
    var selectedTabIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current

    val eventObject = JSONObject(eventResponse)
    val eventSearch = EventSearch(
        localDate = eventObject.getString("localDate"),
        localTime = eventObject.getString("localTime"),
        icon = eventObject.getString("icon"),
        event = eventObject.getString("event"),
        genre = eventObject.getString("genre"),
        venue = eventObject.getString("venue"),
        id = eventObject.getString("id")
    )

    val jsonObject = JSONObject(detailResponse)
    var eventDetails = EventDetail(
        detailname = jsonObject.getString("detailname"),
        detailLocalDate = jsonObject.getString("detailLocalDate"),
        detailLocalTime = jsonObject.getString("detailLocalTime"),
        detailTicketStatus = jsonObject.getString("detailTicketStatus"),
        artistTitle = jsonObject.getString("artistTitle"),
        artistList = jsonObject.getString("artistList"),
        detailVenue = jsonObject.getString("detailVenue"),
        detailGenre = jsonObject.getString("detailGenre"),
        detailPrice = jsonObject.getString("detailPrice"),
        detailTicketUrl = jsonObject.getString("detailTicketUrl"),
        detailSeatMap = jsonObject.getString("detailSeatMap"),
        id = jsonObject.getString("id"),
        currency = jsonObject.getString("currency"),
    )

    fun ExistStatus(eventId:String):Boolean{

        var isClick = false
        val favoriteEvents = eventViewModel.favoriteEvents

        if(favoriteEvents.size != 0){
            for (i in 0 until favoriteEvents.size){
                if(favoriteEvents[i].id == eventId){
                    isClick = true
                    break
                }
            }
        }
        return isClick
    }
    /*
    val changeFavorites: (EventSearch) -> Unit = { event ->
        if(!ExistStatus(event.id)){
            eventViewModel.addToFavorites(event)
        }else{
            eventViewModel.deleteFavorites(event)
        }
    }
*/
    fun changeFavorites(event: EventSearch) {
        if(!ExistStatus(event.id)){
            eventViewModel.addToFavorites(event)
        }else{
            eventViewModel.deleteFavorites(event)
        }
    }

    var jsonArray = JSONArray(eventDetails.artistList)
    var artists = mutableListOf<String>()
    for (i in 0 until jsonArray.length()) {
        val jsonObject = jsonArray.getJSONObject(i)
        val artistgenre = ArtistGenre(
            artistName = jsonObject.getString("artistName"),
            musicRelated = jsonObject.getString("musicRelated")
        )
        if(artistgenre.musicRelated == "Music"){
            artists.add(artistgenre.artistName)
        }
    }

    val snackState = remember { SnackbarHostState()}
    val scope = rememberCoroutineScope()
    fun launchSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.Short){
        scope.launch {
            snackState.showSnackbar(
                message = message,
                duration = duration
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .background(Color(0xFF222221))
                .fillMaxWidth()
                .height(50.dp)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Box(
                    modifier = Modifier
                        .width(50.dp)
                ){
                    IconButton(
                        onClick = { navController.navigate("home") }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.green_back_btn),
                            contentDescription = null,
                            tint = Color(102, 201, 0)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .width(180.dp)
                ){
                    Text(
                        text = eventSearch.event,
                        color = Color(102, 201, 0),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .basicMarquee()
                    )
                }

                Box(
                    modifier = Modifier
                        .width(20.dp),
                    contentAlignment = Alignment.Center
                ){

                }

                Box(
                    modifier = Modifier
                        .width(40.dp),
                    contentAlignment = Alignment.Center
                ){
                    Image(
                        painter = painterResource(id = R.drawable.facebook),
                        contentDescription = null

                    )
                    IconButton(
                        onClick = {
                            val urlIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.facebook.com/sharer/sharer.php?u="+eventDetails.detailTicketUrl)
                            )
                            context.startActivity(urlIntent)
                        }
                    ) {
                    }
                }
                Box(
                    modifier = Modifier
                        .width(6.dp),
                    contentAlignment = Alignment.Center
                ){

                }

                Box(
                    modifier = Modifier
                        .width(36.dp),
                    contentAlignment = Alignment.Center
                ){

                    Image(
                        painter = painterResource(id = R.drawable.twitter),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                    )

                    IconButton(
                        onClick = {
                            val urlIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://twitter.com/intent/tweet?text=Check%20"+eventDetails.detailname+"%20on%20Ticketmaster:%20"+eventDetails.detailTicketUrl)
                            )
                            context.startActivity(urlIntent)
                        }
                    ) {

                    }
                }

                Box(
                    modifier = Modifier
                        .width(6.dp),
                    contentAlignment = Alignment.Center
                ){

                }

                Box(
                    modifier = Modifier
                        .width(45.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    IconButton(
                        onClick = {
                            if (!ExistStatus(eventSearch.id)) {
                                launchSnackbar(
                                    message = eventSearch.event + " added to favorites",
                                    duration = SnackbarDuration.Short
                                )
                            }else {
                                launchSnackbar(
                                    message = eventSearch.event + " removed from favorites",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            changeFavorites(eventSearch)
                        }
                    ) {
                        if (!ExistStatus(eventSearch.id)) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_favorite_border_24),
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_favorite_fill),
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {

            TabRow(
                selectedTabIndex = selectedTabIndex,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        color = Color(102, 201, 0),
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                    )
                },
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedTabIndex = 0
                    },
                    content = {
                        Row(verticalAlignment = Alignment.CenterVertically)
                        {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_info_24),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.widthIn(4.dp))
                            Text(text = "DETAILS")
                        }
                    },
                    modifier = Modifier
                        .background(Color.Black)
                        .height(50.dp),
                    selectedContentColor = Color(102, 201, 0),
                    unselectedContentColor = Color.White
                )

                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        selectedTabIndex = 1
                    },
                    content = {
                        Row(verticalAlignment = Alignment.CenterVertically)
                        {
                            Icon(
                                painter = painterResource(id = R.drawable.artist_icon),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.widthIn(4.dp))
                            Text(text = "ARTIST(S)")
                        }
                    },
                    modifier = Modifier
                        .background(Color.Black)
                        .height(50.dp),
                    selectedContentColor = Color(102, 201, 0),
                    unselectedContentColor = Color.White
                )

                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = {
                        selectedTabIndex = 2
                    },
                    content = {
                        Row(verticalAlignment = Alignment.CenterVertically)
                        {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_map_24),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.widthIn(4.dp))
                            Text(text = "VENUE")
                        }
                    },
                    modifier = Modifier
                        .background(Color.Black)
                        .height(50.dp),
                    selectedContentColor = Color(102, 201, 0),
                    unselectedContentColor = Color.White
                )
            }
        }


        when (selectedTabIndex) {
            0 -> {

                Box(
                ) {
                    DetailTab(detailResponse)

                }

            }

            1 -> {
                if (artists.size > 0) {

                    LazyColumn {
                        items(artists) { artistName ->

                            var artistResponse by remember { mutableStateOf("") }
                            var isLoading by remember { mutableStateOf(true) }

                            LaunchedEffect(Unit) {
                                sendDataToTeamAPI(
                                    context = context,
                                    apiUrl = "https://nodejs-backend-384105.wl.r.appspot.com/team?team=" + artistName
                                ) { apiResponse ->
                                    artistResponse = apiResponse
                                    isLoading = false
                                }
                            }

                            if (isLoading) {

                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }

                            } else {
                                Box(
                                    modifier = Modifier
                                        .background(Color.Transparent)
                                ) {
                                    ArtistTab(artistResponse)
                                }
                            }
                        }
                    }
                } else {

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                            .padding(40.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.Black, RoundedCornerShape(16.dp))
                                .height(40.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Artist/Music data unavailable",
                                color = Color(102, 201, 0),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            2 -> {

                var venueResponse by remember { mutableStateOf("") }
                var isLoading by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    sendDataToTeamAPI(
                        context = context,
                        apiUrl = "https://nodejs-backend-384105.wl.r.appspot.com/venueName?venue=" + eventDetails.detailVenue
                    ) { apiResponse ->
                        venueResponse = apiResponse
                        isLoading = false
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .background(Color.Transparent)
                    ) {
                        VenueTab(venueResponse)
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        Alignment.BottomCenter
    ) {
        SnackbarHost(hostState = snackState)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VenueTab(venueResponse: String){

    val jsonObject = JSONObject(venueResponse)

    val venue = VenueDetail(
        venueName = jsonObject.getString("venueName"),
        longitude = jsonObject.getString("longitude"),
        latitude = jsonObject.getString("latitude"),
        venueAddress = jsonObject.getString("venueAddress"),
        cityName = jsonObject.getString("cityName"),
        phone = jsonObject.optString("phone", ""),
        openHoure = jsonObject.getString("openHoure"),
        childRule = jsonObject.getString("childRule"),
        generalRule = jsonObject.getString("generalRule")
    )

    val context = LocalContext.current

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black, shape = RoundedCornerShape(16.dp))
                    .padding(bottom = 16.dp)
            ) {
                Column() {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .width(170.dp)
                                .padding(start = 25.dp, top = 20.dp)
                        ) {
                            if(venue.venueName != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Name",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if(venue.venueAddress != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Address",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if(venue.cityName != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = "City/State",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if(venue.phone != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Contact Info",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, end = 25.dp)
                        ) {
                            if(venue.venueName != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = venue.venueName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .basicMarquee(),
                                        color = Color(102, 201, 0)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if(venue.venueAddress != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = venue.venueAddress,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .basicMarquee(),
                                        color = Color(102, 201, 0)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if(venue.cityName != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = venue.cityName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .basicMarquee(),
                                        color = Color(102, 201, 0)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if(venue.phone != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = venue.phone,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .basicMarquee(),
                                        color = Color(102, 201, 0)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                    }

                    val destination = LatLng(venue.latitude.toDouble(), venue.longitude.toDouble())
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(destination, 15f)
                    }

                    val markerState = rememberMarkerState(
                        position = destination
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ){
                        GoogleMap(
                            modifier = Modifier
                                .fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                        ){
                            Marker(
                                state = markerState
                            )
                        }
                    }
                }
            }
        }

        if(venue.openHoure != "" || venue.generalRule!= "" ||venue.childRule!=""){
            Card(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(238, 232, 170), shape = RoundedCornerShape(16.dp))
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        if(venue.openHoure != ""){
                            Text(
                                text = "Open Hours",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.Black
                            )

                            var expanded by remember { mutableStateOf(false) }

                            ClickableText(
                                text = buildAnnotatedString {
                                    if (expanded) {
                                        append(venue.openHoure)
                                    } else {
                                        append(venue.openHoure.take(150) + "...")
                                    }
                                },
                                onClick = { expanded = !expanded },
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 20.sp
                                ),
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(
                                modifier = Modifier
                                    .height(10.dp)
                            )
                        }

                        if(venue.generalRule != ""){
                            Text(
                                text = "General Rules",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.Black
                            )

                            var expanded by remember { mutableStateOf(false) }

                            ClickableText(
                                text = buildAnnotatedString {
                                    if (expanded) {
                                        append(venue.generalRule)
                                    } else {
                                        append(venue.generalRule.take(150) + "...")
                                    }
                                },
                                onClick = { expanded = !expanded },
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 20.sp
                                ),
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(
                                modifier = Modifier
                                    .height(10.dp)
                            )
                        }

                        if(venue.childRule != ""){
                            Text(
                                text = "Child Rules",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.Black
                            )

                            var expanded by remember { mutableStateOf(false) }

                            ClickableText(
                                text = buildAnnotatedString {
                                    if (expanded) {
                                        append(venue.childRule)
                                    } else {
                                        append(venue.childRule.take(150) + "...")
                                    }
                                },
                                onClick = { expanded = !expanded },
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 20.sp
                                ),
                                overflow = TextOverflow.Ellipsis
                            )

                        }
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistTab(artistResponse: String) {

    val jsonObject = JSONObject(artistResponse)

    val imagesArray = jsonObject.getJSONArray("images")
    val firstImageUrl = imagesArray.getJSONObject(0).getString("url")

    val artist = ArtistDetail(
        name = jsonObject.getString("name"),
        followers =jsonObject.getJSONObject("followers").getString("total"),
        popularity = jsonObject.getString("popularity"),
        spotifyLink = jsonObject.getJSONObject("external_urls").getString("spotify"),
        album = jsonObject.getString("album"),
        image = firstImageUrl
    )

    var albums= mutableListOf<String>()
    if(artist.album != ""){
        var jsonArray = JSONArray(artist.album)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val album = jsonObject.getJSONArray("images").getJSONObject(0).getString("url")
            albums.add(album)
        }
    }

    val context = LocalContext.current

    fun formatNumber(numberString: String): String {
        val number = numberString.toLongOrNull() ?: return numberString
        if (number >= 1000000) {
            return String.format("%.0fM Followers", number.toDouble() / 1000000)
        } else if (number >= 1000) {
            return String.format("%.0fK Followers", number.toDouble() / 1000)
        } else {
            return number.toString()
        }
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black, shape = RoundedCornerShape(16.dp))
        ) {
            Column() {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    ShowIcon(artist.image)

                    Column(
                        modifier = Modifier
                            .width(160.dp)
                            .padding(start = 20.dp, top = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = artist.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .basicMarquee(),
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = formatNumber(artist.followers),
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .basicMarquee(),
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Check out on Spotify",
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val urlIntent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(artist.spotifyLink)
                                        )
                                        context.startActivity(urlIntent)
                                    },
                                color = Color(102, 201, 0),
                                textDecoration = TextDecoration.Underline
                            )
                        }

                    }
                    Column() {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Popularity",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val progress = (artist.popularity.toInt() / 100f).coerceIn(0f, 1f)
                            CircularProgressIndicator(
                                progress = progress,
                                modifier = Modifier
                                    .size(62.dp),
                                strokeWidth = 9.dp,
                                color = Color(155, 17, 30)
                            )

                            Text(
                                text = artist.popularity,
                                color = Color.Gray,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )

                        }
                    }
                }
                Divider (
                    color = Color.Gray,
                    modifier = Modifier
                        .height(1.2.dp)
                        .fillMaxHeight()
                        .fillMaxWidth()
                )

                if(albums.size > 0){
                    Text(
                        text = "Popular Albums",
                        color = Color.Gray,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 10.dp,end = 10.dp, top = 10.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ){
                        if(albums.size >= 1){
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(10.dp)
                            ){
                                ShowIcon(albums[0])
                            }
                        }
                        if(albums.size >= 2) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(10.dp)
                            ) {
                                ShowIcon(albums[1])
                            }
                        }
                        if(albums.size >= 3) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(10.dp)
                            ) {
                                ShowIcon(albums[2])
                            }
                        }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailTab(eventDetail: String){

    val context = LocalContext.current
    val jsonObject = JSONObject(eventDetail)
    var eventDetails = EventDetail(
        detailname = jsonObject.getString("detailname"),
        detailLocalDate = jsonObject.getString("detailLocalDate"),
        detailLocalTime = jsonObject.getString("detailLocalTime"),
        detailTicketStatus = jsonObject.getString("detailTicketStatus"),
        artistTitle = jsonObject.getString("artistTitle"),
        artistList = jsonObject.getString("artistList"),
        detailVenue = jsonObject.getString("detailVenue"),
        detailGenre = jsonObject.getString("detailGenre"),
        detailPrice = jsonObject.getString("detailPrice"),
        detailTicketUrl = jsonObject.getString("detailTicketUrl"),
        detailSeatMap = jsonObject.getString("detailSeatMap"),
        id = jsonObject.getString("id"),
        currency = jsonObject.getString("currency")
    )
    var price by remember { mutableStateOf("") }

    if(eventDetails.detailPrice != ""){
        if(eventDetails.currency != ""){
            price = eventDetails.detailPrice + " (" + eventDetails.currency + ")"
        }else{
            price = eventDetails.detailPrice
        }
    }

    fun formatDate(dateInput: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
        val date = inputFormat.parse(dateInput)
        return outputFormat.format(date)
    }

    fun formatTime(timeInput: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
        val time = inputFormat.parse(timeInput)
        return outputFormat.format(time)
    }

    fun getBoxColor(status: String): Color {
        return when (status) {
            "offsale" -> Color.Red
            "onsale" -> Color(102, 201, 0)
            "cancelled" -> Color.Black
            "postponed" -> Color.Yellow
            "rescheduled" -> Color.Yellow
            else -> Color.White
        }
    }

    fun ChageStatusFormat(status : String): String {
        var statusOutput = ""
        if(status == "onsale"){
            statusOutput = "On Sale"
        }else if(status == "offsale"){
            statusOutput = "Off Sale"
        }else if(status == "cancelled"){
            statusOutput = "Cancelled"
        }else if(status == "postponed"){
            statusOutput = "Postponed"
        }else if(status == "rescheduled"){
            statusOutput = "Rescheduled"
        }
        return statusOutput
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Box(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
                .background(Color.Black, shape = RoundedCornerShape(16.dp))
        ) {
            Column(

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .width(140.dp)
                    ) {
                        if(eventDetails.artistTitle != ""){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Artist/Teams",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .basicMarquee(),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                        }

                        if(eventDetails.detailVenue != ""){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Venue",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .basicMarquee(),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                        }

                        if(eventDetails.detailLocalDate != ""){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Date",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .basicMarquee(),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                        }

                        if(eventDetails.detailLocalTime != ""){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Time",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .basicMarquee(),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                        }

                        if(eventDetails.detailGenre != ""){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Genres",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .basicMarquee(),
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(15.dp))
                        }

                        if(price != ""){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Price Range",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .basicMarquee(),
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(15.dp))
                        }

                        if(eventDetails.detailTicketStatus != ""){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Ticket Status",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .basicMarquee(),
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(15.dp))
                        }

                        if(eventDetails.detailTicketUrl != ""){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Buy Tickets At",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .basicMarquee(),
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            if(eventDetails.artistTitle != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = eventDetails.artistTitle,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .basicMarquee(),
                                        color = Color(102, 201, 0),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Spacer(modifier = Modifier.height(15.dp))
                            }

                            if(eventDetails.detailVenue != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                ) {
                                    Text(
                                        text = eventDetails.detailVenue,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .basicMarquee(),
                                        color = Color(102, 201, 0),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Spacer(modifier = Modifier.height(15.dp))
                            }

                            if(eventDetails.detailLocalDate != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = formatDate(eventDetails.detailLocalDate),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .basicMarquee(),
                                        color = Color(102, 201, 0),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Spacer(modifier = Modifier.height(15.dp))
                            }

                            if(eventDetails.detailLocalTime != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = formatTime(eventDetails.detailLocalTime),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .basicMarquee(),
                                        color = Color(102, 201, 0),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Spacer(modifier = Modifier.height(15.dp))
                            }

                            if(eventDetails.detailGenre != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = eventDetails.detailGenre,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .basicMarquee(),
                                        color = Color(102, 201, 0),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Spacer(modifier = Modifier.height(15.dp))
                            }

                            if(price != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = price,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .basicMarquee(),
                                        color = Color(102, 201, 0),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Spacer(modifier = Modifier.height(15.dp))
                            }


                            if(eventDetails.detailTicketStatus != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                getBoxColor(eventDetails.detailTicketStatus),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .padding(2.dp)
                                            .width(120.dp)
                                    ){
                                        Text(
                                            text = ChageStatusFormat(eventDetails.detailTicketStatus),
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .basicMarquee(),
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(11.dp))
                            }

                            if(eventDetails.detailTicketUrl != ""){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = eventDetails.detailTicketUrl,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .basicMarquee()
                                            .clickable {
                                                val urlIntent = Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse(eventDetails.detailTicketUrl)
                                                )
                                                context.startActivity(urlIntent)
                                            },
                                        color = Color(102, 201, 0),
                                        textAlign = TextAlign.Center,
                                        textDecoration = TextDecoration.Underline
                                    )
                                }
                            }
                        }
                    }

                }

                if(eventDetails.detailSeatMap != ""){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        ShowMap(url = eventDetails.detailSeatMap)

                    }
                }
            }
        }
    }

}

@Composable
fun ShowMap(url:String) {
    val painter = rememberImagePainter(data = url)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp)

    )
}

fun sendDataToSearchAPI(
    context: Context,
    apiUrl: String,
    onSuccess: (String) -> Unit
) {
    val requestQueue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Request.Method.GET,
        apiUrl,
        { response ->
            onSuccess(response)
        },
        { error ->
            // handle error
            Log.e("Error", error.toString())
        }
    )
    requestQueue.add(stringRequest)
}

fun sendDataToGeoAPI(
    context: Context,
    apiUrl: String,
    onSuccess: (String) -> Unit
) {
    val requestQueue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Request.Method.GET,
        apiUrl,
        { response ->
            onSuccess(response)
        },
        { error ->
            // handle error
            Log.e("Error", error.toString())
        }
    )
    requestQueue.add(stringRequest)
}

fun sendDataToDetailAPI(
    context: Context,
    apiUrl: String,
    onSuccess: (String) -> Unit
) {
    val requestQueue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Request.Method.GET,
        apiUrl,
        { response ->
            onSuccess(response)
        },
        { error ->
            // handle error
            Log.e("Error", error.toString())
        }
    )
    requestQueue.add(stringRequest)
}

fun sendDataToTeamAPI(
    context: Context,
    apiUrl: String,
    onSuccess: (String) -> Unit
) {

    val requestQueue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Request.Method.GET,
        apiUrl,
        { response ->
            onSuccess(response)
        },
        { error ->
            Log.e("Error", error.toString())
        }
    )
    requestQueue.add(stringRequest)
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    HW9Theme{

    }
}