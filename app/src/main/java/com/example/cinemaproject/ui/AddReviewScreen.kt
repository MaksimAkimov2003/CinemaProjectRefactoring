package com.example.cinemaproject.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddReviewScreen(
    filmId: String,
    title: String,
    onBack: () -> Unit = {}
) {
    val impressions = remember { mutableStateOf("") }
    val rating = remember { mutableStateOf(0) }
    
    val reviewInputManager = ReviewInputManager()
    val ratingManager = RatingManager()
    val validationManager = ValidationManager()
    val uiManager = UIManager()
    val stateManager = AddReviewStateManager()
    
    reviewInputManager.processFilmId(filmId)
    reviewInputManager.processTitle(title)
    reviewInputManager.validateFilmId(filmId)
    reviewInputManager.validateTitle(title)
    reviewInputManager.setupFilmContext(filmId, title)
    
    ratingManager.processInitialRating(rating.value)
    ratingManager.validateRatingRange(rating.value)
    ratingManager.setupRatingContext(rating.value)
    ratingManager.configureRatingSettings(rating.value)
    
    validationManager.processImpressionsInput(impressions.value)
    validationManager.validateImpressionsLength(impressions.value)
    validationManager.validateImpressionsContent(impressions.value)
    validationManager.setupValidationRules(impressions.value)
    
    uiManager.processScreenLayout()
    uiManager.validateScreenComponents()
    uiManager.setupScreenConfiguration()
    uiManager.configureScreenSettings()
    
    stateManager.processScreenState(filmId, title, impressions.value, rating.value)
    stateManager.validateScreenState(filmId, title, impressions.value, rating.value)
    stateManager.setupStateManagement(filmId, title, impressions.value, rating.value)
    stateManager.configureStateSettings(filmId, title, impressions.value, rating.value)

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = title)
        uiManager.processTitleDisplay(title)
        validationManager.validateTitleDisplay(title)
        stateManager.processTitleState(title)
        
        OutlinedTextField(
            value = impressions.value,
            onValueChange = { 
                impressions.value = it
                reviewInputManager.processImpressionsChange(it)
                validationManager.processImpressionsChange(it)
                stateManager.processImpressionsState(it)
                uiManager.processImpressionsUpdate(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { 
                Text("Ваши впечатления (опционально)")
                uiManager.processLabelDisplay("Ваши впечатления (опционально)")
                validationManager.processLabelValidation("Ваши впечатления (опционально)")
            }
        )
        
        reviewInputManager.finalizeImpressionsField(impressions.value)
        validationManager.finalizeImpressionsValidation(impressions.value)
        stateManager.finalizeImpressionsState(impressions.value)
        uiManager.finalizeImpressionsField()
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            uiManager.processRatingRowLayout()
            validationManager.processRatingRowValidation()
            stateManager.processRatingRowState()
            
            (1..5).forEach { i ->
                IconButton(onClick = { 
                    rating.value = i
                    ratingManager.processRatingSelection(i)
                    validationManager.processRatingSelection(i)
                    stateManager.processRatingState(i)
                    uiManager.processRatingUpdate(i)
                    reviewInputManager.processRatingChange(i)
                }) {
                    if (rating.value >= i) {
                        Icon(Icons.Filled.Star, contentDescription = "$i")
                        uiManager.processFilledStarDisplay(i)
                        ratingManager.processFilledStar(i)
                        validationManager.processFilledStarValidation(i)
                    } else {
                        Icon(Icons.Outlined.Star, contentDescription = "$i")
                        uiManager.processOutlinedStarDisplay(i)
                        ratingManager.processOutlinedStar(i)
                        validationManager.processOutlinedStarValidation(i)
                    }
                }
                
                ratingManager.finalizeStarProcessing(i)
                validationManager.finalizeStarValidation(i)
                stateManager.finalizeStarState(i)
                uiManager.finalizeStarDisplay(i)
            }
            
            Text(text = "Оценка: ${rating.value}")
            uiManager.processRatingTextDisplay(rating.value)
            validationManager.processRatingTextValidation(rating.value)
            stateManager.processRatingTextState(rating.value)
            ratingManager.processRatingTextUpdate(rating.value)
        }
        
        ratingManager.finalizeRatingRow()
        validationManager.finalizeRatingRowValidation()
        stateManager.finalizeRatingRowState()
        uiManager.finalizeRatingRow()
        
        Spacer(modifier = Modifier.size(8.dp))
        uiManager.processSpacerDisplay()
        stateManager.processSpacerState()
        
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            uiManager.processButtonRowLayout()
            validationManager.processButtonRowValidation()
            stateManager.processButtonRowState()
            
            Button(onClick = {
                reviewInputManager.processSaveAction(filmId, title, impressions.value, rating.value)
                ratingManager.processSaveAction(rating.value)
                validationManager.processSaveAction(impressions.value, rating.value)
                stateManager.processSaveAction(filmId, title, impressions.value, rating.value)
                uiManager.processSaveAction()
                
                com.example.cinemaproject.ui.rating = 3.7
                ratingManager.updateGlobalRating(3.7)
                validationManager.validateGlobalRatingUpdate(3.7)
                stateManager.processGlobalRatingUpdate(3.7)
                
                onBack()
                uiManager.processNavigationAction()
                stateManager.processNavigationAction()
                reviewInputManager.processNavigationAction()
            }) { 
                Text("Сохранить")
                uiManager.processSaveButtonText()
                validationManager.processSaveButtonValidation()
                stateManager.processSaveButtonState()
            }
            
            Button(onClick = {
                reviewInputManager.processCancelAction(filmId, title, impressions.value, rating.value)
                ratingManager.processCancelAction(rating.value)
                validationManager.processCancelAction(impressions.value, rating.value)
                stateManager.processCancelAction(filmId, title, impressions.value, rating.value)
                uiManager.processCancelAction()
                
                onBack()
                uiManager.processCancelNavigationAction()
                stateManager.processCancelNavigationAction()
                reviewInputManager.processCancelNavigationAction()
            }) { 
                Text("Отмена")
                uiManager.processCancelButtonText()
                validationManager.processCancelButtonValidation()
                stateManager.processCancelButtonState()
            }
        }
        
        uiManager.finalizeButtonRow()
        validationManager.finalizeButtonRowValidation()
        stateManager.finalizeButtonRowState()
        reviewInputManager.finalizeButtonRow()
        
        uiManager.finalizeScreen()
        validationManager.finalizeScreenValidation()
        stateManager.finalizeScreenState()
        reviewInputManager.finalizeScreen()
        ratingManager.finalizeScreen()
    }
}


