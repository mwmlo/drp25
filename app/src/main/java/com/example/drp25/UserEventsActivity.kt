package com.example.drp25

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserEventsActivity : AppCompatActivity() {

    private val interests: MutableList<String> = ArrayList()

    private fun createEventCard(event: DataSnapshot, inflater: LayoutInflater, eventsList: LinearLayout) {
        val name: String = event.child("eventName").value as String
        val date = event.child("eventDate").value as String
        val desc = event.child("eventDesc").value as String

        val eventCard = inflater.inflate(
            R.layout.committee_event_view, eventsList, false
        ) as CardView
        eventCard.findViewById<TextView>(R.id.new_event_title).text = name
        eventCard.findViewById<TextView>(R.id.new_event_descr).text = desc
        eventCard.findViewById<TextView>(R.id.new_event_date).text = date
        eventsList.addView(eventCard)
    }

    private fun getInterests() {
        val userRef = FirebaseDatabase.getInstance().reference.child("universities")
            .child(UNI_ID).child("users").child(USER_ID)
        userRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (interest in snapshot.child("interests").children) {
                    val interestName = interest.key.toString()
                    interests.add(interestName)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_events)

        /* LinearLayout containing the content. */
        val eventsList = findViewById<LinearLayout>(R.id.committee_events)

        /* Display events from database */
        getInterests()
        val eventsRef = FirebaseDatabase.getInstance().reference.child("universities")
            .child(UNI_ID).child("events")

        val inflater = LayoutInflater.from(this)
        eventsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (event in snapshot.children) {
                    val society = event.child("society").value as String
                    if (interests.contains(society)) {
                        createEventCard(event, inflater, eventsList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        /* Return to home page */
        val backButton = findViewById<Button>(R.id.profile_back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }
}