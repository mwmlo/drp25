package com.example.drp25

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.drp25.ChatActivity.Companion.setCurrentUser

const val UNI_ID = "imperialId"
var USER_ID = "-NXPnWryIGR2S5aJmSGH"

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val maxButton = findViewById<Button>(R.id.enter_max_button)
        val kevinButton = findViewById<Button>(R.id.enter_kevin_button)

        maxButton.setOnClickListener {
            USER_ID = "-NXPnWs-phdiaSN_S87V"
            setCurrentUser("max")
            listenToUser(UNI_ID, USER_ID)
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        kevinButton.setOnClickListener {
            USER_ID = "-NXPnWryIGR2S5aJmSGH"
            setCurrentUser("kevin")
            listenToUser(UNI_ID, USER_ID)
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }
}