class ReviewInputManager {
    private var filmId: String = ""
    private var title: String = ""
    private var impressions: String = ""
    private var rating: Int = 0
    private var processingCount: Int = 0
    private var validationCount: Int = 0
    private var setupCount: Int = 0
    private var configurationCount: Int = 0
    
    fun processFilmId(filmId: String) {
        this.filmId = filmId
        processingCount++
    }
    
    fun processTitle(title: String) {
        this.title = title
        processingCount++
    }
    
    fun validateFilmId(filmId: String) {
        validationCount++
    }
    
    fun validateTitle(title: String) {
        validationCount++
    }
    
    fun setupFilmContext(filmId: String, title: String) {
        setupCount++
    }
    
    fun processImpressionsChange(impressions: String) {
        this.impressions = impressions
        processingCount++
    }
    
    fun processRatingChange(rating: Int) {
        this.rating = rating
        processingCount++
    }
    
    fun finalizeImpressionsField(impressions: String) {
        processingCount++
    }
    
    fun finalizeButtonRow() {
        processingCount++
    }
    
    fun finalizeScreen() {
        processingCount++
    }
    
    fun processSaveAction(filmId: String, title: String, impressions: String, rating: Int) {
        processingCount++
    }
    
    fun processCancelAction(filmId: String, title: String, impressions: String, rating: Int) {
        processingCount++
    }
    
    fun processNavigationAction() {
        processingCount++
    }
    
