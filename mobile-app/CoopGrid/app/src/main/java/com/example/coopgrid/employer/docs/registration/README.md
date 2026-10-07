# Employer Registration & Onboarding Module Blueprint

## Overview
This module manages the complete multi-step registration flow specifically designed for **Employers**. It handles phone-based OTP authentication, terms acceptance, basic personal details, conditional business onboarding, and location setup.

---

## Directory Mapping
- `data/`: Network DTOs, API Interfaces, Encrypted Storage & Token Managers.
- `navigation/`: Jetpack Compose Nested Navigation & Route Definitions.
- `presentation/`: UI Components & Step Screens (`step0`, `step1`, `step2`, `step3`, `step21`).
- `viewmodel/`: Centralized State Management (`RegistrationViewModel` & `RegistrationUiState`).

---

## Step-by-Step Onboarding Architecture

                        [ Step 00: Phone OTP & Terms ]
                                    │
                        [ Step 01: Basic Information ]
                                    │
                        [ Step 02: Employer Role Type ]
                                    │
             ┌──────────────────────┴──────────────────────┐
             ▼                                             ▼
    [ Farmer / Household ]                    [ Wholesaler / Company ]
             │                                             │
             │                                  [ Step 21: Business Info ]
             │                                             │
             └──────────────────────┬──────────────────────┘
                                    ▼
                        [ Step 03: Address Details ]

## Step Descriptions & Routing Logic

### 1. [Step 00: Phone OTP & Terms](steps/STEP_00_PHONE_OTP.md)
* **Goal:** User authentication and legal consent.
* **Flow:** Accepts phone number, verifies OTP via Rust backend, requires Terms & Conditions agreement, and securely saves Access & Refresh Tokens in Android Keystore / Local Storage.

### 2. [Step 01: Basic Information](steps/STEP_01_BASIC_INFO.md)
* **Goal:** Collect basic profile details.
* **Fields:** Full Name, Profile Picture, and Primary Language preference.

### 3. [Step 02: Employer Role Selection](steps/STEP_02_ROLE_SELECTION.md)
* **Goal:** Determine the Employer category to trigger conditional navigation.
* **Employer Types:**
  - **Farmer**
  - **Household**
  - **Wholesaler**
  - **Company**
* **Branching Logic:**
  - If **Farmer** or **Household** $\rightarrow$ Navigate directly to **Step 03**.
  - If **Wholesaler** or **Company** $\rightarrow$ Navigate to **Step 21**.

### 4. [Step 21: Business & Company Details](steps/STEP_21_BUSINESS_INFO.md) *(Conditional)*
* **Goal:** Collect entity credentials for Wholesalers and Companies.
* **Fields:** Company/Business Name, GST/Tax details, Business Type, and Verification Documents.
* **Next Target:** Navigates to **Step 03** upon completion.

### 5. [Step 03: Address & Location Details](steps/STEP_03_ADDRESS_DETAILS.md)
* **Goal:** Finalize location setup for job postings and local service delivery.
* **Fields:** Complete Address, City, State, Pincode, and GPS Coordinates.

---

## Related Documentation
- [Architecture & Security Specifications](ARCHITECTURE_&_SECURITY.md)