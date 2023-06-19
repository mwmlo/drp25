package com.example.drp25

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text

class UserEventsActivity : AppCompatActivity() {

    private val interests: MutableList<String> = ArrayList()

    private fun getInterestedFriends(society: String, view: View) {

    }

    private fun createEventCard(event: DataSnapshot, inflater: LayoutInflater, eventsList: LinearLayout, society: String) {
        val name: String = event.child("eventName").value as String
        val date = event.child("eventDate").value as String
        val desc = event.child("eventDesc").value as String

        val eventCard = inflater.inflate(
            R.layout.user_event_view, eventsList, false
        ) as CardView
        eventCard.setCardBackgroundColor(getColor(R.color.light_grey_0))
        /* Views in the card. */
        eventCard.findViewById<TextView>(R.id.share_prompt).text = name
        eventCard.findViewById<TextView>(R.id.new_event_descr).text = desc
        eventCard.findViewById<TextView>(R.id.new_event_date).text = date

        val shareBtn = eventCard.findViewById<Button>(R.id.share_button)
        shareBtn.setOnClickListener {
            // Inflate the pop-up overlay layout
            val view = inflater.inflate(R.layout.popup_share, null)

            // Fill in share prompt and friend details
            val invite = "Invite your friends to come along to $name!"
            view.findViewById< TextView>(R.id.share_prompt).text = invite

            val friends: MutableList<String> = ArrayList()
            // Get list of matched friends with the same interest
            val userRef = FirebaseDatabase.getInstance().reference.child("universities")
                .child(UNI_ID).child("users").child(USER_ID)
            userRef.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (match in snapshot.child("matched").children) {
                        val sharedInterests = match.child("sharedInterests").children
                        for (interest in sharedInterests) {
                            if (society == interest.value.toString()) {
                                friends.add(match.child("matchId").value.toString())
                            }
                        }

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
            // Find name of friends
            val usersRef = FirebaseDatabase.getInstance().reference.child("universities")
                .child(UNI_ID).child("users")
            val names: MutableList<String> = ArrayList()
            usersRef.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (user in snapshot.children) {
                        if (friends.contains(user.key)) {
                            names.add(user.child("name").value.toString())
                            val namesString = names.toString()
                                .replace(",", ", ")
                                .replace("[", "")
                                .replace("]", "")
                                .trim()
                            val friendPrompt = "$namesString might be interested."
                            view.findViewById<TextView>(R.id.friends_list).text = friendPrompt
                        }

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            // Create the pop-up window
            val popupWindow = PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true)

            // Set up dismiss listener to close the pop-up window when clicked outside
            popupWindow.isOutsideTouchable = true
            popupWindow.isFocusable = true

            // Show the pop-up window
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        }
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
                        createEventCard(event, inflater, eventsList, society)
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