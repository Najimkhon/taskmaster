package com.projemanag.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.adapters.MemberListItemsAdapter
import com.hfad.taskmaster2.databinding.DialogListBinding
import com.hfad.taskmaster2.models.User



abstract class MembersListDialog(
    context: Context,
    private var list: ArrayList<User>,
    private val title: String = ""
) : Dialog(context) {

    private var adapter: MemberListItemsAdapter? = null
    private lateinit var bindingDialogList: DialogListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        bindingDialogList = DialogListBinding.inflate(LayoutInflater.from(context), null, false)
        val view = bindingDialogList.root
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View) {
        bindingDialogList.tvTitle.text = title

        if (list.size > 0) {

            bindingDialogList.rvList.layoutManager = LinearLayoutManager(context)
            adapter = MemberListItemsAdapter(context, list)
            bindingDialogList.rvList.adapter = adapter

            adapter!!.setOnClickListener(object :
                MemberListItemsAdapter.OnClickListener {
                override fun onClick(position: Int, user: User, action:String) {
                    dismiss()
                    onItemSelected(user, action)
                }
            })
        }
    }

    protected abstract fun onItemSelected(user: User, action:String)
}
// END