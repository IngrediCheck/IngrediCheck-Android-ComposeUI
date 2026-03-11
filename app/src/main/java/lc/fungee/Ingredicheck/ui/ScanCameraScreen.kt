package lc.fungee.Ingredicheck.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import lc.fungee.Ingredicheck.R

/**
 * Placeholder scanner screen that wires up:
 * - Camera permission flow (like iOS TabBar.swift handleScannerTap)
 * - A dedicated screen that will host CameraX + barcode scanning
 * - A back navigation callback to return to Home.
 *
 * The actual camera preview and SSE/polling integration will be layered
 * on top of this shell.
 */
@Composable
fun ScanCameraScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var showPermissionRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            showPermissionRationale = true
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top bar with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Back",
                    color = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable(onClick = onClose)
                )
                Text(
                    text = "Scan product",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                // Spacer icon to balance layout
                Box(modifier = Modifier.size(48.dp))
            }

            // Middle area: camera preview placeholder for now
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .background(Color(0xFF101010)),
                contentAlignment = Alignment.Center
            ) {
                if (hasCameraPermission) {
                    Text(
                        text = "Camera preview goes here\n(Barcode scanning to be wired with backend)",
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Camera permission required to scan products.",
                        color = Color.White
                    )
                }
            }

            // Bottom area: permission explanation + open settings if needed
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (showPermissionRationale && !hasCameraPermission) {
                    Text(
                        text = "To scan barcodes, allow camera access in Settings.",
                        color = Color.White
                    )
                    Button(onClick = {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                        context.startActivity(intent)
                    }) {
                        Text(text = "Open Settings")
                    }
                }

                Button(onClick = onClose) {
                    Text(text = "Back to home")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScanCameraScreenPreview() {
    ScanCameraScreen(onClose = {})
}

