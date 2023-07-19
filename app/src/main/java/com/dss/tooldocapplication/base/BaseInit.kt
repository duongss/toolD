package com.dss.tooldocapplication.base

import android.os.Bundle

interface BaseInit<T> {
    fun bindingView(): T

    fun initConfig(savedInstanceState: Bundle?)

    fun initObserver()

    fun initListener()

}