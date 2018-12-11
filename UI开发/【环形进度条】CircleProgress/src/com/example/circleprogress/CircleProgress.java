package com.example.circleprogress;

import java.text.DecimalFormat;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * TODO 环形进度条
 * @author Arvin
 * @since 2018.3.29
 */
public class CircleProgress extends View {

	/****************基础属性(不对外暴露)*****************/
	//定义环形进度条外围矩形
	private RectF mWheelRectangle = new RectF();
	//已进行的进度条画笔
	private Paint mProgressWheelPaint;
	//定义圆环默认轮廓画笔
	private Paint mDefaultWheelPaint;
	//定义百分比文本画笔
	private Paint mTextPaint;
	//定义圆弧离矩形的距离
	private float pressExtraStrokeWidth;
	//定义进度条变化的动画
	private ProgressAnimation progressAnim;
	//定义当前进度对应的百分比
	private float mPercent;
	//定义百分表文本的纵坐标
	private float mPercent_y;
	//定义环形进度进行的偏移角度
	private float mSweepAngle;	
	
	/****************动态属性(对外暴露接口)*********************/
	//定义环形进度条默认底色
	private int mDefaultColor = Color.rgb(250, 250, 250) ;
	//定义进度条颜色
	private int mProgressColor = Color.rgb(249, 135, 49) ;
	//定义百分比颜色
	private int mTextPColor = Color.rgb(249, 135, 2) ;
	//定义百分比文本字体大小
	private float mTextPSize = 80f;
	//定义环形进度条的轮廓宽度
	private float circleStrokeWidth = 40f;
	//定义当前进度值
	private int mCurProgress;
	//定义最大默认进度值
	private int maxProgress = 100;
	//定义百分比显示格式；格式为保留小数点后一位
	private DecimalFormat mPercentFormat = new DecimalFormat("#.0");
	
	public CircleProgress(Context context) {
		super(context);
		init(null, 0);
	}

