package com.cornford.games.pipepower.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
//import android.support.annotation.Nullable;
//import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.cornford.games.pipepower.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

public class SoundGraphView extends View implements ViewTreeObserver.OnGlobalLayoutListener {

    public static final int DB_LEVEL_LOW = 50;
    public static final int DB_LEVEL_MIDDLE = 65;
    public static final int DB_LEVEL_HIGH = 75;
    public static final int DB_LEVEL_VERY_HIGH = 85;

    private final int COLOR_NORMAL = Color.rgb( 52, 183, 235);
    private final int COLOR_LOW = getResources().getColor(R.color.low_level_color);
    private final int COLOR_MIDDLE = getResources().getColor(R.color.low_high_level_color);
    private final int COLOR_HIGH = getResources().getColor(R.color.high_level_color);
    private final int COLOR_VERY_HIGH = getResources().getColor(R.color.very_high_color);

    private final float MAX_HEIGHT = 120f;
    private final int INITIAL_VALUE = 0;
    private final int ROW_OFFSET = 2;
    private final int rowCount = 20;
    private final List<Integer> rowsList = new ArrayList<>(rowCount);
    private float oneRowWidth;
    private float scaleType;
    private float viewHeight;
    private float viewWidth;
    private final List<Paint> rowPaintList = new ArrayList<>(rowCount);

    final Path path = new Path();
    private LinearGradient gradient;

    public SoundGraphView(Context context) {
        super(context);
        init();
    }

    public SoundGraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SoundGraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.getViewTreeObserver().addOnGlobalLayoutListener(this);
        for (int i = 0; i < rowCount; i++) {
            rowsList.add(INITIAL_VALUE);
        }
        for (int i = 0; i < rowCount; i++) {
            rowPaintList.add(new Paint());
        }
    }

    public void addNewValue(final int newValue) {
        for(int i = 0; i < rowsList.size() - 1; i++) {
            rowsList.set(i, rowsList.get(i + 1));
        }
        rowsList.set(rowsList.size() - 1, newValue);
        invalidate();
    }

    @Override
    public void onGlobalLayout() {
        oneRowWidth = (getWidth() / rowCount) - ROW_OFFSET / 2;
        scaleType = (float)getHeight() / MAX_HEIGHT;
        viewHeight = getHeight();
        viewWidth = getWidth();
        path.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, Path.Direction.CCW);

        Log.v("SoundGroupView", "viewHeight " + viewHeight);
        gradient = new LinearGradient(0, viewHeight, 0, viewHeight - 400,
                new int []{Color.parseColor("#6B00FE"), Color.parseColor("#cc00ff"), Color.parseColor("#F44FB2")},
                new float[]{0, 0.5f, 1}, Shader.TileMode.CLAMP);
    }

    final Path dbPath = new Path();
    final RectF rectF = new RectF();
    final Paint testPaint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(path);
        super.onDraw(canvas);
        if (oneRowWidth == 0 || rowsList.size() <= 0 || rowPaintList.size() <= 0) {
            return;
        }
        for (int i = 0; i < rowCount; i++) {
            Paint paint = rowPaintList.get(i);
            final int dbValue = rowsList.get(i);

            float height = dbValue * scaleType;
            if (height > viewHeight) {
                height = viewHeight;
            }

            //paint.setColor(generateColor(dbValue));

            /*gradient = new LinearGradient(0, 0, oneRowWidth - ROW_OFFSET * 2, height,
                    Color.parseColor("#6B00FE"), generateColor(dbValue), Shader.TileMode.MIRROR);*/
            paint.setShader(gradient);

            /*testPaint.setAlpha(100);
            testPaint.setColor(Color.RED);
            canvas.drawRect(0, 0, viewWidth, viewHeight, testPaint);*/

            rectF.set((i * oneRowWidth) + ROW_OFFSET, viewHeight - height, (oneRowWidth * i + oneRowWidth) - ROW_OFFSET, viewHeight);
            canvas.drawRoundRect(rectF, oneRowWidth/2, oneRowWidth/2, paint);
            //canvas.drawRect((i * oneRowWidth) + ROW_OFFSET, viewHeight - height, (oneRowWidth * i + oneRowWidth) - ROW_OFFSET, viewHeight, paint);
        }
    }

    private int generateColor(final int dbValue) {
        return ColorUtils.blendARGB(Color.parseColor("#6B00FE"), Color.parseColor("#F44FB2"), ((float)dbValue / 120.0F));
    }

}
