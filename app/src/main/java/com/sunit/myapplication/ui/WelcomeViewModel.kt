package com.sunit.myapplication.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunit.myapplication.data.model.ApiResponse
import kotlinx.coroutines.launch

class WelcomeViewModel(private val welcomeRepository: WelcomeRepository): ViewModel() {

    private val _denyList: MutableLiveData<List<String>> = MutableLiveData(emptyList())

    val denyList: LiveData<List<String>>
        get() = _denyList

    fun getDenyList() {
        viewModelScope.launch {
            runCatching {
                welcomeRepository.getDenyList()
            }.onSuccess(::handleSuccess)
                .onFailure(::handleFailure)
        }
    }

    private fun handleSuccess(apiResponse: ApiResponse) {
        _denyList.value = apiResponse.record.denylist
    }

    private fun handleFailure(throwable: Throwable) {
        Log.e(WelcomeViewModel::class.java.toString(), throwable.toString())
        _denyList.value = emptyList()
    }
}