	public CircleProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public CircleProgress(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {
		mProgressWheelPaint = new Paint();
		mProgressWheelPaint.setColor(mProgressColor);
		mProgressWheelPaint.setStyle(Paint.Style.STROKE);
		mProgressWheelPaint.setStrokeCap(Paint.Cap.ROUND);
		mProgressWheelPaint.setAntiAlias(true);

		mDefaultWheelPaint = new Paint();
		mDefaultWheelPaint.setColor(mDefaultColor);
		mDefaultWheelPaint.setStyle(Paint.Style.STROKE);
		mDefaultWheelPaint.setStrokeCap(Paint.Cap.ROUND);
		mDefaultWheelPaint.setAntiAlias(true);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mTextPColor);

		progressAnim = new ProgressAnimation();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawArc(mWheelRectangle, 0, 359, false,
				mDefaultWheelPaint);
		canvas.drawArc(mWheelRectangle, 90, mSweepAngle, false,
				mProgressWheelPaint);
		canvas.drawText(
				mPercent + "%", 
				mWheelRectangle.centerX()- (mTextPaint.measureText(String.valueOf(mPercent) + "%") / 2),
				mPercent_y, 
				mTextPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = getDefaultSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		int min = Math.min(width, height);// 获取View最短边的长度
		setMeasuredDimension(min, min);// 强制改View为以最短边为长度的正方形
		circleStrokeWidth = Textscale(circleStrokeWidth, min);// 圆弧的宽度
		pressExtraStrokeWidth = Textscale(2, min);// 圆弧离矩形的距离
		mWheelRectangle.set(
				circleStrokeWidth + pressExtraStrokeWidth,
				circleStrokeWidth + pressExtraStrokeWidth,
				min- circleStrokeWidth - pressExtraStrokeWidth, 
				min - circleStrokeWidth - pressExtraStrokeWidth);// 设置矩形
		mTextPaint.setTextSize(Textscale(mTextPSize, min));
		mPercent_y = min/2 + Textscale(mTextPSize, min)/2;
		mProgressWheelPaint.setStrokeWidth(circleStrokeWidth);
		mDefaultWheelPaint.setStrokeWidth(circleStrokeWidth);
	}

	/**
	 * TODO 定义进度条绘制动画
	 */
	public class ProgressAnimation extends Animation {
		public ProgressAnimation() {

		}

		/**
		 * 每次系统调用这个方法时， 改变mSweepAngle，mPercent
		 * 然后调用postInvalidate()重新绘制view
		 */
		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0f) {
				mPercent = Float.parseFloat(mPercentFormat.format(interpolatedTime
						* mCurProgress * 100f / maxProgress));// 将浮点数四舍五入保留一位小数
				mSweepAngle = interpolatedTime * mCurProgress * 360
						/ maxProgress;
			} else {
				mPercent = Float.parseFloat(mPercentFormat.format(mCurProgress * 100f
						/ maxProgress));// 将浮点数四舍五入保留一位小数
				mSweepAngle = mCurProgress * 360 / maxProgress;
			}
			postInvalidate();
		}
	}
	
	/**
	 * TODO 根据控件的大小改变绝对位置的比例
	 * @param float n, float m
	 * @return float
	 */
	private float Textscale(float n, float m) {
		return Math.min(n, m);
	}
	
	/***********************Interface start*****************************/

	/**
	 * TODO 更新数据后同时执行绘制动画(自定义动画时长)
	 * @param mCurProgress
	 * @param time
	 * @return 
	 */
	public void updateProgress(int mProgress, int animDuration) {
		mCurProgress = mProgress;
		setAnimDuration(animDuration);
		this.startAnimation(progressAnim);
	}
	
	/**
	 * TODO 更新数据后同时执行绘制动画(500ms)
	 * @param mCurProgress
	 * @param time
	 * @return 
	 */
	public void updateProgress(int mProgress) {
		mCurProgress = mProgress;
		setAnimDuration(500);
		this.startAnimation(progressAnim);
	}

	
	/**
	 * TODO 设置最大进度值
	 * @param mMaxProgress
	 * @return 
	 */
	public void setMaxProgress(int mMaxProgress) {
		maxProgress = mMaxProgress;
	}

	/**
	 * TODO 设置进度条默认底色
	 * @param red
	 * @param green
	 * @param blue
	 */
	public void setDefaultWheelColor(int red, int green, int blue) {
		mDefaultColor = Color.rgb(red, green, blue);
		mDefaultWheelPaint.setColor(mDefaultColor);
	}
	
	/**
	 * TODO 设置进度条默认底色
	 * @param colorId
	 */
	public void setDefaultWheelColor(int colorId) {
		mDefaultColor = getResources().getColor(colorId) ;
		mDefaultWheelPaint.setColor(mDefaultColor);
	}
	
	/**
	 * TODO 设置进度条颜色
	 * @param red
	 * @param green
	 * @param blue
	 */
	public void setProgressColor(int red, int green, int blue) {
		mProgressColor = Color.rgb(red, green, blue) ;
		mProgressWheelPaint.setColor(mProgressColor);
	}
	
	/**
	 * TODO 设置进度条颜色
	 * @param colorId
	 */
	public void setProgressColor(int colorId) {
		mProgressColor = getResources().getColor(colorId);
		mProgressWheelPaint.setColor(mProgressColor);
	}
	
	/**
	 * TODO 设置百分比文本颜色
	 * @param red
	 * @param green
	 * @param blue
	 */
	public void setPercentColor(int red, int green, int blue) {
		mTextPColor = Color.rgb(red, green, blue) ;
		mTextPaint.setColor(mTextPColor);
	}
	
	/**
	 * TODO 设置百分比文本颜色
	 * @param colorId
	 */
	public void setPercentColor(int colorId) {
		mTextPColor = getResources().getColor(colorId);
		mTextPaint.setColor(mTextPColor);
	}
	
	/**
	 * TODO 设置百分比文本字体大小
	 * @param float txtSize
	 */
	public void setPercentTxtSize(float txtSize){
		mTextPSize = txtSize ;
	}
	
	/**
	 * TODO 设置进度条轮廓尺寸
	 * @param float mProgressWidth
	 */
	public void setProgressWidth(float mProgressWidth){
		circleStrokeWidth = mProgressWidth ;
	}
	
	/**
	 * TODO 设置进度条绘制动画的执行时间
	 * @param durationTime
	 */
	public void setAnimDuration(int durationTime) {// 按照比例设置动画执行时间
		progressAnim.setDuration(durationTime * mCurProgress / maxProgress);
	}

}
