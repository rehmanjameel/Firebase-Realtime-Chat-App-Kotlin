package com.example.firebasechatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechatapp.R
import com.example.firebasechatapp.models.Message
import kotlinx.android.synthetic.main.recycler_text_layout.view.*

class ChatAdapter(val message: ArrayList<Message>, val itemClick: (Message) -> Unit) :
        RecyclerView.Adapter<ChatAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflating the message-textview-layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_text_layout, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindForeCast(message[position])
    }

    override fun getItemCount() = message.size

    class ViewHolder(view: View, val itemClick: (Message) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bindForeCast(message: Message) {
            with(message) {
                itemView.messageTextView.text = message.text
                itemView.setOnClickListener { itemClick(this) }
            }
        }
    }
}