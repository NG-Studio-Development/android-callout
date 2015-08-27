package su.whs.call.utils;

import android.view.View;

public class BackStack {
	private View[] mViews;
	private int mActiveIndex;
	public BackStack(View[] views) {
		mViews = views;
		mActiveIndex = 0;
	}
	
	public View getActiveView() {
		return mViews[mActiveIndex];
	}
	
	public void show(int index) {
		for(int i=0; i<mViews.length; i++) 
			if (i!=index) { 
				mViews[i].setVisibility(View.GONE); 
			} else { 
				mViews[i].setVisibility(View.VISIBLE); 
			}
	}
	
	public void backward() {
		if (hasPrev()) 
			mActiveIndex++;
		show(mActiveIndex);
	}
	
	public void forward() {
		if (hasNext()) 
			mActiveIndex++;
		show(mActiveIndex);
	}
	
	public boolean hasNext() {
		return mActiveIndex < (mViews.length-1);
	}
	
	public boolean hasPrev() {
		return mActiveIndex > 0;
	}
	
}
