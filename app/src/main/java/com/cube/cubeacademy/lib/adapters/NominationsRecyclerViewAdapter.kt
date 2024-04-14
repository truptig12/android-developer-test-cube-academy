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
			 * Binds the data from the provided NominationWithNominee object to the views in the ViewHolder.
			 * The Nominee's name is retrieved from the Nominee object linked to each Nomination via a Room database relation.
			 * This relation is defined in the NominationWithNominee data class where the Nominee object is annotated
			 * with @Relation to link 'nomineeId' from the Nomination to 'nomineeId' in the Nominee entity.
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