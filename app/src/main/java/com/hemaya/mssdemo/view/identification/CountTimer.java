package com.hemaya.mssdemo.view.identification;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.hemaya.mssdemo.R;

public class CountTimer {
   public CountDownTimer countDownTimer;
    TextView timerTextView;
    private Context context;
    private TimerListener timerListener;


    public CountTimer(TextView timerTextView, Context context) {
        this.timerTextView = timerTextView;
        this.context = context;
        this.timerListener = (TimerListener) context;

    }

    public void startOTPTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) { // 1 minute in milliseconds
            public void onTick(long millisUntilFinished) {
                // Update the timer text each second
                long secondsRemaining = millisUntilFinished / 1000;
                String timeFormatted = String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60);
                if (secondsRemaining == 50) {
                    timerListener.onSetOtp();
                }
                timerTextView.setText(context.getResources().getString(R.string.remaining)+" "+timeFormatted);
            }

            public void onFinish() {
                // Timer finished, handle expiration (e.g., enable resend button)
                timerListener.onTimerFinish();

            }
        }.start();
    }

    public interface TimerListener {
        void onSetOtp();
        void onTimerFinish();
    }
}
