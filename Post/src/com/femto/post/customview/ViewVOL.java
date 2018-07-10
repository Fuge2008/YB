package com.femto.post.customview;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.android.futures.entity.KDJEntity;
import com.android.futures.entity.MACDEntity;
import com.android.futures.entity.MALineEntity;
import com.android.futures.entity.OHLCEntity;
import com.android.futures.entity.RSIEntity;
import com.femto.post.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

public class ViewVOL extends View {
	/** OHLC数据 */
	private List<OHLCEntity> mOHLCData;
	/** MA数据 */
	private List<MALineEntity> MALineData;
	// 下部表的数据
	MACDEntity mMACDData;
	KDJEntity mKDJData;
	RSIEntity mRSIData;
	/** 默认Y轴字体颜色 **/
	/** 默认XY轴字体大小 **/
	public static final int DEFAULT_AXIS_TITLE_SIZE = 15;
	private static final int DEFAULT_AXIS_Y_TITLE_COLOR = Color.YELLOW;
	/** 显示的OHLC数据起始位置 */
	private int mDataStartIndext = 0;
	/** 显示的OHLC数据个数 */
	private int mShowDataNum = 50;
	/** Candle宽度 */
	private double mCandleWidth;
	private int lowertop;
	/** 显示的最小Candle数 */
	private final static int MIN_CANDLE_NUM = 10;
	private static final int LOWER_CHART_TOP = 0;
	/** 当前数据的最大最小值 */
	private double mMaxPrice;
	private double mMinPrice;

