package com.karol.sezonnazdrowie.view.controls;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

import com.karol.sezonnazdrowie.R;

public class ExpandableListView extends ListView {

    public ExpandableListView(Context context) {
        super(context);
        setupProperties();
    }

    public ExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupProperties();
    }

    public ExpandableListView(Context context, AttributeSet attrs, int defStyle) {
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
        setDivider(ContextCompat.getDrawable(getContext(), R.drawable.list_view_divider_light));
        setDividerHeight((int) getResources().getDisplayMetrics().density);
        setVerticalScrollBarEnabled(false);
        setFocusable(false);
    }
}