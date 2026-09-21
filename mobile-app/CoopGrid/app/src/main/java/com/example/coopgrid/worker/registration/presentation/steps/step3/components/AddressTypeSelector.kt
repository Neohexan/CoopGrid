package com.example.coopgrid.worker.registration.presentation.steps.step3.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.worker.registration.presentation.steps.step3.model.AddressType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressTypeSelector(
    selectedType: AddressType,
    currentLanguage: AppLanguage,
    onTypeSelected: (AddressType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(AddressType.entries.toTypedArray()) { type ->
            val label = if (currentLanguage == AppLanguage.HINGLISH) type.labelHinglish else type.labelEnglish
            FilterChip(
                selected = (type == selectedType),
                onClick = { onTypeSelected(type) },
                label = { Text(text = label) }
            )
        }
    }
}