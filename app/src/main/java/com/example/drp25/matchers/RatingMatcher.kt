package com.example.drp25.matchers

import com.example.drp25.Matcher
import com.google.firebase.database.DataSnapshot
import kotlin.math.abs

class RatingMatcher : Matcher {
    override fun isMatch(user1Snapshot: DataSnapshot, user2Snapshot: DataSnapshot): Boolean {
        val interests1Ref = user1Snapshot.child("interests")
        val interests2Ref = user2Snapshot.child("interests")

        // return true iff share at least one interest within +/- one star engagement range
        for (child in interests1Ref.children) {
            val interest: String = child.key!!
            if (interests2Ref.hasChild(interest)) {
                val rating1 = child.getValue(Int::class.java)!!
                val rating2 = interests2Ref.child(interest).getValue(Int::class.java)!!
                if (abs(rating1 - rating2) <= 1) {
                    return true
                }
            }
        }
        return false
    }

}