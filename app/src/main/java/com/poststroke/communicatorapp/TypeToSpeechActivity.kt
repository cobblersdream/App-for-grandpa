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
    private lateinit var readButton: Button
    private lateinit var sizeSlider: SeekBar
    private lateinit var speechRateSlider: SeekBar
    private lateinit var progressBar: ProgressBar  // Loading indicator
    private lateinit var prefs: SharedPreferences

    private var textSize: Float = 18f  // Default text size
    private var speechRate: Float = 1.0f  // Default speech rate
    private var isTtsInitialized = false  // Flag for TTS initialization status

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_to_speech)

        // Initialize SharedPreferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Restore saved preferences
        textSize = prefs.getFloat("text_size_pref", textSize)
        speechRate = prefs.getFloat("speech_rate_pref", speechRate)

        // Initialize views
        textInputField = findViewById(R.id.textInputField)
        readButton = findViewById(R.id.readButton)
        sizeSlider = findViewById(R.id.sizeSlider)
        speechRateSlider = findViewById(R.id.speechRateSlider)
        progressBar = findViewById(R.id.ttsProgressBar)  // Progress bar for loading

        // Initially disable the read button and show progress bar until TTS is ready
        readButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        // Initialize TextToSpeech
        initializeTextToSpeech()

        // Set text size and speech rate sliders based on preferences
        textInputField.textSize = textSize
        sizeSlider.progress = textSize.toInt()
        speechRateSlider.progress = (speechRate * 10).toInt()

        // Show keyboard when input field gains focus
        textInputField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                showKeyboard(view)
            }
        }

        // Handle text size adjustments
        sizeSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textSize = progress.toFloat()
                textInputField.textSize = textSize
                saveTextSizeToPreferences(textSize)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Handle speech rate adjustments
        speechRateSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                speechRate = progress / 10f  // Convert to a float range between 0.1x to 2.0x
                saveSpeechRateToPreferences(speechRate)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Handle Read button click
        readButton.setOnClickListener {
            val text = textInputField.text.toString()
            if (isTtsInitialized && text.isNotEmpty()) {
                tts.setSpeechRate(speechRate)
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
            readButton.isEnabled = true  // Enable the read button
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

    // Save text size to preferences
    private fun saveTextSizeToPreferences(size: Float) {
        prefs.edit().putFloat("text_size_pref", size).apply()
    }

    // Save speech rate to preferences
    private fun saveSpeechRateToPreferences(rate: Float) {
        prefs.edit().putFloat("speech_rate_pref", rate).apply()
    }

    override fun onDestroy() {
        if (this::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
