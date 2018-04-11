package com.anmerris.pwic.model

data class User(val id: Long = 0L, val name: String = "") {
    val isValid: Boolean
        get() = id != 0L && "" != name
}
