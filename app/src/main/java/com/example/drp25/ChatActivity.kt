package com.example.drp25

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
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
val USER_ID = "-NXGEo30rzoWUgTYoYi_"

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

    private fun createDemoChannel(client: ChatClient, user: User) {
        // Create a fake demo channel for the user
        client.createChannel(
            channelType = "messaging",
            channelId = "124",
            memberIds = listOf(user.id, "friend-1"),
            extraData = emptyMap()
        ).enqueue { result ->
            if (result.isSuccess) {
                Log.e("createChannel", "success")
            } else {
                Log.e("createChannel", "fail")
                Log.e("createChannel", result.toString())
            }
        }
    }
    fun createDemoFriends(client: ChatClient, user: User) {
        // Create friends and channels
        Backend.createFriend("friend-1")
        createDemoChannel(client, user)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // MVP: Create a demo user who is logged in
        val user = User(
            id = "demo-user",
            name = "Your Name",
            image = "https://bit.ly/2TIt8NR"
        )

        // Step 0 - inflate binding
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        val client = ChatClient.Builder("4tm42krd5mvf", applicationContext)
            .withPlugin(offlinePluginFactory)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .build()

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
                    memberIds = listOf(user.id, "tim"),
                    extraData = emptyMap()
                ).enqueue { result ->
                    if (result.isSuccess) {
                        Log.e("createChannel", "success")
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

        /* Create views for the list of channels */
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

        /* When a channel is clicked, the user is taken to the channel. */
        binding.channelListView.setChannelItemClickListener { channel ->
            startActivity(ChannelActivity.newIntent(this, channel))
        }

        /* Functionality of "Meet Someone New" button -> takes user to match page. */
        binding.homeMatchButton.setOnClickListener { _ ->
            val intent = Intent(this, MatchActivity::class.java)
            startActivity(intent)
        }

        // indicates this is the person logged in (currently Kevin)
        listenToUser(UNI_ID, USER_ID)
    }
}