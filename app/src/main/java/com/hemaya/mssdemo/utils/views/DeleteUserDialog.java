package com.hemaya.mssdemo.utils.views;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.utils.useCase.HomeUseCase;


public class DeleteUserDialog {
    Context context;
    Button deleteBtn,cancelBtn;
    private AlertDialog dialog;
    HomeUseCase homeUseCase;

    public DeleteUserDialog(Context context, HomeUseCase homeUseCase) {
        this.context = context;
        this.homeUseCase = homeUseCase;
        init();
    }

    private void init() {
        showDetailsDialog();
    }

    private void showDetailsDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.delete_user, null);

        deleteBtn = dialogView.findViewById(R.id.deleteUser);
        cancelBtn = dialogView.findViewById(R.id.cancel_button);
        dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        onclick();

        // Show the dialog
        dialog.show();
    }



    private void onclick() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeUseCase.deleteUser();
                dialog.dismiss();
            }
        });
    }
}

