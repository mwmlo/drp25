package com.example.drp25

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.drp25.ChatClient.client
import com.example.drp25.databinding.ActivityChatBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.UserId
import kotlinx.coroutines.channels.Channel

class MatchActivity : AppCompatActivity() {
    private lateinit var linearLayout: LinearLayout
    private lateinit var context: Context
    private val observer = object : Observer {
        override fun notify(matchIds: List<String>) {
            linearLayout.removeAllViews()

            for (matchId in matchIds) {
                val nameRef = FirebaseDatabase.getInstance().reference.child("universities")
                    .child(UNI_ID).child("users").child(matchId).child("name")
                nameRef.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // update view
                        val entry = TextView(context)
                        val text = snapshot.getValue(String::class.java)
                        entry.text = text
                        entry.setPadding(128, 16, 128, 16)
                        entry.textSize = 48f
                        linearLayout.addView(entry)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        context = this
        linearLayout = findViewById(R.id.match_matches)
        addMatchObserver(observer)

        /* Functionality for SEND button -> takes user to chat page. */
        val sendBtn = findViewById<Button>(R.id.match_send_button)
        sendBtn.setOnClickListener { view ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("user", "Pierre")
            intent.putExtra("fromMatch", true)
            startActivity(intent)

            // Makes a new channel with a given person, say Pierre
            // Takes you to the chat of this person
            // aka ChannelActivity.newIntent(this, channel)

//            val binding = ActivityChatBinding.inflate(layoutInflater)
//            setContentView(binding.root)

         //   client = com.example.drp25.ChatClient.client

//            client = ChatClient.Builder("4tm42krd5mvf", applicationContext)
//                .withPlugin(offlinePluginFactory)
//                .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
//                .build()

//binding.channelListView.
            // get the createDemo.... to return a channel
            //
            /*/* When a channel is clicked, the user is taken to the channel. */
            binding.channelListView.setChannelItemClickListener { channel ->
                startActivity(ChannelActivity.newIntent(this, channel))
            }
           */
            // make an intentval intent = Intent(ChannelActivity::class.java)
            // startActivity()
        }
    }

}