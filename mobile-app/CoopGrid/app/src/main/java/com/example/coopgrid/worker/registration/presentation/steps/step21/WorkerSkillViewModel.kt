package com.example.coopgrid.worker.registration.presentation.steps.step21

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.worker.data.util.WorkerJsonReader
import com.example.coopgrid.worker.registration.presentation.steps.step21.model.JobCategory
import com.example.coopgrid.worker.registration.presentation.steps.step23.model.AgriBusinessCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerSkillViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _categories = MutableStateFlow<List<JobCategory>>(emptyList())
    val categories: StateFlow<List<JobCategory>> = _categories.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val list = WorkerJsonReader.loadWorkerSkillsCategoriesFromAssets(context)
            _categories.value = list
        }
    }
}