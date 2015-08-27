package su.whs.call.views;

import su.whs.call.R;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.view.View.OnTouchListener;

public class MapControlLayer extends RelativeLayout {
    private boolean mOnDown = false;
    private ImageView mMarker;
    private ImageView mMapLocation;
    private float mX = 0;
    private float mY = 0;
    private boolean mMarkerVisible = false;

    public interface OnMapClickListener {
        void onClick(float X, float Y);
    }

    private OnMapClickListener mOnClickListener = null;

    public MapControlLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        LayoutInflater.from(context).inflate(R.layout.map_controls, this, true);
        mMarker = (ImageView) findViewById(R.id.mapMarker);
        mMarker.setVisibility(View.GONE);
        mMapLocation = (ImageView) findViewById(R.id.mapLocation);

        mMapLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickListener.onClick(55.749792f, 37.632495f);
            }
        });
    }

    public void setMarkerPosition(float X, float Y) {

        final RelativeLayout.LayoutParams lp = (LayoutParams) mMarker.getLayoutParams();
        lp.setMargins((int) X - mMarker.getWidth() / 2, (int) Y - mMarker.getHeight(), 0, 0);
        mMarker.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMarker.setLayoutParams(lp);
                mMarker.setVisibility(View.VISIBLE);
            }
        }, 50);

        mMarkerVisible = true;
    }

    public void setOnMapClickListener(OnMapClickListener l) {
        mOnClickListener = l;
    }

    public boolean isMarkerVisible() {
        return mMarkerVisible;
    }


}
