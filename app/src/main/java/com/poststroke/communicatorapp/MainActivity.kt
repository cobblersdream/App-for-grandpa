package com.poststroke.communicatorapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.poststroke.communicatorapp.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Navigate to SurvivalWordsActivity
        findViewById<Button>(R.id.btnSurvivalWords).setOnClickListener {
            startActivity(Intent(this, SurvivalWordsActivity::class.java))
        }

        // Navigate to DictionaryActivity
        findViewById<Button>(R.id.btnDictionary).setOnClickListener {
            startActivity(Intent(this, DictionaryActivity::class.java))
        }

        // Navigate to TypeToSpeechActivity
        findViewById<Button>(R.id.btnTypeToSpeech).setOnClickListener {
            startActivity(Intent(this, TypeToSpeechActivity::class.java))
        }

        // Navigate to HelpWithSpeakingActivity
        findViewById<Button>(R.id.btnHelpWithSpeaking).setOnClickListener {
            startActivity(Intent(this, HelpWithSpeakingActivity::class.java))
        }
    }
}
