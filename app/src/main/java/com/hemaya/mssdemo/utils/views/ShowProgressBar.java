package com.hemaya.mssdemo.utils.views;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.hemaya.mssdemo.R;


public class ShowProgressBar {
    private Dialog loadingDialog;
    private Context context;

    public ShowProgressBar(Context context) {
        this.context = context;
    }
    public void showLoadingDialog() {
        // Initialize the dialog
        loadingDialog = new Dialog(context);

        // Inflate the custom layout
        View view = LayoutInflater.from(context).inflate(R.layout.progress_bar, null);

        // Set the content view for the dialog
        loadingDialog.setContentView(view);

        // Optional: disable outside touch and back button dismiss
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        // Show the dialog
        loadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

}
