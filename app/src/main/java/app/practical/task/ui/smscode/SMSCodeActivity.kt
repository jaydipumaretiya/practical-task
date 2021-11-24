package app.practical.task.ui.smscode

import `in`.aabhasjindal.otptextview.OTPListener
import android.content.Intent
import android.os.CountDownTimer
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import app.practical.task.R
import app.practical.task.databinding.ActivitySmsCodeBinding
import app.practical.task.ui.base.BaseActivity
import app.practical.task.ui.base.delegate.viewBinding
import app.practical.task.ui.home.HomeActivity
import app.practical.task.ui.login.AuthViewModel
import app.practical.task.ui.login.AuthViewModelFactory
import app.practical.task.ui.login.LoginActivity
import app.practical.task.util.Constants
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class SMSCodeActivity : BaseActivity(R.layout.activity_sms_code) {

    private val binding by viewBinding(ActivitySmsCodeBinding::inflate)
    private lateinit var authViewModel: AuthViewModel

    override fun setContent() {
        val phoneWithCode = intent.getStringExtra(Constants.PHONE_NUMBER)
        binding.tvMessage.text = String.format(getString(R.string.enter_opt), phoneWithCode)

        startCountDown()

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                authViewModel.signInWithPhoneAuthCredential(credential, phoneWithCode!!)
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
                Toast.makeText(this@SMSCodeActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                // Save verification ID and resending token so we can use them later
                LoginActivity.storedVerificationId = verificationId
                LoginActivity.resendToken = token
                binding.btnSubmit.isEnabled = true
                binding.btnResend.isEnabled = false
                startCountDown()
            }
        }

        authViewModel = ViewModelProvider(
            this, AuthViewModelFactory(this)
        )[AuthViewModel::class.java]

        authViewModel.getUserLiveData()?.observe(this@SMSCodeActivity, {
            if (it != null) {
                hideLoading()
                sessionManager!!.userUID = it.uid
                sessionManager!!.isLogin = true
                startActivity(Intent(this@SMSCodeActivity, HomeActivity::class.java))
                finishAffinity()
            }
        })

        authViewModel.onError()?.observe(this@SMSCodeActivity, {
            hideLoading()
            if (it != null) {
                Toast.makeText(this@SMSCodeActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })

        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {
                showLoading()
                authViewModel.verify(
                    LoginActivity.storedVerificationId,
                    binding.otpView.otp!!,
                    phoneWithCode!!
                )
            }
        }

        binding.btnSubmit.setOnClickListener {
            showLoading()
            authViewModel.verify(
                LoginActivity.storedVerificationId,
                binding.otpView.otp!!,
                phoneWithCode!!
            )
        }

        binding.btnResend.setOnClickListener {
            authViewModel.resendCode(phoneWithCode!!, LoginActivity.resendToken, callbacks)
        }
    }

    private fun startCountDown() {
        object : CountDownTimer(60000, 1000) {
            override fun onTick(duration: Long) {
                binding.btnResend.isEnabled = false
                val min = duration / 1000 / 60
                val sec = duration / 1000 % 60
                if (sec < 10) {
                    binding.tvTime.text = "$min:0$sec"
                } else binding.tvTime.text = "$min:$sec"
            }

            override fun onFinish() {
                binding.btnResend.isEnabled = true
                binding.tvTime.text = "00:00"
            }
        }.start()
    }
}