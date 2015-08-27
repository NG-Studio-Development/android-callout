package su.whs.call.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class SpecialGridView extends LinearLayout {
	private BaseAdapter mAdapter;
	private List<LinearLayout> mRows = new ArrayList<LinearLayout>();
	public SpecialGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context,attrs,0);
	}
	
	private void init(Context context, AttributeSet attrs, int defStyle) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		setOrientation(LinearLayout.VERTICAL);
	}
	
	public void setAdapter(BaseAdapter adapter) {
		mAdapter = adapter;
		if (adapter==null) {
			removeAllViews();
		}
		
		mAdapter.registerDataSetObserver(new DataSetObserver() {
		});
	}
	
	private View makeView(int position, View convertView, LinearLayout parent) {
		View v = mAdapter.getView(position, convertView, parent);
		return v;
	}
	
	private LinearLayout buildRow(int columns, LinearLayout oldView) {
		LinearLayout l = new LinearLayout(getContext());
		l.setOrientation(LinearLayout.HORIZONTAL);
		
		return l;
	}
	
	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		super.onMeasure(widthSpec, heightSpec);
	}
}
