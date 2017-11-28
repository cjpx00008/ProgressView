package com.trinasolar.library;

import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import java.text.DecimalFormat;

/**
 * Created by hongfei.wang on 2017/11/23.
 *
 * @author hongfei.wang
 */

public class ProgressView extends View {

    private static final int PROGRESS_START_ANGLE = 135;
    private static final int PROGRESS_SWEEP_ANGLE = 270;
    private static final float PROGRESS_RATIO = 270f/100;

    private static final int DEGREE_START_ANGLE = 140;
    private static final int DEGREE_SWEEP_ANGLE = 260;

    private static final int DEGREE_LINE_COUNT = 51;
    private static final float DEGREE_TEXT_COUNT = 21;
    private static final float ROTATE_DEGREE = 260f / (DEGREE_LINE_COUNT - 1);
    private static final float DEGREE_TEXT_ANGLE = 260f / (DEGREE_TEXT_COUNT - 1);

    private static final float ARROW_RATIO = 260f / 100;

    private DecimalFormat df = new DecimalFormat("###.00");

    private Paint mProgressPaint;
    private Paint mProgressBgPaint;
    private Paint mDegreeBgPaint;
    private Paint mDegreeLinePaint;
    private Paint mDegreeTextPaint;
    private Paint mDegreeLightTextPaint;
    private Paint mTitlePaint;
    private Paint mArrowBgPaint;
    private Paint mArrowContentPaint;
    private Paint mProgressTextPaint;

    private Path mArrowPath;

    private Bitmap mDegreeBitmap;
    private Bitmap mArrowBitmap;

    private float mCenterX;
    private float mCenterY;

    private float mLineStartY;
    private float mLineEndY;
    private int mSize;
    private float mSizeRatio;

    private float mProgressWidth = dpToPx(44);
    private float mDegreeBgWidth = dpToPx(40);
    private float mDegreeBgPadding = dpToPx(40);
    private float mDegreeLineWidth = dpToPx(1);
    private float mDegreeLineHeight = dpToPx(7);
    private float mTitlePadding = dpToPx(30);

    private float mArrowWidth = dpToPx(24);
    private float mArrowPadding = dpToPx(3);
    private float mArrowCircleRadius = dpToPx(2);
    private float mArrowLength = dpToPx(44);

    private float mDegreeDefaultTextSize = dpToPx(12);
    private float mDegreeDefaultLightTextSize = dpToPx(9);

    private float mDefaultTitleTextSize = dpToPx(18);
    private float mDefaultProgressTextSize = dpToPx(38);

    private RectF mProgressRect;
    private RectF mDegreeBgRect;

    private float mProgress;

    private Animator mAnimator;

    private String mTitle;

