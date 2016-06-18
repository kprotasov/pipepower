package com.cornford.games.pipepower;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by kprotasov on 14.02.2015.
 */
public class ValueMeter {

    private static double EMA = 0.0;
    private static final double EMA_FILTER = 0.6;
    private MediaRecorder mediaRecorder;
    private AudioRecord rec;
    private double dbLevel;
    private static final int SAMPLE_RATE_IN_HZ = 44100;

    int bufferSize;

    private int bufferElementsToRec = 1024;
    private int bufferPerElement = 2;
    private boolean isRecording = false;
    private Thread recordingThread;

    public void start(){
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
        bufferSize = bufferSize * 4;
        rec = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        rec.startRecording();
    }

    public void stopRecording() {
        isRecording = false;
        recordingThread = null;
    }

    public void stop(){
        rec.stop();
        rec.release();
    }

    public double getTestDbLevel(){
        short data[] = new short[bufferSize];
        double average = 0.0;
        rec.read(data, 0, bufferSize);
        /**for (short s : data){
            if (s > 0){
                average += Math.abs(s);
            }else{
                bufferSize --;
            }
        }*/int max = 0;
        for (short s : data)
        {
            if (Math.abs(s) > max)
            {
                max = Math.abs(s);
            }
        }
        double x = max;
        EMA = EMA_FILTER * x + (1.0 - EMA_FILTER) * EMA; // скользящее среднее
        double db = 0.0;
        double p = EMA / 51805.5336;
        double p0 = 0.00002;
        db = 20 * Math.log10(p / p0);
        dbLevel = db;
        return db;
    }

    public double getDbLevelAsync(){
        return dbLevel;
    }

    public double getDbLevel(){
        int bufferSizeS = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
        bufferSizeS = bufferSizeS * 4;
        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT, bufferSizeS);
        short data[] = new short[bufferSizeS];
        double average = 0.0;
        recorder.startRecording();
        recorder.read(data, 0, bufferSizeS);
        recorder.stop();
        for (short s : data){
            if (s > 0){
                average += Math.abs(s);
            }else{
                bufferSizeS --;
            }
        }
        double x = average / bufferSizeS;
        recorder.release();
        double db = 0.0;
        double p = x / 51805.5336;
        double p0 = 0.00002;
        db = 20 * Math.log10(p / p0);
        return db;
    }

    public double getDbAmplitude(){
        double amp = mediaRecorder.getMaxAmplitude();
        EMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * EMA;
        double p = mediaRecorder.getMaxAmplitude() / 51805.5336;
        double p0 = 0.00002;
        return 20 * Math.log10(p / p0);
    }

}
