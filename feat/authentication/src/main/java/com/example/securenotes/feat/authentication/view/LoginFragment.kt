package com.example.securenotes.feat.authentication.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.securenotes.core.common.Result
import com.example.securenotes.core.ui.DataBindingFragment
import com.example.securenotes.feat.authentication.R
import com.example.securenotes.feat.authentication.ShortToast
import com.example.securenotes.feat.authentication.databinding.FragmentLoginBinding
import com.example.securenotes.feat.authentication.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

@AndroidEntryPoint
class LoginFragment : DataBindingFragment<FragmentLoginBinding>() {

    companion object {
        private const val ERROR_CODE_BIOMETRIC_NOT_SET = 11
    }

    private lateinit var biometricPrompt: BiometricPrompt

    private var biometricAuthenticators by Delegates.notNull<Int>()
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inflateBinding(R.layout.fragment_login, inflater, container, false)
        binding.viewModel = loginViewModel

        setupViews()
        observeUiState()
        observeLoginResult()

        return binding.root
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.uiState.collect { uiState ->

                    if (uiState.isAuthenticated) {
                        // TODO Add navigation animation
                        findNavController().navigate(R.id.action_loginFragment_to_homeGraph)
                    }

                    biometricAuthenticators = 0
                    if (uiState.isBiometricLoginEnabled) {
                        biometricAuthenticators = BIOMETRIC_STRONG
                        setupAlternativeAuth()
                    }
                    if (uiState.isScreenLockLoginEnabled) {
                        biometricAuthenticators = biometricAuthenticators or DEVICE_CREDENTIAL
                        setupAlternativeAuth()
                    }
                }
            }
        }
    }

    private fun observeLoginResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginResultStream.collect { result ->

                    when (result) {
                        is Result.Success -> {
                            binding.piLogin.hide()
                            val isPasswordCorrect = result.data
                            if (!isPasswordCorrect) {
                                onLoginFailed()
                            }
                        }

                        is Result.Error -> {
                            binding.piLogin.hide()
                            Toast.makeText(
                                context,
                                getString(R.string.error_unknown),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Result.Loading -> binding.piLogin.show()
                    }
                }
            }
        }
    }

    private fun setupViews() {
        binding.piLogin.setVisibilityAfterHide(View.GONE)

        binding.btnLogin.setOnClickListener {
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
            loginViewModel.logInWithPassword()
        }

        biometricPrompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(requireContext()),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errorString: CharSequence) {
                    super.onAuthenticationError(errorCode, errorString)
                    if (errorCode == ERROR_CODE_BIOMETRIC_NOT_SET) {
                        Toast.makeText(
                            requireContext().applicationContext,
                            getString(R.string.error_setup_biometric_data),
                            Toast.LENGTH_SHORT
                        ).show()
                        enrollBiometricsLauncher.launch(
                            Intent(Settings.ACTION_SECURITY_SETTINGS)
                        )
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    loginViewModel.logInWithBiometric()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onLoginFailed()
                }
            }
        )
    }

    private fun setupAlternativeAuth() {
        val context = requireContext()
        val biometricAvailability =
            BiometricManager.from(context).canAuthenticate(biometricAuthenticators)

        when (biometricAvailability) {
            BIOMETRIC_SUCCESS -> binding.btnAlternativeLogin.setOnClickListener {
                showBiometricPrompt()
            }

            BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val enrollIntent =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(
                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                biometricAuthenticators
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

            BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(
                    context,
                    getString(R.string.error_no_suitable_hardware),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> Unit
        }
    }

    private fun showBiometricPrompt() {
        biometricPrompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setAllowedAuthenticators(biometricAuthenticators)
                .setTitle(getString(R.string.dialog_biometric_title))
                .setSubtitle(getString(R.string.dialog_biometric_subtitle))
                .apply {
                    if (!loginViewModel.uiState.value.isScreenLockLoginEnabled) {
                        setNegativeButtonText(getString(android.R.string.cancel))
                    }
                }
                .build()
        )
    }

    private fun onLoginFailed() {
        loginViewModel.increaseFailedAuthenticationAttempts()
        when (loginViewModel.remainingAuthenticationAttempts) {
            null -> ShortToast.showText(
                requireContext(),
                lifecycleScope,
                R.string.error_login_failed,
                durationMillis = 750
            )

            0 -> {
                loginViewModel.deleteAllData()
                Toast.makeText(
                    context,
                    R.string.toast_notes_deleted,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                biometricPrompt.cancelAuthentication()
                ShortToast.showText(
                    requireContext(),
                    lifecycleScope,
                    getString(
                        R.string.error_login_failed_with_attempts,
                        loginViewModel.remainingAuthenticationAttempts
                    ),
                    durationMillis = 1500
                )
            }
        }
    }
}
