package com.example.drp25

import io.getstream.chat.java.models.User
import io.getstream.chat.java.services.framework.DefaultClient
import java.util.Properties


class Backend {

    fun generateUserToken(name: String) {
        var token = User.createToken(name, null, null);
    }

    companion object {
        fun serverInit() {
            val properties = Properties()
            properties.put(DefaultClient.API_KEY_PROP_NAME, "4tm42krd5mvf")
            properties.put(DefaultClient.API_SECRET_PROP_NAME,
                "fxkykpy58zm6tnr5a4d2pgzmmxd297xq4688gs9eddb8qju2a7xzucynntg49qk5")
            val client = DefaultClient(properties)
            DefaultClient.setInstance(client)
        }
        fun createFriend(userId: String) {
            val user1 = User.UserRequestObject.builder().id(userId).role("user").build();
            User.upsert().user(user1).request();
        }
    }

}