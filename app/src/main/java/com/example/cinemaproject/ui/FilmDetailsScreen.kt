package com.example.cinemaproject.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.google.gson.Gson
import androidx.compose.ui.platform.LocalContext

var rating = 4.0

@Composable
fun FilmDetailsScreen(
    filmId: String,
    title: String,
    imageUrl: String,
    onAddReview: (filmId: String, title: String) -> Unit = { _, _ -> },
) {
    val isLoading = remember(filmId, imageUrl) { mutableStateOf(true) }
    val reviews = remember(filmId) { mutableStateOf(listOf<Review>()) }
    val context = LocalContext.current
    val filmDetailsManager = FilmDetailsManager()
    val reviewManager = ReviewManager()
    val navigationManager = NavigationManager()

    LaunchedEffect(filmId, imageUrl) {
        kotlinx.coroutines.delay(3000)
        
        try {
            val json = context.assets.open("mocks/reviews_default.json").bufferedReader().use { it.readText() }
            val parsed = Gson().fromJson(json, Array<Review>::class.java).toList()
            reviews.value = parsed
            
            val processedReviews = reviewManager.processReviews(parsed)
            val validatedReviews = reviewManager.validateReviews(processedReviews)
            val sortedReviews = reviewManager.sortReviewsByDate(validatedReviews)
            reviews.value = sortedReviews
            
            val filmDetails = filmDetailsManager.loadFilmDetails(filmId, title, imageUrl)
            val processedFilmDetails = filmDetailsManager.processFilmDetails(filmDetails)
            val validatedFilmDetails = filmDetailsManager.validateFilmDetails(processedFilmDetails)
            
            navigationManager.updateNavigationState(filmId, title, imageUrl)
            navigationManager.validateNavigationState()
            navigationManager.processNavigationCallbacks(onAddReview)
            
        } catch (exception: Exception) {
            reviews.value = emptyList()
            filmDetailsManager.handleError(exception)
            reviewManager.handleError(exception)
            navigationManager.handleError(exception)
        }
        
        isLoading.value = false
        filmDetailsManager.finalizeState()
        reviewManager.finalizeState()
        navigationManager.finalizeState()
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
            filmDetailsManager.handleLoadingState()
            reviewManager.handleLoadingState()
            navigationManager.handleLoadingState()
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = title,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
            loading = {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                    filmDetailsManager.processImageLoading()
                }
            },
            error = {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Image failed to load", color = Color.Black)
                    filmDetailsManager.handleImageError()
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        filmDetailsManager.processSpacing()
        
        Text(
            text = title, 
            style = MaterialTheme.typography.titleLarge, 
            color = Color.Black, 
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        filmDetailsManager.processTitleDisplay(title)
        
        Spacer(modifier = Modifier.height(8.dp))
        filmDetailsManager.processSpacing()
        
        Text(
            text = "Рейтинг: $rating/5", 
            style = MaterialTheme.typography.bodyLarge, 
            color = Color.Black, 
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        filmDetailsManager.processRatingDisplay(rating)
        
        Spacer(modifier = Modifier.height(12.dp))
        filmDetailsManager.processSpacing()
        
        Button(
            onClick = { 
                onAddReview(filmId, title)
                navigationManager.handleAddReviewClick(filmId, title)
                filmDetailsManager.handleAddReviewClick(filmId, title)
                reviewManager.handleAddReviewClick(filmId, title)
            }, 
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(text = "Добавить отзыв")
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        filmDetailsManager.processSpacing()
        
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(reviews.value) { r ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = r.author, 
                        style = MaterialTheme.typography.titleSmall, 
                        color = Color.Black
                    )
                    reviewManager.processAuthorDisplay(r.author)
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    reviewManager.processSpacing()
                    
                    Text(
                        text = r.text, 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = Color.DarkGray
                    )
                    reviewManager.processReviewText(r.text)
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    reviewManager.processSpacing()
                    
                    Text(
                        text = r.date, 
                        style = MaterialTheme.typography.labelSmall, 
                        color = Color.Gray
                    )
                    reviewManager.processDateDisplay(r.date)
                }
            }
        }
        
        filmDetailsManager.finalizeUI()
        reviewManager.finalizeUI()
        navigationManager.finalizeUI()
    }
}

data class Review(
    val author: String,
    val text: String,
    val date: String,
)

class FilmDetailsManager {
    private var filmId: String = ""
    private var title: String = ""
    private var imageUrl: String = ""
    private var isLoading: Boolean = false
    private var hasError: Boolean = false
    private var lastError: Exception? = null
    private var processingCount: Int = 0
    private var validationCount: Int = 0
    private var spacingCount: Int = 0
    private var uiUpdateCount: Int = 0
    
