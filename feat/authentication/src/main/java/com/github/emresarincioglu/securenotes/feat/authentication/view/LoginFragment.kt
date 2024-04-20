package com.github.emresarincioglu.securenotes.feat.authentication.view

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.github.emresarincioglu.securenotes.core.ui.DataBindingFragment
import com.github.emresarincioglu.securenotes.feat.authentication.R
import com.github.emresarincioglu.securenotes.feat.authentication.databinding.FragmentLoginBinding
import com.github.emresarincioglu.securenotes.feat.authentication.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

class LoginFragment : DataBindingFragment<FragmentLoginBinding>() {

    private var biometricAuthenticators = 0
    private val loginViewModel by viewModels<LoginViewModel>()

    private val enrollBiometricsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_CANCELED) {
                binding.btnAlternativeLogin.setOnClickListener {
                    showBiometricPrompt()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        inflateBinding(R.layout.fragment_login, inflater, container, false)
        binding.viewModel = loginViewModel

        setupViews()
        observeUiState()
        return binding.root
    }

    private fun observeUiState() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.uiState.collect { uiState ->

                    if (uiState.userPassword == null) {
                        findNavController().navigate(
                            R.id.action_loginFragment_to_homeGraph,
                            args = Bundle().apply {
                                putBoolean("is_password_created", false)
                            }
                        )
                    }

                    biometricAuthenticators = if (uiState.biometricLoginEnabled) {
                        biometricAuthenticators or BIOMETRIC_STRONG
                    } else {
                        biometricAuthenticators and BIOMETRIC_STRONG.inv()
                    }

                    biometricAuthenticators = if (uiState.screenLockLoginEnabled) {
                        biometricAuthenticators or DEVICE_CREDENTIAL
                    } else {
                        biometricAuthenticators and DEVICE_CREDENTIAL.inv()
                    }
                }
            }
        }
    }

    private fun setupViews() {

        setupBiometrics()
        binding.btnLogin.setOnClickListener {

            if (loginViewModel.isExceededLoginAttemptLimit()) {
                Toast.makeText(
                    context,
                    getString(R.string.error_exceed_login_attempt_limit),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (loginViewModel.isPasswordCorrect()) {
                loginViewModel.removeLoginAttemptCount()
                findNavController().navigate(
                    R.id.action_loginFragment_to_homeGraph,
                    args = Bundle().apply {
                        putBoolean("is_password_created", true)
                    }
                )
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.error_password_incorrect),
                    Toast.LENGTH_SHORT
                ).show()
                loginViewModel.increaseLoginAttemptCount()
            }
        }
    }

    private fun setupBiometrics() {

        val context = requireContext()
        val biometricAvailability =
            BiometricManager.from(context).canAuthenticate(biometricAuthenticators)

        when (biometricAvailability) {
            BIOMETRIC_SUCCESS -> binding.btnAlternativeLogin.setOnClickListener {
                showBiometricPrompt()
            }

            BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val enrollIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, biometricAuthenticators
                        )
                    }
                } else {
                    Toast.makeText(
                        context.applicationContext,
                        getString(R.string.toast_enroll_biometric),
                        Toast.LENGTH_SHORT
                    ).show()
                    Intent(Settings.ACTION_SECURITY_SETTINGS)
                }

                binding.btnAlternativeLogin.setOnClickListener {
                    enrollBiometricsLauncher.launch(enrollIntent)
                }
            }
        }
    }

    private fun showBiometricPrompt() {

        val context = requireContext()
        val biometricPrompt =
            BiometricPrompt(this, ContextCompat.getMainExecutor(context),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errorString: CharSequence) {
                        super.onAuthenticationError(errorCode, errorString)

                        val isBiometricNotSet = errorCode == 11
                        if (isBiometricNotSet) {
                            Toast.makeText(
                                context.applicationContext,
                                getString(R.string.error_setup_biometric_data),
                                Toast.LENGTH_SHORT
                            ).show()
                            enrollBiometricsLauncher.launch(Intent(Settings.ACTION_SECURITY_SETTINGS))
                        }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        findNavController().navigate(
                            R.id.action_loginFragment_to_homeGraph,
                            args = Bundle().apply {
                                putBoolean("is_password_created", true)
                            }
                        )
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(
                            context.applicationContext,
                            getString(R.string.error_biometric_authentication_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )

        biometricPrompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setAllowedAuthenticators(biometricAuthenticators)
                .setTitle(getString(R.string.dialog_biometric_title))
                .setSubtitle(getString(R.string.dialog_biometric_subtitle))
                .build()
        )
    }
}