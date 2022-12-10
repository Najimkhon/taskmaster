package com.hfad.taskmaster2.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.taskmaster2.adapters.LabelColorListItemsAdapter
import com.hfad.taskmaster2.databinding.ActivityCardDetailsBinding
import com.hfad.taskmaster2.databinding.ActivityMainBinding
import com.hfad.taskmaster2.databinding.DialogListBinding
import com.hfad.taskmaster2.databinding.ItemLabelColorBinding

abstract class LabelColorListDialog(
    context: Context,
    private val list: ArrayList<String>,
    private val title: String = "",
    private val mSelectedColor: String = ""
) : Dialog(context) {

    private var adapter: LabelColorListItemsAdapter? = null

    private lateinit var bindingDialogList: DialogListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingDialogList = DialogListBinding.inflate(LayoutInflater.from(context), null, false)
        val view = bindingDialogList.root
        setContentView(view)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        setUpRecyclerView(bindingDialogList)



    }

    private fun setUpRecyclerView(binding: DialogListBinding){
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListItemsAdapter(context, list, mSelectedColor)
        binding.rvList.adapter = adapter
        adapter!!.onItemClickListener = object : LabelColorListItemsAdapter.OnItemClickListener{
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }

        }
    }

    protected abstract fun onItemSelected(color:String)

}