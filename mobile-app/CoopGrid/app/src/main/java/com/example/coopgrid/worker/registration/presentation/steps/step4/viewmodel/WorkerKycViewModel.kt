package com.example.coopgrid.worker.registration.presentation.steps.step4.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.data.utils.NativeImageCompressor
import com.example.coopgrid.worker.registration.data.form.repository.KycRepository
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.CommonIdentityErrors
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.WorkerKycState
import com.example.coopgrid.worker.registration.presentation.steps.step4.util.KycValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WorkerKycViewModel @Inject constructor(
     private val kycRepository: KycRepository // Jab aap apna chunk upload repo inject karna chahein
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerKycUiState())
    val uiState: StateFlow<WorkerKycUiState> = _uiState.asStateFlow()

    // Single entry point for all KYC screen events
    fun onEvent(event: WorkerKycEvent) {
        when (event) {
            is WorkerKycEvent.InitializeServices -> {
                _uiState.update { state ->
                    state.copy(
                        kycState = state.kycState.copy(selectedServices = event.services)
                    )
                }
            }
            is WorkerKycEvent.DocTypeChanged -> {
                _uiState.update { state ->
                    state.copy(
                        kycState = state.kycState.copy(
                            commonIdentity = state.kycState.commonIdentity.copy(
                                docType = event.docType,
                                docNumber = "" // Reset doc number on change
                            )
                        ),
                        identityErrors = state.identityErrors.copy(docNumberError = null)
                    )
                }
            }

            is WorkerKycEvent.DocNumberChanged -> {
                _uiState.update { state ->
                    state.copy(
                        kycState = state.kycState.copy(
                            commonIdentity = state.kycState.commonIdentity.copy(docNumber = event.docNumber)
                        ),
                        identityErrors = state.identityErrors.copy(docNumberError = null)
                    )
                }
            }

            is WorkerKycEvent.ImageSelected -> {
                handleImageSelection(event.context, event.uri, event.fieldType)
            }

            is WorkerKycEvent.UpdateSkillState -> {
                _uiState.update { it.copy(kycState = it.kycState.copy(personalSkillKyc = event.updated)) }
            }

            is WorkerKycEvent.UpdateMachineryState -> {
                _uiState.update { it.copy(kycState = it.kycState.copy(machineryRentalKyc = event.updated)) }
            }

            is WorkerKycEvent.UpdateAgriState -> {
                _uiState.update { it.copy(kycState = it.kycState.copy(agriSupplyKyc = event.updated)) }
            }

            is WorkerKycEvent.UpdateCommonIdentity -> {
                _uiState.update { state ->
                    state.copy(
                        kycState = state.kycState.copy(commonIdentity = event.common),
                        identityErrors = state.identityErrors.copy(docNumberError = null)
                    )
                }
            }

            is WorkerKycEvent.SubmitClicked -> {
                validateAndSubmit(event.onSuccess)
            }
        }
    }

    private fun handleImageSelection(context: Context, uri: Uri, fieldType: KycDocFieldType) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Turn on loading indicator for this specific field
            setLoading(fieldType, true)

            // 2. Run Native Image Compression (Max 1280px, 75% Quality)
            val compressedFile = NativeImageCompressor.compressImage(
                context = context,
                imageUri = uri
            )

            if (compressedFile != null) {
                // 3. 64KB Chunked Upload Call
                val uploadResult = kycRepository.uploadFileInChunks(
                    file = compressedFile,
                    fieldType = fieldType,
                    originalUri = uri
                )

                uploadResult.onSuccess { mediaId ->
                    withContext(Dispatchers.Main) {
                        // 4. Update state with actual server URL
                        updateFieldUri(fieldType, mediaId)
                        setLoading(fieldType, false)
                    }
                }.onFailure { error ->
                    withContext(Dispatchers.Main) {
                        // Handle failure error state
                        setLoading(fieldType, false)

                        // Error message UI state me set kar dein taaki Snackbar ya Toast dikh sake
                        _uiState.update { state ->
                            state.copy(
                                errorMessage = "Upload failed for ${fieldType.name}: ${error.localizedMessage ?: "Unknown error"}"
                            )
                        }

                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    setLoading(fieldType, false)
                    _uiState.update {
                        it.copy(errorMessage = "Image compression failed. Please try another image.")
                    }
                }
            }
        }
    }

    private fun setLoading(fieldType: KycDocFieldType, isLoading: Boolean) {
        _uiState.update { state ->
            val updatedMap = state.uploadProgressMap.toMutableMap()
            updatedMap[fieldType] = isLoading
            state.copy(uploadProgressMap = updatedMap)
        }
    }

    private fun updateFieldUri(fieldType: KycDocFieldType, mediaId: String) {
        _uiState.update { state ->
            val currentKyc = state.kycState
            val updatedKyc = when (fieldType) {
                KycDocFieldType.FRONT_IMAGE -> currentKyc.copy(
                    commonIdentity = currentKyc.commonIdentity.copy(frontImageId = mediaId)
                )
                KycDocFieldType.BACK_IMAGE -> currentKyc.copy(
                    commonIdentity = currentKyc.commonIdentity.copy(backImageId = mediaId)
                )
                KycDocFieldType.SKILL_CERTIFICATE -> currentKyc.copy(
                    personalSkillKyc = currentKyc.personalSkillKyc.copy(certificateImageId = mediaId)
                )

                KycDocFieldType.MACHINERY_LICENSE -> currentKyc.copy(
                    machineryRentalKyc = currentKyc.machineryRentalKyc.copy(licensePhotoId = mediaId)
                )
                KycDocFieldType.MACHINERY_PHOTO -> currentKyc.copy(
                    machineryRentalKyc = currentKyc.machineryRentalKyc.copy(machinePhotoId = mediaId)
                )
                KycDocFieldType.AGRI_LICENSE -> currentKyc.copy(
                    agriSupplyKyc = currentKyc.agriSupplyKyc.copy(licenseDocId = mediaId)
                )
                KycDocFieldType.AGRI_SHOP_PHOTO -> currentKyc.copy(
                    agriSupplyKyc = currentKyc.agriSupplyKyc.copy(shopPhotoId = mediaId)
                )
            }
            state.copy(kycState = updatedKyc)
        }
    }

    private fun validateAndSubmit(onSuccess: (WorkerKycState) -> Unit) {
        val currentState = _uiState.value.kycState
        val common = currentState.commonIdentity

        val isDocBlank = common.docNumber.isBlank()
        val isNumValid = KycValidation.validateIdentityNumber(common.docType, common.docNumber)
        val hasFront = !common.frontImageId.isNullOrBlank()
        val hasBack = !common.backImageId.isNullOrBlank()

        val errors = CommonIdentityErrors(
            docNumberError = when {
                isDocBlank -> "Document number is required"
                !isNumValid -> "Invalid document number format"
                else -> null
            },
            frontImageError = if (!hasFront) "Front photo is required" else null,
            backImageError = if (!hasBack) "Back photo is required" else null
        )

        _uiState.update { it.copy(identityErrors = errors) }

        // Agar saari validations pass ho jati hain, tabhi parent ko data handoff karenge
        if (!isDocBlank && isNumValid && hasFront && hasBack) {
            onSuccess(currentState)
        }
    }
}
