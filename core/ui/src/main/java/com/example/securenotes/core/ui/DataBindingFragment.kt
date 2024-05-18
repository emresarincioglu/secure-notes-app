package com.example.securenotes.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * Base class for fragments that use data binding.
 *
 * Use [inflateBinding] in [onCreateView] method to inflate data binding.
 * Access data binding via [binding] field.
 *
 * Example usage:
 * ```
 * override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
 *     inflateBinding(R.layout.fragment_main, inflater, container, false)
 *     binding.viewModel = someViewModel
 *     return binding.root
 * }
 * ```
 *
 * @param T Data binding class.
 */
open class DataBindingFragment<T : ViewDataBinding> : Fragment() {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * @see DataBindingUtil.inflate
     */
    protected fun inflateBinding(
        @LayoutRes layoutId: Int,
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ) {
        _binding = DataBindingUtil.inflate<T>(inflater, layoutId, container, attachToRoot).apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }
}