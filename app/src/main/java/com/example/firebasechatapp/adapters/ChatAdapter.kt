package com.example.firebasechatapp.adapters


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasechatapp.AppGlobals
import com.example.firebasechatapp.R
import com.example.firebasechatapp.SavedPreference
import com.example.firebasechatapp.models.Message
import kotlinx.android.synthetic.main.recycler_text_layout.view.*
import kotlinx.android.synthetic.main.right_message_view.view.*
import java.lang.IllegalArgumentException

class ChatAdapter(val context: Context, val message: ArrayList<Message>, val itemClick: (Message) -> Unit) :
        RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    //private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("messages")

    private val mContext = context
    private val RIGHT_MESSAGE_VIEW = 0
    private val LEFT_MESSAGE_VIEW = 1

    private val myName = "Name"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("nameKey", Context.MODE_PRIVATE)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {

        /*RIGHT_MESSAGE_VIEW -> RightViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_text_layout, parent, false))

        LEFT_MESSAGE_VIEW -> LeftViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.left_message_view, parent, false))

        else -> throw IllegalArgumentException()*/

        // inflating the message-textview-layout

        if (viewType == LEFT_MESSAGE_VIEW){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_text_layout, parent, false)
            Log.d("DebuggingLeft: ", message.toString())
            return ViewHolder(view, itemClick)
        }else if (viewType == RIGHT_MESSAGE_VIEW) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.right_message_view, parent, false)
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

        private val appGlobals = AppGlobals()
        fun bindForeCast(message: Message, holder: ViewHolder) {
            with(message) {
            if (itemViewType == 0){
                holder.itemView.rightNameMessageTextView.text = message.text
                Log.d("MsgEditView1: ", "${itemView.rightNameMessageTextView.text }")
                holder.itemView.rightNameTextView.text = message.name
                Log.d("NameEditView1: ", "${itemView.rightNameTextView.text}")
                val image = message.image
                val imageUrl = appGlobals.getValueString("ImageUrl")
                Glide.with(itemView)
                    .load(image)
                    .error(R.drawable.ic_baseline_person_pin_24)
                    .into(holder.itemView.rightUserImageId)
            } else if (itemViewType == 1){

                holder.itemView.leftMessageTextView.text = message.text
                Log.d("MsgEditView: ", "${itemView.leftMessageTextView.text}")
                holder.itemView.nameTextViewLeft.text = message.name
                Log.d("NameEditView: ", "${itemView.nameTextViewLeft.text}")
                val image = message.image
                Glide.with(itemView)
                    .load(image)
                    .error(R.drawable.ic_baseline_person_pin_24)
                    .into(holder.itemView.leftUserImageId)
            }
                itemView.setOnClickListener { itemClick(this) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val appGlobals = AppGlobals()
//        val getName = sharedPreferences.getString(myName, "")
//        val getName = SavedPreference.getUsername(context)
        val getName = appGlobals.getValueString("userName")
        Log.d("UserName: ", getName.toString())
        println("DebuggingName: " + message[position].name.toString())

        return if (message[position].name == getName.toString()) {
            RIGHT_MESSAGE_VIEW
        }else {
            LEFT_MESSAGE_VIEW
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