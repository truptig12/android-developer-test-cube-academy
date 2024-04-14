package com.cube.cubeacademy.lib.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cube.cubeacademy.databinding.ViewNominationListItemBinding
import com.cube.cubeacademy.lib.db.NominationWithNominee
import com.cube.cubeacademy.lib.models.Nomination

class NominationsRecyclerViewAdapter : ListAdapter<NominationWithNominee, NominationsRecyclerViewAdapter.ViewHolder>(DIFF_CALLBACK) {
	class ViewHolder(val binding: ViewNominationListItemBinding) : RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val binding = ViewNominationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = getItem(position)
		holder.binding.apply {
			/**
			 * TODO: This should show the nominee name instead of their id! Where can you get their name from?
			 */
			name.text = "${item.nominee.firstName} ${item.nominee.lastName}"
			reason.text = item.nomination.reason
		}
	}

	companion object {
		val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NominationWithNominee>() {
			override fun areItemsTheSame(oldItem: NominationWithNominee, newItem: NominationWithNominee) = oldItem.nomination.nominationId == newItem.nomination.nominationId
			override fun areContentsTheSame(oldItem: NominationWithNominee, newItem: NominationWithNominee) = oldItem == newItem
		}
	}
}