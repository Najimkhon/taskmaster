package com.hfad.taskmaster2.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hfad.taskmaster2.activities.TaskListActivity
import com.hfad.taskmaster2.databinding.ItemTaskBinding
import com.hfad.taskmaster2.models.Task

open class TaskListItemsAdapter(private val context: Context, private var list: ArrayList<Task>):RecyclerView.Adapter<TaskListItemsAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val viewBinding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutParams = LinearLayout.LayoutParams((parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins((15.toDP()).toPx(), 0, (40.toDP()).toPx(), 0)
        viewBinding.root.layoutParams = layoutParams
        return MyViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder){
            if (position == list.size - 1){
                holder.itemTaskBinding.tvAddTaskList.visibility = View.VISIBLE
                holder.itemTaskBinding.llTaskItem.visibility = View.GONE
            }else{
                holder.itemTaskBinding.tvAddTaskList.visibility = View.GONE
                holder.itemTaskBinding.llTaskItem.visibility = View.VISIBLE
            }

            holder.itemTaskBinding.tvTaskListTitle.text = model.title
            holder.itemTaskBinding.tvAddTaskList.setOnClickListener{
                holder.itemTaskBinding.tvAddTaskList.visibility = View.GONE
                holder.itemTaskBinding.cvAddTaskListName.visibility = View.VISIBLE
            }
            holder.itemTaskBinding.ibCloseListName.setOnClickListener{
                holder.itemTaskBinding.tvAddTaskList.visibility = View.VISIBLE
                holder.itemTaskBinding.cvAddTaskListName.visibility = View.GONE
            }
            holder.itemTaskBinding.ibDoneListName.setOnClickListener{
                val listName = holder.itemTaskBinding.etTaskListName.text.toString()
                if (listName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.createTaskList(listName)
                    }else{
                        Toast.makeText(context, "Please, enter a list name!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            holder.itemTaskBinding.ibEditListName.setOnClickListener{
                holder.itemTaskBinding.etEditTaskListName.setText(model.title)
                holder.itemTaskBinding.llTitleView.visibility = View.GONE
                holder.itemTaskBinding.cvEditTaskListName.visibility = View.VISIBLE
            }

            holder.itemTaskBinding.ibCloseEditableView.setOnClickListener{
                holder.itemTaskBinding.llTitleView.visibility = View.VISIBLE
                holder.itemTaskBinding.cvEditTaskListName.visibility = View.GONE
            }

            holder.itemTaskBinding.ibDoneEditListName.setOnClickListener{
                val listName = holder.itemTaskBinding.etEditTaskListName.text.toString()
                if (listName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.updateTaskList(position, listName, model)
                    }else{
                        Toast.makeText(context, "Please, enter a list name!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            holder.itemTaskBinding.ibDeleteList.setOnClickListener{
                alertDialogForDeleteList(position, model.title)
            }

            holder.itemTaskBinding.tvAddCard.setOnClickListener{
                holder.itemTaskBinding.cvAddCard.visibility = View.VISIBLE
                holder.itemTaskBinding.tvAddCard.visibility = View.GONE
            }

            holder.itemTaskBinding.ibCloseCardName.setOnClickListener{
                holder.itemTaskBinding.cvAddCard.visibility = View.GONE
                holder.itemTaskBinding.tvAddCard.visibility = View.VISIBLE
            }
            holder.itemTaskBinding.ibDoneCardName.setOnClickListener{
                val cardName = holder.itemTaskBinding.etCardName.text.toString()
                if (cardName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.createCard(position, cardName)
                    }else{
                        Toast.makeText(context, "Please, enter a card name!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            holder.itemTaskBinding.rvCardList.layoutManager = LinearLayoutManager(context)
            holder.itemTaskBinding.rvCardList.setHasFixedSize(true)
            val adapter = CardListItemsAdapter(context, model.cardList)
            holder.itemTaskBinding.rvCardList.adapter = adapter

            adapter.setOnClickListener(object: CardListItemsAdapter.OnClickListener{
                override fun onClick(cardPosition: Int) {
                    if (context is TaskListActivity){
                        context.cardDetails(holder.adapterPosition, cardPosition)
                    }
                }
            } )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun Int.toDP(): Int =
        (this/ Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    inner class MyViewHolder(val itemTaskBinding: ItemTaskBinding): RecyclerView.ViewHolder(itemTaskBinding.root){

    }

    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }
}