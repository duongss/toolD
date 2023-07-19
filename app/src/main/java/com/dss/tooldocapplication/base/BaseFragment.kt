package com.dss.tooldocapplication.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewBinding> : Fragment(), BaseInit<T> {

    open val binding by lazy { bindingView() }

    private var saveInstanceStateCalled: Boolean = false

    open val hasEventBus = false

    protected lateinit var dialogLoading: DialogLoading

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        extractData(savedInstanceState)
        dialogLoading = DialogLoading(requireContext())
        initConfig(savedInstanceState)
        initObserver()
        initListener()
    }


    override fun onDestroyView() {
        dialogLoading.dismissDialog()
        super.onDestroyView()
    }

    open fun extractData(savedInstanceState: Bundle?) {}


    override fun onPause() {
        super.onPause()
        saveInstanceStateCalled = true
    }

    override fun onResume() {
        super.onResume()
        saveInstanceStateCalled = false
    }

    fun canChangeFragmentManagerState(): Boolean {
        return !(saveInstanceStateCalled)
    }

    protected fun backListener(
        viewBack: View? = null,
        func: () -> Unit
    ) {
        viewBack?.setOnClickListener {
            func()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    func()
                }
            })
    }
}