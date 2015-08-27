package su.whs.call.views;

import su.whs.call.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IconButton extends RelativeLayout {
	private ImageView mImage;
	private TextView mText;

	public IconButton(Context context) {
		super(context);
		init(context, null, 0);
	}

	public IconButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public IconButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		LayoutInflater.from(context).inflate(R.layout.icon_button, this, true);
		mImage = (ImageView) findViewById(R.id.buttonIcon);
	
		
		mText = (TextView) findViewById(R.id.buttonText);

		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.whsIconButton);
			final int N = a.getIndexCount();

			for (int i = 0; i < N; ++i) {
				int attr = a.getIndex(i);
				switch (attr) {
				case R.styleable.whsIconButton_drawable:
					setDrawable(a.getDrawable(attr));
					break;
				case R.styleable.whsIconButton_text:
					setText(a.getString(attr));
					break;
				case R.styleable.whsIconButton_circleBg:
					mImage.setBackgroundResource(a.getResourceId(attr, R.drawable.circle_icon_bg));
					break;
				}
			}
			a.recycle();
		}
		setClickable(true);

	}

    public ImageView getImageView()
    {
        return mImage;
    }

	public void setDrawable(Drawable d) {
		mImage.setImageDrawable(d);
	}
	
	public void setBitmap(final Bitmap bm) {
		this.post(new Runnable() {
			@Override
			public void run() {
				mImage.setImageBitmap(bm);
			}
		});
		
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		mImage.setOnClickListener(l);
	}

	public void setText(String s) {
		mText.setText(s);
	}
}
