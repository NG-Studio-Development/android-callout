package su.whs.call.views;

import su.whs.call.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RateStarsView extends View {
	private double mStars = 0;
	private static Bitmap mEmpty = null;
	private static Bitmap mSolid;
    private static Bitmap mPartSolid;
    private static Bitmap mPart1;
    private static Bitmap mPart2;
    private static Bitmap mPart3;
    private static Bitmap mPart4;
    private static Bitmap mPart5;
    private static Bitmap mPart6;
    private static Bitmap mPart7;
    private static Bitmap mPart8;
    private static Bitmap mPart9;
	private static Paint mBitmapPaint;
	private static int mStep;
	
	public RateStarsView(Context context, AttributeSet attrs) {
		super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		loadBitmaps(context);
	}
	
	private static synchronized void loadBitmaps(Context context) {
		if (mEmpty!=null) return;
		mEmpty = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_star_empty);
		mSolid = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_star_solid);
        mPartSolid = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_star_part_empty);
        mPart1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.part_1);
        mPart2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.part_2);
        mPart3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.part_3);
        mPart4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.part_4);
        mPart5 = BitmapFactory.decodeResource(context.getResources(), R.drawable.part_5);
        mPart6 = BitmapFactory.decodeResource(context.getResources(), R.drawable.part_6);
        mPart7 = BitmapFactory.decodeResource(context.getResources(), R.drawable.part_7);
        mPart8 = BitmapFactory.decodeResource(context.getResources(), R.drawable.part_8);
        mPart9 = BitmapFactory.decodeResource(context.getResources(), R.drawable.part_9);
		mStep = mSolid.getWidth() + mSolid.getWidth() / 3;
		mBitmapPaint = new Paint();
	}

	@Override
	public void onDraw(Canvas canvas) {
		for (int i=0; i < 5; i++) {

            if(i - mStars + 1 <= 0) {
                canvas.drawBitmap(mSolid, i * mStep, 0, mBitmapPaint);
            } else if((i - mStars + 1) > 0 && (i - mStars + 1) < 1){

                int c = (int) mStars;
                double part = mStars - c;

                if(part > 0 && part <= 0.1) {
                    canvas.drawBitmap(mPart1, i * mStep, 0, mBitmapPaint);
                } else if (part > 0.1 && part <= 0.2) {
                    canvas.drawBitmap(mPart2, i * mStep, 0, mBitmapPaint);
                } else if (part > 0.2 && part <= 0.3) {
                    canvas.drawBitmap(mPart3, i * mStep, 0, mBitmapPaint);
                } else if (part > 0.3 && part <= 0.4) {
                    canvas.drawBitmap(mPart4, i * mStep, 0, mBitmapPaint);
                } else if (part > 0.4 && part <= 0.5) {
                    canvas.drawBitmap(mPart5, i * mStep, 0, mBitmapPaint);
                } else if (part > 0.5 && part <= 0.6) {
                    canvas.drawBitmap(mPart6, i * mStep, 0, mBitmapPaint);
                } else if (part > 0.6 && part <= 0.7) {
                    canvas.drawBitmap(mPart7, i * mStep, 0, mBitmapPaint);
                } else if (part > 0.7 && part <= 0.8) {
                    canvas.drawBitmap(mPart8, i * mStep, 0, mBitmapPaint);
                } else if (part > 0.8 && part <= 0.9) {
                    canvas.drawBitmap(mPart9, i * mStep, 0, mBitmapPaint);
                } else {
                    canvas.drawBitmap(mSolid, i * mStep, 0, mBitmapPaint);
                }

            } else {
                canvas.drawBitmap(mEmpty, i * mStep, 0, mBitmapPaint);
            }

			//canvas.drawBitmap(i < mStars ? mSolid : mEmpty, i * mStep, 0, mBitmapPaint);
		}
		setDrawingCacheEnabled(true);
	}
	
	public void setStars(double stars) {

		mStars = stars;
		setDrawingCacheEnabled(false);
		invalidate();
	}
	
	@Override
	protected void onMeasure(int w, int h) {
		super.onMeasure(w, h);
		setMeasuredDimension(mStep * 5, mSolid.getHeight());
	}
}
