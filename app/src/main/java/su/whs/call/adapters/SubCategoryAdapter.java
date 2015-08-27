package su.whs.call.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import su.whs.call.R;
import su.whs.call.models.SubCategory;

public class SubCategoryAdapter extends ArrayAdapter<SubCategory> {
	private LayoutInflater mInflater;
	public SubCategoryAdapter(Context context,List<SubCategory> objects) {
		super(context, R.layout.subcategory_list_item, R.id.text1, objects);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row==null) {
			row = mInflater.inflate(R.layout.subcategory_list_item, null);
		}
		
		TextView text = (TextView) row.findViewById(R.id.text1);
		text.setText(getItem(position).getName());
		return row;
	}
}
