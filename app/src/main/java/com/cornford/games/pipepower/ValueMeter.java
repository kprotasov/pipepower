package com.cornford.games.pipepower;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by kprotasov on 14.02.2015.
 */
public class ValueMeter {

    public static final int SAMPLE_RATE_IN_HZ = /*11025;*/44100;
    private static double EMA = 0.0;
    private static final double EMA_FILTER = 0.6;
    private MediaRecorder mediaRecorder;
    private AudioRecord rec;
    private double dbLevel;

    int bufferSize;

    private int bufferElementsToRec = 1024;
    private int bufferPerElement = 2;
    private boolean isRecording = false;
    private Thread recordingThread;

    private String filePath;

    public void start(final String fileName){
        this.filePath = fileName;
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
        //bufferSize = bufferSize * 4;
        rec = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        rec.startRecording();
        //startRecording();
    }

    private void startRecording() {
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        });
        recordingThread.start();
    }

    private void writeAudioDataToFile() {
        try {
            final OutputStream outputStream = new FileOutputStream(filePath);
            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            final DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
            short[] audioData = new short[bufferSize];
            while(isRecording) {
                final int numberOfShort = rec.read(audioData, 0, bufferSize);
                for (int i = 0; i < numberOfShort; i++) {
                    dataOutputStream.writeShort(audioData[i]);
                }
            }
            dataOutputStream.close();
        }catch (final IOException e) {
            e.printStackTrace();
        }
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
        int max = 0;
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

}
