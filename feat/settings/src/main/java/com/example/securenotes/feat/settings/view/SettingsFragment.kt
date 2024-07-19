package com.example.securenotes.feat.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.securenotes.core.common.Result
import com.example.securenotes.core.ui.DataBindingFragment
import com.example.securenotes.feat.settings.R
import com.example.securenotes.feat.settings.databinding.FragmentSettingsBinding
import com.example.securenotes.feat.settings.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.securenotes.core.navigation.R as coreNavigationR

@AndroidEntryPoint
class SettingsFragment : DataBindingFragment<FragmentSettingsBinding>() {

    private val settingsViewModel by viewModels<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inflateBinding(R.layout.fragment_settings, inflater, container, false)
        binding.viewModel = settingsViewModel

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(
                R.id.action_settingsFragment_to_homeGraph,
                args = Bundle().apply {
                    putBoolean(
                        "is_password_created",
                        settingsViewModel.uiState.value.isPasswordCreated
                    )
                }
            )
        }

        setupViews()
        observeUiState()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val showPasswordDialog = requireArguments().getBoolean("show_password_dialog")
        if (showPasswordDialog) {
            binding.tvPassword.performClick()
        }
    }

    private fun setupViews() {
        setupAuthenticationSettingViews()
        setupSecuritySettingViews()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    settingsViewModel.uiState.collect { uiState ->
                        if (uiState.isPasswordCreated && !uiState.isAuthenticated) {
                            binding.unbind()
                            findNavController().navigate(
                                coreNavigationR.id.action_global_to_authenticationGraph
                            )
                        }
                    }
                }

                settingsViewModel.setPasswordResultStream.collect { result ->
                    when (result) {
                        Result.Success(false) -> Toast.makeText(
                            context,
                            getString(R.string.error_password_could_not_changed),
                            Toast.LENGTH_SHORT
                        ).show()

                        is Result.Error -> Toast.makeText(
                            context,
                            getString(R.string.error_unknown),
                            Toast.LENGTH_SHORT
                        ).show()

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupAuthenticationSettingViews() {

        binding.tvPassword.setOnClickListener {
            showPasswordDialog()
        }

        binding.rlBiometric.setOnClickListener {
            with(binding.swBiometric) {
                isChecked = !isChecked
            }
        }

        binding.rlScreenLock.setOnClickListener {
            with(binding.swScreenLock) {
                isChecked = !isChecked
            }
        }

        binding.tvLogOut.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.log_out_title)
                .setMessage(R.string.dialog_log_out_message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    settingsViewModel.logOut()
                }
                .show()
        }
    }

    private fun setupSecuritySettingViews() {

        binding.llAttemptLimit.setOnClickListener {
            val layout = layoutInflater.inflate(R.layout.dialog_login_attempt_limit_view, null)
            val editText = layout.findViewById<EditText>(R.id.et_int_input)

            AlertDialog.Builder(requireContext())
                .setView(layout)
                .setTitle(R.string.attempt_limit_title)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    val limit = editText.text.toString().trim().toIntOrNull()
                    settingsViewModel.setAuthenticationAttemptLimit(limit)
                }
                .show()
        }

        binding.llSessionTimeout.setOnClickListener {
            val layout = layoutInflater.inflate(R.layout.dialog_session_timeout_view, null)
            val numberPicker = layout.findViewById<NumberPicker>(R.id.np_session_timeout).apply {
                minValue = 1
                maxValue = 30
                value = settingsViewModel.uiState.value.sessionTimeout
            }

            AlertDialog.Builder(requireContext())
                .setView(layout)
                .setTitle(R.string.session_timeout_title)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    val timeout = numberPicker.value
                    settingsViewModel.setSessionTimeout(timeout)
                }
                .show()
        }
    }

    private fun showPasswordDialog() {

        val layout = layoutInflater.inflate(R.layout.dialog_password_view, null)
        val etCurrentPassword = layout.findViewById<EditText>(R.id.et_current_password)
        val etNewPassword = layout.findViewById<EditText>(R.id.et_new_password)

        val isPasswordCreated = settingsViewModel.uiState.value.isPasswordCreated
        if (!isPasswordCreated) {
            // Hide current password edittext
            etCurrentPassword.visibility = View.GONE
            etNewPassword.hint = ""
        }

        AlertDialog.Builder(requireContext())
            .setView(layout)
            .setTitle(R.string.password_title)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                val oldPassword = if (isPasswordCreated) etCurrentPassword.text.toString() else null
                val newPassword = etNewPassword.text.toString()
                settingsViewModel.setPassword(oldPassword, newPassword.takeIf { it.isNotBlank() })
            }
            .show()
    }
}
