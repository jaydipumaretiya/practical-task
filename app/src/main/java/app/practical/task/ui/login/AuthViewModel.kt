package app.practical.task.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.practical.task.data.AuthAppRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class AuthViewModel(private val authAppRepository: AuthAppRepository) : ViewModel() {

    private var userLiveData: MutableLiveData<FirebaseUser?>? = null
    private var error: MutableLiveData<Exception?>? = null

    init {
        userLiveData = authAppRepository.getUserLiveData()
        error = authAppRepository.onError()
    }

    fun sendCode(
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        authAppRepository.login(phoneNumber, callbacks)
    }

    fun resendCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        authAppRepository.resendCode(phoneNumber, token, callbacks)
    }

    fun verify(
        verificationId: String,
        code: String,
        phoneWithCode: String
    ) {
        authAppRepository.verity(verificationId, code, phoneWithCode)
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, phoneWithCode: String) {
        authAppRepository.signInWithPhoneAuthCredential(credential, phoneWithCode)
    }

    fun getUserLiveData(): MutableLiveData<FirebaseUser?>? {
        return userLiveData
    }

    fun onError(): MutableLiveData<Exception?>? {
        return error
    }

    fun logOut() {
        authAppRepository.logOut()
    }
}
