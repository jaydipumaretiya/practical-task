package app.practical.task.ui.profile

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.practical.task.data.ProfileAppRepository

class ProfileViewModelFactory(private val activity: Activity) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(ProfileAppRepository(activity)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}