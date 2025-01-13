package com.hemaya.mssdemo.utils.views;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.model.UserModel.User;
import com.hemaya.mssdemo.model.UserModel.UserViewModel;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.utils.useCase.HomeUseCase;

public class DialogRename {
    Context context;
    EditText userNameEdit;
    Button saveRename;
    HomeUseCase homeUseCase;
    TextView renameTitle;
    User user;
    private UserViewModel userViewModel;
    private AlertDialog dialog;
    String name;

    public DialogRename(Context context, HomeUseCase homeUseCase, UserViewModel userViewModel) {
        this.context = context;
        this.homeUseCase = homeUseCase;
        this.userViewModel = userViewModel;
        init();
    }

    private void init() {
        user = homeUseCase.getUser();
        showDetailsDialog();
    }

    private void showDetailsDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.user_details, null);

        renameTitle = dialogView.findViewById(R.id.renameTitle);
        userNameEdit = dialogView.findViewById(R.id.edit_text_name);
        saveRename = dialogView.findViewById(R.id.saveRename);


        dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();
        name = user.getName();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        setData();
        applyGravityToAllEditTexts(userNameEdit);

        // Show the dialog
        dialog.show();
    }

    private void setData() {

        renameTitle.setText(context.getString(R.string.renameTitle) + " " + user.getName());
        userNameEdit.setText(user.getName());
        onclick();

    }

    private void onclick() {
        saveRename.setOnClickListener(v -> {
            homeUseCase.updateUserName(userNameEdit.getText().toString(), user.getId());
            user.setName(userNameEdit.getText().toString());
            userViewModel.setUser(user);
            dialog.dismiss();
            new GenericPopUp(context, context.getResources().getString(R.string.renameSuccess) + " " + name + " " + context.getResources().getString(R.string.to) + " " + user.getName() + " " + context.getResources().getString(R.string.successfully));
        });
    }

    private void applyGravityToAllEditTexts(EditText view) {

        if (new SharedPreferenceStorage(context).equals("ar")) {
            Log.d("** edittext", "applyGravityToAllEditTexts: ");
            view.setGravity(Gravity.RIGHT);
        } else {
            view.setGravity(Gravity.LEFT);
        }
    }
}
