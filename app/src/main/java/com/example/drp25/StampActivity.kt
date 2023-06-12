package com.example.drp25

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class StampActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stamp)

        val selectedMatchName:String? = intent.getStringExtra("selectedMatchName")

        /* Functionality for SEND button -> takes user to chat page. */
        val sendBtn = findViewById<Button>(R.id.match_send_button)
        sendBtn.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("fromMatch", true)
            intent.putExtra("matchedName", selectedMatchName)
            startActivity(intent)
        }
    }
}