package com.cornford.games.pipepower.test;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by kprotasov on 13.06.2016.
 */
public class RecordManager {

    private MediaRecorder recorder;
    private MediaPlayer player;

    private boolean playingStarted = false;
    private boolean recordingStarted = false;

    public void startPlaying(final String filePath) {
        player = new MediaPlayer();
        try{
            player.setDataSource(filePath);
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mediaPlayer) {
                    playingStarted = true;
                    player.start();
                }
            });
        }catch(final IOException e) {
            e.printStackTrace();
        }

    }

    public void stopPlaying() {
        if (player != null) {
            player.release();
            player = null;
        }
        playingStarted = false;
    }

    private AudioRecord rec;
    final short[] buffer = new short[4088];
    boolean isRecording = false;
    int bufferSize;
    private static final int SAMPLE_RATE_IN_HZ = 44100;
    public void startRecordTest(final String fileName) {
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
        bufferSize = bufferSize * 4;
        rec = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        rec.startRecording();
        isRecording = true;
        final Thread recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile(fileName);
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private void writeAudioDataToFile(final String filename) {
        byte data[] = new byte[bufferSize];
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int read = 0;
        while (isRecording) {
            rec.read(buffer, 0, buffer.length);
            if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                try {
                    byte[] bytes2 = new byte[buffer.length * 2];
                    ByteBuffer.wrap(bytes2).order(ByteOrder.LITTLE_ENDIAN)
                            .asShortBuffer().put(buffer);
                    os.write(bytes2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecordingTest() {
        if (null != rec) {
            isRecording = false;

            rec.stop();
            rec.release();

            rec = null;
        }
    }

    public void startRecording(final String fileName) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(fileName);
        try {
            recorder.prepare();
            recorder.start();
            recordingStarted = true;
        }catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingStarted = false;
        }
    }

}
