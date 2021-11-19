package com.example.myviewinglist.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myviewinglist.databinding.EntryListItemBinding
import com.example.myviewinglist.model.Entry

class EntryListAdapter(private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<EntryListAdapter.EntryViewHolder>() {

    private var dataList = mutableListOf<Entry>()

    fun setListData(data: MutableList<Entry>) {
        dataList = data
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Entry>() {
        override fun areItemsTheSame(oldItem: Entry, newItem: Entry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Entry, newItem: Entry): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.type == newItem.type
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val itemBinding = EntryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EntryViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry: Entry = dataList[position]
        holder.bind(entry)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class EntryViewHolder(private val itemBinding: EntryListItemBinding):
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        fun bind(entry: Entry) {
            itemBinding.entry = entry
            //itemBinding.entryCover.load()
            //itemBinding.entryType.text = setTypeText(entry.type, context)
            itemBinding.executePendingBindings()
        }

        init {
            itemBinding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}