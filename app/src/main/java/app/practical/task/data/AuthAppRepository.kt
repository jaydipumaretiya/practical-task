package app.practical.task.data

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.practical.task.data.model.User
import app.practical.task.util.Constants
import app.practical.task.util.SessionManager
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class AuthAppRepository(activity: Activity) {

    private var activity: Activity? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var database: DatabaseReference
    private var userLiveData: MutableLiveData<FirebaseUser?>? = null
    private var error: MutableLiveData<Exception?>? = null
    private var loggedOutLiveData: MutableLiveData<Boolean>? = null

    init {
        this.activity = activity
        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        userLiveData = MutableLiveData()
        error = MutableLiveData()
        loggedOutLiveData = MutableLiveData()
        if (firebaseAuth!!.currentUser != null) {
            userLiveData!!.postValue(firebaseAuth!!.currentUser)
            loggedOutLiveData!!.postValue(false)
        }
    }

    fun login(
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth!!)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity!!)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun resendCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(firebaseAuth!!)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity!!)
            .setCallbacks(callbacks)
        if (token != null) {
            optionsBuilder.setForceResendingToken(token)
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    fun verity(
        verificationId: String,
        code: String,
        phoneWithCode: String
    ) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential, phoneWithCode)
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, phoneWithCode: String) {
        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithCredential:success")
                    writeNewUser(firebaseAuth!!.currentUser, phoneWithCode)
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        error!!.postValue(task.exception)
                    }
                }
            }
    }

    fun logOut() {
        firebaseAuth!!.signOut()
        loggedOutLiveData!!.postValue(true)
    }

    fun getUserLiveData(): MutableLiveData<FirebaseUser?>? {
        return userLiveData
    }

    fun onError(): MutableLiveData<Exception?>? {
        return error
    }

    private fun writeNewUser(firebaseUser: FirebaseUser?, phoneNumber: String) {
        database.child(Constants.DATABASE_USER).child(firebaseUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val user = User(
                            dataSnapshot.child("phoneNumber").value.toString(),
                            dataSnapshot.child("name").value.toString(),
                            dataSnapshot.child("email").value.toString(),
                            dataSnapshot.child("profile").value.toString()
                        )
                        SessionManager(activity!!).storedUser = user
                        userLiveData!!.postValue(firebaseUser)
                    } else {
                        val user = User(phoneNumber, "", "", "")
                        SessionManager(activity!!).storedUser = user
                        database.child(Constants.DATABASE_USER).child(firebaseUser.uid)
                            .setValue(user)
                        userLiveData!!.postValue(firebaseUser)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }
}