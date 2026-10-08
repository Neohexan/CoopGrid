# Profile Server - Global API Specifications

This document defines global conventions, header requirements, response structures, and error payload formats enforced across all profile microservice endpoints (**Admin**, **Employer**, and **Worker** domains).

---

## 1. Upstream Headers & Context Passing

Since the **Rust API Gateway** handles public JWT authentication, internal requests forwarded to this service rely on the following headers:

| Header Name | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `X-User-ID` | `String` | **Yes** | Unique identifier of the authenticated user |
| `X-User-Role` | `String` | **Yes** | Role of the caller (`admin`, `employer`, `worker`) |
| `X-Correlation-ID` | `String` | Optional | Tracing ID passed down from Gateway for log tracking |

---

## 2. Standardized Response Formats

To ensure uniform parsing across the system, all API responses adhere to strict JSON conventions.

### 2.1 Success Response Wrapper (`200 OK` / `201 Created`)

Single-entity and collection endpoints wrap data cleanly. The following is an
example success response:

```json
{
  "success": true,
  "domain": "worker",
  "data": {
    "user_id": "wrk_98765",
    "display_name": "Arvind Kumar",
    "phone": "+919876543210",
    "is_active": true,
    "created_at": "2026-10-08T18:30:00Z"
  }
}
```

### 2.2 Standard Error Response

When a request fails, the API returns `success: false` with a descriptive error
object. The following is an example error response:

```json
{
  "success": false,
  "domain": "employer",
  "error": {
    "code": "PROFILE_NOT_FOUND",
    "message": "Profile for user_id 'emp_1234' does not exist in employer domain.",
    "timestamp": "2026-10-08T18:31:00Z",
    "details": null
  }
}
```

---

## 3. Global HTTP Status Codes & Error Enums

| HTTP Code | Error Enum (`error.code`) | Description / Trigger Scenario |
| :--- | :--- | :--- |
| `200 OK` | `N/A` | Request succeeded (`GET`, `PATCH`, `DELETE`). |
| `201 Created` | `N/A` | Resource was successfully created in the SQLite database. |
| `400 Bad Request` | `INVALID_PAYLOAD` | Malformed JSON request body or missing required fields. |
| `404 Not Found` | `PROFILE_NOT_FOUND` | `user_id` or a sub-resource (skill/document) does not exist in the domain database. |
| `409 Conflict` | `PROFILE_ALREADY_EXISTS` | Attempt to create an already existing profile. |
| `422 Unprocessable Entity` | `SCHEMA_VALIDATION_FAILED` | Pydantic data type or field validation error. |
| `500 Internal Server Error` | `DATABASE_ERROR` | SQLite concurrency lock timeout or disk write error. |

---

## 4. RESTful URL Naming Conventions

All endpoints follow strict REST principles and use the `/api/v1` version
prefix.

### 4.1 Domain URL Patterns

- **Admin Domain:** `/admin/profiles/...`
- **Employer Domain:** `/employer/profiles/...`
- **Worker Domain:** `/worker/profiles/...`

### 4.2 HTTP Verb Matrix

- **POST:** Naye profile ya child record (e.g. skills) ko insert karne ke liye.
- **GET:** Existing profile data fetch karne ke liye.
- **PATCH:** Profile ke specific fields ko partial update karne ke liye (bina baaki fields rewrite kiye).
- **DELETE:** Profile ya sub-record ko delete karne ke liye.