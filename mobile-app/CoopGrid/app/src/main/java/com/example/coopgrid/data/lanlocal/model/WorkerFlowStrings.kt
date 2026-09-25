package com.example.coopgrid.data.lanlocal.model

import com.example.coopgrid.worker.registration.presentation.steps.step0.string.WorkerOtpStrings
import com.example.coopgrid.worker.registration.presentation.steps.step0.string.WorkerPhoneNumStrings
import com.example.coopgrid.worker.registration.presentation.steps.step0.string.WorkerTerms
import com.example.coopgrid.worker.registration.presentation.steps.step1.WorkerPersonal
import com.example.coopgrid.worker.registration.presentation.steps.step2.strings.ServiceType
import com.example.coopgrid.worker.registration.presentation.steps.step21.WorkerSkill
import com.example.coopgrid.worker.registration.presentation.steps.step22.strings.MachineryRental
import com.example.coopgrid.worker.registration.presentation.steps.step23.strings.AgriSupplyProfile
import com.example.coopgrid.worker.registration.presentation.steps.step3.strings.WorkerAddress
import com.example.coopgrid.worker.registration.presentation.steps.step4.strings.WorkerKyc
import kotlinx.serialization.Serializable

// --- WORKER FLOW SCHEMAS ---
@Serializable
data class WorkerFlowStrings(
    val workerPhone: WorkerPhoneNumStrings = WorkerPhoneNumStrings(),
    val workerOtp: WorkerOtpStrings = WorkerOtpStrings(),
    val terms: WorkerTerms = WorkerTerms(),
    val workerPersonal: WorkerPersonal = WorkerPersonal(),
    val serviceType: ServiceType = ServiceType(),
    val workerSkill: WorkerSkill = WorkerSkill(),
    val machineryRental: MachineryRental = MachineryRental(),
    val agriSupplyProfile: AgriSupplyProfile = AgriSupplyProfile(),
    val address: WorkerAddress = WorkerAddress(),
    val kyc: WorkerKyc = WorkerKyc(),
)