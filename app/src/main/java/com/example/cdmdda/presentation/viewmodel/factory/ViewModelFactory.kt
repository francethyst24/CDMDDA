package com.example.cdmdda.presentation.viewmodel.factory

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider

/**
 * Get a [ViewModel] in an [ComponentActivity].
 */
@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.viewModelBuilder(
    noinline viewModelInitializer: () -> VM
): Lazy<VM> {
    return ViewModelLazy(
        viewModelClass = VM::class,
        storeProducer = { viewModelStore },
        factoryProducer = {
            return@ViewModelLazy object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")// Casting T as ViewModel
                    return viewModelInitializer.invoke() as T
                }
            }
        }
    )
}

/**
 * Get a [ViewModel] in a [Fragment].
 */
@MainThread
inline fun <reified VM : ViewModel> Fragment.activityViewModelBuilder(
    noinline viewModelInitializer: FragmentActivity.() -> VM
): Lazy<VM> {
    return ViewModelLazy(
        viewModelClass = VM::class,
        storeProducer = { requireActivity().viewModelStore },
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")// Casting T as ViewModel
                    return requireActivity().viewModelInitializer() as T
                }
            }
        }
    )
}