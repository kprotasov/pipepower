package com.cornford.games.pipepower;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kprotasov on 18.06.2016.
 */
public class SoundPlayer {

    public static void playSound(final String fileName) {
        boolean isPlaying = true;
        final int bufferSize = AudioTrack.getMinBufferSize(ValueMeter.SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        Log.v("BufferSize", "before buffer size " + bufferSize);
        short[] audioData = new short[bufferSize / 4];
        final File soundFile = new File(fileName);
        try {
            final DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(soundFile)));
            Log.v("BufferSize", "buffer size " + bufferSize);
            final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, bufferSize, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
            audioTrack.play();
            while(isPlaying && dataInputStream.available() > 0) {
                int i = 0;
                while(dataInputStream.available() > 0 && i < audioData.length) {
                    audioData[i] = dataInputStream.readShort();
                    i++;
                }
                audioTrack.write(audioData, 0, audioData.length);
            }
            dataInputStream.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void playSoundTest(final String fileName) {
        final File soundFile = new File(fileName);
        final int shortSizeInBytes = Short.SIZE / Byte.SIZE;
        final int bufferSizeInBytes = (int) (soundFile.length() / shortSizeInBytes);
        final short[] audioData = new short[bufferSizeInBytes];
        try {
            final InputStream inputStream = new FileInputStream(soundFile);
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            final DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            int i = 0;
            while (dataInputStream.available() > 0) {
                audioData[i] = dataInputStream.readShort();
                i++;
            }
            dataInputStream.close();
            final AudioTrack audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    ValueMeter.SAMPLE_RATE_IN_HZ,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSizeInBytes,
                    AudioTrack.MODE_STREAM);
            audioTrack.play();
            audioTrack.write(audioData, 0, bufferSizeInBytes);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
