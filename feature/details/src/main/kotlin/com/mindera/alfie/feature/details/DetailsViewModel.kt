package com.mindera.alfie.feature.details

import androidx.annotation.DrawableRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val details = savedStateHandle.toRoute<Screen.Details>()
    
    private val _repoId = MutableStateFlow(details.repoId)
    val repoId: StateFlow<Int> = _repoId.asStateFlow()
    
    private val _repoName = MutableStateFlow(details.repoName)
    val repoName: StateFlow<String> = _repoName.asStateFlow()
    
    private val _repoStars = MutableStateFlow(details.repoStars)
    val repoStars: StateFlow<Int> = _repoStars.asStateFlow()
    
    private val _repoUrl = MutableStateFlow(details.repoUrl)
    val repoUrl: StateFlow<String> = _repoUrl.asStateFlow()
}