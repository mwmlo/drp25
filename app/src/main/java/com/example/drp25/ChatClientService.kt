package com.example.drp25

import android.content.Context
import android.util.Log
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.Call
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.ConnectionData
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory

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

    fun connectCurrentUser(user: User): Call<ConnectionData> {
        val token = Backend.generateUserToken(user.id)
        Log.e("connect", token)
        return client.connectUser(
            user = user,
            token = token
        )
    }

    fun createChannel(channelId: String, firstUserId: String, secondUserId: String) {
        val channelClient = client.channel(channelType = "messaging", channelId = channelId)
        channelClient.watch().enqueue { result ->
            if (result.isSuccess) {
                channelClient.addMembers(listOf(firstUserId, secondUserId)).enqueue()
            } else {
                // Handle result.error()
            }
        }

//        val channelCall = client.createChannel(
//            channelType = "messaging",
//            channelId = channelId,
//            memberIds = listOf(firstUserId, secondUserId),
//            extraData = emptyMap()
//        )
//        channelCall.enqueue { result ->
//            if (result.isSuccess) {
//                Log.e("createChannel$channelId", "success")
//            } else {
//                Log.e("createChannel$channelId", "fail")
//                Log.e("createChannel$channelId", result.toString())
//            }
//        }
    }


}