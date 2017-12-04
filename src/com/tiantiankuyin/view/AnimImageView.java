package com.tiantiankuyin.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.R;

/**
 * 自定义加载动画控制类
 * */
public class AnimImageView extends View {
	private static final long ANIMATION_INTERVAL = 80;

	/** 界面上下文 */
	Context ctx;
	/** 绘制控制索引 */
	int index = 1;
	/** 动画暂停控制位 */
	private boolean isStop = false;
	/** 屏幕宽 */
	private int screen_W = Env.getScreenWidth();
	/** 屏幕宽 */
	private int screen_H = Env.getScreenHeight();

	public AnimImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.ctx = context;
	}

	public AnimImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctx = context;
	}

	public AnimImageView(Context context) {
		super(context);
		this.ctx = context;
	}

	/**
	 * 动画暂停
	 * */
	public void stop() {
		isStop = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		switch (index) {
		case 1:
			index++;
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
					R.drawable.loading_1), (screen_W - BitmapFactory
					.decodeResource(getResources(), R.drawable.loading_1)
					.getWidth()) / 2, (screen_H / 2 - BitmapFactory
					.decodeResource(getResources(), R.drawable.loading_1)
					.getHeight()) / 2 - 10, null);
			break;
		case 2:
			index++;
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
					R.drawable.loading_2), (screen_W - BitmapFactory
					.decodeResource(getResources(), R.drawable.loading_1)
					.getWidth()) / 2, (screen_H / 2 - BitmapFactory
					.decodeResource(getResources(), R.drawable.loading_1)
					.getHeight()) / 2 - 10, null);
			break;
		case 3:
			index++;
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
					R.drawable.loading_3), (screen_W - BitmapFactory
					.decodeResource(getResources(), R.drawable.loading_1)
					.getWidth()) / 2, (screen_H / 2 - BitmapFactory
					.decodeResource(getResources(), R.drawable.loading_1)
					.getHeight()) / 2 - 10, null);
			break;
		case 4:
			index = 1;
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
					R.drawable.loading_4), (screen_W - BitmapFactory
					.decodeResource(getResources(), R.drawable.loading_1)
					.getWidth()) / 2, (screen_H / 2 - BitmapFactory
					.decodeResource(getResources(), R.drawable.loading_1)
					.getHeight()) / 2 - 10, null);
			break;
		}
		if(screen_W > 320){
			canvas.drawBitmap(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.loading_txt),
				(screen_W - BitmapFactory.decodeResource(getResources(),
						R.drawable.loading_txt).getWidth()) / 2,
				(screen_H / 2 + BitmapFactory.decodeResource(getResources(),
						R.drawable.loading_1).getHeight()) / 2 + 10, null);
		}else{
			canvas.drawBitmap(
					BitmapFactory.decodeResource(getResources(),
							R.drawable.loading_txt),
					(screen_W - BitmapFactory.decodeResource(getResources(),
							R.drawable.loading_txt).getWidth()) / 2,
					(screen_H / 2 + BitmapFactory.decodeResource(getResources(),
							R.drawable.loading_1).getHeight()) / 2 , null);
		}
		
		if (!isStop) {
			postInvalidateDelayed(ANIMATION_INTERVAL);
		}
		super.onDraw(canvas);
	}
}
