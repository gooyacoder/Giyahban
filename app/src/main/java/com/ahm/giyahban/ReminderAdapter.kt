package com.ahm.giyahban

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.Date

class ReminderAdapter(
    private val onDelete: (Reminder) -> Unit,
    private val onEdit: (Reminder) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.VH>() {

    private val items = mutableListOf<Reminder>()
    private val formatter = DateFormat.getDateTimeInstance(
        DateFormat.MEDIUM, DateFormat.SHORT
    )

    fun submit(list: List<Reminder>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.message.text = item.message
        holder.time.text = formatter.format(Date(item.triggerAt))
        holder.itemView.setOnClickListener { onEdit(item) }
        holder.delete.setOnClickListener { onDelete(item) }
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.itemTitle)
        val message: TextView = v.findViewById(R.id.itemMessage)
        val time: TextView = v.findViewById(R.id.itemTime)
        val delete: TextView = v.findViewById(R.id.deleteButton)
    }
}
