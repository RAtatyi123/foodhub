package com.example.myapplication.activities

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    // Добавьте эти функции в BaseFragment
    open fun showProgressDialog(message: String) {
        (requireActivity() as? BaseActivity)?.showProgressdialog(message)
    }

    open fun hideProgressDialog() {
        (requireActivity() as? BaseActivity)?.hideProgressdialog()
    }

    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        (requireActivity() as? BaseActivity)?.showErrorSnackBar(message, errorMessage)
    }

    fun doubleBackToExit() {
        (requireActivity() as? BaseActivity)?.doubleBackToExit()
    }
}