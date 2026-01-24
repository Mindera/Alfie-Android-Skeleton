package com.mindera.alfie.feature.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindera.alfie.core.ui.R
import com.mindera.alfie.domain.usecase.GetReposUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LandingUiState {
    object Loading : LandingUiState()
    data class Success(val tiles: List<Tile>, val hasMore: Boolean) : LandingUiState()
    data class Error(val message: String) : LandingUiState()
}

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val getReposUseCase: GetReposUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<LandingUiState>(LandingUiState.Loading)
    val uiState: StateFlow<LandingUiState> = _uiState.asStateFlow()
    
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()
    
    private var currentPage = 1
    private val tiles = mutableListOf<Tile>()
    private var hasMore = true
    
    // Pool of available images
    private val imagePool = listOf(
        R.drawable.m1, R.drawable.m2, R.drawable.m3, R.drawable.m4, R.drawable.m5,
        R.drawable.m6, R.drawable.m7, R.drawable.m8, R.drawable.m9, R.drawable.m10,
        R.drawable.m11, R.drawable.m12, R.drawable.m13, R.drawable.m14, R.drawable.m15
    )
    
    init {
        loadRepos()
    }
    
    fun loadRepos() {
        viewModelScope.launch {
            _uiState.value = LandingUiState.Loading
            fetchRepos(page = 1, isLoadMore = false)
        }
    }
    
    fun loadMoreRepos() {
        if (!hasMore || _isLoadingMore.value) return
        
        viewModelScope.launch {
            _isLoadingMore.value = true
            fetchRepos(page = currentPage + 1, isLoadMore = true)
            _isLoadingMore.value = false
        }
    }
    
    private suspend fun fetchRepos(page: Int, isLoadMore: Boolean) {
        getReposUseCase(page = page, perPage = 10)
            .onSuccess { repos ->
                if (!isLoadMore) {
                    tiles.clear()
                }
                
                val newTiles = repos.map { repo ->
                    Tile(
                        id = repo.id,
                        title = repo.name,
                        stars = repo.stars,
                        url = repo.url,
                        imageResId = imagePool[repo.id % imagePool.size]
                    )
                }
                
                tiles.addAll(newTiles)
                currentPage = page
                hasMore = newTiles.size >= 10
                
                _uiState.value = LandingUiState.Success(
                    tiles = tiles.toList(),
                    hasMore = hasMore
                )
            }
            .onFailure { error ->
                _uiState.value = LandingUiState.Error(
                    message = error.message ?: "Failed to load repositories"
                )
            }
    }
}
