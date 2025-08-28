package com.sun.moviedb.screen.profile

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.sun.moviedb.data.repository.auth.AuthRepository
import com.sun.moviedb.data.repository.auth.AuthRepositoryImpl
import com.sun.moviedb.databinding.FragmentProfileBinding
import com.sun.moviedb.screen.login.LoginActivity
import com.sun.moviedb.service.SyncLocalFirebaseService
import com.sun.moviedb.utils.base.BaseFragment
import java.util.concurrent.Executors
import kotlinx.coroutines.runBlocking

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        super.initView()
        setupStatusBarPadding()
        setupUserInteraction()
        setupLogout()
    }


    private fun setupUserInteraction() {
        val user = FirebaseAuth.getInstance().currentUser
        binding.profileName.text = user?.displayName ?: ""
        binding.profileEmail.text = user?.email ?: ""
        Glide.with(this)
            .load(user?.photoUrl)
            .placeholder(com.sun.moviedb.R.drawable.ic_avatar_placeholder)
            .error(com.sun.moviedb.R.drawable.ic_avatar_placeholder)
            .into(binding.profileAvatar)
    }

    private fun setupStatusBarPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun setupLogout() {
        binding.btnSignOut.setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                SyncLocalFirebaseService.startBackupSearchHistory(requireContext())
                val authRepository: AuthRepository = AuthRepositoryImpl(requireContext())
                val result = runBlocking { authRepository.signOut() }
                requireActivity().runOnUiThread {
                    if (result.isSuccess) {
                        Toast.makeText(
                            requireContext(),
                            getString(com.sun.moviedb.R.string.sign_out_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        Intent(requireContext(), LoginActivity::class.java).also {
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                            requireActivity().finish()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(com.sun.moviedb.R.string.sign_out_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
