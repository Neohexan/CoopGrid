package com.example.coopgrid.employer.data.di

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val uri = chain.request().url.toUri().toString()

        // 1. Send OTP Mock Response
        if (uri.contains("auth/send-otp")) {
            val jsonResponse = """
                {
                    "success": true,
                    "message": "OTP aapke phone par bhej diya gaya hai (Mock)",
                    "request_id": "mock_req_12345"
                }
            """.trimIndent()

            return Response.Builder()
                .code(200)
                .message("OK")
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .body(jsonResponse.toResponseBody("application/json".toMediaType()))
                .build()
        }

        // 2. Verify OTP Mock Response
        if (uri.contains("auth/verify-otp")) {
            val jsonResponse = """
                {
                    "success": true,
                    "message": "OTP verification successful!",
                    "access_token": "mock_access_token_xyz_999",
                    "refresh_token": "mock_refresh_token_abc_111",
                    "user_id": "usr_emp_777",
                    "is_profile_complete": false
                }
            """.trimIndent()

            return Response.Builder()
                .code(200)
                .message("OK")
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .body(jsonResponse.toResponseBody("application/json".toMediaType()))
                .build()
        }

        return chain.proceed(chain.request())
    }
}