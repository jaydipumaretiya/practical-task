package app.practical.task.ui.home

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import app.practical.task.R
import app.practical.task.databinding.ActivityHomeBinding
import app.practical.task.ui.base.BaseActivity
import app.practical.task.ui.base.delegate.viewBinding
import app.practical.task.ui.login.AuthViewModel
import app.practical.task.ui.login.AuthViewModelFactory
import app.practical.task.ui.login.LoginActivity
import app.practical.task.ui.profile.ProfileActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeActivity : BaseActivity(R.layout.activity_home) {

    private val binding by viewBinding(ActivityHomeBinding::inflate)
    private lateinit var authViewModel: AuthViewModel

    override fun setContent() {
        setSupportActionBar(binding.toolbar)

//        val phoneNumber = binding.edtPhoneNumber
//        val login = binding.login
        authViewModel = ViewModelProvider(
            this, AuthViewModelFactory(this)
        )[AuthViewModel::class.java]

//        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
//            val loginState = it ?: return@Observer
//
//            // disable login button unless both username / password is valid
//            login.isEnabled = loginState.isDataValid
//
////            if (loginState.usernameError != null) {
////                username.error = getString(loginState.usernameError)
////            }
////            if (loginState.passwordError != null) {
////                password.error = getString(loginState.passwordError)
////            }
//        })
//
//        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
//            val loginResult = it ?: return@Observer
//
//            hideLoading()
//            if (loginResult.error != null) {
//                showLoginFailed(loginResult.error)
//            }
//            if (loginResult.success != null) {
//                updateUiWithUser(loginResult.success)
//            }
//            setResult(Activity.RESULT_OK)
//
//            //Complete and destroy login activity once successful
//            finish()
//        })
//
//        phoneNumber.apply {
//            afterTextChanged {
//                loginViewModel.loginDataChanged(
//                    phoneNumber.text.toString()
//                )
//            }
//
//            setOnEditorActionListener { _, actionId, _ ->
//                when (actionId) {
//                    EditorInfo.IME_ACTION_DONE ->
//                        loginViewModel.login(
//                            phoneNumber.text.toString()
//                        )
//                }
//                false
//            }
//
//            login.setOnClickListener {
//                showLoading()
//                loginViewModel.login(phoneNumber.text.toString())
//            }
//        }
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionProfile -> {
                startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
                true
            }
            R.id.actionLogout -> {
                MaterialAlertDialogBuilder(this@HomeActivity)
                    .setMessage("Are you sure you want to LOGOUT?")
                    .setPositiveButton("Logout") { dialog, which ->
                        authViewModel.logOut()
                        sessionManager!!.isLogin = false
                        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                        finishAffinity()
                    }
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}