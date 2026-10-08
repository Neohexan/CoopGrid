# Employer Domain Specification

The **Employer Domain** manages employer profiles and associated corporate metadata inside `data/employer.db`.

> 📌 **Note for Developers:**  
> The JSON request and response snippets provided in this document serve as **sample illustration blueprints**. Field types and exact schemas are dynamically enforced by Pydantic schemas in code (`app/domains/employer/schemas.py`). Refer to the live FastAPI OpenAPI docs (`/docs`) for exact schema contracts.

---

## 1. Database Entity Blueprint (`data/employer.db`)

### 1.1 `employer_profiles` Table
Stores primary identity and contact details for the employer.

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `user_id` | `VARCHAR` | `PRIMARY KEY` | Unique ID provided by upstream Gateway |
| `full_name` | `VARCHAR` | `NOT NULL` | Contact person full name |
| `email` | `VARCHAR` | `NOT NULL`, `UNIQUE` | Official contact email |
| `phone` | `VARCHAR` | `NOT NULL` | Contact phone number |
| `is_verified` | `BOOLEAN` | `DEFAULT FALSE` | Profile verification status |
| `created_at` | `DATETIME` | `DEFAULT CURRENT` | System creation timestamp |
| `updated_at` | `DATETIME` | `AUTO UPDATE` | Last modification timestamp |

### 1.2 `company_details` Table
Stores optional corporate registration data linked via `user_id`.

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | `PRIMARY KEY` | Internal database surrogate key |
| `user_id` | `VARCHAR` | `FOREIGN KEY`, `UNIQUE` | Maps to `employer_profiles.user_id` |
| `company_name`| `VARCHAR` | `NOT NULL` | Official company name |
| `industry` | `VARCHAR` | `NULLABLE` | Sector / Industry vertical |
| `website` | `VARCHAR` | `NULLABLE` | Official website URL |
| `tax_id` | `VARCHAR` | `NULLABLE` | Tax/GST/Business identification number |
| `address` | `TEXT` | `NULLABLE` | Physical office address |

---

## 2. API Endpoints Overview

All endpoints are hosted under the `/employer` router path.

### 2.1 Create Profile
* **Method & Route:** `POST /employer/profiles/`
* **Purpose:** Registers a new employer profile and optional company details.
* **Sample Payload Structure:**
```json
{
  "user_id": "string",
  "full_name": "string",
  "email": "string",
  "phone": "string",
  "company": {
    "company_name": "string",
    "industry": "string (optional)",
    "website": "string (optional)",
    "tax_id": "string (optional)",
    "address": "string (optional)"
  }
}
```

### 2.2 Fetch Profile

* **Method & Route:** `GET /api/v1/employer/profiles/{user_id}`
* **Purpose:** Retrieves the employer profile and nested company details by user ID.
* **Sample Response Blueprint:**

```json
{
  "success": true,
  "domain": "employer",
  "data": {
    "user_id": "string",
    "full_name": "string",
    "email": "string",
    "phone": "string",
    "is_verified": true,
    "company": {
      "company_name": "string",
      "industry": "string",
      "website": "string",
      "tax_id": "string",
      "address": "string"
    },
    "created_at": "ISO-8601 Timestamp"
  }
}
```

### 2.3 Partial Update Profile

* **Method & Route:** `PATCH /employer/profiles/{user_id}`
* **Purpose:** Partially updates fields in `employer_profiles` or
  `company_details` without requiring full payload re-submission.