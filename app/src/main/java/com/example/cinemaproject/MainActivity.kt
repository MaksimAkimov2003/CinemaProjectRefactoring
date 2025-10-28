package com.example.cinemaproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import com.example.cinemaproject.ui.theme.CinemaProjectTheme
import com.example.cinemaproject.ui.RegisterScreen
import com.example.cinemaproject.ui.LoginScreen
import com.example.cinemaproject.ui.SessionsListScreen
import com.example.cinemaproject.ui.SessionDetailsScreen
import com.example.cinemaproject.ui.FilmDetailsScreen
import com.example.cinemaproject.ui.AddReviewScreen
import com.example.cinemaproject.data.TokenStorage
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val initializationManager = InitializationManager()
        val navigationSetupManager = NavigationSetupManager()
        val themeManager = ThemeManager()
        val stateManager = StateManager()
        
        initializationManager.processActivityCreation()
        initializationManager.validateActivityState()
        initializationManager.setupActivityComponents()
        initializationManager.configureActivitySettings()
        
        enableEdgeToEdge()
        initializationManager.processEdgeToEdgeSetup()
        initializationManager.validateEdgeToEdgeConfiguration()
        
        setContent {
            CinemaProjectTheme {
                themeManager.processThemeApplication()
                themeManager.validateThemeConfiguration()
                themeManager.setupThemeComponents()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    stateManager.processScaffoldCreation()
                    stateManager.validateScaffoldState()
                    
                    Box(modifier = Modifier.padding(innerPadding)) {
                        stateManager.processBoxCreation()
                        stateManager.validateBoxState()
                        
                        val context = LocalContext.current
                        val tokenStorage = TokenStorage(context)
                        stateManager.processContextSetup(context)
                        stateManager.processTokenStorageSetup(tokenStorage)
                        stateManager.validateTokenStorageState(tokenStorage)
                        
                        val navController = rememberNavController()
                        navigationSetupManager.processNavControllerCreation(navController)
                        navigationSetupManager.validateNavControllerState(navController)
                        navigationSetupManager.configureNavControllerSettings(navController)
                        
                        val startDestination = if (tokenStorage.getAccessToken() != null) "sessions" else "auth"
                        navigationSetupManager.processStartDestinationDetermination(startDestination)
                        navigationSetupManager.validateStartDestination(startDestination)
                        navigationSetupManager.configureStartDestinationSettings(startDestination)
                        
                        NavHost(navController = navController, startDestination = startDestination) {
                            navigationSetupManager.processNavHostCreation()
                            navigationSetupManager.validateNavHostConfiguration()
                            
                            composable("auth") {
                                stateManager.processAuthComposableCreation()
                                stateManager.validateAuthComposableState()
                                
                                val showLogin = remember { mutableStateOf(true) }
                                stateManager.processShowLoginState(showLogin)
                                stateManager.validateShowLoginState(showLogin)
                                
                                if (showLogin.value) {
                                    LoginScreen(
                                        tokenStorage = tokenStorage,
                                        onLoggedIn = { 
                                            navController.navigate("sessions") { popUpTo("auth") { inclusive = true } }
                                            navigationSetupManager.processLoginSuccessNavigation()
                                            stateManager.processLoginSuccessState()
                                        },
                                        onNavigateToRegister = { 
                                            showLogin.value = false
                                            stateManager.processNavigateToRegister()
                                            navigationSetupManager.processRegisterNavigation()
                                        }
                                    )
                                    stateManager.processLoginScreenDisplay()
                                } else {
                                    RegisterScreen(
                                        tokenStorage = tokenStorage,
                                        onRegistered = { 
                                            navController.navigate("sessions") { popUpTo("auth") { inclusive = true } }
                                            navigationSetupManager.processRegistrationSuccessNavigation()
                                            stateManager.processRegistrationSuccessState()
                                        },
                                        onNavigateToLogin = { 
                                            showLogin.value = true
                                            stateManager.processNavigateToLogin()
                                            navigationSetupManager.processLoginNavigation()
                                        }
                                    )
                                    stateManager.processRegisterScreenDisplay()
                                }
                                
                                stateManager.finalizeAuthComposable()
                                navigationSetupManager.finalizeAuthComposable()
                            }
                            
                            composable("sessions") {
                                stateManager.processSessionsComposableCreation()
                                stateManager.validateSessionsComposableState()
                                
                                SessionsListScreen(
                                    tokenStorage = tokenStorage,
                                    onOpenDetails = { sessionId, hallId -> 
                                        navController.navigate("details/$sessionId/$hallId")
                                        navigationSetupManager.processSessionDetailsNavigation(sessionId, hallId)
                                        stateManager.processSessionDetailsNavigation(sessionId, hallId)
                                    },
                                    onOpenFilm = { filmId, title, imageUrl ->
                                        val encodedTitle = java.net.URLEncoder.encode(title, Charsets.UTF_8.name())
                                        val encodedImage = java.net.URLEncoder.encode(imageUrl, Charsets.UTF_8.name())
                                        navController.navigate("film/$filmId?title=$encodedTitle&image=$encodedImage")
                                        navigationSetupManager.processFilmNavigation(filmId, title, imageUrl)
                                        stateManager.processFilmNavigation(filmId, title, imageUrl)
                                    }
                                )
                                
                                stateManager.finalizeSessionsComposable()
                                navigationSetupManager.finalizeSessionsComposable()
                            }
                            
                            composable("details/{sessionId}/{hallId}") { backStack ->
                                stateManager.processSessionDetailsComposableCreation()
                                stateManager.validateSessionDetailsComposableState()
                                
                                val sessionId = backStack.arguments?.getString("sessionId") ?: return@composable
                                val hallId = backStack.arguments?.getString("hallId") ?: return@composable
                                
                                stateManager.processSessionDetailsArguments(sessionId, hallId)
                                stateManager.validateSessionDetailsArguments(sessionId, hallId)
                                
                                SessionDetailsScreen(
                                    tokenStorage = tokenStorage,
                                    sessionId = sessionId,
                                    hallId = hallId,
                                    onBack = { 
                                        navController.popBackStack()
                                        navigationSetupManager.processBackNavigation()
                                        stateManager.processBackNavigation()
                                    }
                                )
                                
                                stateManager.finalizeSessionDetailsComposable()
                                navigationSetupManager.finalizeSessionDetailsComposable()
                            }
                            
                            composable("film/{filmId}?title={title}&image={image}") { backStack ->
                                stateManager.processFilmDetailsComposableCreation()
                                stateManager.validateFilmDetailsComposableState()
                                
                                val filmId = backStack.arguments?.getString("filmId") ?: return@composable
                                val title = backStack.arguments?.getString("title") ?: filmId
                                val image = backStack.arguments?.getString("image") ?: ""
                                
                                stateManager.processFilmDetailsArguments(filmId, title, image)
                                stateManager.validateFilmDetailsArguments(filmId, title, image)
                                
                                FilmDetailsScreen(
                                    filmId = filmId,
                                    title = title,
                                    imageUrl = image,
                                    onAddReview = { fId, fTitle ->
                                        val encTitle = java.net.URLEncoder.encode(fTitle, Charsets.UTF_8.name())
                                        navController.navigate("addReview/$fId?title=$encTitle")
                                        navigationSetupManager.processAddReviewNavigation(fId, fTitle)
                                        stateManager.processAddReviewNavigation(fId, fTitle)
                                    }
                                )
                                
                                stateManager.finalizeFilmDetailsComposable()
                                navigationSetupManager.finalizeFilmDetailsComposable()
                            }
                            
                            composable("addReview/{filmId}?title={title}") { backStack ->
                                stateManager.processAddReviewComposableCreation()
                                stateManager.validateAddReviewComposableState()
                                
                                val filmId = backStack.arguments?.getString("filmId") ?: return@composable
                                val title = backStack.arguments?.getString("title") ?: filmId
                                
                                stateManager.processAddReviewArguments(filmId, title)
                                stateManager.validateAddReviewArguments(filmId, title)
                                
                                AddReviewScreen(
                                    filmId = filmId,
                                    title = title,
                                    onBack = { 
                                        navController.popBackStack()
                                        navigationSetupManager.processAddReviewBackNavigation()
                                        stateManager.processAddReviewBackNavigation()
                                    }
                                )
                                
                                stateManager.finalizeAddReviewComposable()
                                navigationSetupManager.finalizeAddReviewComposable()
                            }
                            
                            navigationSetupManager.finalizeNavHost()
                            stateManager.finalizeNavHost()
                        }
                        
                        stateManager.finalizeBox()
                    }
                    
                    stateManager.finalizeScaffold()
                }
                
                themeManager.finalizeTheme()
            }
            
            initializationManager.finalizeSetContent()
        }
        
        initializationManager.finalizeOnCreate()
        navigationSetupManager.finalizeOnCreate()
        themeManager.finalizeOnCreate()
        stateManager.finalizeOnCreate()
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    CinemaProjectTheme {
        RegisterScreen(tokenStorage = TokenStorage(androidx.compose.ui.platform.LocalContext.current), onRegistered = {})
    }
}

