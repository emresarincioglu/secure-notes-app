package com.github.emresarincioglu.home.viewmodel

import androidx.lifecycle.ViewModel
import com.github.emresarincioglu.home.model.uistate.HomeScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.asStateFlow()
}