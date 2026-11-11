package com.example.unnamulaibilling.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unnamulaibilling.data.model.Seller
import com.example.unnamulaibilling.ui.viewmodel.InvoiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerScreen(
    viewModel: InvoiceViewModel,
    onNext: () -> Unit
) {
    val seller by viewModel.seller.collectAsStateWithLifecycle()
    
    var name by remember(seller) { mutableStateOf(seller.name) }
    var description by remember(seller) { mutableStateOf(seller.description ?: "") }
    var address by remember(seller) { mutableStateOf(seller.address) }
    var phone by remember(seller) { mutableStateOf(seller.phone ?: "") }
    var gstin by remember(seller) { mutableStateOf(seller.gstin ?: "") }
    var panNo by remember(seller) { mutableStateOf(seller.panNo ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seller Details", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.updateSeller(
                        Seller(
                            name = name,
                            description = description.takeIf { it.isNotBlank() },
                            address = address,
                            phone = phone.takeIf { it.isNotBlank() },
                            gstin = gstin.takeIf { it.isNotBlank() },
                            panNo = panNo.takeIf { it.isNotBlank() }
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
                label = { Text("Business Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = gstin,
                onValueChange = { gstin = it },
                label = { Text("GSTIN") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = panNo,
                onValueChange = { panNo = it },
                label = { Text("PAN No") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