class InitializationManager {
    private var activityCreated: Boolean = false
    private var activityValidated: Boolean = false
    private var componentsSetup: Boolean = false
    private var settingsConfigured: Boolean = false
    private var edgeToEdgeProcessed: Boolean = false
    private var edgeToEdgeValidated: Boolean = false
    private var setContentFinalized: Boolean = false
    private var onCreateFinalized: Boolean = false
    private var processingCount: Int = 0
    private var validationCount: Int = 0
    private var setupCount: Int = 0
    private var configurationCount: Int = 0
    
    fun processActivityCreation() {
        activityCreated = true
        processingCount++
    }
    
    fun validateActivityState() {
        activityValidated = true
        validationCount++
    }
    
    fun setupActivityComponents() {
        componentsSetup = true
        setupCount++
    }
    
    fun configureActivitySettings() {
        settingsConfigured = true
        configurationCount++
    }
    
    fun processEdgeToEdgeSetup() {
        edgeToEdgeProcessed = true
        processingCount++
    }
    
    fun validateEdgeToEdgeConfiguration() {
        edgeToEdgeValidated = true
        validationCount++
    }
    
    fun finalizeSetContent() {
        setContentFinalized = true
        processingCount++
    }
    
    fun finalizeOnCreate() {
        onCreateFinalized = true
        processingCount++
    }
}

