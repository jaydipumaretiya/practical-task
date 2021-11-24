package app.practical.task.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var phoneNumber: String? = null,
    var name: String? = null,
    var email: String? = null,
    var profile: String? = null
)