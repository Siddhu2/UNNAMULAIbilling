package com.example.unnamulaibilling.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unnamulaibilling.data.model.InvoiceItem
import com.example.unnamulaibilling.ui.viewmodel.InvoiceViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    viewModel: InvoiceViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    
    var showItemDialog by remember { mutableStateOf(false) }
    var editingItemIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoice Items", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        editingItemIndex = null
                        showItemDialog = true
                    }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Item"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNext,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text("Next", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (items.isEmpty()) {
                item {
                    Text(
                        "No items added yet. Tap + to add items.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            itemsIndexed(items) { index, item ->
                ItemCard(
                    item = item,
                    onEdit = {
                        editingItemIndex = index
                        showItemDialog = true
                    },
                    onDelete = { viewModel.removeItem(index) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showItemDialog) {
        AddItemDialog(
            itemToEdit = editingItemIndex?.let { items.getOrNull(it) },
            viewModel = viewModel,
            onDismiss = {
                showItemDialog = false
                editingItemIndex = null
            },
            onSave = { newItem ->
                if (editingItemIndex != null) {
                    viewModel.updateItem(editingItemIndex!!, newItem.copy(sNo = editingItemIndex!! + 1))
                } else {
                    viewModel.addItem(newItem.copy(sNo = items.size + 1))
                }
                showItemDialog = false
                editingItemIndex = null
            }
        )
    }
}

@Composable
fun ItemCard(
    item: InvoiceItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.descriptionAndMaterial,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                val subDetails = buildString {
                    append("Qty: ${item.quantity}")
                    append(" | Rate: ₹${item.rate}")
                    if (item.hsnCode.isNotBlank()) {
                        append(" | HSN: ${item.hsnCode}")
                    }
                }
                Text(
                    subDetails,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Amount: ₹${String.format(Locale.US, "%.2f", item.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Item",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Item",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDialog(
    itemToEdit: InvoiceItem? = null,
    viewModel: InvoiceViewModel,
    onDismiss: () -> Unit,
    onSave: (InvoiceItem) -> Unit
) {
    var description by remember(itemToEdit) { mutableStateOf(itemToEdit?.descriptionAndMaterial ?: "") }
    
    val initialQuantityInput = remember(itemToEdit) {
        itemToEdit?.let { item ->
            if (item.quantityValue > 0) {
                if (item.quantityValue % 1.0 == 0.0) item.quantityValue.toLong().toString()
                else item.quantityValue.toString()
            } else {
                item.quantity.filter { it.isDigit() || it == '.' }
            }
        } ?: ""
    }
    var quantityInput by remember(itemToEdit) { mutableStateOf(initialQuantityInput) }

    val initialUnit = remember(itemToEdit) {
        itemToEdit?.let { item ->
            if (item.unit.isNotBlank()) item.unit
            else {
                val nonDigits = item.quantity.filter { !it.isDigit() && it != '.' }.trim()
                if (nonDigits.isNotBlank()) nonDigits else "Kgs"
            }
        } ?: "Kgs"
    }
    var unit by remember(itemToEdit) { mutableStateOf(initialUnit) }

    val initialRateInput = remember(itemToEdit) {
        itemToEdit?.let { item ->
            if (item.rate > 0) {
                if (item.rate % 1.0 == 0.0) item.rate.toLong().toString()
                else item.rate.toString()
            } else ""
        } ?: ""
    }
    var rateInput by remember(itemToEdit) { mutableStateOf(initialRateInput) }

    var hsnCode by remember(itemToEdit) { mutableStateOf(itemToEdit?.hsnCode ?: "") }

    // Real-time calculated amount
    val quantityVal = quantityInput.toDoubleOrNull() ?: 0.0
    val rateVal = rateInput.toDoubleOrNull() ?: 0.0
    val amountVal = quantityVal * rateVal
    val amountText = String.format(Locale.US, "%.2f", amountVal)

    // Auto-complete logic using savedItems
    val savedItems by viewModel.savedItems.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(false) }

    // Unit dropdown menu state
    var unitDropdownExpanded by remember { mutableStateOf(false) }
    val commonUnits = listOf("Kgs", "Nos", "Hrs", "Tons", "Unit", "Mtrs", "Bags", "Sq.Ft")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    if (itemToEdit != null) "Edit Item" else "Add Item",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // 1. Description & Material with Auto-complete
                Box {
                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                            expanded = true
                        },
                        label = { Text("Description & Material") },
                        placeholder = { Text("e.g. ALUMINIUM CABLE SCRAP - 7602") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    val filteredItems = savedItems.filter {
                        it.descriptionAndMaterial.contains(description, ignoreCase = true)
                    }

                    DropdownMenu(
                        expanded = expanded && description.isNotBlank() && filteredItems.isNotEmpty(),
                        onDismissRequest = { expanded = false }
                    ) {
                        filteredItems.take(5).forEach { savedItem ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        savedItem.descriptionAndMaterial,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                onClick = {
                                    description = savedItem.descriptionAndMaterial
                                    hsnCode = savedItem.hsnCode
                                    if (savedItem.unit.isNotBlank()) {
                                        unit = savedItem.unit
                                    }
                                    if (savedItem.rate > 0) {
                                        rateInput = if (savedItem.rate % 1.0 == 0.0) {
                                            savedItem.rate.toLong().toString()
                                        } else {
                                            savedItem.rate.toString()
                                        }
                                    }
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // 2. KGS / Unit (Quantity and Unit fields)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityInput,
                        onValueChange = { quantityInput = it },
                        label = { Text("Quantity") },
                        placeholder = { Text("620") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unit") },
                            placeholder = { Text("Kgs") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { unitDropdownExpanded = true }) {
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Select Unit"
                                    )
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = unitDropdownExpanded,
                            onDismissRequest = { unitDropdownExpanded = false }
                        ) {
                            commonUnits.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        unit = option
                                        unitDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 3. Rate (₹)
                OutlinedTextField(
                    value = rateInput,
                    onValueChange = { rateInput = it },
                    label = { Text("Rate (₹)") },
                    placeholder = { Text("60.0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 4. Amount (₹) - Read-only field
                OutlinedTextField(
                    value = "₹ $amountText",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // Dialog Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val combinedQuantity = "$quantityInput$unit"
                            onSave(
                                InvoiceItem(
                                    sNo = itemToEdit?.sNo ?: 0,
                                    descriptionAndMaterial = description,
                                    hsnCode = hsnCode,
                                    quantity = combinedQuantity,
                                    quantityValue = quantityVal,
                                    unit = unit,
                                    rate = rateVal,
                                    amount = amountVal
                                )
                            )
                        },
                        enabled = description.isNotBlank() && quantityInput.isNotBlank() && rateInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            if (itemToEdit != null) "Save" else "Add",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
