package com.example.drp25

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class NewEventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_event)

        val eventName = findViewById<EditText>(R.id.event_name_fill)
        val eventDate = findViewById<EditText>(R.id.event_date_fill)
        val eventDesc = findViewById<EditText>(R.id.event_desc_fill)
        val submitEventBtn = findViewById<Button>(R.id.submit_event_button)

        submitEventBtn.setOnClickListener {
            addEvent(UNI_ID,
                eventName.text.toString(),
                eventDate.text.toString(),
                eventDesc.text.toString())
            val intent = Intent(this, CommitteeEventsActivity::class.java)
            startActivity(intent)
        }
    }
}