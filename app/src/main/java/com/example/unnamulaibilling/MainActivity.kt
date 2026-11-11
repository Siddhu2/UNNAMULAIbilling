package com.example.unnamulaibilling

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.unnamulaibilling.data.local.BillingDatabase
import com.example.unnamulaibilling.data.repository.ItemRepository
import com.example.unnamulaibilling.ui.navigation.AppRoute
import com.example.unnamulaibilling.ui.screens.BankDetailsScreen
import com.example.unnamulaibilling.ui.screens.BuyerScreen
import com.example.unnamulaibilling.ui.screens.InvoiceDetailsScreen
import com.example.unnamulaibilling.ui.screens.ItemsScreen
import com.example.unnamulaibilling.ui.screens.PreviewScreen
import com.example.unnamulaibilling.ui.screens.SellerScreen
import com.example.unnamulaibilling.ui.theme.UNNAMULAIBillingTheme
import com.example.unnamulaibilling.ui.viewmodel.InvoiceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = BillingDatabase.getDatabase(this)
        val repository = ItemRepository(database.savedItemDao())

        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return InvoiceViewModel(repository) as T
            }
        }

        setContent {
            UNNAMULAIBillingTheme {
                val viewModel: InvoiceViewModel = viewModel(factory = factory)
                val backStack = remember { mutableStateListOf<Any>(AppRoute.SellerScreen) }

                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = { key ->
                        when (key) {
                            AppRoute.SellerScreen -> NavEntry(key) {
                                SellerScreen(
                                    viewModel = viewModel,
                                    onNext = { backStack.add(AppRoute.InvoiceDetailsScreen) }
                                )
                            }
                            AppRoute.InvoiceDetailsScreen -> NavEntry(key) {
                                InvoiceDetailsScreen(
                                    viewModel = viewModel,
                                    onBack = { backStack.removeLastOrNull() },
                                    onNext = { backStack.add(AppRoute.BuyerScreen) }
                                )
                            }
                            AppRoute.BuyerScreen -> NavEntry(key) {
                                BuyerScreen(
                                    viewModel = viewModel,
                                    onBack = { backStack.removeLastOrNull() },
                                    onNext = { backStack.add(AppRoute.ItemsScreen) }
                                )
                            }
                            AppRoute.ItemsScreen -> NavEntry(key) {
                                ItemsScreen(
                                    viewModel = viewModel,
                                    onBack = { backStack.removeLastOrNull() },
                                    onNext = { backStack.add(AppRoute.BankDetailsScreen) }
                                )
                            }
                            AppRoute.BankDetailsScreen -> NavEntry(key) {
                                BankDetailsScreen(
                                    viewModel = viewModel,
                                    onBack = { backStack.removeLastOrNull() },
                                    onNext = { backStack.add(AppRoute.PreviewScreen) }
                                )
                            }
                            AppRoute.PreviewScreen -> NavEntry(key) {
                                PreviewScreen(
                                    viewModel = viewModel,
                                    onBack = { backStack.removeLastOrNull() }
                                )
                            }
                            else -> error("Unknown route: $key")
                        }
                    }
                )
            }
        }
    }
}
