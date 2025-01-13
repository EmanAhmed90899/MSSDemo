package com.hemaya.mssdemo.utils.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hemaya.mssdemo.R;


public class GenericPopUp {
    Context mContext;
    String message;

    public GenericPopUp(Context context, String message) {
        mContext = context;
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
// Set width to 80% of the screen width
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 0.5); // 80% of screen width
            window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);


        }
        dialog.show();


        // Close popup on button click
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.equals(mContext.getString(R.string.errorName))) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    System.exit(0);
                }
                dialog.dismiss();
            }
        });
    }
}

