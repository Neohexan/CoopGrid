package com.example.coopgrid.worker.registration.presentation.steps.step22.components


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.worker.registration.presentation.steps.step22.model.MachineryRentalItem
import com.example.coopgrid.worker.registration.presentation.steps.step22.model.SampleMachineryCategories
import com.example.coopgrid.worker.registration.presentation.steps.step22.strings.HinglishMachineryStrings

@Preview(name = "Machinery Card - Expanded", showBackground = true)
@Composable
fun MachineryRentalCardPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            MachineryRentalCard(
                itemNumber = 1,
                item = MachineryRentalItem(
                    customMachineName = "Mahindra 575 Tractor",
                    rate = "800",
                    isExpanded = true
                ),
                categories = SampleMachineryCategories,
                strings = HinglishMachineryStrings,
                currentLanguage = AppLanguage.ENGLISH,
                showRemoveButton = true,
                onItemChange = {},
                onRemoveClick = {},
                onToggleExpand = {}
            )
        }
    }
}