    public ProgressView(Context context) {
        super(context);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
        mTitle = typedArray.getString(R.styleable.ProgressView_title);
        if (mTitle == null) {
            mTitle = getResources().getString(R.string.default_title);
        }
        mDefaultTitleTextSize = typedArray.getDimension(R.styleable.ProgressView_titleTextSize, mDefaultTitleTextSize);
        mDefaultProgressTextSize = typedArray.getDimension(R.styleable.ProgressView_progressTextSize, mDefaultProgressTextSize);
        typedArray.recycle();
        initPaint();
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initPaint() {
        initProgressBgPaint();
        initProgressPaint();
        initDegreeBgPaint();
        initDegreeLinePaint();
        initDegreeTextPaint();
        initDegreeLightTextPaint();
        initTitlePaint();
        initArrowPaint();
        initProgressTextPaint();
    }

    private void initProgressTextPaint() {
        mProgressTextPaint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        mProgressTextPaint.setColor(getResources().getColor(R.color.progress_text_color));
        mProgressTextPaint.setTextAlign(Paint.Align.CENTER);
        mProgressTextPaint.setTextSize(mDefaultProgressTextSize);
        mProgressTextPaint.setFakeBoldText(true);
    }

    private void initArrowPaint() {
        mArrowPath = new Path();
        mArrowBgPaint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        mArrowBgPaint.setColor(Color.WHITE);
        mArrowBgPaint.setShadowLayer(10, 2, 2, Color.LTGRAY);

        mArrowContentPaint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        mArrowContentPaint.setColor(getResources().getColor(R.color.arrow_content_color));
    }

    private void initTitlePaint() {
        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTitlePaint.setColor(getResources().getColor(R.color.title_color));
        mTitlePaint.setTextAlign(Paint.Align.CENTER);
        mTitlePaint.setFakeBoldText(true);
        mTitlePaint.setTextSize(mDefaultTitleTextSize);
    }

    private void initDegreeLightTextPaint() {
        mDegreeLightTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDegreeLightTextPaint.setColor(getResources().getColor(R.color.degree_light_text_color));
        mDegreeLightTextPaint.setTextAlign(Paint.Align.CENTER);
        mDegreeLightTextPaint.setTextSize(mDegreeDefaultLightTextSize);
    }

    private void initDegreeTextPaint() {
        mDegreeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDegreeTextPaint.setColor(getResources().getColor(R.color.degree_text_color));
        mDegreeTextPaint.setTextAlign(Paint.Align.CENTER);
        mDegreeTextPaint.setTextSize(mDegreeDefaultTextSize);
    }

    private void initDegreeLinePaint() {
        mDegreeLinePaint = new Paint();
        mDegreeLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDegreeLinePaint.setColor(getResources().getColor(R.color.degree_line_color));
        mDegreeLinePaint.setStrokeWidth(mDegreeLineWidth);
    }

    private void initDegreeBgPaint() {
        mDegreeBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDegreeBgPaint.setStyle(Paint.Style.STROKE);
        mDegreeBgPaint.setStrokeCap(Paint.Cap.ROUND);
        mDegreeBgPaint.setStrokeWidth(mDegreeBgWidth);
        mDegreeBgPaint.setColor(Color.WHITE);
        mDegreeBgPaint.setShadowLayer(10, 1, 2, Color.LTGRAY);
    }

    private void initProgressPaint() {
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setStrokeWidth(mProgressWidth);
    }

    private void initProgressBgPaint() {
        mProgressBgPaint = new Paint();
        mProgressBgPaint.setAntiAlias(true);
        mProgressBgPaint.setStyle(Paint.Style.STROKE);
        mProgressBgPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressBgPaint.setStrokeWidth(mProgressWidth);
        mProgressBgPaint.setColor(getResources().getColor(R.color.progress_bg_color));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize( widthMeasureSpec );
        int height= MeasureSpec.getSize( heightMeasureSpec );
        mSize = width > height ? height : width;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mSizeRatio = mSize / (float)dm.widthPixels;
        initDrawData();
        initProgressShader();
        initDegreeBitmap();
        initArrowBitmap();
//        initPaintSize();
        setMeasuredDimension(mSize, mSize);
    }

    /**
     *  缩放控制
     *  @author admin
     *  create at 2017/11/27 16:10
     */
//    private void initPaintSize() {
//        mProgressBgPaint.setStrokeWidth(mProgressWidth * mSizeRatio);
//        mProgressPaint.setStrokeWidth(mProgressWidth * mSizeRatio);
//        mDegreeBgPaint.setStrokeWidth(mProgressWidth * mSizeRatio);
//
//        mTitlePaint.setTextSize(mDefaultTitleTextSize * mSizeRatio);
//        mProgressTextPaint.setTextSize(mDefaultProgressTextSize * mSizeRatio);
//        mDegreeTextPaint.setTextSize(mDegreeDefaultTextSize * mSizeRatio);
//        mDegreeLightTextPaint.setTextSize(mDegreeDefaultLightTextSize * mSizeRatio);
//    }

    private void initDegreeBitmap() {
        mDegreeBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDegreeBitmap);
        drawDegreeBg(canvas);
        drawDegree(canvas);
    }

