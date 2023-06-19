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

    enum class HobbyCategory {
        ACADEMIC_RELATED,
        ARTS_ENTERTAINMENT,
        CHARITABLE,
        INDOOR,
        OUTDOOR,
        SOCIAL,
        SPORTS
    }

    val hobbiesCategoriesMap = mapOf(
        "Anime" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Archery" to listOf(HobbyCategory.SPORTS, HobbyCategory.OUTDOOR),
        "Art" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Astronomy" to listOf(HobbyCategory.ACADEMIC_RELATED),
        "Badminton" to listOf(HobbyCategory.SPORTS, HobbyCategory.INDOOR),
        "Baking" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Ballet" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Baseball and Softball" to listOf(HobbyCategory.SPORTS, HobbyCategory.OUTDOOR),
        "Basketball" to listOf(HobbyCategory.SPORTS, HobbyCategory.OUTDOOR),
        "Bridge" to listOf(HobbyCategory.SOCIAL),
        "Caving" to listOf(HobbyCategory.OUTDOOR),
        "Charity and Volunteering" to listOf(HobbyCategory.CHARITABLE, HobbyCategory.SOCIAL),
        "Choir and Chamber Music" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Chess" to listOf(HobbyCategory.SOCIAL),
        "Cycling" to listOf(HobbyCategory.SPORTS, HobbyCategory.OUTDOOR),
        "Dance" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Dogs" to listOf(HobbyCategory.SOCIAL),
        "Drama" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Environmental" to listOf(HobbyCategory.CHARITABLE),
        "Football" to listOf(HobbyCategory.SPORTS, HobbyCategory.OUTDOOR),
        "Fencing" to listOf(HobbyCategory.SPORTS),
        "Films and Movies" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Gaming and E-sports" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Golf" to listOf(HobbyCategory.SPORTS, HobbyCategory.OUTDOOR),
        "Hip Hop" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "History" to listOf(HobbyCategory.ACADEMIC_RELATED),
        "Hockey" to listOf(HobbyCategory.SPORTS, HobbyCategory.OUTDOOR),
        "Jazz, Soul and Funk" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "K-pop" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Knitting" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Lacrosse" to listOf(HobbyCategory.SPORTS, HobbyCategory.OUTDOOR),
        "Martial Arts" to listOf(HobbyCategory.SPORTS),
        "Model United Nations" to listOf(HobbyCategory.ACADEMIC_RELATED),
        "Mountaineering" to listOf(HobbyCategory.OUTDOOR),
        "Musical Theatre" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Netball" to listOf(HobbyCategory.SPORTS, HobbyCategory.OUTDOOR),
        "Orchestra" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Photography" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Poker" to listOf(HobbyCategory.SOCIAL),
        "Pokemon" to listOf(HobbyCategory.SOCIAL),
        "Radio" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Rail and Transport" to listOf(HobbyCategory.SOCIAL),
        "Robotics" to listOf(HobbyCategory.ACADEMIC_RELATED),
        "Rugby" to listOf(HobbyCategory.SPORTS, HobbyCategory.OUTDOOR),
        "Science Fiction and Fantasy" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Skating" to listOf(HobbyCategory.SPORTS, HobbyCategory.OUTDOOR),
        "Snooker and Pool" to listOf(HobbyCategory.SPORTS),
        "Squash" to listOf(HobbyCategory.SPORTS),
        "Tabletop Gaming" to listOf(HobbyCategory.ARTS_ENTERTAINMENT),
        "Tennis" to listOf(HobbyCategory.SPORTS, HobbyCategory.OUTDOOR),
        "Volleyball" to listOf(HobbyCategory.SPORTS, HobbyCategory.OUTDOOR)
    )

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
        val spinner: Spinner = findViewById(R.id.spinner)
        var selectedInterest = ""
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hobbiesCategoriesMap.keys.toList())
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
                if (category == "None") {
                    val adapter = ArrayAdapter(this@InterestActivity, android.R.layout.simple_spinner_item, hobbiesCategoriesMap.keys.toList())
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                } else {
                    var categoryToFilter: HobbyCategory? = null
                    when (category) {
                        "Academic related" -> {
                            categoryToFilter = HobbyCategory.ACADEMIC_RELATED
                        }
                        "Arts & Entertainment" -> {
                            categoryToFilter = HobbyCategory.ARTS_ENTERTAINMENT
                        }
                        "Charitable" -> {
                            categoryToFilter = HobbyCategory.CHARITABLE
                        }
                        "Indoor" -> {
                            categoryToFilter = HobbyCategory.INDOOR
                        }
                        "Outdoor" -> {
                            categoryToFilter = HobbyCategory.OUTDOOR
                        }
                        "Social" -> {
                            categoryToFilter = HobbyCategory.SOCIAL
                        }
                        "Sports" -> {
                            categoryToFilter = HobbyCategory.SPORTS
                        }
                    }
                    val adapter = ArrayAdapter(this@InterestActivity, android.R.layout.simple_spinner_item, hobbiesCategoriesMap.filter { it.value.contains(categoryToFilter) }.keys.toList())
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
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
            val intent = Intent(this, UserProfileActivity::class.java)
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