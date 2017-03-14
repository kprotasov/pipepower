package com.cornford.games.pipepower;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by kprotasov on 19.02.2015.
 */
public class SoundMeterView extends View {

    private static final int DELTA_PLUS = 50;
    private static final int DELTA_MINUS = -DELTA_PLUS;
    private int alpha = DELTA_MINUS;
    private int delta = 0;

    private Bitmap speedometerBase;
    private Bitmap speedometerArrow;
    private Bitmap arrowCenter;
    private int degree;
    private int maxDb = 0;
    private Paint textPaint;
    private Paint levelPaint;
    private float textAreaHeight = 165;
    private String calculationString;

    public SoundMeterView (Context context){
        super(context);
        init();
    }

    public SoundMeterView (Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public SoundMeterView (Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        // load resources
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        calculationString = getContext().getString(R.string.calculation);
        speedometerBase = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.speedometer_base);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        speedometerArrow = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.speedometer_arrow, options);

        arrowCenter = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.arrow_center);

        textPaint = new Paint();
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Franklin Gothic Demi Cond Regular.ttf");
        textPaint.setTypeface(tf);
        final float dpScale = getResources().getDisplayMetrics().density;
        textPaint.setTextSize(20 * dpScale);
        textPaint.setColor(Color.argb(255, 0, 176, 227));

        levelPaint = new Paint();
        Typeface tf2 = Typeface.createFromAsset(getContext().getAssets(), "font/Franklin Gothic Demi Cond Regular.ttf");
        levelPaint.setTypeface(tf2);
        levelPaint.setTextSize(45);
        levelPaint.setColor(Color.parseColor("#FF89d2e7"));//levelPaint.setColor(Color.parseColor("#99e8fc"));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH){
        // resize relative width
        double scaleParam = (double)w / (double)speedometerBase.getWidth();
        int newBaseWidth = (int)(speedometerBase.getWidth() * scaleParam);
        int newBaseHeight = (int)(speedometerBase.getHeight() * scaleParam);
        speedometerBase = Bitmap.createScaledBitmap(speedometerBase, newBaseWidth, newBaseHeight, false);
        int newArrowWidth = (int)(speedometerArrow.getWidth() * scaleParam);
        int newArrowHeight = (int)(speedometerArrow.getHeight() * scaleParam);
        speedometerArrow = Bitmap.createScaledBitmap(speedometerArrow, newArrowWidth, newArrowHeight, false);
        int newCenterWidth = (int)(arrowCenter.getWidth() * scaleParam);
        int newCenterHeight = (int)(arrowCenter.getHeight() * scaleParam);
        arrowCenter = Bitmap.createScaledBitmap(arrowCenter, newCenterWidth, newCenterHeight, false);
        textAreaHeight = (float)((double)w / (double)704) * textAreaHeight;
    }

    public void setData(int dB, boolean listenMaxDb){
        // set current position in degrees, decibels value, then invalidate
        this.degree = dB;
        if (listenMaxDb == false){
            maxDb = 0;
        }else {
            if (dB > maxDb) {
                maxDb = dB;
            }
        }
        invalidate();
    }

    public void clearData(){
        this.degree = 0;
        this.maxDb = 0;
        invalidate();
    }

    public int getDb(){
        return degree;
    }

    public int getMaxDb(){
        return maxDb;
    }

    @Override
    public void onDraw(Canvas canvas){
        measureFps();
        canvas.drawBitmap(speedometerBase, 0, 0, null);
        float xcp = (float)((float)speedometerBase.getWidth() * 0.5 - (float)arrowCenter.getWidth() * 0.5);
        float ycp = (float)((float)speedometerBase.getHeight() * 0.5 - (float)arrowCenter.getHeight() * 0.5);
        canvas.drawBitmap(arrowCenter, xcp, ycp, null);
        float xsp = xcp + arrowCenter.getWidth() - speedometerArrow.getWidth();
        float ysp = ycp + arrowCenter.getHeight() - speedometerArrow.getHeight();
        //canvas.drawBitmap(speedometerArrow, xsp, ysp, null);
        Matrix m = new Matrix();
        //float pos = (float)(30 * 1.8);
        float pos = (float)(degree * 1.8);
        float degrees = -18 + pos;
        m.setTranslate(xsp, ysp);
        m.postRotate(degrees, (int) (xcp + arrowCenter.getWidth() * 0.5), (int) (ycp + arrowCenter.getHeight() * 0.5));
        canvas.drawBitmap(speedometerArrow, m, null);

        String dBText;
        if (maxDb <= 0) {
            if (alpha <= 0){
                delta = DELTA_PLUS;
            }else if (alpha >= 255){
                delta = DELTA_MINUS;
            }
            alpha += delta;
            if (alpha > 255){
                alpha = 255;
            }
            if (alpha < 0){
                alpha = 0;
            }

            Log.v("SoundMeterValue", "maxDb " + maxDb + " alpha " + alpha);
            textPaint.setColor(Color.argb(alpha, 0, 176, 227));
            dBText = calculationString;

        }else{
            textPaint.setColor(Color.argb(255, 0, 176, 227));
            dBText ="max " + String.valueOf(maxDb) + " dB";
        }
        Rect textRect = new Rect();
        textPaint.getTextBounds(dBText, 0, dBText.length() - 1, textRect);
        canvas.drawText(dBText, (float) (speedometerBase.getWidth() * 0.5 - textRect.width() * 0.5),
                (float) (speedometerBase.getHeight() - textAreaHeight * 0.5 + textRect.height() * 0.5), textPaint);

        Rect levelRect = new Rect();
        String dBLevel = String.valueOf(degree) + " dB";
        levelPaint.getTextBounds(dBLevel, 0, dBLevel.length() - 1, levelRect);
        float levelPosX = xcp - (float)(levelRect.width() * 0.5);
        float levelPosY = ycp + (float)(speedometerBase.getHeight() * 0.25) - (float)(levelRect.height() * 0.5);
        canvas.drawText(dBLevel, levelPosX, levelPosY, levelPaint);

    }

    long curTime = 0;
    long deltaTime = 0;
    long prevTime = 0;
    long aproxFps = 0;
    private void measureFps() {
        curTime = System.currentTimeMillis();
        deltaTime = curTime - prevTime;
        aproxFps = (long)1000 / deltaTime;
        prevTime = curTime;
    }
}
