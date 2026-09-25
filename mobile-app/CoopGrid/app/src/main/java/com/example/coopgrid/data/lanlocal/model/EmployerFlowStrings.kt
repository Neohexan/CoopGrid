package com.example.coopgrid.data.lanlocal.model

import com.example.coopgrid.employer.registration.presentation.steps.step0.string.EmpOtpStrings
import com.example.coopgrid.employer.registration.presentation.steps.step0.string.EmpPhoneNumStrings
import com.example.coopgrid.employer.registration.presentation.steps.step0.string.EmpTerms
import com.example.coopgrid.employer.registration.presentation.steps.step1.EmployerPersonal
import com.example.coopgrid.employer.registration.presentation.steps.step2.strings.EmployerOnboarding
import com.example.coopgrid.employer.registration.presentation.steps.step21.strings.BusinessDetails
import com.example.coopgrid.employer.registration.presentation.steps.step3.strings.EmpAddress
import kotlinx.serialization.Serializable

@Serializable
data class EmployerFlowStrings(
    val employerPhone: EmpPhoneNumStrings = EmpPhoneNumStrings(),
    val employerOtp: EmpOtpStrings = EmpOtpStrings(),
    val terms: EmpTerms = EmpTerms(),
    val employerPersonal: EmployerPersonal = EmployerPersonal(),
    val employerOnboarding: EmployerOnboarding = EmployerOnboarding(),
    val businessDetails: BusinessDetails = BusinessDetails(),
    val employerAddress: EmpAddress = EmpAddress()
)