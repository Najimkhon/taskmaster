package com.hfad.taskmaster2.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hfad.taskmaster2.activities.CardDetailsActivity
import com.hfad.taskmaster2.databinding.ItemBoardBinding
import com.hfad.taskmaster2.databinding.ItemCardBinding
import com.hfad.taskmaster2.models.Card

open class CardListItemsAdapter(private val context: Context, private var list: ArrayList<Card>)
    : RecyclerView.Adapter<CardListItemsAdapter.MyViewHolder>() {
    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardListItemsAdapter.MyViewHolder {
        return MyViewHolder(ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CardListItemsAdapter.MyViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder) {
            holder.itemCardBinding.tvCardName.text = model.name
            holder.itemCardBinding.card.setOnClickListener{
                if (onClickListener!=null){
                    onClickListener!!.onClick(position)
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
        fun onClick(position: Int)
    }

    inner class MyViewHolder(val itemCardBinding: ItemCardBinding): RecyclerView.ViewHolder(itemCardBinding.root){

    }
}
