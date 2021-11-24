package app.practical.task.data

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import app.practical.task.data.model.User
import app.practical.task.util.Constants
import app.practical.task.util.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileAppRepository(activity: Activity) {

    private var activity: Activity? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var database: DatabaseReference
    private var _user: MutableLiveData<User?>? = null

    init {
        this.activity = activity
        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        _user = MutableLiveData()
    }

    fun update(user: User) {
        SessionManager(activity!!).storedUser = user
        database.child(Constants.DATABASE_USER).child(firebaseAuth!!.currentUser!!.uid)
            .setValue(user)
        _user!!.postValue(user)
    }

    fun getUserLiveData(): MutableLiveData<User?>? {
        return _user
    }
}