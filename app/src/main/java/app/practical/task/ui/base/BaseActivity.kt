package app.practical.task.ui.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.practical.task.R
import app.practical.task.util.SessionManager

abstract class BaseActivity(layoutResId: Int) : AppCompatActivity(layoutResId) {

    var sessionManager: SessionManager? = null
    var dialog: Dialog? = null

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        sessionManager = SessionManager(this)
        setContent()
    }

    protected abstract fun setContent()

    protected fun setGridRecyclerView(
        recyclerView: RecyclerView,
        spanCount: Int
    ): RecyclerView {
        val gridLayoutManager = GridLayoutManager(this, spanCount)
        recyclerView.setHasFixedSize(false)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = gridLayoutManager
        return recyclerView
    }

    fun showLoading() {
        dialog = Dialog(this@BaseActivity)
        val inflate = LayoutInflater.from(this@BaseActivity).inflate(R.layout.dialog_progress, null)
        dialog!!.setContentView(inflate)
        dialog!!.setCancelable(false)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.show()
    }

    fun hideLoading() {
        if (dialog!!.isShowing) {
            dialog!!.cancel()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
