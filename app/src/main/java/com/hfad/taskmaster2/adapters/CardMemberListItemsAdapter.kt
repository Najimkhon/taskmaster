package com.hfad.taskmaster2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.databinding.ItemCardSelectedMemberBinding
import com.hfad.taskmaster2.models.SelectedMembers
import com.squareup.picasso.Picasso
import java.io.IOException

class CardMemberListItemsAdapter(
    private val context: Context, private val list: ArrayList<SelectedMembers>
): RecyclerView.Adapter<CardMemberListItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardMemberListItemsAdapter.MyViewHolder {
        return MyViewHolder(ItemCardSelectedMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CardMemberListItemsAdapter.MyViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            if (position == list.size - 1) {
                holder.itemCardSelectedMemberBinding.ivAddMember.visibility = View.VISIBLE
                holder.itemCardSelectedMemberBinding.ivSelectedMemberImage.visibility = View.GONE
            } else {
                holder.itemCardSelectedMemberBinding.ivAddMember.visibility = View.GONE
                holder.itemCardSelectedMemberBinding.ivSelectedMemberImage.visibility = View.VISIBLE

                try {
                    Picasso.get()
                        .load(model.image)
                        .placeholder(R.drawable.ic_user_place_holder)
                        .error(R.drawable.ic_user_place_holder)
                        .into(holder.itemCardSelectedMemberBinding.ivSelectedMemberImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            holder.itemCardSelectedMemberBinding.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick()
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick()
    }

    inner class MyViewHolder(val itemCardSelectedMemberBinding: ItemCardSelectedMemberBinding):
        RecyclerView.ViewHolder(itemCardSelectedMemberBinding.root)
}

