package app.practical.task.ui.profile

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import app.practical.task.R
import app.practical.task.data.model.User
import app.practical.task.databinding.ActivityProfileBinding
import app.practical.task.ui.base.BaseActivity
import app.practical.task.ui.base.delegate.viewBinding

class ProfileActivity : BaseActivity(R.layout.activity_profile) {

    private val binding by viewBinding(ActivityProfileBinding::inflate)
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var user: User

    override fun setContent() {
        profileViewModel = ViewModelProvider(
            this, ProfileViewModelFactory(this)
        )[ProfileViewModel::class.java]

        profileViewModel.getUserLiveData()!!.observe(this@ProfileActivity, {
            Toast.makeText(this@ProfileActivity, "Data Updated...", Toast.LENGTH_SHORT).show()
        })

        user = sessionManager!!.storedUser!!

        binding.edtName.setText(user.name)
        binding.edtEmail.setText(user.email)
        binding.edtPhoneNumber.setText(user.phoneNumber)

        binding.btnSubmit.setOnClickListener {
            user.name = binding.edtName.text.toString()
            user.email = binding.edtEmail.text.toString()
            user.phoneNumber = binding.edtPhoneNumber.text.toString()

            profileViewModel.update(user)
        }
    }
}