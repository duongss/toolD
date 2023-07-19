package com.dss.tooldocapplication.split.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.dss.tooldocapplication.*
import com.dss.tooldocapplication.split.model.Range


class RangeSplitAdapter(
    val numberOfPages: Int,
    val rangeAllPage: Boolean = false,
    val removeListener: (Range) -> Unit
) : RecyclerView.Adapter<RangeSplitAdapter.ViewHolder>() {

    var dataList: MutableList<Range> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_range, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = dataList.size

    @SuppressLint("NotifyDataSetChanged")
    fun set(dataList: ArrayList<Range>) {
        val clone: List<Range> = ArrayList(dataList)
        this.dataList.clear()
        this.dataList.addAll(clone)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.editFrom.clearFocus()
        holder.editEnd.clearFocus()
        if (item.startIndex == 0) {
            holder.editFrom.text.clear()
        } else {
            holder.editFrom.setText(item.startIndex.toString())
        }

        if (item.endIndex == 0) {
            holder.editEnd.text.clear()
        } else {
            holder.editEnd.setText(item.endIndex.toString())
        }
        holder.tvName.text = item.name
        if (position == 0) {
            holder.ivDelete.invisible()
        } else {
            holder.ivDelete.visible()
        }
        holder.ivDelete.onAvoidDoubleClick {
            removeListener(item)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var editFrom = view.findViewById<EditText>(R.id.edtFrom)
        var editEnd = view.findViewById<EditText>(R.id.edtEnd)
        var ivDelete = view.findViewById<ImageView>(R.id.ivDelete)
        var tvInvalid = view.findViewById<TextView>(R.id.tvInvalid)
        var tvName = view.findViewById<TextView>(R.id.tvTitle)


        init {
            editFrom.addTextChangedListener { s ->
                if (s.isNullOrEmpty()) {
                    dataList[adapterPosition].startIndex = 0
                } else {
                    dataList[adapterPosition].startIndex = s.toString().toInt()
                }

                if (validRange(dataList[adapterPosition])) {
                    tvInvalid.gone()
                } else {
                    tvInvalid.visible()
                }
            }
            editEnd.addTextChangedListener { s ->
                if (s.isNullOrEmpty()) {
                    dataList[adapterPosition].endIndex = 0
                } else {
                    dataList[adapterPosition].endIndex = s.toString().toInt()
                }

                if (validRange(dataList[adapterPosition])) {
                    tvInvalid.gone()
                } else {
                    tvInvalid.visible()
                }
            }
        }

        private fun validRange(data: Range): Boolean {
            if (!rangeAllPage && data.endIndex == numberOfPages) {
                return false
            }
            if (data.endIndex >= data.startIndex && (data.startIndex > 0 && data.endIndex > 0) && data.endIndex <= numberOfPages) {
                return true
            }
            return false
        }
    }
}