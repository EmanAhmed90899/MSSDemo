package com.hemaya.mssdemo.view.identification;

import com.hemaya.mssdemo.R;

public enum Step {
    STEP_ONE(R.layout.step_one_layout,1),
    STEP_TWO(R.layout.step_two_layout,2),
    STEP_THREE(R.layout.step_three_layout,3),
    STEP_FOUR(R.layout.step_four_layout,4),
    STEP_FIVE(R.layout.step_five_layout,5),
    STEP_SIX(R.layout.step_six_layout,6);

    private final int layoutResId;
    public final int step;
    Step(int layoutResId, int step) {
        this.layoutResId = layoutResId;
        this.step = step;

    }

    public int getStep() {
        return step;
    }

    public int getLayoutResId() {
        return layoutResId;
    }

    public Step getNext() {
        int ordinal = this.ordinal();
        Step[] steps = Step.values();
        return (ordinal < steps.length - 1) ? steps[ordinal + 1] : null;
    }

    public Step getPrevious() {
        int ordinal = this.ordinal();
        Step[] steps = Step.values();
        return (ordinal > 0) ? steps[ordinal - 1] : Step.STEP_ONE;
    }

}
