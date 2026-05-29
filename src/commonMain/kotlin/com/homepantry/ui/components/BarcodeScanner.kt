package com.homepantry.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun BarcodeScannerView(
    modifier: Modifier,
    onBarcodeDetected: (String) -> Unit,
    onDismiss: () -> Unit
)
