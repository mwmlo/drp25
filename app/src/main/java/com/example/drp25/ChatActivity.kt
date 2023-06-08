package com.example.drp25

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import com.example.drp25.databinding.ActivityChatBinding
import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.FirebaseDatabase
import io.getstream.chat.android.client.api.models.querysort.QuerySortByField
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory

// our logged in user
const val UNI_ID = "imperialId"
const val USER_ID = "-NXPnWryIGR2S5aJmSGH"

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var channelGlob: Channel

    // Get match for demo
    private val names = listOf("Pierre", "Kevin", "Martha", "India", "Jerry", "Simon")
    private var i = 0

    private fun getMatch(): String {
        val match = names[i]
        i = (i + 1) % (names.size)
        return match
    }

    val matchesRef = FirebaseDatabase.getInstance().reference.child("matches")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val clientService = ChatClientService(applicationContext)

        // Step 0 - inflate binding
        binding = ActivityChatBinding.inflate(layoutInflater)

        // Step 3 - Authenticate and connect the user (MVP)
        val user = User(
            id = "demo-user",
            name = "Name",
            image = "https://bit.ly/2TIt8NR"
        )
        clientService.connectCurrentUser(user)

        val friend1 = User(
            id = "friend-1",
            name = "Friend 1",
            image = "https://bit.ly/2TIt8NR"
        )

        val friend2 = User(
            id = "friend-2",
            name = "Friend 2",
            image = "https://bit.ly/2TIt8NR"
        )

        // Only allows the binding to be visible if the page is not opened from a match activity
        if (intent.hasExtra("fromMatch") && intent.getBooleanExtra("fromMatch", true)) {
            Log.e("LOCATED","Have found a channel with the correct id")
            clientService.createChannel("1234", user.id, friend2.id)
            binding.channelListView.setChannelItemClickListener { channel ->
                if (channel.id == "1234") {
                    Log.e("IDENTIFIED","Have found a channel with the correct id")
                    channelGlob = channel
                }
                startActivity(ChannelActivity.newIntent(this, channel))
            }
            //startActivity(ChannelActivity.newIntent(this, channelGlob))

//            createAndRunChannel(client, user)
        } else {
            Log.e("NOTLOCATED","Have found a channel with the correct id")
            setContentView(binding.root)
            clientService.createChannel("14355", user.id, friend1.id)
            // Note this channel is not showing up
//            createDemoChannel(client, user, "14355")
            // seems to be something wrong with channel.execute.data, causing issues
            // startActivity(ChannelActivity.newIntent(this, ch))
        }

        val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()
        val channelListFactory = ChannelListViewModelFactory(
            filter = Filters.and(
                Filters.eq("type", "messaging"),
                Filters.`in`("members", listOf(user.id)),
            ),
            sort = QuerySortByField.descByName("lastUpdated"),
            limit = 30,
        )
        val channelListViewModel: ChannelListViewModel by viewModels { channelListFactory }

        channelListHeaderViewModel.bindView(binding.channelListHeaderView, this)
        channelListViewModel.bindView(binding.channelListView, this)

        /* When a channel is clicked, the user is taken to the channel. */
        binding.channelListView.setChannelItemClickListener { channel ->
            startActivity(ChannelActivity.newIntent(this, channel))
        }

        /* Functionality of "Meet Someone New" button -> takes user to match page. */
        binding.homeMatchButton.setOnClickListener {
            val intent = Intent(this, MatchActivity::class.java)
            startActivity(intent)
        }

        // indicates this is the person logged in (currently Kevin)
        listenToUser(UNI_ID, USER_ID)

    }
}
