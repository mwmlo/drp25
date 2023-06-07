package com.example.drp25

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.drp25.databinding.ActivityProfileBinding

/** Retrieve user information using Stream SDK to personalise. */

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        /* inflate binding */
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }
}