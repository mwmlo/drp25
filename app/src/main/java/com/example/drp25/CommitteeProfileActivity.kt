package com.example.drp25

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class CommitteeProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_committee_profile)

        val backButton = findViewById<Button>(R.id.committee_profile_back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, CommitteeEventsActivity::class.java)
            startActivity(intent)
        }
    }
}