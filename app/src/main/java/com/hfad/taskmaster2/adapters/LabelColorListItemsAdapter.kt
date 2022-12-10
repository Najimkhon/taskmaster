package com.hfad.taskmaster2.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hfad.taskmaster2.databinding.ItemLabelColorBinding

open class LabelColorListItemsAdapter(private val context: Context, private var list:ArrayList<String>, private val mSelectedColor: String)
    :RecyclerView.Adapter<LabelColorListItemsAdapter.MyViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LabelColorListItemsAdapter.MyViewHolder {
        return MyViewHolder(ItemLabelColorBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: LabelColorListItemsAdapter.MyViewHolder, position: Int) {
        val item = list[position]
        if(holder is MyViewHolder){
            holder.itemLabelColorBinding.viewMain.setBackgroundColor(Color.parseColor(item))
            if (item == mSelectedColor){
                holder.itemLabelColorBinding.ivSelectedColor.visibility = View.VISIBLE
            }else{
                holder.itemLabelColorBinding.ivSelectedColor.visibility = View.GONE
            }
            holder.itemLabelColorBinding.flMain.setOnClickListener{
                if (onItemClickListener != null){
                    onItemClickListener!!.onClick(position, item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(val itemLabelColorBinding: ItemLabelColorBinding): RecyclerView.ViewHolder(itemLabelColorBinding.root){
    }

    interface OnItemClickListener{
        fun onClick(position: Int, color: String)
    }
}


