package su.whs.call.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.w3c.dom.Text;

import java.util.List;

import su.whs.call.Constants;
import su.whs.call.R;
import su.whs.call.helper.Screen;
import su.whs.call.models.Category;
import su.whs.call.models.RegisteredMonth;
import su.whs.call.models.RegisteredYear;
import su.whs.call.views.IconButton;

public class CallsAdapter extends BaseAdapter {

    private static final double ICON_SCALE_FACTOR = 0.4;

    private LayoutInflater mInflater;
	private Context mContext;
    private RegisteredYear mYear;
    private AdapterView.OnItemClickListener onItemClickListener = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options  = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        onItemClickListener = listener;
    }

	public CallsAdapter(Context context, RegisteredYear year) {
		super();
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
        mYear = year;
        year.complete();
	}

    @Override
	public int getCount() {
		return mYear.getMonths().size();
	}

	@Override
	public RegisteredMonth getItem(int position) {
		return mYear.getMonths().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.calls_grid_item, null);
            holder = new Holder();
            holder.callsCount = (TextView) convertView.findViewById(R.id.callsCount);
            holder.monthName = (TextView) convertView.findViewById(R.id.monthName);
            holder.buttonIcon = (ImageView) convertView.findViewById(R.id.buttonIcon);
            int h = (Screen.getDisplayHeight(mContext) / 9);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(h, h);
            params.addRule(RelativeLayout.CENTER_IN_PARENT );
            holder.buttonIcon.setLayoutParams(params);

          //  holder.callsCount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 5f);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

		RegisteredMonth month = (RegisteredMonth) getItem(position);
		holder.callsCount.setText(String.format("%d", month.getTotalCalls()));
        holder.monthName.setText(month.getName());


		return convertView;
	}

    private class Holder {
        public ImageView buttonIcon;
        public TextView callsCount;
        public TextView monthName;
    }

}
