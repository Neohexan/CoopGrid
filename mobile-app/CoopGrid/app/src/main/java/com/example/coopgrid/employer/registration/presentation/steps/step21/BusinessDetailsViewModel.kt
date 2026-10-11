package com.example.coopgrid.employer.registration.presentation.steps.step21


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.employer.data.utils.BusinessDataLoader
import com.example.coopgrid.employer.data.utils.LocationDataLoader
import com.example.coopgrid.employer.registration.presentation.steps.step21.model.CategoryResponse
import com.example.coopgrid.employer.registration.presentation.steps.step21.model.EmployerCategoryItem
import com.example.coopgrid.employer.registration.presentation.steps.step3.models.StateOption
import com.example.coopgrid.employer.registration.viewmodel.EmployerFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


// 2. ViewModel using StateFlow with Hilt Injection
@HiltViewModel
class BusinessDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Categories list state
    private val _categories = MutableStateFlow<List<EmployerCategoryItem>>(emptyList())
    val categories: StateFlow<List<EmployerCategoryItem>> = _categories.asStateFlow()
    // Loading State
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCategoriesFromJson()
    }

    private fun loadCategoriesFromJson() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true

                // Data load karein
                val data = BusinessDataLoader.loadBusinessData(context)

                // Categories state update karein
                _categories.value = data

            } catch (e: Exception) {
                e.printStackTrace()
                _categories.value = emptyList()
            } finally {
                // Loading ko hamesha finally block me false karein taaki error par bhi stuck na ho
                _isLoading.value = false
            }
        }
    }
}