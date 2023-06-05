package com.example.drp25

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

fun getData(ref: DatabaseReference, callback: (List<String>) -> Unit) {
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val dataList = mutableListOf<String>()

            for (childSnapshot in snapshot.children) {
                dataList.add(childSnapshot.value as String)
            }

            // Pass the dataList to the callback function
            callback(dataList)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle the error
        }
    })
}