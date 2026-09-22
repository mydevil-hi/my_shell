package com.terminal.shellbox

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

data class ShellModel(
    val name: String,
    val command: String,
    val description: String
)

class MainActivity : AppCompatActivity() {

    private val shells = listOf(
        ShellModel("Bash", "bash", "البيئة القياسية والأكثر استقراراً"),
        ShellModel("Zsh", "zsh", "إكمال تلقائي وثيمات متطورة"),
        ShellModel("Fish", "fish", "اقتراحات تلقائية وألوان زاهية"),
        ShellModel("Nushell", "nu", "طرفية حديثة تعتمد البيانات البرمجية")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0D1117"))
            setPadding(32, 32, 32, 32)
        }

        val titleText = TextView(this).apply {
            text = "$ ShellBox Environment"
            setTextColor(Color.parseColor("#58A6FF"))
            textSize = 22f
            setPadding(0, 0, 0, 32)
        }
        rootLayout.addView(titleText)

        for (shell in shells) {
            val cardView = createShellCard(shell)
            rootLayout.addView(cardView)
        }

        setContentView(rootLayout)
    }

    private fun createShellCard(shell: ShellModel): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor("#161B22"))
            setPadding(24, 24, 24, 24)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 24)
            layoutParams = params
        }

        val infoLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val nameText = TextView(this).apply {
            text = shell.name
            setTextColor(Color.parseColor("#F0F6FC"))
            textSize = 18f
        }

        val descText = TextView(this).apply {
            text = shell.description
            setTextColor(Color.parseColor("#8B949E"))
            textSize = 12f
        }

        infoLayout.addView(nameText)
        infoLayout.addView(descText)

        val actionBtn = Button(this).apply {
            val isInstalled = isShellInstalled(shell.command)
            text = if (isInstalled) "فتح" else "تنزيل"
            setBackgroundColor(Color.parseColor(if (isInstalled) "#238636" else "#21262D"))
            setTextColor(Color.WHITE)

            setOnClickListener {
                if (isShellInstalled(shell.command)) {
                    val intent = Intent(this@MainActivity, TerminalActivity::class.java).apply {
                        putExtra("EXTRA_SHELL_CMD", shell.command)
                    }
                    startActivity(intent)
                } else {
                    installShellDummy(shell.command) {
                        text = "فتح"
                        setBackgroundColor(Color.parseColor("#238636"))
                    }
                }
            }
        }

        card.addView(infoLayout)
        card.addView(actionBtn)
        return card
    }

    private fun isShellInstalled(command: String): Boolean {
        val binDir = File(filesDir, "usr/bin")
        return File(binDir, command).exists() || File("/system/bin/$command").exists()
    }

    private fun installShellDummy(command: String, onSuccess: () -> Unit) {
        Toast.makeText(this, "جاري التثبيت...", Toast.LENGTH_SHORT).show()
        Thread {
            val binDir = File(filesDir, "usr/bin")
            if (!binDir.exists()) binDir.mkdirs()
            val file = File(binDir, command)
            file.writeText("#!/system/bin/sh\nexec /system/bin/sh")
            file.setExecutable(true)

            runOnUiThread {
                Toast.makeText(this, "تم التثبيت بنجاح!", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
        }.start()
    }
}
