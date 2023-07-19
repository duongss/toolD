package com.dss.tooldocapplication.split.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle

open class BaseViewModel(private val savedStateHandle: SavedStateHandle) :
    androidx.lifecycle.ViewModel() {

    val isShowLoading = MutableLiveData<Boolean>()

    fun showLoading() {
        isShowLoading.postValue(true)
    }

    fun dismissLoading() {
        isShowLoading.postValue(false)
    }

}