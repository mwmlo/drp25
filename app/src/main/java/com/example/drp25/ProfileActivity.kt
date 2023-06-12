package com.example.drp25

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.ScrollView
import android.widget.TableRow
import android.widget.TextView
import com.example.drp25.databinding.ActivityProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/** Retrieve user information using Stream SDK to personalise. */

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // indicates this is the person logged in (currently Kevin)
        listenToUser(UNI_ID, USER_ID)

        /* inflate binding */
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set up views
        val uniRef = FirebaseDatabase.getInstance().reference.child("universities")
            .child(UNI_ID)
        val userRef = uniRef.child("users").child(USER_ID)

        userRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val name: String = snapshot.child("name").value as String
                val course = snapshot.child("course").value
                val year = snapshot.child("year").value

                binding.nameText.text = name

                uniRef.child("name").addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val uniName = snapshot.value as String
                        binding.personalInfoText.text = "Year $year | $course | $uniName"
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

                for (interest in snapshot.child("interests").children) {
                    val inflater = LayoutInflater.from(this@ProfileActivity)
                    val rowView = inflater.inflate(R.layout.profile_interest, binding.interestsTable, false) as TableRow
                    val interestNameView = rowView.findViewById<TextView>(R.id.interest_name)
                    val interestRatingBar = rowView.findViewById<RatingBar>(R.id.interest_rating)
                    interestNameView.text = interest.key
                    interestRatingBar.rating = interest.getValue(Float::class.java)!!
                    binding.interestsTable.addView(rowView)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        /* Functionality of "Back" button -> takes user back to homepage. */
        binding.profileBackButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        /* Functionality of "Meet Someone New" button -> takes user to match page. */
        binding.profileMatchButton.setOnClickListener {
            val intent = Intent(this, MatchActivity::class.java)
            startActivity(intent)
        }

        binding.updateInterestsButton.setOnClickListener {
            val intent = Intent(this, InterestActivity::class.java)
            startActivity(intent)
        }
    }
}