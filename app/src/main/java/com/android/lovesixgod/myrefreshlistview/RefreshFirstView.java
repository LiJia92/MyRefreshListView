package com.android.lovesixgod.myrefreshlistview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Jaceli on 2016-04-15.
 */
public class RefreshFirstView extends View {

    private Bitmap firstBitmap;
    private float mCurrentProgress;

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
