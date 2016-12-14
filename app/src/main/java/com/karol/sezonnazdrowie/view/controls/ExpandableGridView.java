package com.karol.sezonnazdrowie.view.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.GridView;

public class ExpandableGridView extends GridView {

    public ExpandableGridView(Context context) {
        super(context);
        setupProperties();
    }

    public ExpandableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupProperties();
    }

    public ExpandableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupProperties();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }

    private void setupProperties() {
        setNumColumns(2);
        setGravity(Gravity.CENTER);
        setFocusable(false);
    }
}