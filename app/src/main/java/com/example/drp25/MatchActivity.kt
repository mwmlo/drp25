package com.example.drp25

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.ScrollView
import android.widget.TableLayout
import android.widget.TableRow
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
                val inflater = LayoutInflater.from(this@MatchActivity)
                val scrollView = inflater.inflate(R.layout.match_entry_view, parentLayout, false) as ScrollView
                parentLayout.addView(scrollView)
                val pfpImage = scrollView.findViewById<ImageView>(R.id.match_pfp_view)
                val nameText = scrollView.findViewById<TextView>(R.id.name_view)
                val infoText = scrollView.findViewById<TextView>(R.id.info_view)
                val interestsTable = scrollView.findViewById<TableLayout>(R.id.interests_view)
                val matchWithBtn = scrollView.findViewById<Button>(R.id.match_with_button)

                val matchRef = FirebaseDatabase.getInstance().reference.child("universities")
                    .child(UNI_ID).child("users").child(matchId)
                matchRef.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val name: String = snapshot.child("name").value as String
                        val nationality = snapshot.child("nationality").value
                        val course = snapshot.child("course").value
                        val year = snapshot.child("year").value

                        val pfpPath = snapshot.child("pfp").value
                        if (pfpPath != null) {
                            val imageBitmap = BitmapFactory.decodeFile(pfpPath as String)
                            pfpImage.setImageBitmap(imageBitmap)
                        }

                        nameText.text = name
                        infoText.text = "Year $year | $course | $nationality"

                        for (interest in snapshot.child("interests").children) {
                            val rowView = inflater.inflate(R.layout.profile_interest, interestsTable, false) as TableRow
                            val interestNameView = rowView.findViewById<TextView>(R.id.interest_name)
                            interestNameView.text = interest.key
                            interestsTable.addView(rowView)
                        }

                        matchWithBtn.text = "Match with $name"
                        matchWithBtn.setOnClickListener {
                            selectedMatchId = matchId
                            selectedMatchName = name
                            addMatched(UNI_ID, USER_ID, selectedMatchId!!)

                            val intent = Intent(this@MatchActivity, StampActivity::class.java)
                            intent.putExtra("selectedMatchId", selectedMatchId)
                            intent.putExtra("selectedMatchName", selectedMatchName)
                            startActivity(intent)
                        }
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