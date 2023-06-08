package com.example.drp25

import android.util.Log
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.java.models.User
import io.getstream.chat.java.services.framework.DefaultClient
import java.util.*


class Backend {

    companion object {
        fun serverInit() {
            val properties = Properties()
            properties[DefaultClient.API_KEY_PROP_NAME] = "4tm42krd5mvf"
            properties[DefaultClient.API_SECRET_PROP_NAME] = "fxkykpy58zm6tnr5a4d2pgzmmxd297xq4688gs9eddb8qju2a7xzucynntg49qk5"
            val client = DefaultClient(properties)
            DefaultClient.setInstance(client)
        }

        fun generateUserToken(name: String): String {
            return User.createToken(name, null, Date())
        }

        fun createFriend(userId: String, name: String) {
            val user1 = User.UserRequestObject.builder().id(userId).name(name).role("user").build();
            Thread {
                // Do network action in this function
                val response = User.upsert().user(user1).request()
                Log.e("createFriend", response.toString())
            }.start()
        }
    }

}