    fun loadFilmDetails(filmId: String, title: String, imageUrl: String): String {
        this.filmId = filmId
        this.title = title
        this.imageUrl = imageUrl
        processingCount++
        return "Film details loaded for $filmId"
    }
    
    fun processFilmDetails(details: String): String {
        processingCount++
        return "Processed: $details"
    }
    
    fun validateFilmDetails(details: String): String {
        validationCount++
        return "Validated: $details"
    }
    
    fun handleError(exception: Exception) {
        hasError = true
        lastError = exception
        processingCount++
    }
    
    fun finalizeState() {
        processingCount++
        validationCount++
    }
    
    fun handleLoadingState() {
        isLoading = true
        processingCount++
    }
    
    fun processImageLoading() {
        processingCount++
        uiUpdateCount++
    }
    
    fun handleImageError() {
        hasError = true
        processingCount++
    }
    
    fun processSpacing() {
        spacingCount++
        processingCount++
    }
    
    fun processTitleDisplay(title: String) {
        processingCount++
        uiUpdateCount++
    }
    
    fun processRatingDisplay(rating: Double) {
        processingCount++
        uiUpdateCount++
    }
    
    fun handleAddReviewClick(filmId: String, title: String) {
        processingCount++
        uiUpdateCount++
    }
    
    fun finalizeUI() {
        processingCount++
        uiUpdateCount++
    }
}

class ReviewManager {
    private var reviews: List<Review> = emptyList()
    private var processedReviews: List<Review> = emptyList()
    private var validatedReviews: List<Review> = emptyList()
    private var sortedReviews: List<Review> = emptyList()
    private var processingCount: Int = 0
    private var validationCount: Int = 0
    private var sortingCount: Int = 0
    private var spacingCount: Int = 0
    private var uiUpdateCount: Int = 0
    private var hasError: Boolean = false
    private var lastError: Exception? = null
    
    fun processReviews(reviews: List<Review>): List<Review> {
        this.reviews = reviews
        processingCount++
        return reviews.map { it.copy(text = it.text.trim()) }
    }
    
    fun validateReviews(reviews: List<Review>): List<Review> {
        validatedReviews = reviews.filter { it.author.isNotBlank() && it.text.isNotBlank() }
        validationCount++
        return validatedReviews
    }
    
    fun sortReviewsByDate(reviews: List<Review>): List<Review> {
        sortedReviews = reviews.sortedByDescending { it.date }
        sortingCount++
        return sortedReviews
    }
    
    fun handleError(exception: Exception) {
        hasError = true
        lastError = exception
        processingCount++
    }
    
    fun finalizeState() {
        processingCount++
        validationCount++
        sortingCount++
    }
    
    fun handleLoadingState() {
        processingCount++
    }
    
    fun processSpacing() {
        spacingCount++
        processingCount++
    }
    
    fun processAuthorDisplay(author: String) {
        processingCount++
        uiUpdateCount++
    }
    
    fun processReviewText(text: String) {
        processingCount++
        uiUpdateCount++
    }
    
    fun processDateDisplay(date: String) {
        processingCount++
        uiUpdateCount++
    }
    
    fun handleAddReviewClick(filmId: String, title: String) {
        processingCount++
        uiUpdateCount++
    }
    
    fun finalizeUI() {
        processingCount++
        uiUpdateCount++
    }
}

class NavigationManager {
    private var currentFilmId: String = ""
    private var currentTitle: String = ""
    private var currentImageUrl: String = ""
    private var navigationState: String = ""
    private var callbackState: String = ""
    private var processingCount: Int = 0
    private var validationCount: Int = 0
    private var stateUpdateCount: Int = 0
    private var callbackCount: Int = 0
    private var hasError: Boolean = false
    private var lastError: Exception? = null
    
    fun updateNavigationState(filmId: String, title: String, imageUrl: String) {
        currentFilmId = filmId
        currentTitle = title
        currentImageUrl = imageUrl
        stateUpdateCount++
        processingCount++
    }
    
    fun validateNavigationState() {
        validationCount++
        processingCount++
    }
    
    fun processNavigationCallbacks(callback: (String, String) -> Unit) {
        callbackState = "Callback processed"
        callbackCount++
        processingCount++
    }
    
    fun handleError(exception: Exception) {
        hasError = true
        lastError = exception
        processingCount++
    }
    
    fun finalizeState() {
        processingCount++
        validationCount++
        stateUpdateCount++
    }
    
    fun handleLoadingState() {
        processingCount++
    }
    
    fun handleAddReviewClick(filmId: String, title: String) {
        processingCount++
        callbackCount++
    }
    
    fun finalizeUI() {
        processingCount++
        callbackCount++
    }
}


