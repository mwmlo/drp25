package com.example.drp25

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import okhttp3.internal.notify

private val unisRef = FirebaseDatabase.getInstance().reference.child("universities")
private val matcher: Matcher = BasicMatcher()
val gson = Gson()
var matches = listOf<String>()
val matchObservers = mutableListOf<Observer>()

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
    userRef.child("matches").addValueEventListener(object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val value = snapshot.getValue(String::class.java)
            matches = gson.fromJson(value, Array<String>::class.java).toList()
            matchObservers.forEach{observer -> observer.notify(matches)}
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    })
}

private fun addMatch(uniId: String, user1Id: String, user2Id: String) {
    val usersRef = unisRef.child(uniId).child("users")
    val matches1Ref = usersRef.child(user1Id).child("matches")
    val matches2Ref = usersRef.child(user2Id).child("matches")

    matches1Ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val value = snapshot.getValue(String::class.java)
            val matches = gson.fromJson(value, Array<String>::class.java).toMutableList()
            if (!matches.contains(user2Id)) {
                matches.add(user2Id)
                matches1Ref.setValue(gson.toJson(matches))
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    })

    matches2Ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val value = snapshot.getValue(String::class.java)
            val matches = gson.fromJson(value, Array<String>::class.java).toMutableList()
            if (!matches.contains(user1Id)) {
                matches.add(user1Id)
                matches2Ref.setValue(gson.toJson(matches))
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    })
}

private fun removeMatch(uniId: String, user1Id: String, user2Id: String) {
    val usersRef = unisRef.child(uniId).child("users")
    val matches1Ref = usersRef.child(user1Id).child("matches")
    val matches2Ref = usersRef.child(user2Id).child("matches")

    matches1Ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val value = snapshot.getValue(String::class.java)
            val matches = gson.fromJson(value, Array<String>::class.java).toMutableList()
            if (matches.remove(user2Id)) {
                matches1Ref.setValue(gson.toJson(matches))
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    })

    matches2Ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val value = snapshot.getValue(String::class.java)
            val matches = gson.fromJson(value, Array<String>::class.java).toMutableList()
            if (matches.remove(user1Id)) {
                matches2Ref.setValue(gson.toJson(matches))
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    })
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
                                    addMatch(uniId, userId, otherId)
                                } else {
                                    removeMatch(uniId, userId, otherId)
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
        userRef.child("matches").setValue(gson.toJson(listOf<String>()))
        userRef.child("year").setValue(year)
        userRef.child("course").setValue(course)
    }
    return userId
}

fun putInterestRating(uniId: String, userId: String, interest: String, rating: Int) {
    val interestsRef = unisRef.child(uniId).child("users").child(userId).child("interests")
    interestsRef.child(interest).setValue(rating)
}

fun removeInterest(uniId: String, userId: String, interest: String) {
    val interestsRef = unisRef.child(uniId).child("users").child(userId).child("interests")
    interestsRef.child(interest).removeValue()
}