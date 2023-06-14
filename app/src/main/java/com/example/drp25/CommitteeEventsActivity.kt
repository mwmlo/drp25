package com.example.drp25

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class CommitteeEventsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_committee_events)

        val toProfile = findViewById<Button>(R.id.to_profile_button)
        toProfile.setOnClickListener {
            val intent = Intent(this, CommitteeProfileActivity::class.java)
            startActivity(intent)
        }

        val newEvent = findViewById<Button>(R.id.new_event_button)
        newEvent.setOnClickListener {
            // Create new card & populate
        }
    }
}