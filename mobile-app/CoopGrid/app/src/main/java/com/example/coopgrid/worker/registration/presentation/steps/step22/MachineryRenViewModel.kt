package com.example.coopgrid.worker.registration.presentation.steps.step22

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.worker.data.util.WorkerJsonReader
import com.example.coopgrid.worker.registration.presentation.steps.step22.model.MachineryCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MachineryViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _categories = MutableStateFlow<List<MachineryCategory>>(emptyList())
    val categories: StateFlow<List<MachineryCategory>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            // Aapka WorkerJsonReader method yahan call ho raha hai
            val list = WorkerJsonReader.loadMachineryCategoriesFromAssets(context)
            _categories.value = list
            _isLoading.value = false
        }
    }
}