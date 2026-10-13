package com.example.coopgrid.worker.registration.presentation.steps.step4.viewmodel

import android.content.Context
import android.net.Uri
import com.example.coopgrid.worker.registration.presentation.steps.step2.model.ServiceOfferingType
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.AgriSupplyKyc
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.CommonIdentityErrors
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.CommonIdentityKyc
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.IdentityDocType
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.MachineryRentalKyc
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.PersonalSkillKyc
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.WorkerKycState

enum class KycDocFieldType {
    FRONT_IMAGE,
    BACK_IMAGE,
    SKILL_CERTIFICATE,
    MACHINERY_PROOF,
    AGRI_LICENSE
}

data class WorkerKycUiState(
    val kycState: WorkerKycState = WorkerKycState(),
    val selectedServices: List<ServiceOfferingType> = emptyList(),
    val identityErrors: CommonIdentityErrors = CommonIdentityErrors(),
    val uploadProgressMap: Map<KycDocFieldType, Boolean> = emptyMap(),
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
) {
    fun isUploading(fieldType: KycDocFieldType): Boolean = uploadProgressMap[fieldType] == true
}

sealed interface WorkerKycEvent {
    data class InitializeServices(val services: List<ServiceOfferingType>) : WorkerKycEvent
    data class DocTypeChanged(val docType: IdentityDocType) : WorkerKycEvent
    data class DocNumberChanged(val docNumber: String) : WorkerKycEvent
    data class UpdateCommonIdentity(val common: CommonIdentityKyc) : WorkerKycEvent
    data class UpdateSkillState(val updated: PersonalSkillKyc) : WorkerKycEvent
    data class UpdateMachineryState(val updated: MachineryRentalKyc) : WorkerKycEvent
    data class UpdateAgriState(val updated: AgriSupplyKyc) : WorkerKycEvent
    data class ImageSelected(val fieldType: KycDocFieldType, val uri: Uri, val context: Context) : WorkerKycEvent
    data class SubmitClicked(val onSuccess: (WorkerKycState) -> Unit) : WorkerKycEvent
}