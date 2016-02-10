package com.ground0.breadcrumbview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by zer0 on 8/2/16.
 */
public class BreadCrumbView extends View {

  int mParentHeight;
  int mParentWidth;

  Resources.Theme mTheme;
  //Resources required are obtained from theme
  int colorControlActivated, colorControlNormal, textColorActivated, textColorNormal;

  Paint borderPaint = new Paint();

  public int getCurrentStep() {
    return mCurrentStep;
  }

  public void setCurrentStep(int currentStep) {
    this.mCurrentStep = currentStep;
    invalidate();
  }

  public void updateStep() {
    mCurrentStep++;
    invalidate();
  }

  //Following hardcoded values must be obtained from the xml
  int mCurrentStep = 1;
  int mTotalSteps = 2;
  float mRadius = 80;
  float mBorderWidth = 0;

  public BreadCrumbView(Context context) {
    super(context);
    initVariables(context);
  }

  public BreadCrumbView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initVariables(context);
    initAttr(context, attrs);
  }

  public BreadCrumbView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initVariables(context);
    initAttr(context, attrs);
  }

  private void initAttr(Context context, AttributeSet attrs) {
    TypedArray typedArray =
        context.getTheme().obtainStyledAttributes(attrs, R.styleable.BreadCrumbView, 0, 0);
    try {
      mCurrentStep = typedArray.getInt(R.styleable.BreadCrumbView_bcCurrentStep, 0);
      mTotalSteps = typedArray.getInt(R.styleable.BreadCrumbView_bcTotalSteps, 2);
      mRadius = typedArray.getFloat(R.styleable.BreadCrumbView_bcRadius, 80);
      mBorderWidth = typedArray.getFloat(R.styleable.BreadCrumbView_bcBorderWidth, 0);
      textColorActivated = ContextCompat.getColor(context,
          typedArray.getResourceId(R.styleable.BreadCrumbView_bcTextColorActivated,
              android.R.color.primary_text_light));
      textColorNormal = ContextCompat.getColor(context,
          typedArray.getResourceId(R.styleable.BreadCrumbView_bcTextColorNormal,
              android.R.color.primary_text_light));
    } finally {
      typedArray.recycle();
    }
  }

  private void initVariables(Context context) {
    //We will obtain the default colors of the theme

    mTheme = context.getTheme();
    TypedValue typedValue = new TypedValue();
    mTheme.resolveAttribute(R.attr.colorButtonNormal, typedValue, true);
    colorControlNormal = typedValue.data;
    mTheme.resolveAttribute(R.attr.colorControlActivated, typedValue, true);
    colorControlActivated = typedValue.data;
    mTheme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
    textColorActivated = textColorNormal = typedValue.data;

    borderPaint.setStrokeWidth(mBorderWidth);
    borderPaint.setStyle(Paint.Style.STROKE);
    borderPaint.setAntiAlias(true);
    borderPaint.setColor(0x20000000);

  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    mParentHeight = MeasureSpec.getSize(heightMeasureSpec);
    mParentWidth = MeasureSpec.getSize(widthMeasureSpec);
    int expandSpec = MeasureSpec.makeMeasureSpec((int)( 2 * mRadius),
        MeasureSpec.AT_MOST);
    super.onMeasure(widthMeasureSpec, expandSpec);
    mParentWidth = (int) ((float)mParentWidth - 2 * mRadius);
  }

  @Override protected void onDraw(Canvas canvas) {

    if (mCurrentStep > mTotalSteps) mCurrentStep = mTotalSteps;
    drawInactiveCrumbs(canvas);
    drawActiveCrumbs(canvas);
  }

  /*We will start drawing from the left since that is how the breadcrumbs progress*/
  private void drawActiveCrumbs(Canvas canvas) {
    Paint paint1 = new Paint();
    paint1.setAntiAlias(true);
    paint1.setColor(colorControlActivated);

    Paint paint2 = new Paint();
    paint2.setTextAlign(Paint.Align.CENTER);
    paint2.setColor(textColorActivated);
    paint2.setTextSize(mRadius);

    drawCrumbs(canvas, paint1, paint2, 0, mCurrentStep);
  }

  private void drawInactiveCrumbs(Canvas canvas) {
    Paint paint1 = new Paint();
    paint1.setColor(colorControlNormal);

    Paint paint2 = new Paint();
    paint2.setAntiAlias(true);
    paint2.setTextAlign(Paint.Align.CENTER);
    paint2.setColor(textColorNormal);
    paint2.setTextSize(mRadius);

    drawCrumbs(canvas, paint1, paint2, mCurrentStep, mTotalSteps);
  }

  private void drawCrumbs(Canvas canvas, Paint shapePaint, Paint textPaint, int from, int to) {

    if (mTotalSteps > 1) {
      float rLeft, rRight, rTop = 2 * mRadius / 3, rBottom = 4 * mRadius / 3;
      rLeft = (from == 0 ? from : from - 1) * mParentWidth / (mTotalSteps - 1) + mRadius / 2;
      rRight = (to - 1) * mParentWidth / (mTotalSteps - 1) - mRadius / 2;
      canvas.drawRect(mRadius + rLeft, rTop, mRadius + rRight, rBottom, shapePaint);
      //canvas.drawRect(mRadius + rLeft, rTop + mBorderWidth, mRadius + rRight, rBottom - mBorderWidth, borderPaint);
      Paint paint = new Paint();
      paint.setColor(0x11000000);
      canvas.drawRect(mRadius + rLeft, rTop, mRadius + rRight, rBottom, paint);

      float cx, cy = mRadius;
      for (int i = from; i < to; i++) {
        cx = mRadius + i * (mParentWidth) / (mTotalSteps - 1);
        canvas.drawCircle(cx, cy, mRadius, shapePaint);
        canvas.drawCircle(cx, cy, mRadius - mBorderWidth, borderPaint);
        canvas.drawText("" + (i + 1), cx, cy + textPaint.getTextSize() / 3, textPaint);
      }
    }
  }
}
