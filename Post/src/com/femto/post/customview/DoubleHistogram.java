package com.femto.post.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;

public class DoubleHistogram extends View {

	private int mAscent;

	private String mText = "";

	// X轴
	public static float XLength = 600.f;

	// Y轴
	public static float Ylength = 300.0f;

	// X轴最大值
	public static float XMAXLENGTH = 2500.0f;

	// Y轴最大值
	public static float YMAXLENGTH = 250.0f;

	// 画笔
	private static Paint paint;

	// 距离屏幕左边的距离
	private static float marginleft = 40;

	// 距离屏幕上边的距离
	private static float margintop = 60;

	// 统计图的标题
	public String topNoteText = "";

	// 实际提示信息
	public String realityValuesNoteText = "计划订购金额";

	// 计划提示信息
	public String thinkValuesNoteText = "实际订购金额";

	// 平均刻度值
	public static int average = 40;

	// y轴刻度的平均值，需要几个刻度
	public static int yaverage = 6;

	// 以多少像素取几个刻度
	public static float faverage = 40;

	// x轴刻度的间距
	public static float xaverage = 40;

	/** 文本信息 **/
	private String[] noteTextValues;

	/** 文本对应的值 **/
	private double[] realityValues;

	private double[] thinkValues;

	/** 柱状图的颜色 **/
	public static int valuescolors = Color.parseColor("#C96118");

	/** 平均刻度值与实际刻度值的比 **/
	public static float xpaverage = 1;

	/** Y轴提示信息 **/
	public String yNoteText = "金额";

	/** X轴提示信息 **/
	public static String xText = "";
	/** 文本信息的间距 **/
	// x间距
	public static float yspace = 70;

	/** 柱状的宽度 **/
	private static float historgramspace = 30;

	private float yrange = 20f;

	/** 背景颜色 **/
	private int backColor = Color.WHITE;

	/** 背后线条的颜色 **/
	private int backLinesColor = Color.parseColor("#F1AAA3");

	/** 计划数值的颜色 **/
	private int thinkValuesColor = Color.RED;
	/** 真实数值的颜色 **/
	private int realityValuesColor = Color.GREEN;

