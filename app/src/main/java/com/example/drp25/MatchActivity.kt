package com.example.drp25

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MatchActivity : AppCompatActivity() {
    private lateinit var parentLayout: LinearLayout
    private lateinit var context: Context
    private val observer = object : Observer {
        override fun notify(matchIds: Set<String>) {
            parentLayout.removeAllViews()
            for (matchId in matchIds) {
                val linearLayout = LinearLayout(context)
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.setPadding(30, 30, 30, 30)
                parentLayout.addView(linearLayout)
                val matchRef = FirebaseDatabase.getInstance().reference.child("universities")
                    .child(UNI_ID).child("users").child(matchId)
                matchRef.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.child("name").getValue(String::class.java)
                            ?.let { addText(linearLayout, "Name: $it") }
                        snapshot.child("nationality").getValue(String::class.java)
                            ?.let { addText(linearLayout, "Nationality: $it") }
                        snapshot.child("course").getValue(String::class.java)
                            ?.let { addText(linearLayout, "Course: $it") }
                        snapshot.child("year").getValue(String::class.java)
                            ?.let { addText(linearLayout, "Year: $it") }
                        addText(linearLayout, "Interests:")
                        for (interest in snapshot.child("interests").children) {
                            addText(linearLayout, interest.key + " (" + interest.value + " stars)")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }

    private fun addText(linearLayout: LinearLayout, text: String) {
        val entry = TextView(context)
        entry.text = text
        entry.setPadding(16, 16, 16, 16)
        entry.textSize = 20f
        linearLayout.addView(entry)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        context = this
        parentLayout = findViewById(R.id.match_matches)
        addMatchObserver(observer)

        /* Functionality for SEND button -> takes user to chat page. */
        val sendBtn = findViewById<Button>(R.id.match_send_button)
        sendBtn.setOnClickListener { view ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("fromMatch", true)
            startActivity(intent)
        }
    }

}