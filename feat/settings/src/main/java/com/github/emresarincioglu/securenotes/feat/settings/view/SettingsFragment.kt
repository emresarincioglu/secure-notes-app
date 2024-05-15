package com.github.emresarincioglu.securenotes.feat.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.github.emresarincioglu.securenotes.core.ui.DataBindingFragment
import com.github.emresarincioglu.securenotes.feat.settings.R
import com.github.emresarincioglu.securenotes.feat.settings.databinding.FragmentSettingsBinding
import com.github.emresarincioglu.securenotes.feat.settings.viewmodel.SettingsViewModel

class SettingsFragment : DataBindingFragment<FragmentSettingsBinding>() {

    private val settingsViewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val showPasswordDialog = requireArguments().getBoolean("show_password_dialog")
        if (showPasswordDialog) {
            binding.tvPassword.performClick()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        inflateBinding(R.layout.fragment_settings, inflater, container, false)
        binding.viewModel = settingsViewModel

        setupViews()

        return binding.root
    }

    private fun setupViews() {
        setupAuthenticationSettingViews()
        setupSecuritySettingViews()
    }

    private fun setupAuthenticationSettingViews() {

        binding.tvPassword.setOnClickListener {
            val layout = layoutInflater.inflate(R.layout.dialog_password_view, null)
            val etPassword = layout.findViewById<EditText>(R.id.et_password)

            AlertDialog.Builder(requireContext())
                .setView(layout)
                .setTitle(R.string.password_title)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { dialog, which ->

                    val password = etPassword.text?.toString()
                    if (password.isNullOrBlank()) {
                        settingsViewModel.removePassword()
                    } else {
                        settingsViewModel.setPassword(password)
                    }
                }.show()
        }

        binding.rlBiometric.setOnClickListener {
            binding.swBiometric.apply {
                isChecked = !isChecked
            }
        }

        binding.rlScreenLock.setOnClickListener {
            binding.swScreenLock.apply {
                isChecked = !isChecked
            }
        }

        binding.tvLogOut.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_log_out_title)
                .setMessage(R.string.dialog_log_out_message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    settingsViewModel.logOut()
                }.show()
        }
    }

    private fun setupSecuritySettingViews() {

        binding.llAttemptLimit.setOnClickListener {
            val layout = layoutInflater.inflate(R.layout.dialog_login_attempt_limit_view, null)
            val editText = layout.findViewById<EditText>(R.id.et_int_input)

            AlertDialog.Builder(requireContext())
                .setView(layout)
                .setTitle(R.string.dialog_attempt_limit_title)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    val limit =
                        if (editText.text.isNullOrBlank()) 0 else editText.text.toString().toInt()
                    settingsViewModel.setLoginAttemptLimit(limit)
                }.show()
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
                }.show()
        }
    }
}