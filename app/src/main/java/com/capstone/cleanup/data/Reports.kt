package com.capstone.cleanup.data

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Reports(
    val description: String? = null,
    val imgUrl: String? = null,
    val reporter: String? = null,
    val title: String? = null,
    val timeStamp: Long? = null,
    val location: String? = null,
    var id: String? = null,
)