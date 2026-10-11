package com.example.coopgrid.employer.registration.data.details

import com.example.coopgrid.employer.registration.data.details.dto.EmpRegistrationRequest
import com.example.coopgrid.employer.registration.data.details.dto.EmpRegistrationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface EmpApiRgtService {

    @POST("profile/employer/register")
    suspend fun registerEmployer(
        @Body requestDto: EmpRegistrationRequest
    ): EmpRegistrationResponse

}