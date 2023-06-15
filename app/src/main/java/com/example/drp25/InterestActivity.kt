package com.example.drp25

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.internal.notify

class InterestActivity : AppCompatActivity() {

    val allInterests = arrayOf(
        "Anime",
        "Archery",
        "Art",
        "Astronomy",
        "Badminton",
        "Baking",
        "Baseball and Softball",
        "Basketball",
        "Bridge",
        "Caving",
        "Charity and Volunteering",
        "Choir and Chamber Music",
        "Chess",
        "Cycling",
        "Dance",
        "Dogs",
        "Drama",
        "Environmental",
        "Football",
        "Fencing",
        "Films and Movies",
        "Gaming and E-sports",
        "Golf",
        "Hip Hop",
        "History",
        "Hockey",
        "Jazz, Soul and Funk",
        "K-pop",
        "Knitting",
        "Lacrosse",
        "Martial Arts",
        "Model United Nations",
        "Mountaineering",
        "Musical Theatre",
        "Netball",
        "Orchestra",
        "Photography",
        "Poker",
        "Pokemon",
        "Radio",
        "Rail and Transport",
        "Robotics",
        "Rugby",
        "Science Fiction and Fantasy",
        "Skating",
        "Snooker and Pool",
        "Squash",
        "Tabletop Gaming",
        "Tennis",
        "Volleyball"
    )

    val sportInterests = arrayOf(
        "Archery",
        "Badminton",
        "Baseball and Softball",
        "Basketball",
        "Caving",
        "Chess",
        "Cycling",
        "Dance",
        "Football",
        "Fencing",
        "Gaming and E-sports",
        "Golf",
        "Hockey",
        "Lacrosse",
        "Martial Arts",
        "Mountaineering",
        "Netball",
        "Rugby",
        "Skating",
        "Snooker and Pool",
        "Squash",
        "Tennis",
        "Volleyball"
    )

    val displayedEntries = ArrayList<String>()

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Remove back press option, to prevent double updates to database
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest)

        // Display chips of already selected interests
        val interestsChipGroup = findViewById<ChipGroup>(R.id.interests_group)
        displayExistingInterests(interestsChipGroup)

        // Select interest from drop down list
        allInterests.forEach { displayedEntries.add(it) }

        val spinner: Spinner = findViewById(R.id.spinner)
        var selectedInterest = ""
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, displayedEntries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedInterest = spinner.selectedItem as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection
            }
        }

        val filterSpinner: Spinner = findViewById(R.id.filter_spinner)
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val category = filterSpinner.selectedItem as String
                displayedEntries.clear()
                if (category == "None") {
                    allInterests.forEach { displayedEntries.add(it) }
                } else if (category == "Sport") {
                    sportInterests.forEach { displayedEntries.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection
            }
        }

        // Add new interest to list of selected interests
        val addInterestButton = findViewById<Button>(R.id.add_new_interest_button)
        addInterestButton.setOnClickListener {
            if (selectedInterest.isNotEmpty()) {
                addChipIfNotExist(selectedInterest, interestsChipGroup)
                addInterest(UNI_ID, USER_ID, selectedInterest)
                selectedInterest = ""
            }
        }

        // Back button returns to profile
        val backButton = findViewById<Button>(R.id.back_button_interests)
        backButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayExistingInterests(pChipGroup: ChipGroup) {
        val matchRef = FirebaseDatabase.getInstance().reference.child("universities")
            .child(UNI_ID).child("users").child(USER_ID)
        matchRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (interest in snapshot.child("interests").children) {
                    val interestName = interest.key
                    if (interestName != null) {
                        addChipIfNotExist(interestName, pChipGroup)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun addChipIfNotExist(pItem: String, pChipGroup: ChipGroup) {
        var chipAlreadyExists = false

        for (i in 0 until pChipGroup.childCount) {
            val chip = pChipGroup.getChildAt(i)
            if (chip is Chip && chip.text.toString() == pItem) {
                chipAlreadyExists = true
                break
            }
        }

        if (!chipAlreadyExists) {
            addChip(pItem, pChipGroup)
        }
    }

    private fun addChip(pItem: String, pChipGroup: ChipGroup) {
        val lChip = Chip(this)
        lChip.text = pItem
        lChip.setChipIconResource(R.drawable.ic_close)
        // Remove chip from group if it is clicked
        lChip.setOnClickListener{
            val anim = AlphaAnimation(1f,0f)
            anim.duration = 250
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    pChipGroup.removeView(it)
                }
                override fun onAnimationStart(animation: Animation?) {}
            })
            it.startAnimation(anim)
            removeInterest(UNI_ID, USER_ID, pItem)
        }
        pChipGroup.addView(lChip, pChipGroup.childCount - 1)
    }

}