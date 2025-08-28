package com.sun.moviedb.screen.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.sun.moviedb.R
import com.sun.moviedb.data.model.MessageModel
import com.sun.moviedb.databinding.ViewholderChatFrameReceiverBinding
import com.sun.moviedb.databinding.ViewholderChatFrameSenderBinding

class ChatAdapter(val items: MutableList<MessageModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var context: Context

    override fun getItemViewType(position: Int): Int {
        return if (isSender(position)) VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVER
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)

        return when (viewType) {
            VIEW_TYPE_SENDER -> {
                val binding = ViewholderChatFrameSenderBinding.inflate(inflater, parent, false)
                SenderViewHolder(binding)
            }

            VIEW_TYPE_RECEIVER -> {
                val binding = ViewholderChatFrameReceiverBinding.inflate(inflater, parent, false)
                ReceiverViewHolder(binding)
            }

            else -> {
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val message = items[position]
        when (holder) {
            is SenderViewHolder -> holder.bind(message)
            is ReceiverViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun isSender(position: Int): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid
        return items[position].senderId == uid
    }


    // ViewHolder cho sender
    class SenderViewHolder(private val binding: ViewholderChatFrameSenderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: MessageModel) {
            binding.tvChatSender.text = msg.content
            if (msg.linkAvt.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(msg.linkAvt)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imgSenderAvt)
            }
        }
    }

    // ViewHolder cho receiver
    class ReceiverViewHolder(private val binding: ViewholderChatFrameReceiverBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: MessageModel) {
            binding.tvChatReceiver.text = msg.content
            if (msg.linkAvt.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(msg.linkAvt)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imgReceiverAvt)
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_SENDER = 1
        private const val VIEW_TYPE_RECEIVER = 2
    }
}


