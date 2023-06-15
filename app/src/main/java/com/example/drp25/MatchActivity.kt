package com.example.drp25

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
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
        override fun notify(data: Set<String>) {
            val matchIds = data
            parentLayout.removeAllViews()
            for (matchId in matchIds) {
                val inflater = LayoutInflater.from(this@MatchActivity)
                /* Creates a match_entry_view for each match. */
                val cardView = inflater.inflate(
                    R.layout.match_entry_view, parentLayout, false
                ) as CardView
                parentLayout.addView(cardView)
                /* Each View within the match entry. */
                val pfpImage = cardView.findViewById<ImageView>(R.id.match_pfp_view)
                val nameText = cardView.findViewById<TextView>(R.id.name_view)
                val nationalityText = cardView.findViewById<TextView>(R.id.nationality_view)
                val infoText = cardView.findViewById<TextView>(R.id.info_view)
                val interestsTable = cardView.findViewById<LinearLayout>(R.id.interests_view)
                val matchWithBtn = cardView.findViewById<Button>(R.id.match_with_button)

                val matchRef = FirebaseDatabase.getInstance().reference.child("universities")
                    .child(UNI_ID).child("users").child(matchId)
                val usRef = FirebaseDatabase.getInstance().reference.child("universities")
                    .child(UNI_ID).child("users").child(USER_ID)
                matchRef.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val name: String = snapshot.child("name").value as String
                        val nationality = snapshot.child("nationality").value
                        val course = snapshot.child("course").value
                        val year = snapshot.child("year").value

                        val pfpUri = snapshot.child("pfp").value
                        if (pfpUri != null) {
                            Glide.with(this@MatchActivity)
                                .load(pfpUri as String)
                                .into(pfpImage)
                        } else {
                            pfpImage.setImageResource(R.drawable.default_profile)
                        }

                        nameText.text = name
                        nationalityText.text = nationality.toString()
                        infoText.text = getString(R.string.entry_info, year, course)

                        usRef.child("interests").addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(usSnapshot: DataSnapshot) {
                                val sharedInterests = mutableListOf<String>()

                                /* Generates TextViews for each interest. */
                                for (interest in snapshot.child("interests").children) {
                                    if (interest.key?.let { usSnapshot.hasChild(it) } == true) {
                                        // This interest is shared
                                        sharedInterests.add(interest.key!!)
                                        val interestNameView = TextView(context)
                                        interestNameView.text = interest.key
                                        interestNameView.textAlignment = TEXT_ALIGNMENT_CENTER
                                        interestNameView.textSize = 20f
                                        interestsTable.addView(interestNameView)
                                    }
                                }

                                matchWithBtn.text = getString(R.string.entry_match_with, name)
                                matchWithBtn.setOnClickListener {
                                    selectedMatchId = matchId
                                    selectedMatchName = name
                                    addMatched(UNI_ID, USER_ID, selectedMatchId!!, sharedInterests)

                                    val intent = Intent(this@MatchActivity, StampActivity::class.java)
                                    intent.putExtra("selectedMatchId", selectedMatchId)
                                    intent.putExtra("selectedMatchName", selectedMatchName)
                                    startActivity(intent)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        context = this
        parentLayout = findViewById(R.id.match_matches)
        addMatchObserver(observer)
    }

}