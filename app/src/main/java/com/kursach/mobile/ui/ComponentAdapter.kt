package com.kursach.mobile.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.kursach.mobile.R
import com.kursach.mobile.api.DashboardComponent

class ComponentAdapter(
    private val onItemClick: (DashboardComponent) -> Unit
) : ListAdapter<DashboardComponent, ComponentAdapter.ComponentViewHolder>(DiffCallback) {
    private var assignmentByEquipmentId: Map<String, String?> = emptyMap()
    private var selectedId: Long? = null

    fun submitList(items: List<DashboardComponent>, assignmentMap: Map<String, String?>) {
        assignmentByEquipmentId = assignmentMap
        super.submitList(items)
    }

    fun setSelectedId(id: Long?) {
        selectedId = id
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_component_row, parent, false)
        return ComponentViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ComponentViewHolder, position: Int) {
        holder.bind(getItem(position), assignmentByEquipmentId, getItem(position).id == selectedId)
    }

    class ComponentViewHolder(
        itemView: View,
        private val onItemClick: (DashboardComponent) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val card = itemView.findViewById<MaterialCardView>(R.id.componentCard)
        private val name = itemView.findViewById<TextView>(R.id.componentName)
        private val details = itemView.findViewById<TextView>(R.id.componentDetails)
        private val status = itemView.findViewById<TextView>(R.id.componentStatus)
        private val assignee = itemView.findViewById<TextView>(R.id.componentAssignee)

        fun bind(item: DashboardComponent, assignmentMap: Map<String, String?>, selected: Boolean) {
            name.text = item.name
            details.text = itemView.context.getString(
                R.string.component_details_format,
                item.type,
                item.serial,
                item.description.orEmpty().ifBlank { "No description" }
            )
            status.text = itemView.context.getString(R.string.component_status_format, item.status)
            assignee.text = itemView.context.getString(
                R.string.component_assignee_format,
                assignmentMap[item.id.toString()] ?: "Unassigned"
            )

            card.strokeWidth = if (selected) 4 else 1
            card.strokeColor = ContextCompat.getColor(
                itemView.context,
                if (selected) R.color.selected_card_stroke else R.color.unselected_card_stroke
            )

            itemView.setOnClickListener { onItemClick(item) }
            card.setOnClickListener { onItemClick(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<DashboardComponent>() {
        override fun areItemsTheSame(oldItem: DashboardComponent, newItem: DashboardComponent): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: DashboardComponent, newItem: DashboardComponent): Boolean = oldItem == newItem
    }
}
