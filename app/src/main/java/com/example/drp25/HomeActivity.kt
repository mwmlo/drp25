package com.example.drp25

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import com.example.drp25.databinding.ActivityHomeBinding
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : AppCompatActivity() {

    // Get match for demo
    private val NAMES = listOf<String>("Pierre", "Kevin", "Martha", "India", "Jerry", "Simon")
    private var i = 0
    private fun getMatch(): String {
        val match = NAMES.get(i)
        i = (i+1)%(NAMES.size)
        return match
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    val matchesRef = FirebaseDatabase.getInstance().getReference().child("matches")

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.matchButton.setOnClickListener {
            val key = matchesRef.push().key
            if (key != null) {
                matchesRef.child(key).setValue(getMatch())
            }
        }

    }

}