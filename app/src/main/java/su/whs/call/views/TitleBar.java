package su.whs.call.views;

import su.whs.call.R;
import su.whs.call.fragments.NavigationBarClient;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class TitleBar extends LinearLayout implements OnClickListener {

    public interface TitleBarActivity {
        TitleBar getTitleBar();

        void fragmentReady(String tag);
    }

    private ImageButton mHomeButton;
    private TextView mTitle;
    private ImageButton mInfoButton;

    public Button btnReview;

    public TextView getTitle() {
        return mTitle;
    }

    public ImageButton getHomeButton() {
        return mHomeButton;
    }

    public ImageButton getInfoButton() {
        return mInfoButton;
    }

    private NavigationBarClient mListener;
    private FrameLayout mViewContainer;

    public TitleBar(Context context) {
        super(context);
        init(context, null, 0);
    }

    public synchronized void setListener(NavigationBarClient l) {
        mListener = l;
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        LayoutInflater.from(context).inflate(R.layout.title_bar, this, true);

        mHomeButton = (ImageButton) findViewById(R.id.buttonHome);
        mHomeButton.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.textTitle);
        mInfoButton = (ImageButton) findViewById(R.id.buttonInfo);
        mInfoButton.setOnClickListener(this);

        btnReview = (Button) findViewById(R.id.buttonReview);
        btnReview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onInfoIconClick();
            }
        });

        mViewContainer = (FrameLayout) findViewById(R.id.viewContainer);
    }


    public void setHomeIconResource(int id) {
        mHomeButton.setImageResource(id);
    }

    public void setInfoIconResource(int id) {
        mInfoButton.setImageResource(id);
    }

    public void setTile(String title) {
        mTitle.setText(title);
    }

    public void setCustomViewVisibility(int visibility) {
        mViewContainer.setVisibility(visibility);
    }

    public void setCustomView(View v) {
        if (v == null)
            return;
        mViewContainer.removeAllViews();
        mViewContainer.addView(v);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonHome:
                if (mListener != null && !mListener.onHomeIconClick()) {
                    navigateBack();
                }
                break;
            case R.id.buttonInfo:
                if (mListener != null)
                    mListener.onInfoIconClick();
                break;
        }
    }

    public boolean navigateBack() {
        return mListener.onHomeIconClick();
    }

    public void setHomeIconClickListener(OnClickListener l) {
        mHomeButton.setOnClickListener(l);
    }

    public void setInfoIconClickListener(OnClickListener l) {
        mInfoButton.setOnClickListener(l);
    }

    public int getBarHeight() {
        int additionalHeight = 0;
        if (mViewContainer != null && mViewContainer.getVisibility() == View.VISIBLE)
            additionalHeight += mViewContainer.getHeight();
        return super.getHeight() + additionalHeight;
    }
}
