package com.example.unnamulaibilling.ui.navigation

import kotlinx.serialization.Serializable

sealed class AppRoute {
    @Serializable data object SellerScreen : AppRoute()
    @Serializable data object BuyerScreen : AppRoute()
    @Serializable data object InvoiceDetailsScreen : AppRoute()
    @Serializable data object ItemsScreen : AppRoute()
    @Serializable data object BankDetailsScreen : AppRoute()
    @Serializable data object PreviewScreen : AppRoute()
}
