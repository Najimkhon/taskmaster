package com.hfad.taskmaster2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.databinding.ItemBoardBinding
import com.hfad.taskmaster2.models.Board
import com.squareup.picasso.Picasso

open class BoardItemsAdapter    (private val context: Context,
                            private var list:ArrayList<Board>):
        RecyclerView.Adapter<BoardItemsAdapter.MyViewHolder>(){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder){
            Picasso.get()
                .load(model.image)
                .placeholder(R.drawable.ic_board_place_holder)
                .error(R.drawable.ic_board_place_holder)
                .into(holder.itemBoardBinding.ivBoardImage)
            holder.itemBoardBinding.tvName.text = model.name
            holder.itemBoardBinding.tvCreatedBy.text = "Created by: ${model.createdBy}"

            holder.itemBoardBinding.itemView.setOnClickListener{
                if (onClickListener!=null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface OnClickListener{
        fun onClick(position: Int, model: Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    inner class MyViewHolder(val itemBoardBinding: ItemBoardBinding):RecyclerView.ViewHolder(itemBoardBinding.root){

    }



}