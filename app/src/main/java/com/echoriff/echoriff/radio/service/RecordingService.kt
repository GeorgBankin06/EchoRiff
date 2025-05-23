package com.echoriff.echoriff.radio.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.radio.domain.Recording
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordingService : Service() {
    private var recordingCall: Call? = null
    private var isRecording = false
    private lateinit var outputFile: File
    private var startTime: Long = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val streamUrl = intent.getStringExtra(EXTRA_STREAM_URL)
                if (streamUrl != null) startRecording(streamUrl)
            }

            ACTION_STOP -> {
                val name = intent.getStringExtra(EXTRA_RECORD_NAME) ?: "Unnamed"
                stopRecording(name)
            }
        }
        return START_NOT_STICKY
    }

    private fun startRecording(streamUrl: String) {
        val client = OkHttpClient()
        val request = Request.Builder().url(streamUrl).build()

        val privateDir = File(applicationContext.filesDir, "recordings")
        if (!privateDir.exists()) privateDir.mkdirs()

        outputFile = File(privateDir, "record_${System.currentTimeMillis()}.enc") // .enc за скриване

        val masterKey = MasterKey.Builder(applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val encryptedFile = EncryptedFile.Builder(
            applicationContext,
            outputFile,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        isRecording = true

        val outputStream = encryptedFile.openFileOutput()

        recordingCall = client.newCall(request)
        recordingCall?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Recording", "Failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val buffer = ByteArray(4096)
                    val input = response.body?.byteStream()
                    input?.let {
                        startTime = System.currentTimeMillis()
                        while (isRecording && it.read(buffer).also { bytesRead ->
                                if (bytesRead != -1) {
                                    outputStream.write(buffer, 0, bytesRead)
                                }
                            } != -1
                        ) { }
                    }
                } catch (e: Exception) {
                    Log.e("Recording", "Write error: ${e.message}")
                } finally {
                    try {
                        outputStream.flush()
                        outputStream.close()
                    } catch (e: Exception) {
                        Log.e("Recording", "Failed to close stream: ${e.message}")
                    }
                }
            }
        })
    }

    private fun stopRecording(nameRecord: String) {
        isRecording = false
        recordingCall?.cancel()

        val elapsedTime = System.currentTimeMillis() - startTime
        val duration = formatTime(elapsedTime)

        // Save record to SharedPreferences
        val path = outputFile.absolutePath
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val record =
            Recording(fileName = nameRecord, filePath = path, date = date, duration = duration)

        UserPreferences(applicationContext).saveRecord(record)
    }

    private fun formatTime(ms: Long): String {
        val seconds = (ms / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "com.echoriff.ACTION_START_RECORDING"
        const val ACTION_STOP = "com.echoriff.ACTION_STOP_RECORDING"
        const val EXTRA_STREAM_URL = "stream_url"
        const val EXTRA_RECORD_NAME = ""
    }
}