package com.upipulse.ui.screens.timeline

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.upipulse.domain.model.TransactionDirection

@Composable
fun ManualTransactionDialog(
    onDismiss: () -> Unit,
    onSave: (ManualTransactionInput) -> Unit
) {
    var merchant by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf(TransactionDirection.DEBIT) }
    val amountValue = amount.toDoubleOrNull()
    val saveEnabled = amountValue != null && amountValue > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add transaction") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = merchant,
                    onValueChange = { merchant = it },
                    label = { Text("Merchant") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Type", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DirectionChip(TransactionDirection.DEBIT, direction) { direction = it }
                    DirectionChip(TransactionDirection.CREDIT, direction) { direction = it }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        ManualTransactionInput(
                            merchant = merchant.ifBlank { "Manual" },
                            amount = amountValue ?: 0.0,
                            category = category,
                            direction = direction
                        )
                    )
                },
                enabled = saveEnabled
            ) { Text("Save") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun DirectionChip(
    value: TransactionDirection,
    selected: TransactionDirection,
    onSelected: (TransactionDirection) -> Unit
) {
    FilterChip(
        selected = value == selected,
        onClick = { onSelected(value) },
        label = { Text(if (value == TransactionDirection.DEBIT) "Debit" else "Credit") },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    )
}