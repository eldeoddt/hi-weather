package com.example.hiweather_aos.post

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class PostItemData (
    var docId: String? = null,
    var email: String? = null,
    var weather: String? = null,
    var comments: String? = null,
    @ServerTimestamp
    var date_time: Timestamp? = null
)
