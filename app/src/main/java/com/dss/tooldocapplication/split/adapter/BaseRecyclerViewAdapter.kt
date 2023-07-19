package com.dss.tooldocapplication.split.adapter

import android.content.Context
import android.view.ViewGroup
import android.viewbinding.ViewBinding
import androidx.recyclerview.widget.RecyclerView
import com.dss.tooldocapplication.onAvoidDoubleClick


abstract class BaseRecyclerViewAdapter<T, V : ViewBinding>(
    var dataList: MutableList<T> = mutableListOf(),
    var isItemClick: Boolean = false
) : RecyclerView.Adapter<BaseViewHolder<T, V>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T, V> {
        return BaseViewHolder(providesItemViewBinding(parent, viewType))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T, V>, position: Int) {
        bindData(holder.binding, dataList[position], position, holder.binding.root.context)
        if (isItemClick) {
            holder.binding.root.onAvoidDoubleClick {
                onItemSelectListener(dataList[position], holder.binding, position)
            }
        }
    }

    abstract fun providesItemViewBinding(parent: ViewGroup, viewType: Int): V

    fun add(itemList: List<T>) {
        val size = this.dataList.size
        this.dataList.addAll(itemList)
        val sizeNew = this.dataList.size
        notifyItemRangeInserted(size, sizeNew)
    }

    fun addAt(position: Int, item: T) {
        dataList.add(position, item)
        notifyItemChanged(position)
    }

    fun addAt(position: Int, itemList: List<T>) {
        val size = this.dataList.size
        this.dataList.addAll(position, itemList)
        val sizeNew = this.dataList.size
        notifyItemRangeInserted(size, sizeNew)
    }

    fun setItemAt(position: Int, item: T) {
        dataList[position] = item
        notifyItemChanged(position)
    }

    fun getItemAt(position: Int): T? {
        return dataList[position]
    }

    open fun set(dataList: List<T>) {
        val clone: List<T> = ArrayList(dataList)
        this.dataList.clear()
        this.dataList.addAll(clone)
        notifyDataSetChanged()
    }

    open fun set(dataList: ArrayList<T>, noti: Boolean = true) {
        val clone: List<T> = ArrayList(dataList)
        this.dataList.clear()
        this.dataList.addAll(clone)
        if (noti) {
            notifyDataSetChanged()
        }
    }


    fun removeAt(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount - position)
    }

    fun removeAt(t: T) {
        try {
            dataList.remove(t)
            val position = dataList.indexOf(t)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
        } catch (e: Exception) {
        }
    }

    fun clear() {
        dataList.clear()
        notifyItemRangeRemoved(0, dataList.size)
    }

    abstract fun bindData(binding: V, data: T, position: Int, context: Context)

    override fun getItemCount(): Int = dataList.size

    var onItemSelectListener: (T, V, Int) -> Unit = { t, v, position -> }

}