package su.whs.call.views;

import su.whs.call.R;
import su.whs.call.models.UserInfo;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.net.ConnectionHandler.OnImageListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileHeaderView extends FrameLayout {
	private ImageView mBlur;
	private RoundedImageView mAvatar;
	private TextView mTitle;
	
	public ProfileHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.profile_header_view, this, true);
		mBlur = (ImageView)findViewById(R.id.imageBlur);
		mAvatar = (RoundedImageView)findViewById(R.id.imageAvatar);
		mTitle = (TextView)findViewById(R.id.textTitle);
	}
	
	public void setUserInfo(UserInfo ui) {
		ConnectionHandler.getInstance(getContext()).queryImage(ui.getAvatarURL(), new OnImageListener() {
			
			@Override
			public void onImageLoaded(final Bitmap bitmap) {
				post(new Runnable() {
					@Override
					public void run() {
						setAvatar(bitmap);
					}
				});
			}
		});
	}
	
	public void setTitle(String s) {
		mTitle.setText(s);
	}

	public void setAvatar(Bitmap bitmap) {
		if (bitmap==null)
			return;
		float ratio = bitmap.getWidth() != 0 ? bitmap.getHeight()/bitmap.getWidth() : 1; 
		Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 64, (int)(64*ratio), false);
		int dstW = mBlur.getWidth();
		int dstH = mBlur.getHeight();
		int tgtW = 0;
		int tgtH = 0;
		if (dstW>dstH) {
			ratio = dstW / scaled.getWidth();
			tgtW = dstW;
			tgtH = (int) (scaled.getHeight() * ratio);
		} else {
			ratio = dstH / scaled.getHeight();
			tgtH = dstH;
			tgtW = (int) (scaled.getWidth() * ratio);
		}
		
		scaled = Bitmap.createScaledBitmap(scaled, dstW, (int)(scaled.getHeight()*ratio), true);
		mBlur.setImageBitmap(scaled);
		mAvatar.setImageBitmap(bitmap);
	}
}
