package com.dss.tooldocapplication.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    open val binding by lazy { bindingView() }

    private var saveInstanceStateCalled: Boolean = false

    var primaryBaseActivity: Context? = null

    protected lateinit var dialogLoading: DialogLoading

    override fun attachBaseContext(newBase: Context) {
        primaryBaseActivity = newBase
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingView()
        setContentView(binding.root)
        dialogLoading = DialogLoading(this)
        saveInstanceStateCalled = false
        initConfig(savedInstanceState)
        initObserver()
        initListener()
    }

    open fun initConfig(savedInstanceState: Bundle?) {}

    open fun initListener() {}

    open fun initObserver() {}

    abstract fun bindingView(): T

    override fun onStart() {
        super.onStart()
        saveInstanceStateCalled = false
    }

    override fun onResume() {
        super.onResume()
        saveInstanceStateCalled = false
    }

    fun canChangeFragmentManagerState(): Boolean {
        return !(saveInstanceStateCalled || isFinishing)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveInstanceStateCalled = true
    }

    open fun backListener(
        viewBack: View? = null,
        func: () -> Unit
    ) {
        viewBack?.setOnClickListener {
            func()
        }
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    func()
                }
            })
    }

    override fun onDestroy() {
        dialogLoading.dismissDialog()
        super.onDestroy()
    }

}