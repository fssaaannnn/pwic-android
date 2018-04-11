package com.anmerris.pwic.ui.login

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.anmerris.pwic.R
import com.anmerris.pwic.databinding.ActivityLoginBinding
import com.anmerris.pwic.ui.home.HomeActivity
import com.anmerris.pwic.utils.obtainViewModel
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = obtainViewModel(LoginViewModel::class.java)

        if (viewModel.hasLoggedInUser()) {
            HomeActivity.startActivity(this)
            finish()
            return
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.setLifecycleOwner(this)
        binding.loginButton.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                viewModel.loadSessions()
                HomeActivity.startActivity(this@LoginActivity)
                finish()
            }

            override fun failure(exception: TwitterException) {
                Snackbar.make(binding.root, R.string.auth_failed, Snackbar.LENGTH_LONG)
                        .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        binding.loginButton.onActivityResult(requestCode, resultCode, data)
    }
}
