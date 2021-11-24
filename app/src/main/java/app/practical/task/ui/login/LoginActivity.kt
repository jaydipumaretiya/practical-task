package app.practical.task.ui.login

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import app.practical.task.R
import app.practical.task.databinding.ActivityLoginBinding
import app.practical.task.ui.base.BaseActivity
import app.practical.task.ui.base.delegate.viewBinding
import app.practical.task.ui.home.HomeActivity
import app.practical.task.ui.smscode.SMSCodeActivity
import app.practical.task.util.Constants
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class LoginActivity : BaseActivity(R.layout.activity_login) {

    private val binding by viewBinding(ActivityLoginBinding::inflate)
    private lateinit var authViewModel: AuthViewModel
    private var phoneWithCode = ""

    companion object {
        lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
        lateinit var storedVerificationId: String
    }

    override fun setContent() {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                authViewModel.signInWithPhoneAuthCredential(credential, phoneWithCode)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                var errorMessage = ""
                if (e is FirebaseAuthInvalidCredentialsException) {
                    errorMessage = e.localizedMessage
                } else if (e is FirebaseTooManyRequestsException) {
                    errorMessage = e.localizedMessage
                }
                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                showCodeEnterOption()
            }
        }

        authViewModel = ViewModelProvider(
            this, AuthViewModelFactory(this)
        )[AuthViewModel::class.java]


        authViewModel.getUserLiveData()?.observe(this@LoginActivity, {
            if (it != null) {
//                hideLoading()
                sessionManager!!.userUID = it.uid
                sessionManager!!.isLogin = true
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finishAffinity()
            }
        })

        authViewModel.onError()?.observe(this@LoginActivity, {
            hideLoading()
            if (it != null) {
                Toast.makeText(this@LoginActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })

        binding.edtPhoneNumber.apply {

            afterTextChanged {
                phoneWithCode =
                    "+${binding.countryCodePicker.selectedCountryCode}${binding.edtPhoneNumber.text.toString()}"
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        authViewModel.sendCode(
                            phoneWithCode,
                            callbacks
                        )
                }
                false
            }
//
            binding.btnSubmit.setOnClickListener {
                showLoading()
                authViewModel.sendCode(phoneWithCode, callbacks)
            }
        }
    }

    private fun showCodeEnterOption() {
        hideLoading()

        val intent = Intent(this@LoginActivity, SMSCodeActivity::class.java)
        intent.putExtra(Constants.PHONE_NUMBER, phoneWithCode)
        startActivity(intent)
        finishAffinity()
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}