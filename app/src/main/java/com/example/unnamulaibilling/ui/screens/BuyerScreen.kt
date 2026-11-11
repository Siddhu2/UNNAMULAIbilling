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
import com.example.unnamulaibilling.data.model.Buyer
import com.example.unnamulaibilling.ui.viewmodel.InvoiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerScreen(
    viewModel: InvoiceViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val buyer by viewModel.buyer.collectAsStateWithLifecycle()
    
    var name by remember(buyer) { mutableStateOf(buyer.name) }
    var address by remember(buyer) { mutableStateOf(buyer.address) }
    var gstin by remember(buyer) { mutableStateOf(buyer.gstin ?: "") }
    var pinCode by remember(buyer) { mutableStateOf(buyer.pinCode ?: "") }
    var stateCode by remember(buyer) { mutableStateOf(buyer.stateCode ?: "") }
    var place by remember(buyer) { mutableStateOf(buyer.place ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buyer Details", fontWeight = FontWeight.Bold) },
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
                    viewModel.updateBuyer(
                        Buyer(
                            name = name,
                            address = address,
                            gstin = gstin.takeIf { it.isNotBlank() },
                            pinCode = pinCode.takeIf { it.isNotBlank() },
                            stateCode = stateCode.takeIf { it.isNotBlank() },
                            place = place.takeIf { it.isNotBlank() }
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
                value = name,
                onValueChange = { name = it },
                label = { Text("Buyer Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            OutlinedTextField(
                value = gstin,
                onValueChange = { gstin = it },
                label = { Text("GSTIN (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = pinCode,
                onValueChange = { pinCode = it },
                label = { Text("Pin Code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = stateCode,
                onValueChange = { stateCode = it },
                label = { Text("State Code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = place,
                onValueChange = { place = it },
                label = { Text("Place of Supply") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
