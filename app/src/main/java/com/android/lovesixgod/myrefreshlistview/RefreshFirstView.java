package com.android.lovesixgod.myrefreshlistview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * 第一步View
 * Created by Jaceli on 2016-04-15.
 */
public class RefreshFirstView extends View {

    private Bitmap firstBitmap;
    private Bitmap endBitmap;
    private float mCurrentProgress;

    public RefreshFirstView(Context context) {
        super(context);
        init(context);
    }

    public RefreshFirstView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshFirstView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        firstBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.pull_image));
        endBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pull_end_image_frame_05));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureWidth(widthMeasureSpec) * endBitmap.getHeight() / endBitmap.getWidth());
    }

    private int measureWidth(int widMeasureSpec) {
        int result;
        int size = MeasureSpec.getSize(widMeasureSpec);
        int mode = MeasureSpec.getMode(widMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = endBitmap.getWidth();
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.scale(mCurrentProgress, mCurrentProgress);
        canvas.drawBitmap(firstBitmap, 0, 0, null);
    }

    public void setCurrentProgress(float currentProgress) {
        this.mCurrentProgress = currentProgress;
    }

}