	public ViewVOL(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public ViewVOL(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ViewVOL(Context context) {
		super(context);
		init();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mOHLCData == null || mOHLCData.size() <= 0) {
			return;
		}
		int viewHeight = getHeight();
		int viewWidth = getWidth();
		setBackgroundColor(getResources().getColor(R.color.white));
		drawLowerRegion(canvas);
		drawLatitudes(canvas, viewHeight, viewWidth, viewHeight
				/ DEFAULT_UPER_LATITUDE_NUM);
		drawLongitudes(canvas, viewHeight, viewWidth / DEFAULT_LOGITUDE_NUM);
	}

	private void init() {
		// TODO Auto-generated method stub
		mMaxPrice = -1;
		mMinPrice = -1;
		mOHLCData = new ArrayList<OHLCEntity>();
		mMACDData = new MACDEntity(null);
		mKDJData = new KDJEntity(null);
		mRSIData = new RSIEntity(null);

	}

	/**
	 * 初始化MA值，从数组的最后一个数据开始初始化
	 * 
	 * @param entityList
	 * @param days
	 * @return
	 */
	private List<Float> initMA(List<OHLCEntity> entityList, int days) {
		if (days < 2 || entityList == null || entityList.size() <= 0) {
			return null;
		}
		List<Float> MAValues = new ArrayList<Float>();

		float sum = 0;
		float avg = 0;
		for (int i = entityList.size() - 1; i >= 0; i--) {
			float close = (float) entityList.get(i).getClose();
			if (i > entityList.size() - days) {
				sum = sum + close;
				avg = sum / (entityList.size() - i);
			} else {
				sum = close + avg * (days - 1);
				avg = sum / days;
			}
			MAValues.add(avg);
		}

		List<Float> result = new ArrayList<Float>();
		for (int j = MAValues.size() - 1; j >= 0; j--) {
			result.add(MAValues.get(j));
		}
		return result;
	}

	private void initMALineData() {
		MALineEntity MA5 = new MALineEntity();
		MA5.setTitle("MA5");
		MA5.setLineColor(Color.WHITE);
		MA5.setLineData(initMA(mOHLCData, 5));

		MALineEntity MA10 = new MALineEntity();
		MA10.setTitle("MA10");
		MA10.setLineColor(Color.CYAN);
		MA10.setLineData(initMA(mOHLCData, 10));

		MALineEntity MA20 = new MALineEntity();
		MA20.setTitle("MA20");
		MA20.setLineColor(Color.BLUE);
		MA20.setLineData(initMA(mOHLCData, 20));

		MALineData = new ArrayList<MALineEntity>();
		MALineData.add(MA5);
		MALineData.add(MA10);
		MALineData.add(MA20);

	}

	public void setOHLCData(List<OHLCEntity> OHLCData) {
		if (OHLCData == null || OHLCData.size() <= 0) {
			return;
		}
		this.mOHLCData = OHLCData;
		initMALineData();
		mMACDData = new MACDEntity(mOHLCData);
		mKDJData = new KDJEntity(mOHLCData);
		mRSIData = new RSIEntity(mOHLCData);

		setCurrentData();
		postInvalidate();
		// invalidate();
	}

	private void setCurrentData() {
		if (mShowDataNum > mOHLCData.size()) {
			mShowDataNum = mOHLCData.size();
		}
		if (MIN_CANDLE_NUM > mOHLCData.size()) {
			mShowDataNum = MIN_CANDLE_NUM;
		}

		if (mShowDataNum > mOHLCData.size()) {
			mDataStartIndext = 0;
		} else if (mShowDataNum + mDataStartIndext > mOHLCData.size()) {
			mDataStartIndext = mOHLCData.size() - mShowDataNum;
		}
		mMinPrice = mOHLCData.get(mDataStartIndext).getLow();
		mMaxPrice = mOHLCData.get(mDataStartIndext).getHigh();
		for (int i = mDataStartIndext + 1; i < mOHLCData.size()
				&& i < mShowDataNum + mDataStartIndext; i++) {
			OHLCEntity entity = mOHLCData.get(i);
			mMinPrice = mMinPrice < entity.getLow() ? mMinPrice : entity
					.getLow();
			mMaxPrice = mMaxPrice > entity.getHigh() ? mMaxPrice : entity
					.getHigh();
		}

		for (MALineEntity lineEntity : MALineData) {
			for (int i = mDataStartIndext; i < lineEntity.getLineData().size()
					&& i < mShowDataNum + mDataStartIndext; i++) {
				mMinPrice = mMinPrice < lineEntity.getLineData().get(i) ? mMinPrice
						: lineEntity.getLineData().get(i);
				mMaxPrice = mMaxPrice > lineEntity.getLineData().get(i) ? mMaxPrice
						: lineEntity.getLineData().get(i);
			}
		}

	}

	private void drawLowerRegion(Canvas canvas) {

		float lowerHight = getHeight();
		float lowertop = LOWER_CHART_TOP;
		float viewWidth = getWidth();
		int width = getWidth();
		mCandleWidth = (width - 4) / 10.0 * 10.0 / mShowDataNum;
		// 下部表的数据
		// MACDData mMACDData;
		// KDJData mKDJData;
		// RSIData mRSIData;
		Paint whitePaint = new Paint();
		whitePaint.setColor(Color.RED);
		Paint yellowPaint = new Paint();
		yellowPaint.setColor(Color.GREEN);
		Paint magentaPaint = new Paint();
		magentaPaint.setColor(Color.MAGENTA);

		Paint textPaint = new Paint();
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(DEFAULT_AXIS_TITLE_SIZE);
		if (titles.trim().equalsIgnoreCase("MACD")) {
			List<Double> MACD = mMACDData.getMACD();
			List<Double> DEA = mMACDData.getDEA();
			List<Double> DIF = mMACDData.getDIF();

			double low = DEA.get(mDataStartIndext);
			double high = low;
			double rate = 0.0;
			for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum
					&& i < MACD.size(); i++) {
				low = low < MACD.get(i) ? low : MACD.get(i);
				low = low < DEA.get(i) ? low : DEA.get(i);
				low = low < DIF.get(i) ? low : DIF.get(i);

				high = high > MACD.get(i) ? high : MACD.get(i);
				high = high > DEA.get(i) ? high : DEA.get(i);
				high = high > DIF.get(i) ? high : DIF.get(i);
			}
			rate = lowerHight / (high - low);

			Paint paint = new Paint();
			float zero = (float) (high * rate) + lowertop;
			if (zero < lowertop) {
				zero = lowertop;
			}
			// 绘制双线
			float dea = 0.0f;
			float dif = 0.0f;
			for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum
					&& i < MACD.size(); i++) {
				// 绘制矩形
				if (MACD.get(i) >= 0.0) {
					paint.setColor(Color.RED);
					float top = (float) ((high - MACD.get(i)) * rate)
							+ lowertop;

					if (zero - top < 0.55f) {
						canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
								* (i + 1 - mDataStartIndext), zero, viewWidth
								- 2 - (float) mCandleWidth
								* (i - mDataStartIndext), zero, paint);
					} else {
						canvas.drawRect(viewWidth - 1 - (float) mCandleWidth
								* (i + 1 - mDataStartIndext), top, viewWidth
								- 2 - (float) mCandleWidth
								* (i - mDataStartIndext), zero, paint);

					}

				} else {
					paint.setColor(Color.GREEN);
					float bottom = (float) ((high - MACD.get(i)) * rate)
							+ lowertop;

					if (bottom - zero < 0.55f) {
						canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
								* (i + 1 - mDataStartIndext), zero, viewWidth
								- 2 - (float) mCandleWidth
								* (i - mDataStartIndext), zero, paint);
					} else {
						canvas.drawRect(viewWidth - 1 - (float) mCandleWidth
								* (i + 1 - mDataStartIndext), zero, viewWidth
								- 2 - (float) mCandleWidth
								* (i - mDataStartIndext), bottom, paint);
					}
				}

