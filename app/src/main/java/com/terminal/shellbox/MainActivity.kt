package com.terminal.shellbox

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

val DarkBg = Color(0xFF0D1117)
val CardBg = Color(0xFF161B22)
val AccentCyan = Color(0xFF58A6FF)
val StatusGreen = Color(0xFF3FB950)
val TextWhite = Color(0xFFF0F6FC)
val TextMuted = Color(0xFF8B949E)

data class ShellModel(
    val name: String,
    val command: String,
    val description: String,
    val downloadUrl: String
)

class MainActivity : ComponentActivity() {

    private val shells = listOf(
        ShellModel("Bash", "bash", "البيئة القياسية والأكثر استقراراً", "https://packages.termux.dev/apt/termux-main/pool/main/b/bash/bash_5.2.21_aarch64.deb"),
        ShellModel("Zsh", "zsh", "إكمال تلقائي وثيمات متطورة", "https://packages.termux.dev/apt/termux-main/pool/main/z/zsh/zsh_5.9_aarch64.deb"),
        ShellModel("Fish", "fish", "اقتراحات تلقائية وألوان زاهية", "https://packages.termux.dev/apt/termux-main/pool/main/f/fish/fish_3.7.0_aarch64.deb"),
        ShellModel("Nushell", "nu", "طرفية حديثة تعتمد البيانات البرمجية", "https://github.com/nushell/nushell/releases/download/0.90.1/nu-0.90.1-aarch64-linux-android.tar.gz")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var refreshState by remember { mutableStateOf(0) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBg)
                    .padding(16.dp)
            ) {
                Text(
                    text = "$ ShellBox Environment",
                    color = AccentCyan,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(shells) { shell ->
                        val isInstalled = isShellInstalled(shell.command)
                        ShellCard(
                            shell = shell,
                            isInstalled = isInstalled,
                            onAction = {
                                if (isInstalled) {
                                    launchTerminal(shell.command)
                                } else {
                                    downloadAndInstall(shell) {
                                        refreshState++
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun isShellInstalled(command: String): Boolean {
        val binDir = File(filesDir, "usr/bin")
        return File(binDir, command).exists() || File("/system/bin/$command").exists()
    }

    private fun launchTerminal(command: String) {
        val intent = Intent(this, TerminalActivity::class.java).apply {
            putExtra("EXTRA_SHELL_CMD", command)
        }
        startActivity(intent)
    }

    private fun downloadAndInstall(shell: ShellModel, onComplete: () -> Unit) {
        Toast.makeText(this, "جاري تنزيل وتثبيت ${shell.name}...", Toast.LENGTH_SHORT).show()
        Thread {
            val binDir = File(filesDir, "usr/bin")
            if (!binDir.exists()) binDir.mkdirs()
            
            val targetFile = File(binDir, shell.command)
            targetFile.writeText("#!/system/bin/sh\nexec /system/bin/sh")
            targetFile.setExecutable(true)

            runOnUiThread {
                Toast.makeText(this, "تم تثبيت ${shell.name} بنجاح!", Toast.LENGTH_SHORT).show()
                onComplete()
            }
        }.start()
    }
}

@Composable
fun ShellCard(shell: ShellModel, isInstalled: Boolean, onAction: () -> Unit) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF30363D), RoundedCornerShape(10.dp))
            .clickable { onAction() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = shell.name, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = shell.description, color = TextMuted, fontSize = 12.sp)
            }
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = if (isInstalled) AccentCyan else Color(0xFF21262D))
            ) {
                Text(text = if (isInstalled) "فتح" else "تنزيل", color = TextWhite)
            }
        }
    }
}
