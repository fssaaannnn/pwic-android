package com.anmerris.pwic.model

interface UserCallback {
    fun onCurrentUserChanged(currentUser: User)
}