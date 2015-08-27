package su.whs.call;

import java.util.List;

import su.whs.call.net.AddressAutocomplete.AddressInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AddressInfoAdapter extends ArrayAdapter<AddressInfo> {
	private List<AddressInfo> mData;
	private LayoutInflater mInflater;
	public AddressInfoAdapter(Context context, int resource, List<AddressInfo> data) {
		super(context, resource);
		mData = data;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}
	
	@Override
	public AddressInfo getItem(int position) {
		return mData.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row==null) {
			row = mInflater.inflate(R.layout.address_info_list_item, parent, false);
		}
		TextView t = (TextView) row.findViewById(R.id.text1);
		AddressInfo ai = getItem(position);
		t.setText(ai.address());
		return row;
	}
	

}
