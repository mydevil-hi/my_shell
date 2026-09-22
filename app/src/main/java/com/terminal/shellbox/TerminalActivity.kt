package com.terminal.shellbox

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class TerminalActivity : AppCompatActivity() {

    private lateinit var terminalOutput: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF000000.toInt())
        }

        val extraKeysBar = createExtraKeysBar()
        mainLayout.addView(extraKeysBar)

        terminalOutput = TextView(this).apply {
            setTextColor(0xFF00FF00.toInt())
            textSize = 14f
            text = "Terminal Session Started...\nType 'exit' to return to main menu.\n\n$ "
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
        mainLayout.addView(terminalOutput)

        setContentView(mainLayout)

        val shellCmd = intent.getStringExtra("EXTRA_SHELL_CMD") ?: "sh"
        runShellProcess(shellCmd)
    }

    private fun createExtraKeysBar(): LinearLayout {
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(0xFF161B22.toInt())
            setPadding(8, 8, 8, 8)
        }

        val keys = listOf("ESC", "/", "-", "HOME", "↑", "END", "PGUP", "CTRL", "ALT", "↓", "→", "PGDN")
        for (key in keys) {
            val btn = TextView(this).apply {
                text = key
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(16, 8, 16, 8)
                textSize = 12f
                isClickable = true
                setOnClickListener {
                    terminalOutput.append(key)
                }
            }
            bar.addView(btn)
        }
        return bar
    }

    private fun runShellProcess(command: String) {
        Thread {
            try {
                val binPath = File(filesDir, "usr/bin/$command").absolutePath
                val processBuilder = if (File(binPath).exists()) {
                    ProcessBuilder(binPath)
                } else {
                    ProcessBuilder("/system/bin/sh")
                }
                
                val process = processBuilder.start()
                process.waitFor()

                runOnUiThread {
                    finish()
                }
            } catch (e: Exception) {
                runOnUiThread { finish() }
            }
        }.start()
    }
}
