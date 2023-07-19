package com.dss.tooldocapplication.split


import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.dss.tooldocapplication.base.BaseFragment
import com.dss.tooldocapplication.databinding.FragmentPreviewSplitBinding
import com.dss.tooldocapplication.split.adapter.FolderSplitDirAdapter
import com.dss.tooldocapplication.split.viewmodel.SplitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreviewSplitFragment : BaseFragment<FragmentPreviewSplitBinding>() {

    override fun bindingView() = FragmentPreviewSplitBinding.inflate(layoutInflater)


    private lateinit var dirAdapter: FolderSplitDirAdapter

    private val viewModel by activityViewModels<SplitViewModel>()

    companion object {
        fun newInstance() = PreviewSplitFragment()
    }

    override fun initConfig(savedInstanceState: Bundle?) {

        dirAdapter = FolderSplitDirAdapter {
            it.bitmap?.let { it1 ->

            }
        }
        dirAdapter.set(viewModel.listPdfSplitPreview)
        binding.rcv.adapter = dirAdapter
    }

    override fun initObserver() {

    }

    override fun initListener() {

    }

}