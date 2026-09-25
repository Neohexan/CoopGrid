package com.example.coopgrid.data.lanlocal.model

import com.example.coopgrid.common.roleselection.AuthSelectionMain
import kotlinx.serialization.Serializable


// --- ROOT APP STRINGS MODEL ---
@Serializable
data class AppStrings(
    val authSelection: AuthSelectionMain = AuthSelectionMain(),
    val workerFlow: WorkerFlowStrings = WorkerFlowStrings(),
    val employerFlow: EmployerFlowStrings = EmployerFlowStrings()
)