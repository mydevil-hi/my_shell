package com.terminal.shellbox

import android.graphics.Color
import android.os.Bundle
import android.widget.HorizontalScrollView
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
            setBackgroundColor(Color.BLACK)
        }

        val extraKeysBar = createExtraKeysBar()
        mainLayout.addView(extraKeysBar)

        terminalOutput = TextView(this).apply {
            setTextColor(Color.GREEN)
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

    private fun createExtraKeysBar(): HorizontalScrollView {
        val scrollView = HorizontalScrollView(this)
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor("#161B22"))
            setPadding(8, 8, 8, 8)
        }

        val keys = listOf("ESC", "/", "-", "HOME", "↑", "END", "PGUP", "CTRL", "ALT", "↓", "→", "PGDN")
        for (key in keys) {
            val btn = TextView(this).apply {
                text = key
                setTextColor(Color.WHITE)
                setPadding(24, 12, 24, 12)
                textSize = 12f
                isClickable = true
                setOnClickListener {
                    terminalOutput.append(key)
                }
            }
            bar.addView(btn)
        }
        scrollView.addView(bar)
        return scrollView
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