				if (i != mDataStartIndext) {
					canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
							* (i + 1 - mDataStartIndext) + (float) mCandleWidth
							/ 2, (float) ((high - DEA.get(i)) * rate)
							+ lowertop,
							viewWidth - 2 - (float) mCandleWidth
									* (i - mDataStartIndext)
									+ (float) mCandleWidth / 2, dea, whitePaint);

					canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
							* (i + 1 - mDataStartIndext) + (float) mCandleWidth
							/ 2, (float) ((high - DIF.get(i)) * rate)
							+ lowertop,
							viewWidth - 2 - (float) mCandleWidth
									* (i - mDataStartIndext)
									+ (float) mCandleWidth / 2, dif,
							yellowPaint);
				}
				dea = (float) ((high - DEA.get(i)) * rate) + lowertop;
				dif = (float) ((high - DIF.get(i)) * rate) + lowertop;
			}

			canvas.drawText(new DecimalFormat("#.##").format(high), 2, lowertop
					+ DEFAULT_AXIS_TITLE_SIZE - 2, textPaint);
			canvas.drawText(new DecimalFormat("#.##").format((high + low) / 2),
					2, lowertop + lowerHight / 2 + DEFAULT_AXIS_TITLE_SIZE,
					textPaint);
			canvas.drawText(new DecimalFormat("#.##").format(low), 2, lowertop
					+ lowerHight, textPaint);

		} else if (titles.trim().equalsIgnoreCase("KDJ")) {
			List<Double> Ks = mKDJData.getK();
			List<Double> Ds = mKDJData.getD();
			List<Double> Js = mKDJData.getJ();

			double low = Ks.get(mDataStartIndext);
			double high = low;
			double rate = 0.0;
			for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum
					&& i < Ks.size(); i++) {
				low = low < Ks.get(i) ? low : Ks.get(i);
				low = low < Ds.get(i) ? low : Ds.get(i);
				low = low < Js.get(i) ? low : Js.get(i);

				high = high > Ks.get(i) ? high : Ks.get(i);
				high = high > Ds.get(i) ? high : Ds.get(i);
				high = high > Js.get(i) ? high : Js.get(i);
			}
			rate = lowerHight / (high - low);

			// 绘制白、黄、紫线
			float k = 0.0f;
			float d = 0.0f;
			float j = 0.0f;
			for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum
					&& i < Ks.size(); i++) {

				if (i != mDataStartIndext) {
					canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
							* (i + 1 - mDataStartIndext) + (float) mCandleWidth
							/ 2,
							(float) ((high - Ks.get(i)) * rate) + lowertop,
							viewWidth - 2 - (float) mCandleWidth
									* (i - mDataStartIndext)
									+ (float) mCandleWidth / 2, k, whitePaint);

					canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
							* (i + 1 - mDataStartIndext) + (float) mCandleWidth
							/ 2,
							(float) ((high - Ds.get(i)) * rate) + lowertop,
							viewWidth - 2 - (float) mCandleWidth
									* (i - mDataStartIndext)
									+ (float) mCandleWidth / 2, d, yellowPaint);

					canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
							* (i + 1 - mDataStartIndext) + (float) mCandleWidth
							/ 2,
							(float) ((high - Js.get(i)) * rate) + lowertop,
							viewWidth - 2 - (float) mCandleWidth
									* (i - mDataStartIndext)
									+ (float) mCandleWidth / 2, j, magentaPaint);
				}
				k = (float) ((high - Ks.get(i)) * rate) + lowertop;
				d = (float) ((high - Ds.get(i)) * rate) + lowertop;
				j = (float) ((high - Js.get(i)) * rate) + lowertop;
			}

			canvas.drawText(new DecimalFormat("#.##").format(high), 2, lowertop
					+ DEFAULT_AXIS_TITLE_SIZE - 2, textPaint);
			canvas.drawText(new DecimalFormat("#.##").format((high + low) / 2),
					2, lowertop + lowerHight / 2 + DEFAULT_AXIS_TITLE_SIZE,
					textPaint);
			canvas.drawText(new DecimalFormat("#.##").format(low), 2, lowertop
					+ lowerHight, textPaint);

		} else if (titles.trim().equalsIgnoreCase("RSI")) {

		} else if (titles.trim().equalsIgnoreCase("VOL")) {
			List<Double> MACD = mMACDData.getMACD();
			List<Double> DEA = mMACDData.getDEA();
			List<Double> DIF = mMACDData.getDIF();

			double low = DEA.get(mDataStartIndext);
			double high = low;
			double rate = 0.0;
			for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum
					&& i < MACD.size(); i++) {
				low = low < MACD.get(i) ? low : MACD.get(i);
				low = low < DEA.get(i) ? low : DEA.get(i);
				low = low < DIF.get(i) ? low : DIF.get(i);

				high = high > MACD.get(i) ? high : MACD.get(i);
				high = high > DEA.get(i) ? high : DEA.get(i);
				high = high > DIF.get(i) ? high : DIF.get(i);
			}
			rate = lowerHight / (high - low);

			Paint paint = new Paint();
			float zero = (float) (high * rate) + lowertop;
			if (zero < lowertop) {
				zero = lowertop;
			}
			// 绘制双线
			float dea = 0.0f;
			float dif = 0.0f;
			for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum
					&& i < MACD.size(); i++) {
				// 绘制矩形
				if (MACD.get(i) >= 0.0) {
					paint.setColor(Color.RED);
					float top = (float) ((high - MACD.get(i)) * rate)
							+ lowertop;

					if (zero - top < 0.55f) {

					} else {
						canvas.drawRect(viewWidth - 1 - (float) mCandleWidth
								* (i + 1 - mDataStartIndext), top, viewWidth
								- 2 - (float) mCandleWidth
								* (i - mDataStartIndext), zero, paint);

					}

				} else {
					paint.setColor(Color.GREEN);
					float bottom = (float) ((high - MACD.get(i)) * rate)
							+ lowertop;

					if (bottom - zero < 0.55f) {

					} else {
						canvas.drawRect(viewWidth - 1 - (float) mCandleWidth
								* (i + 1 - mDataStartIndext), zero, viewWidth
								- 2 - (float) mCandleWidth
								* (i - mDataStartIndext), bottom, paint);
					}
				}

			}

			canvas.drawText(new DecimalFormat("#.##").format(high), 2, lowertop
					+ DEFAULT_AXIS_TITLE_SIZE - 2, textPaint);
			canvas.drawText(new DecimalFormat("#.##").format((high + low) / 2),
					2, lowertop + lowerHight / 2 + DEFAULT_AXIS_TITLE_SIZE,
					textPaint);
			canvas.drawText(new DecimalFormat("#.##").format(low), 2, lowertop
					+ lowerHight, textPaint);
		}

	}

	private String titles = "VOL";

	public void setTitles(String titles) {
		this.titles = titles;
		postInvalidate();
	}

	/**
	 * 绘制经线
	 * 
	 * @param canvas
	 * @param viewHeight
	 * @param viewWidth
	 */
	private void drawLongitudes(Canvas canvas, int viewHeight,
			float longitudeSpacing) {
		Paint paint = new Paint();
		paint.setColor(mLongiLatitudeColor);
		paint.setPathEffect(DEFAULT_DASH_EFFECT);
		for (int i = 0; i <= DEFAULT_LOGITUDE_NUM; i++) {
			canvas.drawLine(1 + longitudeSpacing * i, topTitleHeight + 2, 1
					+ longitudeSpacing * i, UPER_CHART_BOTTOM, paint);
			canvas.drawLine(1 + longitudeSpacing * i, LOWER_CHART_TOP, 1
					+ longitudeSpacing * i, viewHeight - 1, paint);
		}

	}

	int topTitleHeight = 0;
	/** 默认上表纬线数 */
	public static final int DEFAULT_UPER_LATITUDE_NUM = 3;
	int mLongiLatitudeColor = Color.BLACK;
	/** 默认虚线效果 */
	private static final PathEffect DEFAULT_DASH_EFFECT = new DashPathEffect(
			new float[] { 3, 3, 3, 3 }, 1);
	/** 默认经线数 */
	public static final int DEFAULT_LOGITUDE_NUM = 3;
	/** 默认下表纬线数 */
	private static final int DEFAULT_LOWER_LATITUDE_NUM = 1;
	int UPER_CHART_BOTTOM = 1 + topTitleHeight + 30
			* (DEFAULT_UPER_LATITUDE_NUM + 1);

	/**
	 * 绘制纬线
	 * 
	 * @param canvas
	 * @param viewHeight
	 * @param viewWidth
	 */
	private void drawLatitudes(Canvas canvas, int viewHeight, int viewWidth,
			float latitudeSpacing) {
		Paint paint = new Paint();
		paint.setColor(mLongiLatitudeColor);
		paint.setPathEffect(DEFAULT_DASH_EFFECT);
		for (int i = 0; i <= DEFAULT_UPER_LATITUDE_NUM; i++) {
			canvas.drawLine(1, topTitleHeight + 1 + latitudeSpacing * i,
					viewWidth - 1, topTitleHeight + 1 + latitudeSpacing * i,
					paint);
		}

		canvas.drawLine(1, viewHeight - 2, viewWidth - 1, viewHeight-1, paint);

	}
}
