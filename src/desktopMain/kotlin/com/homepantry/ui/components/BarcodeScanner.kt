package com.homepantry.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
actual fun BarcodeScannerView(
    modifier: Modifier,
    onBarcodeDetected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("Barcode scanning is not supported on Desktop")
    }
}
