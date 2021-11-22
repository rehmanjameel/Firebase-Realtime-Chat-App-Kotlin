package com.example.firebasechatapp.adapters


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechatapp.R
import com.example.firebasechatapp.models.Message
import kotlinx.android.synthetic.main.left_message_view.view.*
import kotlinx.android.synthetic.main.recycler_text_layout.view.*
import java.lang.IllegalArgumentException

class ChatAdapter(val context: Context, val message: ArrayList<Message>, val itemClick: (Message) -> Unit) :
        RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    //private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("messages")

    private val LEFT_MESSAGE_VIEW = 0
    private val RIGHT_MESSAGE_VIEW = 1

    private val myName = "Name"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("nameKey", Context.MODE_PRIVATE)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {

        /*RIGHT_MESSAGE_VIEW -> RightViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_text_layout, parent, false))

        LEFT_MESSAGE_VIEW -> LeftViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.left_message_view, parent, false))

        else -> throw IllegalArgumentException()*/

        // inflating the message-textview-layout

        if (viewType == RIGHT_MESSAGE_VIEW){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_text_layout, parent, false)
            Log.d("DebuggingLeft: ", message.toString())
            return ViewHolder(view, itemClick)
        }else if (viewType == LEFT_MESSAGE_VIEW) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.left_message_view, parent, false)
            Log.d("DebuggingRight: ", message.toString())
            return ViewHolder(view, itemClick)
        } else {
            throw IllegalArgumentException()
        }
        //return ViewHolder(parent, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindForeCast(message[position], holder)
//        Log.d("DebuggingBindView: ", "${holder.bindForeCast(message[position], holder)}")
    }

    override fun getItemCount() = message.size

    class ViewHolder(view: View, val itemClick: (Message) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bindForeCast(message: Message, holder: ViewHolder) {
            with(message) {
            if (itemViewType == 0){
                holder.itemView.rightNameMessageTextView.text = message.text
                Log.d("MsgEditView1: ", "${itemView.rightNameMessageTextView.text }")
                holder.itemView.rightNameTextView.text = message.name
                Log.d("NameEditView1: ", "${itemView.rightNameTextView.text}")
            } else if (itemViewType == 1){

                holder.itemView.messageTextView.text = message.text
                Log.d("MsgEditView: ", "${itemView.messageTextView.text}")
                holder.itemView.nameTextViewLeft.text = message.name
                Log.d("NameEditView: ", "${itemView.nameTextViewLeft.text}")
            }

                itemView.setOnClickListener { itemClick(this) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val getName = sharedPreferences.getString(myName, "")
        Log.d("UserName: ", getName.toString())
        println("DebuggingName: " + message[position].name.toString())

        return if (message[position].name == getName.toString()) {
            LEFT_MESSAGE_VIEW
        }else {
            RIGHT_MESSAGE_VIEW
        }
    }

    /*class LeftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val leftMessageView: TextView = itemView.findViewById(R.id.messageTextView)
        val leftNameView: TextView = itemView.findViewById(R.id.nameTextViewLeft)
    }

    class RightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rightMessageView: TextView = itemView.findViewById(R.id.rightNameMessageTextView)
        val rightNameView: TextView = itemView.findViewById(R.id.rightNameTextView)
    }

    private fun onBindRightMessage(holder: RecyclerView.ViewHolder, message: Message) {
        val rightMessage = holder as RightViewHolder
        rightMessage.rightMessageView.text = message.text
        rightMessage.rightNameView.text = message.name
    }

    private fun onBindLeftMessage(holder: RecyclerView.ViewHolder, message: Message) {
        val leftMessage = holder as LeftViewHolder
        leftMessage.leftMessageView.text = message.text
        leftMessage.leftNameView.text = message.name
    }*/
}