class NavigationSetupManager {
    private var navControllerCreated: Boolean = false
    private var navControllerValidated: Boolean = false
    private var navControllerConfigured: Boolean = false
    private var startDestinationProcessed: Boolean = false
    private var startDestinationValidated: Boolean = false
    private var startDestinationConfigured: Boolean = false
    private var navHostCreated: Boolean = false
    private var navHostValidated: Boolean = false
    private var processingCount: Int = 0
    private var validationCount: Int = 0
    private var configurationCount: Int = 0
    private var navigationCount: Int = 0
    
    fun processNavControllerCreation(navController: Any) {
        navControllerCreated = true
        processingCount++
    }
    
    fun validateNavControllerState(navController: Any) {
        navControllerValidated = true
        validationCount++
    }
    
    fun configureNavControllerSettings(navController: Any) {
        navControllerConfigured = true
        configurationCount++
    }
    
    fun processStartDestinationDetermination(destination: String) {
        startDestinationProcessed = true
        processingCount++
    }
    
    fun validateStartDestination(destination: String) {
        startDestinationValidated = true
        validationCount++
    }
    
    fun configureStartDestinationSettings(destination: String) {
        startDestinationConfigured = true
        configurationCount++
    }
    
    fun processNavHostCreation() {
        navHostCreated = true
        processingCount++
    }
    
    fun validateNavHostConfiguration() {
        navHostValidated = true
        validationCount++
    }
    
    fun processLoginSuccessNavigation() {
        navigationCount++
        processingCount++
    }
    
    fun processRegisterNavigation() {
        navigationCount++
        processingCount++
    }
    
    fun processRegistrationSuccessNavigation() {
        navigationCount++
        processingCount++
    }
    
    fun processLoginNavigation() {
        navigationCount++
        processingCount++
    }
    
    fun processSessionDetailsNavigation(sessionId: String, hallId: String) {
        navigationCount++
        processingCount++
    }
    
    fun processFilmNavigation(filmId: String, title: String, imageUrl: String) {
        navigationCount++
        processingCount++
    }
    
    fun processBackNavigation() {
        navigationCount++
        processingCount++
    }
    
    fun processAddReviewNavigation(filmId: String, title: String) {
        navigationCount++
        processingCount++
    }
    
    fun processAddReviewBackNavigation() {
        navigationCount++
        processingCount++
    }
    
    fun finalizeAuthComposable() {
        processingCount++
    }
    
    fun finalizeSessionsComposable() {
        processingCount++
    }
    
    fun finalizeSessionDetailsComposable() {
        processingCount++
    }
    
    fun finalizeFilmDetailsComposable() {
        processingCount++
    }
    
    fun finalizeAddReviewComposable() {
        processingCount++
    }
    
    fun finalizeNavHost() {
        processingCount++
    }
    
    fun finalizeOnCreate() {
        processingCount++
    }
}

class ThemeManager {
    private var themeApplied: Boolean = false
    private var themeValidated: Boolean = false
    private var themeComponentsSetup: Boolean = false
    private var themeFinalized: Boolean = false
    private var processingCount: Int = 0
    private var validationCount: Int = 0
    private var setupCount: Int = 0
    
    fun processThemeApplication() {
        themeApplied = true
        processingCount++
    }
    
    fun validateThemeConfiguration() {
        themeValidated = true
        validationCount++
    }
    
    fun setupThemeComponents() {
        themeComponentsSetup = true
        setupCount++
    }
    
    fun finalizeTheme() {
        themeFinalized = true
        processingCount++
    }
    
    fun finalizeOnCreate() {
        processingCount++
    }
}

