package com.example.securenotes.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.securenotes.R
import com.example.securenotes.viewmodel.SplashViewModel
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private val splashViewModel by activityViewModels<SplashViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_splash, container, false)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                splashViewModel.getData()
                if (splashViewModel.isAuthenticated!! || !splashViewModel.isPasswordCreated!!) {
                    findNavController().navigate(R.id.action_splashFragment_to_graph_homeGraph)
                } else {
                    findNavController().navigate(R.id.action_splashFragment_to_graph_authenticationGraph)
                }
            }
        }

        return root
    }
}