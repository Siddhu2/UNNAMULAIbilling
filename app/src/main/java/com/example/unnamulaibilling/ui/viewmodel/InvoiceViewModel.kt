package com.example.unnamulaibilling.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unnamulaibilling.data.local.entity.SavedItemEntity
import com.example.unnamulaibilling.data.model.BankDetails
import com.example.unnamulaibilling.data.model.Buyer
import com.example.unnamulaibilling.data.model.InvoiceDetails
import com.example.unnamulaibilling.data.model.Invoice
import com.example.unnamulaibilling.data.model.InvoiceItem
import com.example.unnamulaibilling.data.model.Seller
import com.example.unnamulaibilling.data.model.TaxDetail
import com.example.unnamulaibilling.data.model.TaxSummary
import com.example.unnamulaibilling.data.repository.ItemRepository
import com.example.unnamulaibilling.util.NumberToWordsConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InvoiceViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _seller = MutableStateFlow(
        Seller(
            name = "UNNAMULAI",
            description = "Earth Movers & Transport\nJCB, Hitachi & Lorry",
            address = "No: 50, Thenmathi Nagar, (Backside RCM),\nKozhiyur, Tittagudi - 606 106",
            phone = "9790587210 / 9976378772",
            gstin = null,
            panNo = null
        )
    )
    val seller = _seller.asStateFlow()

    private val _buyer = MutableStateFlow(Buyer("", null, "", null, null, null))
    val buyer = _buyer.asStateFlow()

    private val _invoiceDetails = MutableStateFlow(
        InvoiceDetails(
            invoiceNumber = "1",
            date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            vehicleNo = null,
            vehicleType = null
        )
    )
    val invoiceDetails = _invoiceDetails.asStateFlow()

    private val _bankDetails = MutableStateFlow(
        BankDetails(
            bankName = "ICICI Bank",
            branch = "TITTAGUDI",
            accountNumber = "xxxxxxxxxxxxxx",
            ifscCode = "ICIC0000000"
        )
    )
    val bankDetails = _bankDetails.asStateFlow()

    private val _items = MutableStateFlow<List<InvoiceItem>>(emptyList())
    val items = _items.asStateFlow()

    private val _sgstRate = MutableStateFlow(0.0)
    val sgstRate = _sgstRate.asStateFlow()

    private val _cgstRate = MutableStateFlow(0.0)
    val cgstRate = _cgstRate.asStateFlow()

    private val _igstRate = MutableStateFlow(0.0)
    val igstRate = _igstRate.asStateFlow()

    val taxSummary: StateFlow<TaxSummary> = combine(
        _items, _sgstRate, _cgstRate, _igstRate
    ) { items, sgst, cgst, igst ->
        calculateTaxSummary(items, sgst, cgst, igst)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), calculateTaxSummary(emptyList(), 0.0, 0.0, 0.0))

    val savedItems: StateFlow<List<SavedItemEntity>> = itemRepository.allSavedItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSeller(seller: Seller) {
        _seller.value = seller
    }

    fun updateBuyer(buyer: Buyer) {
        _buyer.value = buyer
    }

    fun updateInvoiceDetails(details: InvoiceDetails) {
        _invoiceDetails.value = details
    }

    fun updateBankDetails(details: BankDetails) {
        _bankDetails.value = details
    }

    fun addItem(item: InvoiceItem) {
        _items.update { it + item }
    }

    fun removeItem(index: Int) {
        _items.update { currentList ->
            currentList.filterIndexed { i, _ -> i != index }.mapIndexed { i, item -> item.copy(sNo = i + 1) }
        }
    }

    fun updateItem(index: Int, item: InvoiceItem) {
        _items.update { currentList ->
            val mutable = currentList.toMutableList()
            if (index in mutable.indices) {
                mutable[index] = item
            }
            mutable
        }
    }

    fun setTaxRates(sgst: Double, cgst: Double, igst: Double) {
        _sgstRate.value = sgst
        _cgstRate.value = cgst
        _igstRate.value = igst
    }

    fun getInvoice(): Invoice {
        return Invoice(
            documentType = "TAX INVOICE",
            seller = _seller.value,
            invoiceDetails = _invoiceDetails.value,
            buyer = _buyer.value,
            items = _items.value,
            taxSummary = taxSummary.value,
            bankDetails = _bankDetails.value,
            termsAndDeclarations = listOf("1. Subject to Tittagudi Jurisdiction.", "2. E. & O. E."),
            authorizedSignatory = _seller.value.name
        )
    }

    private fun calculateTaxSummary(
        items: List<InvoiceItem>,
        sgstRate: Double,
        cgstRate: Double,
        igstRate: Double
    ): TaxSummary {
        val totalQuantityValue = items.sumOf { it.quantityValue }
        val subTotal = items.sumOf { it.amount }

        val sgstAmount = if (sgstRate > 0) subTotal * (sgstRate / 100) else 0.0
        val cgstAmount = if (cgstRate > 0) subTotal * (cgstRate / 100) else 0.0
        val igstAmount = if (igstRate > 0) subTotal * (igstRate / 100) else 0.0

        val grandTotal = subTotal + sgstAmount + cgstAmount + igstAmount

        return TaxSummary(
            totalQuantity = String.format(Locale.US, "%.2f", totalQuantityValue),
            subTotal = subTotal,
            sgst = if (sgstRate > 0) TaxDetail(sgstRate, sgstAmount) else null,
            cgst = if (cgstRate > 0) TaxDetail(cgstRate, cgstAmount) else null,
            igst = if (igstRate > 0) TaxDetail(igstRate, igstAmount) else null,
            grandTotal = grandTotal,
            amountInWords = NumberToWordsConverter.convert(Math.round(grandTotal))
        )
    }
}
