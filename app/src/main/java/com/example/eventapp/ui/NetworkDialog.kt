package com.example.eventapp.ui

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import com.example.eventapp.R

class NetworkDialog(private val context: Context) {
    private var alertDialog: AlertDialog? = null

    fun showNoInternetDialog(retryAction: () -> Unit) {

        if (alertDialog?.isShowing == true) return  // Prevent showing multiple dialogs

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_no_internet, null)
        val retryButton = dialogView.findViewById<Button>(R.id.btn_retry)
        val closeButton = dialogView.findViewById<ImageView>(R.id.btn_close)

        alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        alertDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        alertDialog?.show()

        retryButton.setOnClickListener {
            retryAction.invoke()
        }

        closeButton.setOnClickListener {
            alertDialog?.dismiss()
        }
    }

    fun dismissDialog() {
        alertDialog?.dismiss()
    }

}