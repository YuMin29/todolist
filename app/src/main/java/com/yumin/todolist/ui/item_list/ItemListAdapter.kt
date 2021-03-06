package com.yumin.todolist.ui.item_list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yumin.todolist.LogUtils
import com.yumin.todolist.R
import com.yumin.todolist.data.TodoItem


class ItemListAdapter(clickListener: ItemClickListener?) :
    ListAdapter<TodoItem, ItemListAdapter.ViewHolder>(DiffCallBack()) {
    var mEnableLimitSize: Boolean = false
    var mEnableTextGrayOut: Boolean = false
    var mDisableCheckBox: Boolean = false
    private var mClickListener: ItemClickListener? = clickListener

    companion object {
        val TAG: String = ItemListAdapter::javaClass.toString()
    }

    override fun getItemCount(): Int {
        return when (mEnableLimitSize) {
            false -> super.getItemCount()
            true -> getLimitCount()
        }
    }

    private fun getLimitCount(): Int {
        return if (super.getItemCount() > 7)
            7
        else
            super.getItemCount()
    }

    override fun submitList(list: List<TodoItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mItemLayout: LinearLayout = itemView.findViewById(R.id.item_linearLayout)
        val mTitle: TextView = itemView.findViewById(R.id.title)
        val mCheckBox: CheckBox = itemView.findViewById(R.id.item_check_box)

        fun setCheckBoxClickListener(todoItem: TodoItem, itemListener: ItemClickListener) {
            mCheckBox.setOnClickListener {
                itemListener.onCheckBoxClick(mCheckBox.isChecked, todoItem)
            }
        }

        fun setItemLayoutClickListener(todoItem: TodoItem, itemListener: ItemClickListener) {
            mItemLayout.setOnClickListener {
                itemListener.onItemLayoutClick(todoItem)
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_todo_item_layout, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        LogUtils.logD(TAG, "[onBindViewHolder] position =${position}, item = ${getItem(position)}")

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val name: String? = getItem(position)?.name
        viewHolder.mTitle.text = name
        viewHolder.mTitle.isEnabled = !mEnableTextGrayOut
        viewHolder.mCheckBox.isEnabled = !mDisableCheckBox
        viewHolder.mCheckBox.isChecked = getItem(position)?.finished == true

        mClickListener?.apply {
            viewHolder.setCheckBoxClickListener(getItem(position), this)
            viewHolder.setItemLayoutClickListener(getItem(position), this)
        }
    }
}

interface ItemClickListener {
    fun onCheckBoxClick(isChecked: Boolean, todoItem: TodoItem)
    fun onItemLayoutClick(todoItem: TodoItem)
}

class DiffCallBack : DiffUtil.ItemCallback<TodoItem>() {
    override fun areItemsTheSame(oldTodoItem: TodoItem, newTodoItem: TodoItem): Boolean {
        return oldTodoItem.id == newTodoItem.id
    }

    override fun areContentsTheSame(oldTodoItem: TodoItem, newTodoItem: TodoItem): Boolean {
        return oldTodoItem == newTodoItem
    }
}
