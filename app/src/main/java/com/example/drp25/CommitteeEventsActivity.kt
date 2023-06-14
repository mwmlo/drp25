package com.example.drp25

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.allViews

class CommitteeEventsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_committee_events)

        /* Functionality to reach profile page from events (home) page. */
        val toProfile = findViewById<Button>(R.id.to_profile_button)
        toProfile.setOnClickListener {
            val intent = Intent(this, CommitteeProfileActivity::class.java)
            startActivity(intent)
        }

        /* LinearLayout containing the content. */
        val eventsList = findViewById<LinearLayout>(R.id.committee_events)

        /* Generate some static events. */
        for (i in 1..2) {
            val inflater = LayoutInflater.from(this)
            val eventCard = inflater.inflate(
                R.layout.committee_event_view, eventsList, false
            ) as CardView
            eventCard.findViewById<TextView>(R.id.new_event_title).text =
                resources.getString(R.string.event_name)
            eventCard.findViewById<TextView>(R.id.new_event_descr).text =
                resources.getString(R.string.event_descr)
            eventCard.findViewById<TextView>(R.id.new_event_date).text =
                (14 + i * 5).toString() + "/06/23"
            eventsList.addView(eventCard)
        }

        /* Functionality for the New Event button. */
        val newEvent = findViewById<Button>(R.id.new_event_button)
        newEvent.setOnClickListener {
            val inflater = LayoutInflater.from(this@CommitteeEventsActivity)
            val eventCard = inflater.inflate(
                R.layout.committee_event_view, eventsList, false
            ) as CardView
            /* Sets the event title and description using predefined string
             * resources. Modify to retrieve user-inputted text, if necessary. */
            eventCard.findViewById<TextView>(R.id.new_event_title).text =
                resources.getString(R.string.new_event_name)
            eventCard.findViewById<TextView>(R.id.new_event_descr).text =
                resources.getString(R.string.new_event_descr)
            eventCard.findViewById<TextView>(R.id.new_event_date).text = "29/06/23"
            eventsList.addView(eventCard)
        }
    }
}