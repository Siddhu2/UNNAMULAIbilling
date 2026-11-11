package com.example.unnamulaibilling.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unnamulaibilling.data.model.InvoiceDetails
import com.example.unnamulaibilling.ui.viewmodel.InvoiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailsScreen(
    viewModel: InvoiceViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val details by viewModel.invoiceDetails.collectAsStateWithLifecycle()
    
    var invoiceNumber by remember(details) { mutableStateOf(details.invoiceNumber) }
    var date by remember(details) { mutableStateOf(details.date) }
    var vehicleNo by remember(details) { mutableStateOf(details.vehicleNo ?: "") }
    var vehicleType by remember(details) { mutableStateOf(details.vehicleType ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoice Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.updateInvoiceDetails(
                        InvoiceDetails(
                            invoiceNumber = invoiceNumber,
                            date = date,
                            vehicleNo = vehicleNo.takeIf { it.isNotBlank() },
                            vehicleType = vehicleType.takeIf { it.isNotBlank() }
                        )
                    )
                    onNext()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text("Next", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = invoiceNumber,
                onValueChange = { invoiceNumber = it },
                label = { Text("Invoice Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (dd/MM/yyyy)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = vehicleNo,
                onValueChange = { vehicleNo = it },
                label = { Text("Vehicle No") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = vehicleType,
                onValueChange = { vehicleType = it },
                label = { Text("Vehicle Type") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
