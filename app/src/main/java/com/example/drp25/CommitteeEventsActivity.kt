package com.example.drp25

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.cardview.widget.CardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

        /* Display events from database */
        val eventsRef = FirebaseDatabase.getInstance().reference.child("universities")
            .child(UNI_ID).child("events")

        val inflater = LayoutInflater.from(this)
        eventsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (event in snapshot.children) {
                    val name: String = event.child("eventName").value as String
                    val date = event.child("eventDate").value as String
                    val desc = event.child("eventDesc").value as String

                    val eventCard = inflater.inflate(
                        R.layout.committee_event_view, eventsList, false
                    ) as CardView
                    eventCard.findViewById<TextView>(R.id.share_prompt).text = name
                    eventCard.findViewById<TextView>(R.id.new_event_descr).text = desc
                    eventCard.findViewById<TextView>(R.id.new_event_date).text = date
                    eventsList.addView(eventCard)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        /* Functionality for the New Event button. */
        val newEvent = findViewById<Button>(R.id.new_event_button)
        newEvent.setOnClickListener {
            val intent = Intent(this, NewEventActivity::class.java)
            startActivity(intent)
        }
    }
}