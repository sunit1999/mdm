package com.sunit.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sunit.myapplication.R
import com.sunit.myapplication.databinding.SingleAppItemBinding
import com.sunit.myapplication.ui.model.AppInfo

class AppsListAdapter(private val callback: (AppInfo) -> Unit) :
    ListAdapter<AppInfo, AppsListAdapter.ViewHolder>(AppsListDiffCallback) {

    class ViewHolder(view: View, private val callback: (AppInfo) -> Unit):
    RecyclerView.ViewHolder(view) {
        val binding = SingleAppItemBinding.bind(view)

        fun bind(item: AppInfo) {
            binding.apply {
                appName.text = item.displayName
                appLogo.setImageDrawable(item.icon)
                openAppCta.setOnClickListener {
                    callback.invoke(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_app_item, parent, false)
        return ViewHolder(view, callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    object AppsListDiffCallback: DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.pkgName == newItem.pkgName
        }

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem == newItem
        }
    }
}