package com.poststroke.communicatorapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.poststroke.communicatorapp.R


class HelpWithSpeakingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_with_speaking)

        // Set the message "This section is in development"
        val messageTextView: TextView = findViewById(R.id.developmentMessage)
        messageTextView.text = "This section is in development"

        // Back button logic to return to the home screen
        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()  // Go back to the previous screen
        }
    }
}
