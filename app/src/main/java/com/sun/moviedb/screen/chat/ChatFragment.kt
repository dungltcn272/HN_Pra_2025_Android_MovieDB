package com.sun.moviedb.screen.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.moviedb.data.model.MessageModel
import com.sun.moviedb.data.repository.rtdb.ChatRepository
import com.sun.moviedb.data.repository.rtdb.ChatRepositoryImpl
import com.sun.moviedb.databinding.FragmentChatBinding
import com.sun.moviedb.screen.chat.adapter.ChatAdapter
import com.sun.moviedb.utils.base.BaseFragment
import com.sun.moviedb.utils.session.RoomSession
import com.sun.moviedb.utils.session.UserSession

class ChatFragment : BaseFragment<FragmentChatBinding>(), ChatContract.View {

    private lateinit var chatRepository: ChatRepository
    private lateinit var presenter: ChatContract.Presenter
    private lateinit var adapter: ChatAdapter
    private var messageList = mutableListOf<MessageModel>()
    private var roomId: String = RoomSession.roomId ?: ""
    private val TAG = "ChatFragment"

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChatBinding {
        return FragmentChatBinding.inflate(inflater, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }

    override fun initData() {
        super.initData()
        // Initialize presenter
        chatRepository = ChatRepositoryImpl.getInstance()
        presenter = ChatPresenter(chatRepository)
        presenter.attachView(this)
        // Load messages
        if (roomId.isNotEmpty())
            presenter.receiveMessages(roomId)

        // Initialize adapter
        adapter = ChatAdapter(messageList)
        binding.rvLayoutChat.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvLayoutChat.adapter = adapter

        setupUI()
    }

    private fun setupUI() {
        binding.btnSendMessage.setOnClickListener {
            val message = binding.edtMessage.text.toString().trim()

            Log.d(TAG, "message: $message")
            if (message.isNotEmpty()) {
                val messageModel = MessageModel(
                    senderId = UserSession.userId ?: "",
                    senderName = UserSession.userName ?: "Unknown",
                    content = message,
                    linkAvt = UserSession.linkAvatar ?: "",
                    createAt = System.currentTimeMillis()
                )
                if (roomId.isNotEmpty())
                    presenter.sendMessage(roomId, messageModel)
                binding.edtMessage.text.clear()

            } else {
                showError("Message cannot be empty")
            }
        }
    }

    override fun addMessages(message: MessageModel) {
        messageList.add(message)
        adapter.notifyItemInserted(messageList.size - 1)
        binding.rvLayoutChat.scrollToPosition(messageList.size - 1)
    }

    override fun showLoading(isLoading: Boolean) {}

    override fun showError(message: String) {
        Toast.makeText(requireContext(), "$TAG: $message", Toast.LENGTH_SHORT).show()
    }

}

