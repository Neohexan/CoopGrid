package com.example.coopgrid.worker.registration.presentation.steps.step4

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.worker.registration.presentation.steps.step2.model.ServiceOfferingType
import com.example.coopgrid.worker.registration.presentation.steps.step4.viewmodel.KycDocFieldType
import com.example.coopgrid.worker.registration.presentation.steps.step4.viewmodel.WorkerKycEvent
import com.example.coopgrid.worker.registration.presentation.steps.step4.viewmodel.WorkerKycViewModel
import com.example.coopgrid.worker.registration.viewmodel.WorkerFormViewModel
import com.example.coopgrid.worker.registration.viewmodel.WorkerRegistrationEvent
import java.io.File


@Composable
fun WorkerKycScreen(
    selectedServices: List<ServiceOfferingType>,
    onKycComplete: () -> Unit,
    registrationViewModel: WorkerFormViewModel = hiltViewModel(),
    kycViewModel: WorkerKycViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()

    // 1. Central Single Source of Truth (Parent Form State)
    val formState by registrationViewModel.formState.collectAsStateWithLifecycle()

    // 2. Local KYC Media ViewModel States
    val kycUiState by kycViewModel.uiState.collectAsStateWithLifecycle()
    val uploadProgressMap = kycUiState.uploadProgressMap
    val identityErrors = kycUiState.identityErrors

    // 🟢 Step: Local ViewModel me Services Sync karein
    LaunchedEffect(formState.selectedServices) {
        if (formState.selectedServices.isNotEmpty()) {
            kycViewModel.onEvent(WorkerKycEvent.InitializeServices(formState.selectedServices))
        }
    }

    // Trackers for option dialogs & active upload field
    var activeFieldType by remember { mutableStateOf<KycDocFieldType?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    var showOptionDialog by remember { mutableStateOf(false) }

    // 1. Gallery Image Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            activeFieldType?.let { field ->
                kycViewModel.onEvent(
                    WorkerKycEvent.ImageSelected(
                        fieldType = field,
                        uri = selectedUri,
                        context = context
                    )
                )
            }
        }
    }

    // 2. Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempUri != null) {
            activeFieldType?.let { field ->
                kycViewModel.onEvent(
                    WorkerKycEvent.ImageSelected(
                        fieldType = field,
                        uri = tempUri!!,
                        context = context
                    )
                )
            }
        }
    }

    // 3. Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && tempUri != null) {
            cameraLauncher.launch(tempUri!!)
        } else {
            Toast.makeText(
                context,
                "Camera permission is required to capture KYC documents.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // 4. Passing all required arguments to WorkerKycContent
    WorkerKycContent(
        selectedServices = selectedServices,
        strings = appStrings.workerFlow.kyc,
        kycState = kycUiState.kycState,
        identityErrors = identityErrors,
        isFrontUploading = uploadProgressMap[KycDocFieldType.FRONT_IMAGE] == true,
        isBackUploading = uploadProgressMap[KycDocFieldType.BACK_IMAGE] == true,
        onCommonIdentityChange = { updatedCommon ->
            kycViewModel.onEvent(WorkerKycEvent.UpdateCommonIdentity(updatedCommon))
        },
        onSkillKycChange = { updatedSkill ->
            kycViewModel.onEvent(WorkerKycEvent.UpdateSkillState(updatedSkill))
        },
        onMachineryKycChange = { updatedMachinery ->
            kycViewModel.onEvent(WorkerKycEvent.UpdateMachineryState(updatedMachinery))
        },
        onAgriKycChange = { updatedAgri ->
            kycViewModel.onEvent(WorkerKycEvent.UpdateAgriState(updatedAgri))
        },
        onImageSelected = { fieldType ->
            activeFieldType = fieldType
            showOptionDialog = true // Direct open source choice dialog (Camera vs Gallery)
        },
        onSubmitClick = {
            // Validate locally first via KycViewModel
            kycViewModel.onEvent(
                WorkerKycEvent.SubmitClicked { validKycData ->
                    // Load final compiled KYC data into Parent WorkerFormViewModel
                    registrationViewModel.onEvent(
                        WorkerRegistrationEvent.UpdateKycState(validKycData)
                    )
                    // Move to next step
                    onKycComplete()
                }
            )
        }
    )

    // Option Dialog (Camera vs Gallery)
    if (showOptionDialog) {
        AlertDialog(
            onDismissRequest = { showOptionDialog = false },
            title = { Text("Select Document Source") },
            text = { Text("Choose whether you want to take a live photo or select from the gallery.") },
            confirmButton = {
                Button(
                    onClick = {
                        showOptionDialog = false
                        val file = File(
                            context.externalCacheDir,
                            "kyc_doc_${System.currentTimeMillis()}.jpg"
                        )
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                        tempUri = uri
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text("Camera")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showOptionDialog = false
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Text("Gallery")
                }
            }
        )
    }
}