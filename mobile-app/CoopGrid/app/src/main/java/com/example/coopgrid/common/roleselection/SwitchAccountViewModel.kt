package com.example.coopgrid.common.roleselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.employer.data.local.EmployerTokenManager
import com.example.coopgrid.worker.data.local.WorkerTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwitchAccountViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val workerTokenManager: WorkerTokenManager,
    private val employerTokenManager: EmployerTokenManager
) : ViewModel() {

    fun switchRole(targetRole: String) {
        viewModelScope.launch {
            try {
                val hasValidToken = if (targetRole == "WORKER") {
                    !workerTokenManager.getAccessToken().isNullOrBlank()
                } else {
                    !employerTokenManager.getAccessToken().isNullOrBlank()
                }

                if (hasValidToken) {
                    // ✅ 1. Token mil gaya: Sirf active role update karo.
                    // Datastore mein baki data (profile completion, user id) safe rahega!
                    userPreferences.setActiveRole(targetRole)
                } else {
                    // ❌ Token nahi hai:
                    // Yahan prefs.clear() NAHI karenge, par active role ko blank/null kar denge
                    // taaki AppNavGraph turant samajh jaye ki login screen par bhejna hai!
                    userPreferences.setActiveRole("") // ya userPreferences.clearActiveRoleOnly()
                }
            } catch (e: Exception) {
                // Error handling
            }
        }
    }
}