	public DoubleHistogram(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public DoubleHistogram(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public DoubleHistogram(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		getWidthHeigth();// 得到X轴和Y轴的最大长度
		getmaxyLength();// 得到数据中的最大值，确定Y轴长度
		canvas.drawColor(backColor);// 设置画布的背景色
		paint = new Paint();
		paint.setAntiAlias(true); // 抗锯齿

		DrawXY(canvas);// 画X，Y轴

		// drawCentreNote(canvas);// 画底部提示信息

		drawNoteText(canvas);// 画头部提示的信息

		getyaverage();// 得到可以有几个刻度

		getAverage(XMAXLENGTH);// 得到一个刻度表示的值

		getxpaverage();// 得到实际值对应的画布像素表示的值

		DrawYText(canvas);// 画Y轴的信息

		DrawXText(canvas);// 画X轴信息

		DrawXthinkText(canvas);// 画柱状图
	}

	public void setData(String[] noteTextValues, double[] realityValues,
			double[] thinkValues) {
		this.noteTextValues = noteTextValues;
		this.realityValues = realityValues;
		this.thinkValues = thinkValues;
		postInvalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	/**
	 * 此方法参照android开发说明文档
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureWidth(int measureSpec) {
		paint = new Paint();
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			if (mText.equals("")) {
				DisplayMetrics metrics = getResources().getDisplayMetrics();
				result = (int) (metrics.widthPixels < getAcquiesceWhidth() ? getAcquiesceWhidth()
						: metrics.widthPixels + getPaddingLeft()
								+ getPaddingRight());
			} else {
				result = (int) paint.measureText(mText) + getPaddingLeft()
						+ getPaddingRight();
			}

			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}

		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		paint = new Paint();
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		mAscent = (int) paint.ascent();
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			if (mText.equals("")) {
				DisplayMetrics metrics = getResources().getDisplayMetrics();
				result = (int) (metrics.heightPixels - 150 + getPaddingLeft() + getPaddingRight());
			} else {
				result = (int) (-mAscent + paint.descent()) + getPaddingTop()
						+ getPaddingBottom();
			}
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * 得到实际需要的宽度
	 * 
	 * @return
	 */
	public float getAcquiesceWhidth() {
		float rtn = 100;
		if (noteTextValues.length > 0) {
			rtn = getPaddingLeft() + 40
					+ (yspace * (noteTextValues.length + 1));
		}
		return rtn;
	}

	/**
	 * 得到X轴Y轴的长度
	 */
	private void getWidthHeigth() {

		marginleft = getPaddingLeft() + 50f;
		margintop = getPaddingTop() + 30f;
		// X轴
		XLength = getWidth();
		// Y轴
		Ylength = getHeight() - 70;
	}

	/**
	 * 得到最大值
	 */
	public void getmaxyLength() {
		double temp = thinkValues[0];
		for (int i = 1; i < thinkValues.length; i++) {
			if (temp < thinkValues[i]) {
				temp = thinkValues[i];
			}
		}
		for (int i = 0; i < realityValues.length; i++) {
			if (temp < realityValues[i]) {
				temp = realityValues[i];
			}
		}
		XMAXLENGTH = (float) temp;
	}

	/**
	 * 画X，Y轴线
	 * 
	 * @param canvas
	 */
	public void DrawXY(Canvas canvas) {
		paint.setColor(Color.BLACK);
		canvas.drawLine(marginleft, margintop, marginleft, Ylength, paint);
		canvas.drawLine(marginleft, Ylength, XLength, Ylength, paint);
	}

	/**
	 * 提示信息画中心
	 * 
	 * @param canvas
	 */
	public void drawCentreNote(Canvas canvas) {
		float temp = XLength / 2;// 得到屏幕中间的像素值
		paint.setColor(thinkValuesColor);// 设置颜色
		paint.setTextSize(5);// 设置字体

		float textLength = temp - paint.measureText(thinkValuesNoteText);
		// 画矩形
		canvas.drawRect(textLength - 30, Ylength + 45, textLength - 20,
				Ylength + 55, paint);

		paint.setColor(Color.BLACK);
		// 画提示信息
		canvas.drawText(thinkValuesNoteText, textLength - 10, Ylength + 55,
				paint);

		paint.setColor(realityValuesColor);

		// 画矩形
		canvas.drawRect(temp + 10, Ylength + 45, temp + 20, Ylength + 55, paint);

		paint.setColor(Color.BLACK);
		// 画提示信息
		canvas.drawText(realityValuesNoteText, temp + 30, Ylength + 55, paint);

		canvas.drawText("0", marginleft - 10f, Ylength + 10, paint);

	}

	/**
	 * 画头部提示信息
	 * 
	 * @param canvas
	 */
	public void drawNoteText(Canvas canvas) {
		paint.setColor(Color.BLACK);
		paint.setTextSize(5);

		canvas.drawText(topNoteText,
				((float) ((XLength - paint.measureText(topNoteText)) / 2)),
				margintop + 20, paint);
	}

	public void getyaverage() {
		yaverage = (int) ((Ylength - margintop - 10) % faverage == 0.0 ? (Ylength
				- margintop - 10)
				/ faverage
				: (Ylength - margintop - 10) / faverage);
	}

	public String getAverage(double d) {
		String strRtn = "0";
		int intnum = (int) (d / yaverage);

		int inttemp = Integer
				.valueOf(getNumber(String.valueOf(intnum).length()));
		if (inttemp == 1) {
			strRtn = String
					.valueOf((int) (d % yaverage) == 0 ? ((int) (d / yaverage))
							: ((int) (d / yaverage) + 1));
		} else {
			strRtn = String.valueOf(intnum % inttemp == 0 ? (intnum / inttemp)
					* inttemp : (intnum / inttemp + 1) * inttemp);
		}
		average = Integer.valueOf(strRtn);
		Log.v("msg", "average = " + String.valueOf(average) + " YMAXLENGTH = "
				+ String.valueOf(YMAXLENGTH));
		return strRtn;
	}

	public String getNumber(int num) {
		String strRtn = "1";
		if (num > 0) {
			for (int i = 0; i < num - 1; i++) {
				strRtn += "0";
			}
		}
		return strRtn;
	}

	public void getxpaverage() {
		xpaverage = average / faverage;
		Log.v("msg", "xp =" + String.valueOf(xpaverage));
	}

	/**
	 * 画Y轴的信息
	 * 
	 * @param canvas
	 */
	public void DrawYText(Canvas canvas) {
		Float temp = 0.0f;
		paint.setTextSize(20);
		for (int i = 1; i < yaverage + 1; i++) {
			paint.setColor(Color.BLACK);

			temp = Ylength - Float.valueOf(faverage * i);
			// canvas.drawLine(marginleft-10, temp, marginleft, temp, paint);
			canvas.drawText(
					String.valueOf(Integer.valueOf(average) * i),
					marginleft
							- paint.measureText(String.valueOf(Integer
									.valueOf(average) * i)) - 5.0f,
					temp + 4.5f, paint);
			paint.setColor(backLinesColor);
			canvas.drawLine(marginleft, temp, XLength, temp, paint);
		}
	}

	/**
	 * 画X轴的信息
	 * 
	 * @param canvas
	 */
	public void DrawXText(Canvas canvas) {
		paint.setColor(Color.BLACK);
		paint.setTextSize(15);
		float temp = marginleft + historgramspace + yrange;
		for (int i = 0; i < noteTextValues.length; i++) {
			canvas.drawLine(temp, Ylength, temp, Ylength + 5f, paint);
			canvas.drawText(noteTextValues[i],
					temp - paint.measureText(noteTextValues[i]) / 2,
					Ylength + 25f, paint);
			temp += yspace;
		}
	}

	/**
	 * 画柱状图
	 * 
	 * @param canvas
	 */
	public void DrawXthinkText(Canvas canvas) {

		float temp = marginleft + historgramspace + yrange;
		for (int i = 0; i < thinkValues.length; i++) {
			paint.setColor(thinkValuesColor);
			canvas.drawRect(temp - historgramspace, Ylength
					- (float) (thinkValues[i] / xpaverage), temp, Ylength,
					paint);
			canvas.drawText(
					String.valueOf((int) thinkValues[i]),
					temp
							- historgramspace
							/ 2
							- paint.measureText(String
									.valueOf((int) thinkValues[i])) / 2,
					(float) (Ylength - thinkValues[i] / xpaverage - 3.0f),
					paint);

			paint.setColor(realityValuesColor);
			canvas.drawRect(temp, Ylength
					- (float) (realityValues[i] / xpaverage), temp
					+ historgramspace, Ylength, paint);
			canvas.drawText(
					String.valueOf((int) realityValues[i]),
					temp
							+ historgramspace
							/ 2
							- paint.measureText(String
									.valueOf((int) realityValues[i])) / 2,
					(float) (Ylength - realityValues[i] / xpaverage - 3.0f),
					paint);
			temp += yspace;
		}
	}
}