    private void initArrowBitmap() {
        mArrowBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mArrowBitmap);
        drawArrow(canvas);
    }

    private void drawArrow(Canvas canvas) {
        canvas.translate(mCenterX, mCenterY);
        canvas.rotate(50);
        mArrowPath.moveTo(0, mLineStartY + -mArrowWidth / 2);
        mArrowPath.lineTo(-mArrowWidth / 2, mLineStartY - mArrowLength);
        RectF rect = new RectF();
        rect.set(-mArrowWidth / 2, mLineEndY - mArrowLength - mArrowWidth / 2, mArrowWidth / 2, mLineEndY - mArrowLength + mArrowWidth / 2);
        mArrowPath.addArc(rect, 180, 180);
        mArrowPath.lineTo(0, mLineStartY + mArrowWidth / 2);
        mArrowPath.close();
        mArrowBgPaint.setColor(Color.WHITE);
        canvas.drawPath(mArrowPath, mArrowBgPaint);

        mArrowPath.reset();
        mArrowPath.moveTo(0, mLineStartY + mArrowWidth / 2 - mArrowPadding);
        mArrowPath.lineTo(-mArrowWidth / 2 - mArrowPadding, mLineStartY - mArrowLength - mArrowPadding);
        rect.set(-mArrowWidth / 2 + mArrowPadding, mLineEndY - mArrowLength - mArrowWidth / 2 + mArrowPadding, mArrowWidth / 2 - mArrowPadding, mLineEndY - mArrowLength + mArrowWidth / 2 - mArrowPadding);
        mArrowPath.addArc(rect, 180, 180);
        mArrowPath.lineTo(0, mLineStartY + mArrowWidth / 2 - mArrowPadding);
        mArrowPath.close();
        mArrowContentPaint.setColor(getResources().getColor(R.color.arrow_content_color));
        canvas.drawPath(mArrowPath, mArrowContentPaint);
        mArrowContentPaint.setColor(Color.WHITE);
        canvas.drawCircle(0, mLineStartY - mArrowLength - dpToPx(9),mArrowCircleRadius, mArrowContentPaint);
        mArrowPath.reset();

    }

    private void initDrawData() {
        mCenterX = mSize / 2;
        mCenterY = mSize / 2;
        mProgressRect = new RectF();
        mDegreeBgRect = new RectF();
        mProgressRect.set(mProgressWidth / 2, mProgressWidth / 2, mSize - mProgressWidth / 2, mSize - mProgressWidth / 2);
        mDegreeBgRect.set(mProgressWidth / 2 + mDegreeBgPadding, mProgressWidth / 2 + mDegreeBgPadding, mSize - mProgressWidth / 2 - mDegreeBgPadding, mSize - mProgressWidth / 2 - mDegreeBgPadding);
        mLineStartY = (mSize - mProgressWidth - mDegreeBgPadding)/2;
        mLineEndY = mLineStartY - mDegreeLineHeight;
    }

    private void initProgressShader() {
        Shader shader = new SweepGradient(mCenterX, mCenterY, new int[]{getResources().getColor(R.color.blue), getResources().getColor(R.color.light_blue), getResources().getColor(R.color.green), getResources().getColor(R.color.yellow), getResources().getColor(R.color.red), getResources().getColor(R.color.blue)}, new float[]{0f, 0.2f, 0.375f, 0.58f, 0.8f, 1f});
        Matrix matrix = new Matrix();
        matrix.setRotate(PROGRESS_START_ANGLE, mCenterX, mCenterY);
        shader.setLocalMatrix(matrix);
        mProgressPaint.setShader(shader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgressBg(canvas);
        drawProgress(canvas);
        drawProgressText(canvas);

        canvas.drawBitmap(mDegreeBitmap, 0, 0, null);
        canvas.rotate(mProgress * ARROW_RATIO, mCenterX, mCenterY);
        canvas.drawBitmap(mArrowBitmap, 0, 0, null);
    }

    private void drawProgressText(Canvas canvas) {
        canvas.translate(mCenterX, mCenterY);
        if (mProgress == 0) {
            canvas.drawText("0.00%", 0, (mProgressTextPaint.descent() - mProgressTextPaint.ascent())/2, mProgressTextPaint);
        }
        else  {
            canvas.drawText(df.format(mProgress) + "%", 0, (mProgressTextPaint.descent() - mProgressTextPaint.ascent())/2, mProgressTextPaint);

        }
        canvas.translate(-mCenterX, -mCenterY);
    }

    private void drawDegree(Canvas canvas) {
        canvas.save();

        canvas.translate(mCenterX, mCenterY);
        canvas.rotate(50);

        for (int i = 0; i < DEGREE_LINE_COUNT; i++) {
            if (i % 5 == 0) {
                canvas.drawLine(0, mLineStartY, 0, mLineEndY - dpToPx(3), mDegreeLinePaint);
            }
            else {
                canvas.drawLine(0, mLineStartY, 0, mLineEndY, mDegreeLinePaint);
            }
            canvas.rotate(ROTATE_DEGREE);
        }
        canvas.rotate(100 - ROTATE_DEGREE);
        canvas.translate(0, mLineEndY - mDegreeLineHeight - dpToPx(5));
        canvas.rotate(-50);
        canvas.drawText("0", 0, -mDegreeTextPaint.ascent()/2, mDegreeTextPaint);
        canvas.rotate(50);
        canvas.translate(0, -(mLineEndY - mDegreeLineHeight - dpToPx(5)));
        for (int i = 0; i < DEGREE_TEXT_COUNT; i++) {
            if (i == 0) {
                continue;
            }
            canvas.rotate(DEGREE_TEXT_ANGLE);
            canvas.translate(0, mLineEndY - mDegreeLineHeight - dpToPx(5));
            canvas.rotate(-50 - i * DEGREE_TEXT_ANGLE);
            if (i % 2 == 0) {
                canvas.drawText(String.valueOf(i * 5), 0, -mDegreeTextPaint.ascent() / 2, mDegreeTextPaint);
            } else {
                canvas.drawText(String.valueOf(i * 5), 0, -mDegreeLightTextPaint.ascent() / 2, mDegreeLightTextPaint);
            }
            canvas.rotate(50 + i * DEGREE_TEXT_ANGLE);
            canvas.translate(0, -(mLineEndY - mDegreeLineHeight - dpToPx(5)));
        }


        canvas.restore();
        canvas.translate(mCenterX, mCenterY);
        canvas.drawText(getResources().getString(R.string.default_title), 0, -mTitlePadding, mTitlePaint);

    }

    private void drawDegreeBg(Canvas canvas) {
        canvas.drawArc(mDegreeBgRect, DEGREE_START_ANGLE, DEGREE_SWEEP_ANGLE, false, mDegreeBgPaint);
    }

    private void drawProgress(Canvas canvas) {
        canvas.drawArc(mProgressRect, PROGRESS_START_ANGLE, mProgress * PROGRESS_RATIO, false, mProgressPaint);
    }

    private void drawProgressBg(Canvas canvas) {
        canvas.drawArc(mProgressRect, PROGRESS_START_ANGLE, PROGRESS_SWEEP_ANGLE, false, mProgressBgPaint);
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        mProgress = progress;
        invalidate();
    }

    public void starAnim() {
        Keyframe keyframe1 = Keyframe.ofFloat(0, 0); // 开始：progress 为 0
        Keyframe keyframe2 = Keyframe.ofFloat(0.5f, 100); // 进行到一半是，progres 为 100
        Keyframe keyframe3 = Keyframe.ofFloat(1, mProgress); // 结束时倒回到 80
        PropertyValuesHolder holder = PropertyValuesHolder.ofKeyframe("progress", keyframe1, keyframe2, keyframe3);

        if (mAnimator != null) {
            mAnimator.cancel();
        }
        else {
            mAnimator = ObjectAnimator.ofPropertyValuesHolder(this, holder);
            mAnimator.setDuration(2000);
            mAnimator.setInterpolator(new FastOutSlowInInterpolator());
        }

        mAnimator.start();
    }
}
