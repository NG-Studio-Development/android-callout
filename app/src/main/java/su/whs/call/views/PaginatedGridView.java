package su.whs.call.views;

import su.whs.call.R;
import android.annotation.SuppressLint;

import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnTouchListener;

public class PaginatedGridView extends ViewFlipper {
	private static final int MAX_ITEM_WIDTH = 100;
	private static final int MAX_ITEM_HEIGHT = 80;
	@SuppressWarnings("unused")
	private GestureDetector mGestureDetector = null;
	private ListAdapter mAdapter;
	private boolean mPopulated = false;
	private int mItemsPerRow = 0;
	private int mRowsPerPage = 0;
	private int mItemsPerPage = 0;
	private int mPages = 0;
	private float initialX;
	
	private OnItemClickListener mOnItemClickListener = null;
	
	@SuppressWarnings("deprecation")
	public PaginatedGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		
		setInAnimation(getContext(),android.R.anim.fade_in);
        setOutAnimation(getContext(),android.R.anim.fade_out);
        
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getActionMasked()) {
        case MotionEvent.ACTION_DOWN:
            initialX = touchevent.getX();
            break;
        case MotionEvent.ACTION_UP:
            float finalX = touchevent.getX();
            if (initialX > finalX) {
                if (getDisplayedChild() == 1)
                    break;
 
                /*TruitonFlipper.setInAnimation(this, R.anim.in_right);
                TruitonFlipper.setOutAnimation(this, R.anim.out_left);*/
 
                showNext();
            } else {
                if (getDisplayedChild() == 0)
                    break;
 
                /*TruitonFlipper.setInAnimation(this, R.anim.in_left);
                TruitonFlipper.setOutAnimation(this, R.anim.out_right);*/
 
                showPrevious();
            }
            break;
        }
        return false;
    }
	
	public synchronized void setAdapter(ListAdapter adapter, int overid) {
		if (mAdapter!=null && mAdapter.equals(adapter)) 
			return;
		mOverrideId = overid;
		mAdapter = adapter;
		resetPopulatedFlag();
		populateGrid();
	}
	
	public void setAdapter(ListAdapter adapter) {
		setAdapter(adapter,0);
	}
	
	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed)
			resetPopulatedFlag();
		populateGrid();
	}
	
	private synchronized void resetPopulatedFlag() {
		mPopulated = false;
	}
	
	private synchronized void populateGrid() {
		
		if (mAdapter==null||mPopulated) 
			return;
		mPopulated = true;
		int maxX = 110;
		int maxY = 240;
		/*
		View[] views = new View[mAdapter.getCount()];
		for (int i=0; i<views.length; i++) {
			views[i] = mAdapter.getView(i, null, null);
			addView(views[i]);
			views[i].measure(MAX_ITEM_WIDTH, MAX_ITEM_HEIGHT);
			int vW = views[i].getMeasuredWidth();
			int vH = views[i].getMeasuredHeight();
			removeView(views[i]);
			maxX = vW > maxX ? vW : maxX;
			maxY = vH > maxY ? vH : maxY;
		} */
		if (maxX > 0 && maxY > 0) {
			mItemsPerRow = 3; // getWidth() / maxX;
			mRowsPerPage = getHeight() / maxY;
			mItemsPerPage = mItemsPerRow * mRowsPerPage;
			mPages = 1 + mAdapter.getCount() / mItemsPerPage;
		}
	 	stopFlipping();
	 	
		// removeAllViews();
		createViews();
		onFinishTemporaryDetach();
		onFinishInflate();
		invalidate();
		// showNext();
	}
	
	private int mOverrideId = 0;
	
	protected int getOverridenOnClickId() {
		return mOverrideId;
	}
	
	@SuppressLint("NewApi")
	private void createViews() {
		int length = mAdapter.getCount();
		int index = 0;
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		GridLayout.LayoutParams wrapLP = new GridLayout.LayoutParams();
		wrapLP.setGravity(Gravity.CENTER);
		int id = getOverridenOnClickId();
		for (int i=0; i<mPages; i++) {
			GridLayout gl = new GridLayout(getContext());
			gl.setBackgroundResource(R.drawable.background);
			gl.setAlignmentMode(GridLayout.ALIGN_MARGINS);
			gl.setLayoutParams(lp);
			gl.setBackgroundColor(0xffffff);
			gl.setColumnCount(mItemsPerRow);
			gl.setRowCount(mRowsPerPage);
			for (int j=0; j<mItemsPerPage && index < length; j++) {
				View v = mAdapter.getView(index, null, gl);
				if (id>0) {
					attachOnClickListener(v,id, index);
				} else {
					attachOnClickListener(v, index);
				}
				index++;
				gl.addView(v);
				
			}
			addView(gl,lp);
			gl.setVisibility(View.VISIBLE);
		}
	}
	
	private void attachOnClickListener(View v, int id, int index) {
		View f = v.findViewById(id);
		if (f!=null)
			attachOnClickListener(f, index);
	}
	
	private void attachOnClickListener(final View v, final int index) {
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				callOnItemClick(v, index, index);
			}
		});
	}
	
	protected synchronized void callOnItemClick(View view, int position, long id) {
		if (mOnItemClickListener!=null)
			mOnItemClickListener.onItemClick(null, view, position, id);
	}
	
	public synchronized void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
	}
}
