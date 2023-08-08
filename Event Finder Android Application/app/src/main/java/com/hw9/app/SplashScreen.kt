package com.hw9.app

import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//In this block, some code is adapted from https://github.com/al3fret/Authentication/tree/splash/app/src/main/java/com/afret/authentication/presentation/splash
//The block is 15-21
@HiltViewModel
class SplashScreen @Inject constructor() : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    init {
        viewModelScope.launch {
            delay(100)
            _isLoading.value = false
        }
    }
}