package com.example.drp25

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.drp25.databinding.ActivityHomeBinding
//import com.google.firebase.database.FirebaseDatabase

// our logged in user
//val UNI_ID = "imperialId"
//val USER_ID = "-NXGEo30rzoWUgTYoYi_"

class HomeActivity : AppCompatActivity() {

    // Get match for demo
//    private val NAMES = listOf<String>("Pierre", "Kevin", "Martha", "India", "Jerry", "Simon")
//    private var i = 0
//    private fun getMatch(): String {
//        val match = NAMES.get(i)
//        i = (i+1)%(NAMES.size)
//        return match
//    }

    private lateinit var binding: ActivityHomeBinding

//    val matchesRef = FirebaseDatabase.getInstance().getReference().child("matches")

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)

        /* Defines function of "Meet Someone New" button. */
//        binding.matchButton.setOnClickListener {
//            val key = matchesRef.push().key
//            if (key != null) {
//                matchesRef.child(key).setValue(getMatch())
//            }
//        }

        // indicates this is the person logged in (currently Kevin)
//        listenToUser(UNI_ID, USER_ID)

        binding.homeMatchButton.setOnClickListener { _ ->
            val intent = Intent(this, MatchActivity::class.java)
            startActivity(intent)
        }

//        binding.chatButton.setOnClickListener { _ ->
//            val intent = Intent(this, ChatActivity.kt::class.java)
//            startActivity(intent)
//        }
    }



}