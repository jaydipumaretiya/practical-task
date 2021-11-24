package app.practical.task.ui.splash

import android.content.Intent
import app.practical.task.R
import app.practical.task.ui.base.BaseActivity
import app.practical.task.ui.home.HomeActivity
import app.practical.task.ui.login.LoginActivity

class SplashActivity : BaseActivity(R.layout.activity_splash) {

    override fun setContent() {
        val splashThread = object : Thread() {
            override fun run() {
                synchronized(this) {
                    try {
                        sleep(2000)
                        if (sessionManager!!.isLogin) {
                            startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                        } else {
                            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        }
                        finishAffinity()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        splashThread.start()
    }
}