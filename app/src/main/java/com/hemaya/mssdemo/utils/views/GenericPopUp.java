package com.hemaya.mssdemo.utils.views;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hemaya.mssdemo.R;


public class GenericPopUp {
    Context mContext;
    String title;
    String message;

    public GenericPopUp(Context context, String title, String message) {
        mContext = context;
        this.title = title;
        this.message = message;
    }

    public void showCustomPopup() {
        // Inflate the custom layout
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(popupView);

        // Set up the popup elements
        TextView messageTxt = popupView.findViewById(R.id.popup_message);
        Button button = popupView.findViewById(R.id.popup_button);

        // Customize the elements
        messageTxt.setText(message);

        // Create and show the popup
        AlertDialog dialog = builder.create();
        dialog.show();


        // Close popup on button click
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (message.equals(mContext.getString(R.string.errorName))) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    System.exit(0);
                }
            }
        });
    }
}

