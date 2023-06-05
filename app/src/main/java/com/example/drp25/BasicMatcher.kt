package com.example.drp25

import com.google.firebase.database.DataSnapshot

class BasicMatcher : Matcher {
    override fun isMatch(user1Snapshot: DataSnapshot, user2Snapshot: DataSnapshot): Boolean {
        return true
    }
}