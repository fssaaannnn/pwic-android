package com.anmerris.pwic.ui

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.annotation.VisibleForTesting
import com.anmerris.pwic.Injection
import com.anmerris.pwic.model.PwicModel
import com.anmerris.pwic.ui.home.HomeViewModel
import com.anmerris.pwic.ui.login.LoginViewModel


class ViewModelFactory private constructor(
        private val application: Application,
        private val model: PwicModel) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
            with(modelClass) {
                when {
                    isAssignableFrom(LoginViewModel::class.java) ->
                        LoginViewModel(model)
                    isAssignableFrom(HomeViewModel::class.java) ->
                        HomeViewModel(application, model)
                    else ->
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            } as T

    companion object {

        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application) =
                INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                    INSTANCE ?: ViewModelFactory(application,
                            Injection.provideModel(application))
                            .also { INSTANCE = it }
                }

        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }

}
