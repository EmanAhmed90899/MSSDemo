package com.hemaya.mssdemo.utils.views;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;

import com.hemaya.mssdemo.R;


public class RequestPermissionDialog {
    private Button grantPermissionButton;
    private Button denyPermissionButton;
    private Context context;
    private AlertDialog alertDialog;
    public static final int ACCESS_CAMERA_REQUEST_CODE = 1;
    private AlertDialog.Builder builder;
    private View dialogView;

    public RequestPermissionDialog(Context context) {
        this.context = context;
        init();
    }

    public void init() {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        dialogView = inflater.inflate(R.layout.permission_dialog_layout, null);

        builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
    }

    public void create() {
        alertDialog = builder.create();

        grantPermissionButton = dialogView.findViewById(R.id.grant_permission_button);
        denyPermissionButton = dialogView.findViewById(R.id.cancel_permission_button);
        grantPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA) && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_PHONE_STATE) && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.RECEIVE_SMS) && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_SMS)) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, ACCESS_CAMERA_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, ACCESS_CAMERA_REQUEST_CODE);
                }
                alertDialog.dismiss();
            }
        });

        denyPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
//
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                System.exit(0);
            }

        });
        show();
    }

    public void show() {
        alertDialog.show();
    }

}
