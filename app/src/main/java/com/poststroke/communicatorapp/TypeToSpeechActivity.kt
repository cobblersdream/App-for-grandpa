package com.poststroke.communicatorapp

import android.content.SharedPreferences
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import java.util.Locale

class TypeToSpeechActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var textInputField: EditText
    private lateinit var enterButton: Button
    private lateinit var progressBar: ProgressBar  // Loading indicator
    private lateinit var prefs: SharedPreferences

    private var isTtsInitialized = false  // Flag for TTS initialization status

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_to_speech)

        // Initialize views
        textInputField = findViewById(R.id.textInputField)
        enterButton = findViewById(R.id.enterButton)
        progressBar = findViewById(R.id.ttsProgressBar)

        // Initially disable the enter button and show progress bar until TTS is ready
        enterButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        // Initialize TextToSpeech
        initializeTextToSpeech()

        // Show keyboard when input field gains focus
        textInputField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                showKeyboard(view)
            }
        }

        // Handle Enter button click
        enterButton.setOnClickListener {
            val text = textInputField.text.toString()
            if (isTtsInitialized && text.isNotEmpty()) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
            } else {
                Toast.makeText(this, "Please enter some text or wait for TTS to initialize", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle back button click
        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()  // Go back to the previous screen
        }
    }

    // Initialize TextToSpeech
    private fun initializeTextToSpeech() {
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            isTtsInitialized = true
            enterButton.isEnabled = true  // Enable the Enter button
        } else {
            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show()
        }

        // Hide progress bar after initialization
        progressBar.visibility = View.GONE
    }

    // Show the keyboard when input field is focused
    private fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onDestroy() {
        if (this::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
