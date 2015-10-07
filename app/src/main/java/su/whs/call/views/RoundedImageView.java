package su.whs.call.views;

/*public class RoundedImageView extends ImageView {
	private Paint mBackground;
	private Paint mBorder;
	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
        //setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mBackground = new Paint();
		mBackground.setColor(0xff363636);
		mBorder = new Paint();
		mBorder.setColor(0xfff18800);
		mBorder.setStrokeWidth(4);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		Path clipPath = new Path();
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        float cX = rect.width()/2;
        float cY = rect.height()/2;
        
        float r = cX - 8;
        clipPath.addCircle(cX, cY, r, Path.Direction.CW);
        canvas.drawCircle(cX, cY,r+2, mBorder);
        
        canvas.drawCircle(cX, cY, r, mBackground);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
	}

} */

/*public class RoundedImageView extends ImageView {

	public RoundedImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		Drawable drawable = getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		Bitmap b =  ((BitmapDrawable)drawable).getBitmap() ;
		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

		int w = getWidth(), h = getHeight();



		Bitmap roundBitmap =  getCroppedBitmap(bitmap, w);
		canvas.drawBitmap(roundBitmap, 0, 0, null);
	}

	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		Bitmap sbmp;
		bmp = getQuadBitmap(bmp);
		if(bmp.getWidth() != radius || bmp.getHeight() != radius)
			sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		else
			sbmp = bmp;
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
				sbmp.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffa19774;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
				sbmp.getWidth() / 2+0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);


		return output;
	}


	static Bitmap getQuadBitmap(Bitmap srcBmp) {
		Bitmap dstBmp = null;
		if (srcBmp.getWidth() >= srcBmp.getHeight()){

			dstBmp = Bitmap.createBitmap(
					srcBmp,
					srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
					0,
					srcBmp.getHeight(),
					srcBmp.getHeight()
			);

		}else{

			dstBmp = Bitmap.createBitmap(
					srcBmp,
					0,
					srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
					srcBmp.getWidth(),
					srcBmp.getWidth()
			);
		}

		return dstBmp;
	}

} */
