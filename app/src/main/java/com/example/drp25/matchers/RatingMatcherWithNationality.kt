package com.example.drp25.matchers

import com.example.drp25.Matcher
import com.google.firebase.database.DataSnapshot

class RatingMatcherWithNationality : Matcher {
    private val ratingMatcher = RatingMatcher()

    override fun isMatch(user1Snapshot: DataSnapshot, user2Snapshot: DataSnapshot): Boolean {
        val nationality1 = user1Snapshot.child("nationality").value
        val nationality2 = user2Snapshot.child("nationality").value
        return if (nationality1 == nationality2) {
            false
        } else {
            ratingMatcher.isMatch(user1Snapshot, user2Snapshot)
        }
    }

}