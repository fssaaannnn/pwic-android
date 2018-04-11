package com.anmerris.pwic.ui.home

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.anmerris.pwic.R
import com.anmerris.pwic.databinding.HomeActivityBinding
import com.anmerris.pwic.utils.obtainViewModel
import com.anmerris.pwic.utils.replaceFragmentInActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<HomeActivityBinding>(this, R.layout.home_activity)

        setSupportActionBar(binding.toolbar)

        setupViewFragment()

        viewModel = obtainViewModel()
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.content_frame)
                ?: HomeFragment.newInstance().let {
                    replaceFragmentInActivity(it, R.id.content_frame)
                }
    }


    fun obtainViewModel(): HomeViewModel = obtainViewModel(HomeViewModel::class.java)

    companion object {
        fun startActivity(context: Context) {
            val i = Intent(context, HomeActivity::class.java)
            context.startActivity(i)
        }
    }
}
