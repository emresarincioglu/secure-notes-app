package com.github.emresarincioglu.home.viewmodel

import androidx.annotation.IntRange
import androidx.lifecycle.ViewModel
import com.github.emresarincioglu.home.model.Note
import com.github.emresarincioglu.home.model.uistate.HomeScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.asStateFlow()

    fun swapNotes(@IntRange(from = 0) from: Int, @IntRange(from = 0) to: Int) {

        if (from != to) {
            // TODO: Swap notes in database
        }
    }
}