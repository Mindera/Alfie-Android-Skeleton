package com.mindera.alfie.feature.splash

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initially isLoading is true and shouldNavigate is false`() = runTest(testDispatcher) {
        val viewModel = SplashViewModel()
        
        assertTrue(viewModel.isLoading.value)
        assertFalse(viewModel.shouldNavigate.value)
    }
    
    @Test
    fun `after 2 seconds isLoading becomes false and shouldNavigate becomes true`() = runTest(testDispatcher) {
        val viewModel = SplashViewModel()
        
        viewModel.shouldNavigate.test {
            assertFalse(awaitItem())
            
            advanceTimeBy(2000)
            
            assertTrue(awaitItem())
            assertFalse(viewModel.isLoading.value)
        }
    }
}