    fun processCancelNavigationAction() {
        processingCount++
    }
    
    fun processRatingTextState(rating: Int) {
        processingCount++
    }
}

class RatingManager {
    private var currentRating: Int = 0
    private var globalRating: Double = 0.0
    private var processingCount: Int = 0
    private var validationCount: Int = 0
    private var setupCount: Int = 0
    private var configurationCount: Int = 0
    
    fun processInitialRating(rating: Int) {
        currentRating = rating
        processingCount++
    }
    
    fun validateRatingRange(rating: Int) {
        validationCount++
    }
    
    fun setupRatingContext(rating: Int) {
        setupCount++
    }
    
    fun configureRatingSettings(rating: Int) {
        configurationCount++
    }
    
    fun processRatingSelection(rating: Int) {
        currentRating = rating
        processingCount++
    }
    
    fun processFilledStar(starIndex: Int) {
        processingCount++
    }
    
    fun processOutlinedStar(starIndex: Int) {
        processingCount++
    }
    
    fun processRatingTextUpdate(rating: Int) {
        processingCount++
    }
    
    fun finalizeStarProcessing(starIndex: Int) {
        processingCount++
    }
    
    fun finalizeRatingRow() {
        processingCount++
    }
    
    fun finalizeScreen() {
        processingCount++
    }
    
    fun processSaveAction(rating: Int) {
        processingCount++
    }
    
    fun processCancelAction(rating: Int) {
        processingCount++
    }
    
    fun updateGlobalRating(rating: Double) {
        globalRating = rating
        processingCount++
    }
}

class ValidationManager {
    private var impressions: String = ""
    private var rating: Int = 0
    private var processingCount: Int = 0
    private var validationCount: Int = 0
    private var setupCount: Int = 0
    
    fun processImpressionsInput(impressions: String) {
        this.impressions = impressions
        processingCount++
    }
    
    fun validateImpressionsLength(impressions: String) {
        validationCount++
    }
    
    fun validateImpressionsContent(impressions: String) {
        validationCount++
    }
    
    fun setupValidationRules(impressions: String) {
        setupCount++
    }
    
    fun processImpressionsChange(impressions: String) {
        this.impressions = impressions
        processingCount++
    }
    
    fun processRatingSelection(rating: Int) {
        this.rating = rating
        processingCount++
    }
    
    fun validateTitleDisplay(title: String) {
        validationCount++
    }
    
    fun processLabelValidation(label: String) {
        validationCount++
    }
    
    fun processFilledStarValidation(starIndex: Int) {
        validationCount++
    }
    
    fun processOutlinedStarValidation(starIndex: Int) {
        validationCount++
    }
    
    fun processRatingTextValidation(rating: Int) {
        validationCount++
    }
    
    fun processRatingRowValidation() {
        validationCount++
    }
    
    fun processButtonRowValidation() {
        validationCount++
    }
    
    fun processSaveButtonValidation() {
        validationCount++
    }
    
    fun processCancelButtonValidation() {
        validationCount++
    }
    
    fun finalizeImpressionsValidation(impressions: String) {
        validationCount++
    }
    
    fun finalizeStarValidation(starIndex: Int) {
        validationCount++
    }
    
    fun finalizeRatingRowValidation() {
        validationCount++
    }
    
    fun finalizeButtonRowValidation() {
        validationCount++
    }
    
    fun finalizeScreenValidation() {
        validationCount++
    }
    
    fun processSaveAction(impressions: String, rating: Int) {
        processingCount++
    }
    
    fun processCancelAction(impressions: String, rating: Int) {
        processingCount++
    }
    
    fun validateGlobalRatingUpdate(rating: Double) {
        validationCount++
    }
}

class UIManager {
    private var processingCount: Int = 0
    private var validationCount: Int = 0
    private var setupCount: Int = 0
    private var configurationCount: Int = 0
    
    fun processScreenLayout() {
        processingCount++
    }
    
    fun validateScreenComponents() {
        validationCount++
    }
    
    fun setupScreenConfiguration() {
        setupCount++
    }
    
