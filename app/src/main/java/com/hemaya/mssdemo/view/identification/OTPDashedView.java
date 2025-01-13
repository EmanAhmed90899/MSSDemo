package com.hemaya.mssdemo.view.identification;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class OTPDashedView extends View {
    private static final int MAX_LENGTH = 6; // Adjust for the number of OTP digits
    private String otpInput = ""; // Stores OTP input as a string
    public Paint dashPaint, textPaint;
    private float digitWidth ;  // Width for each digit area, adjust as needed
    private float digitGap ;  // Gap between each digit

    public OTPDashedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Initialize dash paint for the underline
        dashPaint = new Paint();
        dashPaint.setColor(Color.GRAY);
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setStrokeWidth(5);
//        dashPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

        // Initialize text paint for digits
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(45);  // Adjust text size as needed
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float startX = getWidth() / 2 - ((digitWidth + digitGap) * MAX_LENGTH / 2);

        for (int i = 0; i < MAX_LENGTH; i++) {
            float x = startX + i * (digitWidth + digitGap);
            float y = getHeight() / 2;


            if (otpInput.isEmpty()) {
                // Draw the dashed underline for each digit slot
                canvas.drawLine(x, y, x + digitWidth, y, dashPaint);

            } else {

                // Draw each digit if available in otpInput
                if (i < otpInput.length()) {
                    canvas.drawText(
                            String.valueOf(otpInput.charAt(i)),
                            x + digitWidth / 2,
                            y + 20,
                            textPaint
                    );
                }
            }
        }
    }

    /**
     * Set the OTP programmatically and update the view.
     * This will auto-fill the OTP in the view.
     *
     * @param otp The OTP string to display (should be of MAX_LENGTH).
     */
    public void setOtp(String otp) {
        if (otp.length() <= MAX_LENGTH) {
            otpInput = otp;
            invalidate(); // Redraw the view with the new OTP
        } else {
            throw new IllegalArgumentException("OTP length exceeds the maximum allowed length");
        }
    }

    /**
     * Get the currently displayed OTP.
     *
     * @return The current OTP displayed in the view.
     */
    public String getOtp() {
        return otpInput;
    }

    public void clearOtp() {
        otpInput = "";
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Calculate total available width (screen width minus padding)
        float  totalWidth = w - getPaddingLeft() - getPaddingRight();

        // Dynamic digit width and gap calculation
        digitGap = totalWidth * 0.02f; // 2% of total width for gap (adjustable)
        digitWidth = (totalWidth - (digitGap * (MAX_LENGTH - 1))) / MAX_LENGTH;
    }
}
