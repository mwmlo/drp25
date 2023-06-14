package com.example.drp25

import android.widget.LinearLayout
import android.widget.RatingBar
import com.example.drp25.matchers.RatingMatcherWithNationality
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

private val unisRef = FirebaseDatabase.getInstance().reference.child("universities")
private val matcher: Matcher = RatingMatcherWithNationality()
val matches = mutableSetOf<String>()
val matchObservers = mutableListOf<Observer>()

fun sendStamp(uniId: String, userId: String, imageId: Int) {
    val stampsRef = unisRef.child(uniId).child("users").child(userId).child("stamps")
    stampsRef.push().setValue(imageId)
}

fun updatePfp(uniId: String, userId: String, filepath: String) {
    unisRef.child(uniId).child("users").child(userId).child("pfp").setValue(filepath)
}

fun addMatchObserver(observer: Observer) {
    matchObservers.add(observer)
    observer.notify(matches)
}

fun listenToUser(uniId: String, userId: String) {
    val userRef = unisRef.child(uniId).child("users").child(userId)
    userRef.addValueEventListener(object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            updateMatches(uniId, userId, snapshot)
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    })
}

private fun addMatch(matchId: String) {
    if (matches.add(matchId)) {
        matchObservers.forEach{observer -> observer.notify(matches)}
    }
}

private fun removeMatch(matchId: String) {
    if (matches.remove(matchId)) {
        matchObservers.forEach{observer -> observer.notify(matches)}
    }
}

fun updateMatches(uniId: String, userId: String, snapshot: DataSnapshot) {
    val usersRef = unisRef.child(uniId).child("users")
    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                val otherId = child.key
                if (otherId != userId) {
                    otherId?.let { usersRef.child(it) }
                        ?.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(otherSnapshot: DataSnapshot) {
                                if (matcher.isMatch(snapshot, otherSnapshot)) {
                                    addMatch(otherId)
                                } else {
                                    removeMatch(otherId)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            //TODO
        }
    })
}

// returns generated userId (or null if unsuccessful)
fun addUser(uniId: String, name: String, nationality: String, year: String, course: String): String? {
    val usersRef = unisRef.child(uniId).child("users")
    val userId = usersRef.push().key
    if (userId != null) {
        val userRef = usersRef.child(userId)
        userRef.child("name").setValue(name)
        userRef.child("nationality").setValue(nationality)
        userRef.child("year").setValue(year)
        userRef.child("course").setValue(course)
    }
    return userId
}

fun putInterestRating(uniId: String, userId: String, interest: String, rating: Int) {
    val interestsRef = unisRef.child(uniId).child("users").child(userId).child("interests")
    interestsRef.child(interest).setValue(rating)
}

fun clearInterests(uniId: String, userId: String) {
    val interestsRef = unisRef.child(uniId).child("users").child(userId).child("interests")
    interestsRef.removeValue()
}

fun removeInterest(uniId: String, userId: String, interest: String) {
    val interestsRef = unisRef.child(uniId).child("users").child(userId).child("interests")
    interestsRef.child(interest).removeValue()
}