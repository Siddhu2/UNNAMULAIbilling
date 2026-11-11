package com.example.unnamulaibilling.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unnamulaibilling.data.model.BankDetails
import com.example.unnamulaibilling.ui.viewmodel.InvoiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankDetailsScreen(
    viewModel: InvoiceViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val bankDetails by viewModel.bankDetails.collectAsStateWithLifecycle()
    val sgstRate by viewModel.sgstRate.collectAsStateWithLifecycle()
    val cgstRate by viewModel.cgstRate.collectAsStateWithLifecycle()
    val igstRate by viewModel.igstRate.collectAsStateWithLifecycle()
    
    var bankName by remember(bankDetails) { mutableStateOf(bankDetails.bankName) }
    var branch by remember(bankDetails) { mutableStateOf(bankDetails.branch) }
    var accountNumber by remember(bankDetails) { mutableStateOf(bankDetails.accountNumber) }
    var ifscCode by remember(bankDetails) { mutableStateOf(bankDetails.ifscCode) }
    
    var sgstInput by remember { mutableStateOf(if (sgstRate > 0) sgstRate.toString() else "") }
    var cgstInput by remember { mutableStateOf(if (cgstRate > 0) cgstRate.toString() else "") }
    var igstInput by remember { mutableStateOf(if (igstRate > 0) igstRate.toString() else "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bank & Taxes", fontWeight = FontWeight.Bold) },
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
                    viewModel.updateBankDetails(
                        BankDetails(
                            bankName = bankName,
                            branch = branch,
                            accountNumber = accountNumber,
                            ifscCode = ifscCode
                        )
                    )
                    viewModel.setTaxRates(
                        sgst = sgstInput.toDoubleOrNull() ?: 0.0,
                        cgst = cgstInput.toDoubleOrNull() ?: 0.0,
                        igst = igstInput.toDoubleOrNull() ?: 0.0
                    )
                    onNext()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text("Review Invoice", fontWeight = FontWeight.Bold)
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
            Text(
                "Bank Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = { Text("Bank Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = branch,
                onValueChange = { branch = it },
                label = { Text("Branch") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = accountNumber,
                onValueChange = { accountNumber = it },
                label = { Text("Account Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = ifscCode,
                onValueChange = { ifscCode = it },
                label = { Text("IFSC Code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Taxes (%)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = sgstInput,
                    onValueChange = { sgstInput = it },
                    label = { Text("SGST %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = cgstInput,
                    onValueChange = { cgstInput = it },
                    label = { Text("CGST %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = igstInput,
                    onValueChange = { igstInput = it },
                    label = { Text("IGST %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
