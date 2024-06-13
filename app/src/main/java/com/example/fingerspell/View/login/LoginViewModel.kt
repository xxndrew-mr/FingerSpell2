package com.example.fingerspell.View.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fingerspell.data.pref.UserModel
import com.example.fingerspell.data.pref.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel()  {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}