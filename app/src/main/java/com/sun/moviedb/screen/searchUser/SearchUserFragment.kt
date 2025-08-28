package com.sun.moviedb.screen.searchUser

import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.moviedb.R
import com.sun.moviedb.data.model.User
import com.sun.moviedb.data.repository.UserRepositoryImpl
import com.sun.moviedb.databinding.FragmentSearchUserBinding
import com.sun.moviedb.screen.searchUser.adapter.ChosenUserRecyclerAdapter
import com.sun.moviedb.screen.searchUser.adapter.SearchUserRecyclerAdapter
import com.sun.moviedb.utils.base.BaseFragment

class SearchUserFragment : BaseFragment<FragmentSearchUserBinding>(), SearchUserContract.View {

    private lateinit var presenter: SearchUserContract.Presenter
    private lateinit var searchUserAdapter: SearchUserRecyclerAdapter
    private lateinit var chosenUserAdapter: ChosenUserRecyclerAdapter

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchUserBinding {
        return FragmentSearchUserBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        super.initView()
        binding.searchUserBackground.setOnClickListener { event ->
            hideKeyboardAndClearFocus(binding.searchEditText)
        }
        setupRecyclerViews()
        setupStandaloneSearchBar()
        setupInviteButton()
    }

    override fun initData() {
        super.initData()
        val userRepository = UserRepositoryImpl()
        presenter = SearchUserPresenterImpl(userRepository)
        presenter.attachView(this)
        presenter.loadInitialUsers()
    }

    private fun setupRecyclerViews() {
        searchUserAdapter = SearchUserRecyclerAdapter { user ->
            presenter.selectUser(user)
        }
        binding.recyclerViewWatchParty.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchUserAdapter
        }

        chosenUserAdapter = ChosenUserRecyclerAdapter { user ->
            presenter.deselectUser(user)
        }
        binding.recyclerViewWatchPartyChosen.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = chosenUserAdapter
        }
    }

    private fun setupStandaloneSearchBar() {
        binding.searchTextInputLayout.hint = getString(R.string.search_for_people)
        binding.searchEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                binding.searchTextInputLayout.hint = ""
            } else {
                binding.searchTextInputLayout.setHint(R.string.search_for_people)
            }
        }
        binding.searchTextInputLayout.setOnClickListener {
            showSearchInputDialog()
        }
        binding.searchTextInputLayout.setOnClickListener {
            binding.searchEditText.setText("")
            presenter.searchUsers("")
        }
    }

    private fun showSearchInputDialog() {
        val editText = android.widget.EditText(requireContext()).apply {
            hint = getString(R.string.search_for_people)
            setText(binding.searchEditText.text?.takeIf {
                it.isNotBlank() && it.toString() != getString(
                    R.string.search_for_people
                )
            } ?: "")
            isSingleLine = true
            inputType = InputType.TYPE_CLASS_TEXT
            imeOptions = EditorInfo.IME_ACTION_SEARCH
        }

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Search Users")
            .setView(editText)
            .setPositiveButton(getString(android.R.string.ok)) { dialogInterface, _ ->
                val query = editText.text.toString().trim()
                binding.searchEditText.setText(query)
                presenter.searchUsers(query)
                dialogInterface.dismiss()
            }
            .setNegativeButton(getString(android.R.string.cancel)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = editText.text.toString().trim()
                binding.searchEditText.setText(query)
                presenter.searchUsers(query)
                dialog.dismiss()
                true
            } else {
                false
            }
        }
        dialog.show()
    }

    private fun setupInviteButton() {
        binding.inviteButtonWatchParty.setOnClickListener {
            presenter.onInviteClicked()
        }
    }

    override fun showLoading() {
        binding.progressBarSearch.visibility = View.VISIBLE
        binding.recyclerViewWatchParty.visibility = View.GONE
        binding.textViewEmptyState.visibility = View.GONE
    }

    override fun hideLoading() {
        binding.progressBarSearch.visibility = View.GONE
    }

    override fun displaySearchableUsers(users: List<User>) {
        searchUserAdapter.submitList(users)
        binding.recyclerViewWatchParty.visibility =
            if (users.isNotEmpty()) View.VISIBLE else View.GONE
    }

    override fun displayChosenUsers(users: List<User>) {
        chosenUserAdapter.submitList(users)
    }

    override fun removeUserFromSearchableList(user: User) {
        Log.d("SearchUserFragment", "Relying on displaySearchableUsers for updates.")
    }

    override fun addUserToSearchableList(user: User) {
        Log.d("SearchUserFragment", "Relying on displaySearchableUsers for updates.")
    }

    override fun removeUserFromChosenList(user: User) {
        Log.d("SearchUserFragment", "Relying on displayChosenUsers for updates.")
    }

    override fun addUserToChosenList(user: User) {
        Log.d("SearchUserFragment", "Relying on displayChosenUsers for updates.")
    }

    override fun showSearchableUsersEmpty(message: String) {
        binding.textViewEmptyState.text = message
        binding.textViewEmptyState.visibility = View.VISIBLE
        binding.recyclerViewWatchParty.visibility = View.GONE
    }

    override fun hideSearchableUsersEmpty() {
        binding.textViewEmptyState.visibility = View.GONE
    }

    override fun showChosenUsersEmpty(message: String) {
        binding.textViewEmptyStateChosen.text = message
        binding.textViewEmptyStateChosen.visibility = View.VISIBLE
    }

    override fun hideChosenUsersEmpty() {
        binding.textViewEmptyStateChosen.visibility = View.GONE
    }

    override fun displayError(message: String) {
        hideLoading()
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        binding.textViewEmptyState.text = "Error: $message"
        binding.textViewEmptyState.visibility = View.VISIBLE
        binding.recyclerViewWatchParty.visibility = View.GONE
    }

    override fun updateInviteButton(count: Int, isEnabled: Boolean) {
        binding.inviteButtonWatchParty.isEnabled = isEnabled
        binding.inviteButtonWatchParty.text =
            if (isEnabled && count > 0) "Invite ($count)" else getString(R.string.invite)
    }

    override fun showInviteSentSuccess(count: Int) {
        Toast.makeText(context, "$count invites sent successfully!", Toast.LENGTH_LONG).show()
    }

    override fun showInviteSentError(message: String) {
        Toast.makeText(context, "Error sending invites: $message", Toast.LENGTH_LONG).show()
    }

    override fun showSendingInvitesLoading(isLoading: Boolean) {
        binding.inviteButtonWatchParty.isEnabled = !isLoading
        if (isLoading) {
            binding.inviteButtonWatchParty.text = "Sending..."
        }
    }


    override fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarSearch.visibility = View.VISIBLE
        } else {
            binding.progressBarSearch.visibility = View.GONE
        }
    }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }

    private fun hideKeyboardAndClearFocus(viewToClearFocusFrom: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(viewToClearFocusFrom.windowToken, 0)
        viewToClearFocusFrom.clearFocus()
    }
}

