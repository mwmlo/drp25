package com.example.drp25

import com.google.firebase.database.DataSnapshot

interface Matcher {
    fun isMatch(user1Snapshot: DataSnapshot, user2Snapshot: DataSnapshot): Boolean
}