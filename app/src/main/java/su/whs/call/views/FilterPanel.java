package su.whs.call.views;

import su.whs.call.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View.OnTouchListener;

import java.util.logging.Filter;

public class FilterPanel extends LinearLayout implements OnClickListener, OnTouchListener {
    private Button mLeftButton = null;
    private Button mRightButton = null;
    private FilterListener mFilterListener;

    public interface FilterListener {
        void onLeftButtonActivated();

        void onRightButtonActivated();
    }

    public FilterPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        LayoutInflater.from(context).inflate(R.layout.filter_panel, this);
        mLeftButton = (Button) findViewById(R.id.leftButton);
        mRightButton = (Button) findViewById(R.id.rightButton);
        mLeftButton.setOnTouchListener(this);
        mRightButton.setOnTouchListener(this);
        mLeftButton.setSelected(true);
        mLeftButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);
    }

    public void setFilterListener(FilterListener filterListener) {
        mFilterListener = filterListener;
    }

    public void removeFilterListener(FilterListener filterListener) {
        if (mFilterListener == filterListener) {
            mFilterListener = null;
        }
    }

    public void activateLeft() {
        mLeftButton.setTextColor(Color.WHITE);
        mRightButton.setTextColor(Color.BLACK);
        mLeftButton.setSelected(true);
        mRightButton.setSelected(false);
        if (mFilterListener != null) {
            mFilterListener.onLeftButtonActivated();
        }
    }

    public void activateRight() {
        mLeftButton.setTextColor(Color.BLACK);
        mRightButton.setTextColor(Color.WHITE);
        mRightButton.setSelected(true);
        mLeftButton.setSelected(false);
        if (mFilterListener != null) {
            mFilterListener.onRightButtonActivated();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {

        } else if (action == MotionEvent.ACTION_UP) {

        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftButton:


                activateLeft();
                break;
            case R.id.rightButton:

                activateRight();
                break;
        }

    }

}
