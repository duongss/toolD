package com.dss.tooldocapplication.split


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import com.dss.tooldocapplication.*
import com.dss.tooldocapplication.base.BaseActivity
import com.dss.tooldocapplication.base.addFragment
import com.dss.tooldocapplication.databinding.FragmentSplitBinding
import com.dss.tooldocapplication.split.adapter.RangeSplitAdapter
import com.dss.tooldocapplication.split.adapter.SplitMethodAdapter
import com.dss.tooldocapplication.split.model.Range
import com.dss.tooldocapplication.split.model.Split
import com.dss.tooldocapplication.split.viewmodel.SplitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplitActivity : BaseActivity<FragmentSplitBinding>() {

    override fun bindingView() = FragmentSplitBinding.inflate(layoutInflater)

    private lateinit var adapterView: SplitMethodAdapter

    private lateinit var adapterCustomRange: RangeSplitAdapter

    private lateinit var adapterRemoveRange: RangeSplitAdapter

    private var canSplit = false

    private var listCustomRange = arrayListOf<Range>()

    private var listRemoveRange = arrayListOf<Range>()

    private val viewModel by viewModels<SplitViewModel>()

    companion object {
        const val BUNDLE_FILE_SELECTED = "bundle_file_selected"
        const val BUNDLE_FILE_PASSWORD = "bundle_file_selected"

        fun newIntent(context: Context, file: String, password: String): Intent {
            return Intent(context, SplitActivity::class.java).apply {
                putExtra(BUNDLE_FILE_SELECTED, file)
                putExtra(BUNDLE_FILE_PASSWORD, password)
            }
        }
    }

    override fun initConfig(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        viewModel.initData()
        (getString(R.string.total_pages) + ": ${viewModel.numberOfPages}").also {
            binding.tvTotalPage.text = it
        }
        binding.frContainer.visible()
        binding.lnGroupApply.alpha = 0.3f
        binding.lnGroupApply.isEnabled = false

        initAdapterType()
        initAdapterSplitCustom()
        initAdapterSplitDelete()
    }

    override fun initObserver() {
        viewModel.isShowLoading.observe(this) { isLoading ->
            if (isLoading) {
                dialogLoading.show {
                    dialogLoading.dismissDialog()
                }
            } else {
                dialogLoading.dismissDialog()
            }
        }

        viewModel.isLoadPreview.observe(this) {
            binding.btnPreview.isEnabled = it
            if (it) {
                binding.btnPreview.alpha = 1f
            } else {
                binding.btnPreview.alpha = 0.3f
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun initListener() {
        backListener(binding.toolbar.btnBack) {
            finish()
        }

        binding.btnAddRangeCustom.onAvoidDoubleClick {
            addRange(listCustomRange, adapterCustomRange)
        }

        binding.btnAddRangeDelete.onAvoidDoubleClick {
            addRange(listRemoveRange, adapterRemoveRange)
        }

        binding.btnSplit.onAvoidDoubleClick {
            hideKey()
            viewModel.currentSplit.rangeCustom = listCustomRange
            viewModel.currentSplit.rangeRemove = listRemoveRange
            viewModel.currentSplit.fixedIndex = binding.edtFixed.text.toString()

            if (viewModel.inputFile.exists()) {
                viewModel.split(this) {
                    if (it) {

                    } else {
                        toastMsg(R.string.invalid_field)
                    }
                }
            } else {
                toastMsg(R.string.title_file_not_exists)
            }
        }

        binding.btnPreview.onAvoidDoubleClick {
            if (!viewModel.checkSplit()) {
                viewModel.currentSplit = Split(
                    getString(R.string.split_4),
                    viewModel.numberOfPages.toString() + " " + getString(R.string.sub_split_4),
                    Split.Type.ALL_PAGE
                )
            }
            hideKey()
            viewModel.currentSplit.rangeCustom = listCustomRange
            viewModel.currentSplit.rangeRemove = listRemoveRange
            viewModel.currentSplit.fixedIndex = binding.edtFixed.text.toString()

            if (viewModel.inputFile.exists()) {
                viewModel.preview {
                    if (it) {
                        addFragment(PreviewSplitFragment.newInstance())
                    } else {
                        toastMsg(R.string.invalid_field)
                    }
                }
            } else {
                toastMsg(R.string.title_file_not_exists)
            }
        }
    }

    private fun hideKey() {
        hideSoftKeyboard()
        binding.edtFixed.clearFocus()
        adapterCustomRange.notifyDataSetChanged()
        adapterRemoveRange.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initAdapterType() {
        val listSplit = arrayListOf(
            Split(
                getString(R.string.split_1),
                getString(R.string.sub_split_1),
                Split.Type.CUSTOM_RANGE
            ), Split(
                getString(R.string.split_2),
                getString(R.string.sub_split_2),
                Split.Type.FIXED_RANGE
            ), Split(
                getString(R.string.split_3),
                getString(R.string.sub_split_3),
                Split.Type.DELETE_PAGE
            ), Split(
                getString(R.string.split_4),
                viewModel.numberOfPages.toString() + " " + getString(R.string.sub_split_4),
                Split.Type.ALL_PAGE
            )
        )
        adapterView = SplitMethodAdapter()
        adapterView.set(listSplit)
        binding.rcvFeature.adapter = adapterView

        adapterView.onItemSelectListener = { split, itemSplitBinding, i ->
            adapterView.dataList.forEach {
                it.isSelected = false
            }
            split.isSelected = true
            adapterView.notifyDataSetChanged()

            binding.layoutSplit1.gone()
            binding.layoutSplit2.gone()
            binding.layoutSplit3.gone()
            viewModel.currentSplit = split
            binding.lnGroupApply.alpha = 1f
            binding.lnGroupApply.isEnabled = true
            canSplit = false

            when (split.type) {
                Split.Type.CUSTOM_RANGE -> {
                    binding.layoutSplit1.visible()
                }
                Split.Type.FIXED_RANGE -> {
                    binding.layoutSplit2.visible()
                }
                Split.Type.DELETE_PAGE -> {
                    binding.layoutSplit3.visible()
                }
                Split.Type.ALL_PAGE -> {
                    canSplit = true
                }
            }
        }
    }

    private fun initAdapterSplitCustom() {
        adapterCustomRange =
            RangeSplitAdapter(viewModel.numberOfPages, rangeAllPage = true) { range ->
                listCustomRange.remove(range)
                listCustomRange.forEachIndexed { index, r ->
                    r.name = getString(R.string.range) + " " + (index + 1)
                }
                adapterCustomRange.set(listCustomRange)
            }
        binding.rcvSplitCustom.adapter = adapterCustomRange
        addRange(listCustomRange, adapterCustomRange)
    }

    private fun initAdapterSplitDelete() {
        adapterRemoveRange = RangeSplitAdapter(viewModel.numberOfPages, removeListener = { range ->
            listRemoveRange.remove(range)
            listRemoveRange.forEachIndexed { index, r ->
                r.name = getString(R.string.range) + " " + (index + 1)
            }
            adapterRemoveRange.set(listRemoveRange)
        })
        binding.rcvSplitRemove.adapter = adapterRemoveRange
        addRange(listRemoveRange, adapterRemoveRange)
    }

    private fun addRange(list: ArrayList<Range>, adapter: RangeSplitAdapter) {
        list.add(Range(getString(R.string.range) + " " + (list.size + 1)))
        adapter.set(list)
    }

}