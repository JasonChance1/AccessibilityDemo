package com.example.accessibilitydemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.accessibilitydemo.database.AppDatabase
import com.example.accessibilitydemo.database.entity.Record
import com.example.accessibilitydemo.databinding.ItemRecordBinding
import com.example.accessibilitydemo.util.setVisible

class RecordAdapter(val context: Context, val records: MutableList<Record>) :
    RecyclerView.Adapter<RecordAdapter.ViewHolder>() {
        private val recordDao = AppDatabase.get().recordDao()

    class ViewHolder(val binding: ItemRecordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemRecordBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun getItemCount() = records.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position<records.size){
            val record = records[position]
            holder.binding.name.setText(record.name)
            holder.binding.apply {
                change.setOnClickListener {
                    name.isEnabled = true
                    change.setVisible(false)
                    complete.setVisible(true)
                }

                complete.setOnClickListener {
                    name.isEnabled = false
                    change.setVisible(true)
                    complete.setVisible(false)
                    record.name = name.text.toString()
                    recordDao.update(record)
                }

                load.setOnClickListener {
                    onLoad?.invoke(record)
                }
            }
        }
    }

    fun update(records: List<Record>){
        this.records.clear()
        this.records.addAll(records)
        notifyDataSetChanged()
    }

    var onLoad: ((Record) -> Unit)? = null
}