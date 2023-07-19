package com.dss.tooldocapplication.base

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.pdfviewer.alldocument.pdfreader.R
import com.pdfviewer.alldocument.pdfreader.databinding.ToolbarBinding
import com.wavez.common.extension.autoColorDarkMode
import com.wavez.common.extension.invisible
import com.wavez.common.extension.visible


class ToolbarApp {
    private lateinit var binding: ToolbarBinding

    private lateinit var context: Context

    fun bind(toolbarBinding: ToolbarBinding, context: Context): ToolbarApp {
        binding = toolbarBinding
        this.context = context

        binding.ivBack.autoColorDarkMode(R.color.main_color)
        binding.root.autoColorDarkMode(R.color.main_color)
        return this
    }


    fun customToolbar(
        title: String = "",
        drawableButton: Int = R.drawable.ic_search,
        drawableButton1: Int = R.drawable.ic_main_more,
        drawableButton2: Int = R.drawable.ic_all_file_arrow,
        colorText: Int = R.color.white,
        isButton: Boolean = false,
        isButton1: Boolean = false,
        isButton2: Boolean = false
    ): ToolbarApp {
        if (isButton) {
            binding.ivButton.setImageResource(drawableButton)
            binding.ivButton.visible()
            binding.guideline2.setGuidelinePercent(0.8f)
            if (isButton1) {
                binding.ivButton1.setImageResource(drawableButton1)
                binding.ivButton1.visible()
                binding.guideline2.setGuidelinePercent(0.7f)
                if (isButton2) {
                    binding.ivButton2.setImageResource(drawableButton2)
                    binding.ivButton2.visible()
                    binding.guideline2.setGuidelinePercent(0.6f)
                }
            }
        } else {
            binding.ivButton.invisible()
            binding.guideline2.setGuidelinePercent(1f)
        }

        binding.tvTitle.text = title
        binding.tvTitle.setTextColor(ContextCompat.getColor(context, colorText))

        return this
    }

    fun backListener(
        fragmentActivity: ComponentActivity,
        viewLifecycleOwner: LifecycleOwner? = null, // using in fragment,activity is null
        func: () -> Unit
    ) {
        binding.ivBack.setOnClickListener {
            func()
        }
        viewLifecycleOwner?.let {
            fragmentActivity.onBackPressedDispatcher.addCallback(
                it,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        func()
                    }
                })
        }

    }

    fun buttonListener(func: () -> Unit) {
        binding.ivButton.setOnClickListener {
            func()
        }
    }

    fun button1Listener(func: () -> Unit) {
        binding.ivButton1.setOnClickListener {
            func()
        }
    }

    fun button2Listener(func: () -> Unit) {
        binding.ivButton2.setOnClickListener {
            func()
        }
    }

}