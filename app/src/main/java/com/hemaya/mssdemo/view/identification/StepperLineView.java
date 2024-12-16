package com.hemaya.mssdemo.view.identification;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.model.identification.StepperModel;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;

import java.util.ArrayList;
import java.util.List;

public class StepperLineView extends View {

    private int steps = 6; // Total number of steps
    private int currentStep = 1; // Current step in the progress

    private Paint activeLinePaint;
    private Paint inactiveLinePaint;
    private TextView titleStepper, contentStepper;
    private List<StepperModel> stepperModelList;

    public StepperLineView(Context context) {
        super(context);
        init(context);
    }

    public StepperLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StepperLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setTextViews(TextView titleStepper, TextView contentStepper) {
        this.titleStepper = titleStepper;
        this.contentStepper = contentStepper;
    }

    private void init(Context context) {
        // Paint for inactive lines (gray)
        inactiveLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        inactiveLinePaint.setColor(ContextCompat.getColor(context, R.color.whiteGray));
        inactiveLinePaint.setStrokeWidth(10f);

        // Paint for active lines (colored)
        activeLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        activeLinePaint.setColor(ContextCompat.getColor(context, R.color.appColor));
        activeLinePaint.setStrokeWidth(10f);

        stepperModelList = new ArrayList<>();
        stepperModelList.add(new StepperModel(ContextCompat.getString(context, R.string.terms_and_conditions), ContextCompat.getString(context, R.string.terms_and_conditions_text)));
        stepperModelList.add(new StepperModel(ContextCompat.getString(context, R.string.identity_verification), ContextCompat.getString(context, R.string.identity_verification_text)));
        stepperModelList.add(new StepperModel(ContextCompat.getString(context, R.string.mobile_number_verification), ContextCompat.getString(context, R.string.mobile_number_verification_text)));
        stepperModelList.add(new StepperModel(ContextCompat.getString(context, R.string.otp), ContextCompat.getString(context, R.string.otp_text)));
        stepperModelList.add(new StepperModel(ContextCompat.getString(context, R.string.soft_token_activation), ContextCompat.getString(context, R.string.soft_token_activation_text)));
        stepperModelList.add(new StepperModel(ContextCompat.getString(context, R.string.set_pin), ContextCompat.getString(context, R.string.set_pin_text)));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float segmentSpacing = (float) getWidth() / steps;
        float centerY = getHeight() / 2f;

        if (new SharedPreferenceStorage(getContext()).getLanguage().equals("ar")) {
            for (int i = 0; i < steps; i++) {
                // Calculate positions starting from the right
                float endX = canvas.getWidth() - (i * segmentSpacing);  // End position for this segment
                float startX = endX - segmentSpacing;             // Start position for this segment

                // Draw active line for completed steps, inactive for remaining steps
                if (i < currentStep) {
                    canvas.drawLine(startX, centerY, endX, centerY, activeLinePaint);
                } else {
                    canvas.drawLine(startX, centerY, endX, centerY, inactiveLinePaint);
                }
            }

        } else {
            // Draw each step line segment
            for (int i = 0; i < steps; i++) {
                float startX = i * segmentSpacing;
                float endX = startX + segmentSpacing;

                // Draw active line for completed steps, inactive for remaining steps
                if (i < currentStep) {
                    canvas.drawLine(startX, centerY, endX, centerY, activeLinePaint);
                } else {
                    canvas.drawLine(startX, centerY, endX, centerY, inactiveLinePaint);
                }
            }
        }

    }

    // Public method to update the current step
    public void setCurrentStep(int step) {
        currentStep = Math.max(1, Math.min(step, steps)); // Ensure step is within range
        invalidate(); // Redraw the view
        titleStepper.setText(stepperModelList.get(currentStep - 1).getTitle());
        contentStepper.setText(stepperModelList.get(currentStep - 1).getContent());
    }

    // Optionally, add a method to set the total number of steps
    public void setSteps(int steps) {
        this.steps = steps;
        invalidate(); // Redraw the view
    }


}

