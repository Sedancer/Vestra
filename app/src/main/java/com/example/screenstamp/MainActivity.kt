package com.example.screenstamp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.screenstamp.theme.ScreenStampTheme
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    private lateinit var prefs: PrefsManager

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val file = File(filesDir, "overlay_image.png")
            contentResolver.openInputStream(it)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            prefs.imagePath = file.absolutePath
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PrefsManager(this)

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        setContent {
            ScreenStampTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ControlPanel(
                        prefs = prefs,
                        onSelectImage = { selectImageLauncher.launch("image/*") },
                        onShow = { sendCommand(OverlayService.ACTION_START) },
                        onHide = { sendCommand(OverlayService.ACTION_STOP) }
                    )
                }
            }
        }
    }

    private fun sendCommand(action: String) {
        val intent = Intent(this, OverlayService::class.java).apply {
            this.action = action
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlPanel(
    prefs: PrefsManager,
    onSelectImage: () -> Unit,
    onShow: () -> Unit,
    onHide: () -> Unit
) {
    var width by remember { mutableStateOf(prefs.width.toString()) }
    var height by remember { mutableStateOf(prefs.height.toString()) }
    var x by remember { mutableStateOf(prefs.x.toString()) }
    var y by remember { mutableStateOf(prefs.y.toString()) }
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onSelectImage, modifier = Modifier.fillMaxWidth()) {
            Text("Select Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = width,
            onValueChange = { width = it; it.toIntOrNull()?.let { v -> prefs.width = v } },
            label = { Text("Width (px)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = androidx.compose.ui.text.input.ImeAction.Done),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { focusManager.clearFocus() }),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = height,
            onValueChange = { height = it; it.toIntOrNull()?.let { v -> prefs.height = v } },
            label = { Text("Height (px)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = androidx.compose.ui.text.input.ImeAction.Done),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { focusManager.clearFocus() }),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = x,
            onValueChange = { x = it; it.toIntOrNull()?.let { v -> prefs.x = v } },
            label = { Text("X Coordinate (px)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = androidx.compose.ui.text.input.ImeAction.Done),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { focusManager.clearFocus() }),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = y,
            onValueChange = { y = it; it.toIntOrNull()?.let { v -> prefs.y = v } },
            label = { Text("Y Coordinate (px)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = androidx.compose.ui.text.input.ImeAction.Done),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { focusManager.clearFocus() }),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onShow) {
                Text("Show")
            }
            Button(onClick = onHide) {
                Text("Hide")
            }
        }
    }
}

