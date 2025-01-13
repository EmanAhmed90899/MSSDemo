package com.hemaya.mssdemo.utils.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;

import com.hemaya.mssdemo.R;

public class SettingPermissionDialog {
    Context context;

    public SettingPermissionDialog(Context context) {
        this.context = context;
    }

    public void show() {
        AlertDialog dialogAlert = new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.permission_required))
                .setMessage(context.getResources().getString(R.string.permission_required_message))
                .setPositiveButton(context.getResources().getString(R.string.gotoSettings), (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                })
                .setNegativeButton(context.getResources().getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .create();
        dialogAlert.show();

        dialogAlert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        Button positiveButton = dialogAlert.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setBackgroundResource(R.drawable.button_drawable);
        positiveButton.setTextSize(10);
        positiveButton.setTextColor(context.getResources().getColor(R.color.white));
        setMargin(positiveButton);

        Button negativeButton = dialogAlert.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setBackgroundResource(R.drawable.button_drawable);
        negativeButton.setTextSize(10);
        negativeButton.setTextColor(context.getResources().getColor(R.color.white));
        setMargin(negativeButton);
    }

    private void setMargin(Button button) {
        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
        button.setPadding(10, 0, 10, 0);
        // Ensure the layout parameters are of the correct type
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layoutParams;

            // Set the desired margins in pixels
            int leftMargin = convertDpToPx(16); // Replace with your desired value
            int rightMargin = convertDpToPx(16); // Replace with your desired value
            marginParams.setMargins(leftMargin, marginParams.topMargin, rightMargin, marginParams.bottomMargin);

            // Apply the updated layout parameters to the button
            button.setLayoutParams(marginParams);
        }
    }

    private int convertDpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

}