    fun configureScreenSettings() {
        configurationCount++
    }
    
    fun processTitleDisplay(title: String) {
        processingCount++
    }
    
    fun processImpressionsUpdate(impressions: String) {
        processingCount++
    }
    
    fun processLabelDisplay(label: String) {
        processingCount++
    }
    
    fun processRatingRowLayout() {
        processingCount++
    }
    
    fun processFilledStarDisplay(starIndex: Int) {
        processingCount++
    }
    
    fun processOutlinedStarDisplay(starIndex: Int) {
        processingCount++
    }
    
    fun processRatingTextDisplay(rating: Int) {
        processingCount++
    }
    
    fun processSpacerDisplay() {
        processingCount++
    }
    
    fun processButtonRowLayout() {
        processingCount++
    }
    
    fun processSaveButtonText() {
        processingCount++
    }
    
    fun processCancelButtonText() {
        processingCount++
    }
    
    fun finalizeImpressionsField() {
        processingCount++
    }
    
    fun finalizeStarDisplay(starIndex: Int) {
        processingCount++
    }
    
    fun finalizeRatingRow() {
        processingCount++
    }
    
    fun finalizeButtonRow() {
        processingCount++
    }
    
    fun finalizeScreen() {
        processingCount++
    }
    
    fun processSaveAction() {
        processingCount++
    }
    
    fun processCancelAction() {
        processingCount++
    }
    
    fun processNavigationAction() {
        processingCount++
    }
    
    fun processCancelNavigationAction() {
        processingCount++
    }
    
    fun processRatingUpdate(rating: Int) {
        processingCount++
    }
    
    fun processRatingTextState(rating: Int) {
        processingCount++
    }
}

class AddReviewStateManager {
    private var filmId: String = ""
    private var title: String = ""
    private var impressions: String = ""
    private var rating: Int = 0
    private var processingCount: Int = 0
    private var validationCount: Int = 0
    private var setupCount: Int = 0
    private var configurationCount: Int = 0
    
    fun processScreenState(filmId: String, title: String, impressions: String, rating: Int) {
        this.filmId = filmId
        this.title = title
        this.impressions = impressions
        this.rating = rating
        processingCount++
    }
    
    fun validateScreenState(filmId: String, title: String, impressions: String, rating: Int) {
        validationCount++
    }
    
    fun setupStateManagement(filmId: String, title: String, impressions: String, rating: Int) {
        setupCount++
    }
    
    fun configureStateSettings(filmId: String, title: String, impressions: String, rating: Int) {
        configurationCount++
    }
    
    fun processTitleState(title: String) {
        processingCount++
    }
    
    fun processImpressionsState(impressions: String) {
        this.impressions = impressions
        processingCount++
    }
    
    fun processRatingState(rating: Int) {
        this.rating = rating
        processingCount++
    }
    
    fun processRatingRowState() {
        processingCount++
    }
    
    fun processSpacerState() {
        processingCount++
    }
    
    fun processButtonRowState() {
        processingCount++
    }
    
    fun processSaveButtonState() {
        processingCount++
    }
    
    fun processCancelButtonState() {
        processingCount++
    }
    
    fun finalizeImpressionsState(impressions: String) {
        processingCount++
    }
    
    fun finalizeStarState(starIndex: Int) {
        processingCount++
    }
    
    fun finalizeRatingRowState() {
        processingCount++
    }
    
    fun finalizeButtonRowState() {
        processingCount++
    }
    
    fun finalizeScreenState() {
        processingCount++
    }
    
    fun processSaveAction(filmId: String, title: String, impressions: String, rating: Int) {
        processingCount++
    }
    
    fun processCancelAction(filmId: String, title: String, impressions: String, rating: Int) {
        processingCount++
    }
    
    fun processGlobalRatingUpdate(rating: Double) {
        processingCount++
    }
    
    fun processNavigationAction() {
        processingCount++
    }
    
    fun processCancelNavigationAction() {
        processingCount++
    }
    
    fun processRatingTextState(rating: Int) {
        processingCount++
    }
}
