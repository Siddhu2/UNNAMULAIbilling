package com.example.unnamulaibilling.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Seller(
    val name: String,
    val description: String?,
    val address: String,
    val phone: String?,
    val gstin: String?,
    @Json(name = "pan_no") val panNo: String?
)

@JsonClass(generateAdapter = true)
data class InvoiceDetails(
    @Json(name = "invoice_number") val invoiceNumber: String,
    val date: String,
    @Json(name = "vehicle_no") val vehicleNo: String?,
    @Json(name = "vehicle_type") val vehicleType: String?
)

@JsonClass(generateAdapter = true)
data class Buyer(
    val name: String,
    val gstin: String?,
    val address: String,
    @Json(name = "pin_code") val pinCode: String?,
    @Json(name = "state_code") val stateCode: String?,
    val place: String?
)

@JsonClass(generateAdapter = true)
data class InvoiceItem(
    @Json(name = "s_no") val sNo: Int,
    @Json(name = "description_and_material") val descriptionAndMaterial: String,
    @Json(name = "hsn_code") val hsnCode: String,
    val quantity: String,
    @Json(name = "quantity_value") val quantityValue: Double,
    val unit: String,
    val rate: Double,
    val amount: Double
)

@JsonClass(generateAdapter = true)
data class TaxDetail(
    @Json(name = "rate_percent") val ratePercent: Double?,
    val amount: Double?
)

@JsonClass(generateAdapter = true)
data class TaxSummary(
    @Json(name = "total_quantity") val totalQuantity: String?,
    @Json(name = "sub_total") val subTotal: Double,
    val sgst: TaxDetail?,
    val cgst: TaxDetail?,
    val igst: TaxDetail?,
    @Json(name = "grand_total") val grandTotal: Double,
    @Json(name = "amount_in_words") val amountInWords: String?
)

@JsonClass(generateAdapter = true)
data class BankDetails(
    @Json(name = "bank_name") val bankName: String,
    val branch: String,
    @Json(name = "account_number") val accountNumber: String,
    @Json(name = "ifsc_code") val ifscCode: String
)

@JsonClass(generateAdapter = true)
data class Invoice(
    @Json(name = "document_type") val documentType: String,
    val seller: Seller,
    @Json(name = "invoice_details") val invoiceDetails: InvoiceDetails,
    val buyer: Buyer,
    val items: List<InvoiceItem>,
    @Json(name = "tax_summary") val taxSummary: TaxSummary,
    @Json(name = "bank_details") val bankDetails: BankDetails?,
    @Json(name = "terms_and_declarations") val termsAndDeclarations: List<String>?,
    @Json(name = "authorized_signatory") val authorizedSignatory: String?
)
