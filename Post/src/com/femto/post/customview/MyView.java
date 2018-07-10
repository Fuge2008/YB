package com.femto.post.customview;

import com.femto.post.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MyView extends View{

	public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		drawLine(canvas);
	}

	private void drawLine(Canvas canvas) {
		// TODO Auto-generated method stub
		Paint paint = new Paint();
		paint.setColor(getResources().getColor(R.color.red));
		paint.setStrokeWidth(10);
		canvas.drawLine(15, 15, 90, 90, paint);
	}

}