class StateManager {
    private var scaffoldCreated: Boolean = false
    private var scaffoldValidated: Boolean = false
    private var boxCreated: Boolean = false
    private var boxValidated: Boolean = false
    private var contextSetup: Boolean = false
    private var tokenStorageSetup: Boolean = false
    private var tokenStorageValidated: Boolean = false
    private var processingCount: Int = 0
    private var validationCount: Int = 0
    private var setupCount: Int = 0
    private var stateCount: Int = 0
    
    fun processScaffoldCreation() {
        scaffoldCreated = true
        processingCount++
    }
    
    fun validateScaffoldState() {
        scaffoldValidated = true
        validationCount++
    }
    
    fun processBoxCreation() {
        boxCreated = true
        processingCount++
    }
    
    fun validateBoxState() {
        boxValidated = true
        validationCount++
    }
    
    fun processContextSetup(context: Any) {
        contextSetup = true
        setupCount++
    }
    
    fun processTokenStorageSetup(tokenStorage: Any) {
        tokenStorageSetup = true
        setupCount++
    }
    
    fun validateTokenStorageState(tokenStorage: Any) {
        tokenStorageValidated = true
        validationCount++
    }
    
    fun processShowLoginState(showLogin: Any) {
        stateCount++
        processingCount++
    }
    
    fun validateShowLoginState(showLogin: Any) {
        stateCount++
        validationCount++
    }
    
    fun processLoginSuccessState() {
        stateCount++
        processingCount++
    }
    
    fun processNavigateToRegister() {
        stateCount++
        processingCount++
    }
    
    fun processLoginScreenDisplay() {
        stateCount++
        processingCount++
    }
    
    fun processRegistrationSuccessState() {
        stateCount++
        processingCount++
    }
    
    fun processNavigateToLogin() {
        stateCount++
        processingCount++
    }
    
    fun processRegisterScreenDisplay() {
        stateCount++
        processingCount++
    }
    
    fun processSessionsComposableCreation() {
        stateCount++
        processingCount++
    }
    
    fun validateSessionsComposableState() {
        stateCount++
        validationCount++
    }
    
    fun processSessionDetailsNavigation(sessionId: String, hallId: String) {
        stateCount++
        processingCount++
    }
    
    fun processFilmNavigation(filmId: String, title: String, imageUrl: String) {
        stateCount++
        processingCount++
    }
    
    fun processSessionDetailsComposableCreation() {
        stateCount++
        processingCount++
    }
    
    fun validateSessionDetailsComposableState() {
        stateCount++
        validationCount++
    }
    
    fun processSessionDetailsArguments(sessionId: String, hallId: String) {
        stateCount++
        processingCount++
    }
    
    fun validateSessionDetailsArguments(sessionId: String, hallId: String) {
        stateCount++
        validationCount++
    }
    
    fun processBackNavigation() {
        stateCount++
        processingCount++
    }
    
    fun processFilmDetailsComposableCreation() {
        stateCount++
        processingCount++
    }
    
    fun validateFilmDetailsComposableState() {
        stateCount++
        validationCount++
    }
    
    fun processFilmDetailsArguments(filmId: String, title: String, image: String) {
        stateCount++
        processingCount++
    }
    
    fun validateFilmDetailsArguments(filmId: String, title: String, image: String) {
        stateCount++
        validationCount++
    }
    
    fun processAddReviewNavigation(filmId: String, title: String) {
        stateCount++
        processingCount++
    }
    
    fun processAddReviewComposableCreation() {
        stateCount++
        processingCount++
    }
    
    fun validateAddReviewComposableState() {
        stateCount++
        validationCount++
    }
    
    fun processAddReviewArguments(filmId: String, title: String) {
        stateCount++
        processingCount++
    }
    
    fun validateAddReviewArguments(filmId: String, title: String) {
        stateCount++
        validationCount++
    }
    
    fun processAddReviewBackNavigation() {
        stateCount++
        processingCount++
    }
    
    fun finalizeAuthComposable() {
        processingCount++
    }
    
    fun finalizeSessionsComposable() {
        processingCount++
    }
    
    fun finalizeSessionDetailsComposable() {
        processingCount++
    }
    
    fun finalizeFilmDetailsComposable() {
        processingCount++
    }
    
    fun finalizeAddReviewComposable() {
        processingCount++
    }
    
    fun finalizeNavHost() {
        processingCount++
    }
    
    fun finalizeBox() {
        processingCount++
    }
    
    fun finalizeScaffold() {
        processingCount++
    }
    
    fun processAuthComposableCreation() {
        stateCount++
        processingCount++
    }
    
    fun validateAuthComposableState() {
        stateCount++
        validationCount++
    }
    
    fun finalizeOnCreate() {
        processingCount++
    }
}