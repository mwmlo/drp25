package com.example.drp25

import android.content.Context
import android.util.Log
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.java.models.User
import java.util.*

class ChatClientService(applicationContext: Context) {

    // Step 1 - Set up the OfflinePlugin for offline storage
    private val offlinePluginFactory = StreamOfflinePluginFactory(
        config = Config(
            backgroundSyncEnabled = true,
            userPresence = true,
            persistenceEnabled = true,
            uploadAttachmentsNetworkType = UploadAttachmentsNetworkType.NOT_ROAMING,
        ),
        appContext = applicationContext,

        )

    // Step 2 - Set up the client for API calls with the plugin for offline storage
    private val client = ChatClient.Builder("4tm42krd5mvf", applicationContext)
        .withPlugin(offlinePluginFactory)
        .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
        .build()

    fun connectCurrentUser(user: io.getstream.chat.android.client.models.User) {
        client.connectUser(
            user = user,
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZGVtby11c2VyIn0.WX_Ovhfcj7wXRFdRd2uu9rqHK8shSNwI9jD6x-Tdl7A"
        ).enqueue()
    }

    fun createChannel(channelId: String, firstUserId: String, secondUserId: String) {
        val channelCall = client.createChannel(
            channelType = "messaging",
            channelId = channelId,
            memberIds = listOf(firstUserId, secondUserId),
            extraData = emptyMap()
        )
        channelCall.enqueue { result ->
            if (result.isSuccess) {
                Log.e("createChannel$channelId", "success")
            } else {
                Log.e("createChannel$channelId", "fail")
                Log.e("createChannel$channelId", result.toString())
            }
        }
    }

    fun addMemberToChannel(channelId: String, userId: String) {
        val channelClient = client.channel(channelId)
        channelClient.addMembers(listOf(userId)).enqueue()
    }

    private fun generateUserToken(name: String): String {
        val expiry = GregorianCalendar()
        expiry.add(Calendar.MINUTE, 60)
        val issuedAt = GregorianCalendar()
        return User.createToken(name, expiry.time, issuedAt.time)
    }


}