package com.example.drp25

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.ScrollView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MatchActivity : AppCompatActivity() {
    private lateinit var parentLayout: LinearLayout
    private lateinit var context: Context
    private var selectedMatchId: String? = null
    private var selectedMatchName: String? = null

    private val observer = object : Observer {
        override fun notify(matchIds: Set<String>) {
            parentLayout.removeAllViews()
            for (matchId in matchIds) {
                val scrollView = ScrollView(context)
                parentLayout.addView(scrollView)
                val linearLayout = LinearLayout(context)
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.setPadding(30, 30, 30, 30)
//                parentLayout.addView(linearLayout)
                scrollView.addView(linearLayout)
                val matchRef = FirebaseDatabase.getInstance().reference.child("universities")
                    .child(UNI_ID).child("users").child(matchId)
                matchRef.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val name: String = snapshot.child("name").value as String
                        val nationality = snapshot.child("nationality").value
                        val course = snapshot.child("course").value
                        val year = snapshot.child("year").value

                        addText(linearLayout, "Name: $name")
                        addText(linearLayout, "Nationality: $nationality")
                        addText(linearLayout, "Course: $course")
                        addText(linearLayout, "Year: $year")

                        addText(linearLayout, "Interests:")
                        for (interest in snapshot.child("interests").children) {
                            addText(linearLayout, interest.key.toString())
                        }

                        val button = getButton(linearLayout, "Match with $name")
                        button.setOnClickListener {
                            selectedMatchId = matchId
                            selectedMatchName = name
                            val intent = Intent(this@MatchActivity, StampActivity::class.java)
                            intent.putExtra("selectedMatchId", selectedMatchId)
                            intent.putExtra("selectedMatchName", selectedMatchName)
                            startActivity(intent)
                        }
                        linearLayout.addView(button)
                    }

                    override fun onCancelled(error: DatabaseError) {
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

    private fun getButton(linearLayout: LinearLayout, text: String): Button {
        val btn = Button(context)
        btn.text = text
        btn.setPadding(16, 16, 16, 16)
        btn.textSize = 20f
        return btn
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        context = this
        parentLayout = findViewById(R.id.match_matches)
        addMatchObserver(observer)
    }

}