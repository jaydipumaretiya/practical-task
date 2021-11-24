package app.practical.task.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.practical.task.data.ProfileAppRepository
import app.practical.task.data.model.User
import com.google.firebase.auth.FirebaseUser

class ProfileViewModel(private val profileAppRepository: ProfileAppRepository) : ViewModel() {

    private var userLiveData: MutableLiveData<User?>? = null

    init {
        userLiveData = profileAppRepository.getUserLiveData()
    }

    fun update(
        user: User
    ) {
        profileAppRepository.update(user)
    }

    fun getUserLiveData(): MutableLiveData<User?>? {
        return userLiveData
    }
}
