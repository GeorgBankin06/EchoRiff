package com.echoriff.echoriff.radio.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordingService : Service() {
    private var recordingCall: Call? = null
    private var isRecording = false
    private lateinit var outputFile: File

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
        outputFile =
            File(
                applicationContext.getExternalFilesDir(null),
                "record_${System.currentTimeMillis()}.mp3"
            )

        val outputStream = FileOutputStream(outputFile)

        recordingCall = client.newCall(request)
        recordingCall?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Recording", "Failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val buffer = ByteArray(1024)
                val input = response.body?.byteStream()
                input?.let {
                    while (isRecording && it.read(buffer).also { bytesRead ->
                            if (bytesRead != -1) {
                                outputStream.write(buffer, 0, bytesRead)
                            }
                        } != -1
                    ) {
                    }
                    outputStream.flush()
                    outputStream.close()
                }
            }
        })
        isRecording = true
    }

    private fun stopRecording(nameRecord: String) {
        isRecording = false
        recordingCall?.cancel()

        // Save record to SharedPreferences
        val prefs = applicationContext.getSharedPreferences("recordings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val path = outputFile.absolutePath
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        val recordingJson = JSONObject().apply {
            put("name", nameRecord)
            put("path", path)
            put("date", date)
        }

        val allRecords =
            prefs.getStringSet("record_list", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        allRecords.add(recordingJson.toString())
        editor.putStringSet("record_list", allRecords)
        editor.apply()
    }

    override fun onDestroy() {
//        stopRecording("pedal2")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "com.echoriff.ACTION_START_RECORDING"
        const val ACTION_STOP = "com.echoriff.ACTION_STOP_RECORDING"
        const val EXTRA_STREAM_URL = "stream_url"
        const val EXTRA_RECORD_NAME = ""
    }
}