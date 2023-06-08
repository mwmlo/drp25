package com.example.drp25

import com.example.drp25.databinding.ActivityChatBinding
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory


object ChatClient
{
    var client: ChatClient
    init {
        // Step 1 - Set up the OfflinePlugin for offline storage
        val offlinePluginFactory = StreamOfflinePluginFactory(
            config = Config(
                backgroundSyncEnabled = true,
                userPresence = true,
                persistenceEnabled = true,
                uploadAttachmentsNetworkType = UploadAttachmentsNetworkType.NOT_ROAMING,
            ),
            appContext = ChatActivity.getContext(),

        )

        // Step 2 - Set up the client for API calls with the plugin for offline storage
        client = ChatClient.Builder("4tm42krd5mvf", ChatActivity.getContext())
            .withPlugin(offlinePluginFactory)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .build()
    }

}