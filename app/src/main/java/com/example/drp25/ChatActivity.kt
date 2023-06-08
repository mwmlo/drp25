package com.example.drp25

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import com.example.drp25.databinding.ActivityChatBinding
import com.google.firebase.database.FirebaseDatabase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.querysort.QuerySortByField
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.offline.extensions.watchChannelAsState
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory

// our logged in user
val UNI_ID = "imperialId"
val USER_ID = "-NXPnWryIGR2S5aJmSGH"

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    // Get match for demo
    private val NAMES = listOf<String>("Pierre", "Kevin", "Martha", "India", "Jerry", "Simon")
    private var i = 0
    private fun getMatch(): String {
        val match = NAMES.get(i)
        i = (i+1)%(NAMES.size)
        return match
    }
    val matchesRef = FirebaseDatabase.getInstance().getReference().child("matches")

    private fun createDemoChannel(client: ChatClient, user: User, id: String): Channel {
        // Create a fake demo channel for the user
        var channelCall = client.createChannel(
            channelType = "messaging",
            channelId = id,
            memberIds = listOf(user.id), // modify this as appropriate
            extraData = emptyMap()
        )
        channelCall.enqueue { result ->
            if (result.isSuccess) {
                Log.e("createChannel"+id, "success")
            } else {
                Log.e("createChannel"+id, "fail")
                Log.e("createChannel"+id, result.toString())
            }
        }
        return channelCall.execute().data()
    }

    private fun createAndRunChannel(client: ChatClient, user: User) {
        // randomly generate id please
        TODO() //Make a new user and add them to channel list
        startActivity(ChannelActivity.newIntent(this, createDemoChannel(client, user, "155")))

    }
    fun createDemoFriends(client: ChatClient, user: User, id: String) {
        // Create friends and channels
        // supplying a new id creates a channel, these persist after reloading the site
        // createDemoChannel(client, user, id)
        Backend.createFriend("friend-1", client)
  //      Backend.createFriend("friend-21", client)
//        Backend.createFriend("friend-321", client)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // MVP: Create a demo user who is logged in
        val user = User(
            id = "demo-user",
            name = "Name",
            image = "https://bit.ly/2TIt8NR"
        )

        // Step 0 - inflate binding
        binding = ActivityChatBinding.inflate(layoutInflater)

        // Step 1 - Set up the OfflinePlugin for offline storage
        val offlinePluginFactory = StreamOfflinePluginFactory(
            config = Config(
                backgroundSyncEnabled = true,
                userPresence = true,
                persistenceEnabled = true,
                uploadAttachmentsNetworkType = UploadAttachmentsNetworkType.NOT_ROAMING,
            ),
            appContext = applicationContext,
        )


        // Step 2 - Set up the client for API calls with the plugin for offline storage
        var client = ChatClient.Builder("4tm42krd5mvf", applicationContext)
            .withPlugin(offlinePluginFactory)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .build()


//        var client = com.example.drp25.ChatClient.client
        // Step 3 - Authenticate and connect the user (MVP)
        client.connectUser(
            user = user,
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZGVtby11c2VyIn0.WX_Ovhfcj7wXRFdRd2uu9rqHK8shSNwI9jD6x-Tdl7A"
        ).enqueue { connectResult ->
            if (connectResult.isSuccess) {
                Log.e("connectUser", "success")
                // Create a fake demo channel for the user
                client.createChannel(
                    channelType = "messaging",
                    channelId = "123",
                    memberIds = listOf(user.id),
                    extraData = emptyMap()
                ).enqueue { result ->
                    if (result.isSuccess) {
                        Log.e("createChannel", "success")
                        createDemoFriends(client, user, "243")
                    } else {
                        Log.e("createChannel", "fail")
                        Log.e("createChannel", result.toString())
                    }
                }
            } else {
                Log.e("connectUser", "fail")
                Log.e("connectUser", connectResult.toString())
            }
        }

        /* Connects list header + list to the view.  */
        val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()
        val channelListFactory: ChannelListViewModelFactory = ChannelListViewModelFactory(
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

        /* When the user avatar is clicked, the user is taken to their profile page. */
        binding.channelListHeaderView.setOnUserAvatarClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        /* When a channel is clicked, the user is taken to the channel. */
        binding.channelListView.setChannelItemClickListener { channel ->
            startActivity(ChannelActivity.newIntent(this, channel))
        }

        /* Functionality of "Meet Someone New" button -> takes user to match page. */
        binding.homeMatchButton.setOnClickListener { _ ->
            val intent = Intent(this, MatchActivity::class.java)
            startActivity(intent)
        }

        // Only allows the binding to be visible if the page is not opened from a match activity
        if (intent.hasExtra("fromMatch") && intent.getBooleanExtra("fromMatch", true) == true) {

        } else {
            // indicates this is the person logged in (currently Kevin)
            listenToUser(UNI_ID, USER_ID)
            setContentView(binding.root)


        }

    }

    companion object {
        // Cannot access the application context of original chat activity
        private var instance: ChatActivity? = null
        fun getContext(): Context = instance!!.applicationContext
    }
}