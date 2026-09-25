package com.example.coopgrid.worker.registration.presentation.steps.step23


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.worker.data.util.WorkerJsonReader
import com.example.coopgrid.worker.registration.presentation.steps.step23.model.AgriBusinessCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgriSupplyViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _categories = MutableStateFlow<List<AgriBusinessCategory>>(emptyList())
    val categories: StateFlow<List<AgriBusinessCategory>> = _categories.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val list = WorkerJsonReader.loadAgriCategoriesFromAssets(context)
            _categories.value = list
        }
    }
}