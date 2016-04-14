package com.cornford.games.pipepower;

import android.app.Activity;
import android.graphics.Canvas;
import android.renderscript.Sampler;
import android.util.Log;

/**
 * Created by kprotasov on 21.02.2015.
 */
public class AppLoop extends Thread {

    private SoundMeterView soundMeterView;
    private ValueMeter valueMeter;
    private boolean running = false;
    private int FPS = 10;
    private MainActivity activity;
    double dbLevel = 0;
    private int step = 0;
    private static final int START_LISTEN_MAX_DB_STEP = 25;

    public AppLoop(MainActivity activity, SoundMeterView soundMeterView, ValueMeter valueMeter){
        this.activity = activity;
        this.soundMeterView = soundMeterView;
        this.valueMeter = valueMeter;
        this.step = 0;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    @Override
    public void run(){
        long frameTime = 1000 / FPS;
        long startTime;
        long sleepTime;
        while(running){
            Canvas canvas = null;
            startTime = System.currentTimeMillis();
            try{
                dbLevel = valueMeter.getTestDbLevel();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (running) {
                            step++;
                            if (step >= START_LISTEN_MAX_DB_STEP) {
                                soundMeterView.setData((int) dbLevel, true);
                            }else{
                                soundMeterView.setData((int) dbLevel, false);
                            }
                            activity.setWarningLevel(MainActivity.WARNING_LEVEL_PLAY, (int) dbLevel);
                        }
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }
            sleepTime = frameTime - (System.currentTimeMillis() - startTime);
            try{
                if (sleepTime > 0){
                    sleep(sleepTime);
                }else{
                    sleep(10);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
