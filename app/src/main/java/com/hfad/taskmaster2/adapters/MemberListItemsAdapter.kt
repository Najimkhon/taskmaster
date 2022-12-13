package com.hfad.taskmaster2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hfad.taskmaster2.R

import com.hfad.taskmaster2.databinding.ItemMemberBinding
import com.hfad.taskmaster2.models.User
import com.hfad.taskmaster2.utils.Constants
import java.io.IOException

open class MemberListItemsAdapter(private val context: Context, private val list: ArrayList<User> ):
    RecyclerView.Adapter<MemberListItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MemberListItemsAdapter.MyViewHolder {
        return MyViewHolder(ItemMemberBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MemberListItemsAdapter.MyViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder){
            holder.itemMember.tvMemberName.text = model.name
            holder.itemMember.tvMemberEmail.text = model.email
            try {
                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.itemMember.ivMemberImage)
            }catch (e: IOException){
                e.printStackTrace()
            }

            if(model.selected){
                holder.itemMember.ivSelectedMember.visibility = View.VISIBLE
            }else{
                holder.itemMember.ivSelectedMember.visibility = View.GONE
            }

            holder.itemMember.itemView.setOnClickListener{
                if (onClickListener != null){
                    if (model.selected){
                        onClickListener!!.onClick(position, model, Constants.UN_SELECT)
                    }else{
                        onClickListener!!.onClick(position, model, Constants.UN_SELECT)
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(val itemMember: ItemMemberBinding ): RecyclerView.ViewHolder(itemMember.root)

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, user: User, action: String)
    }
}

