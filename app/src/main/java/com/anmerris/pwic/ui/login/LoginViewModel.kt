package com.anmerris.pwic.ui.login

import android.arch.lifecycle.ViewModel
import com.anmerris.pwic.model.PwicModel

class LoginViewModel(private val model: PwicModel) : ViewModel() {

    fun loadSessions() {
        model.loadSessions()
    }

    fun hasLoggedInUser(): Boolean {
        return model.hasLoggedInUser()
    }

}
