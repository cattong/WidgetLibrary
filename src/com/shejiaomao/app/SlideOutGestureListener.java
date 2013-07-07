package com.shejiaomao.app;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.shejiaomao.util.CompatUtil;

public class SlideOutGestureListener implements OnGestureListener {
	//mdpi下的比例计算;
	private static final float FACTOR_PORTRAIT;
	private static final float FACTOR_LANDSCAPE;
	//手指在屏幕上移动距离小于此值不会被认为是手势
	private static int SLIDE_MIN_DISTANCE_X;
	private static int SLIDE_MAX_DISTANCE_Y;

	private static int DISPLAY_WINDOW_WIDTH;
	private static int DISPLAY_WINDOW_HEIGHT;
	static {
		FACTOR_PORTRAIT = 100f / 320;
		FACTOR_LANDSCAPE = FACTOR_PORTRAIT;
	}

	private Context context;
	private SlideDirection slideDirection;

	//划动方向
	public enum SlideDirection {
		None,
		Left,
		Right,
		Top,
		Bottom
	}

	public SlideOutGestureListener(Context context, SlideDirection slideDirection) {
		this.context = context;
		this.slideDirection = slideDirection;
		initEnv(context);
	}

	private void initEnv(Context context) {
		// 获得屏幕大小
		WindowManager windowManager = ((Activity)context).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		DISPLAY_WINDOW_WIDTH = display.getWidth();
		DISPLAY_WINDOW_HEIGHT = display.getHeight();

		SLIDE_MIN_DISTANCE_X = (int)(DISPLAY_WINDOW_WIDTH * FACTOR_PORTRAIT);
		SLIDE_MAX_DISTANCE_Y = (int)(DISPLAY_WINDOW_HEIGHT * FACTOR_LANDSCAPE);
		//orientation = context.getResources().getConfiguration().orientation;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float x1 = (e1 != null ? e1.getX() : 0);
		float x2 = (e2 != null ? e2.getX() : 0);
		float y1 = (e1 != null ? e1.getY() : 0);
		float y2 = (e2 != null ? e2.getY() : 0);
		float distanceX = x1 - x2;
		float distanceY = y1 - y2;

		if (distanceX > 0
			&& Math.abs(distanceX) > SLIDE_MIN_DISTANCE_X
			&& Math.abs(distanceY) < SLIDE_MAX_DISTANCE_Y
			&& slideDirection == SlideDirection.Left) {
			//slide to left
			((Activity)context).finish();
			CompatUtil.overridePendingTransitionSlideOutToLeft(context);
			return true;
		} else if (
			distanceX < 0
			&& Math.abs(distanceX) > SLIDE_MIN_DISTANCE_X
			&& Math.abs(distanceY) < SLIDE_MAX_DISTANCE_Y
			&& slideDirection == SlideDirection.Right) {
			//slide to right
			((Activity)context).finish();
			CompatUtil.overridePendingTransitionSlideOutToRight(context);
			return true;
		}

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {


	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {

		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	public SlideDirection getSlideDirection() {
		return slideDirection;
	}

	public void setSlideDirection(SlideDirection slideDirection) {
		this.slideDirection = slideDirection;
	}

}
