# Project Plan

An Android application that generates a PDF invoice based on a specific JSON structure. It features input forms for Seller details, Buyer details, Invoice details, and Bank details. Users can dynamically add, edit, and delete invoice items. A key feature is that previously entered items must be saved locally (e.g., using Room Database) to provide auto-completion or a quick-select list so users don't have to re-type them. The app must generate a PDF invoice matching a strict tabular layout containing all these details, with tax calculations (SGST, CGST, IGST) and amounts in words.

## Project Brief

# UNNAMULAI Billing - Project Brief

## Features
1. **Comprehensive Invoice Forms:** Dedicated input sections for capturing Seller details, Buyer details, Invoice metadata, and Bank details.
2. **Dynamic Item Management with Auto-complete:** Users can dynamically add, edit, and delete invoice line items. Previously entered items are saved locally to provide quick-select and auto-complete suggestions.
3. **Automated Calculations:** Automatically calculates necessary taxes (SGST, CGST, IGST) and converts the final total amount into words.
4. **Strict Tabular PDF Generation:** Compiles the input data into a strictly formatted, tabular PDF document matching the specific JSON structure.

## High-Level Tech Stack
* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose
* **Navigation & Adaptive Strategy:** Jetpack Navigation 3 (state-driven) and the Compose Material Adaptive library for building responsive, future-proof layouts.
* **Architecture & Concurrency:** MVVM (Model-View-ViewModel) pattern leveraging Kotlin Coroutines for asynchronous data handling.
* **Local Persistence:** Room Database (explicitly required for locally saving and auto-completing previously entered invoice items).
* **PDF Generation:** Android's native `PdfDocument` API (or a targeted third-party PDF layout library) to handle the strict tabular rendering.

## Implementation Steps

### Task_1_DataLayer: Implement Room Database for local persistence of invoice items (auto-complete) and define core invoice data models.
- **Status:** COMPLETED
- **Updates:** Coder agent has successfully created the models, Room entity, DAO, Database and Repository for the data layer. Project builds successfully.
- **Acceptance Criteria:**
  - Room entities and DAOs created
  - Repository implemented
  - Data models for Seller, Buyer, Bank, and Invoice defined

### Task_2_UI_And_State: Implement MVVM ViewModels with tax calculations and number-to-words logic, plus Jetpack Compose UI for all forms with Navigation 3.
- **Status:** COMPLETED
- **Updates:** Fixed theme contrast colors across all screens and added Room DB pre-seeding for items. Build and unit tests pass.
- **Acceptance Criteria:**
  - ViewModels manage form state and calculations accurately
  - Compose UI built for forms and item auto-complete
  - Navigation 3 implemented seamlessly

### Task_3_PDF_Generation: Implement strictly formatted tabular PDF generation using native PdfDocument API and connect it to the UI for export/sharing.
- **Status:** COMPLETED
- **Updates:** Coder agent successfully implemented PDF generation using PdfDocument API, drawing a strict tabular tax invoice layout on the canvas. Added FileProvider configuration for sharing and integrated it into the Compose UI. Build is successful.
- **Acceptance Criteria:**
  - PDF generated with strict tabular format containing all invoice details
  - File saved/shared successfully

### Task_4_RunAndVerify: Run and Verify step. Instruct critic_agent to verify application stability, confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** Critic agent completed final verification. Application stability, high contrast UI, pre-seeded auto-complete items, and tabular PDF generation and sharing verified successfully.
- **Acceptance Criteria:**
  - make sure all existing tests pass
  - build pass
  - app does not crash
  - application stability verified
  - PDF layout confirmed
- **Duration:** N/A

