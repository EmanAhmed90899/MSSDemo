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
    private double timerMinutes;
    private String timeMessage;


    public CountTimer(TextView timerTextView, Context context, double timerMinutes,String timeMessage) {
        this.timerTextView = timerTextView;
        this.timeMessage = timeMessage;
        this.context = context;
        this.timerMinutes = timerMinutes;
        this.timerListener = (TimerListener) context;

    }

    public void startOTPTimer() {
        countDownTimer = new CountDownTimer((long) (timerMinutes*60000), 1000) { // 1 minute in milliseconds
            public void onTick(long millisUntilFinished) {
                // Update the timer text each second
                long secondsRemaining = millisUntilFinished / 1000;
                String timeFormatted = String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60);
                timerTextView.setText(timeMessage+" "+timeFormatted);
                if (secondsRemaining == 67) {
                    timerListener.onRegeneratedEnabled();
                }
            }

            public void onFinish() {
                // Timer finished, handle expiration (e.g., enable resend button)
                timerListener.onTimerFinish();

            }
        }.start();
    }

    public void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public interface TimerListener {
        void onTimerFinish();
        void onRegeneratedEnabled();
    }
}
