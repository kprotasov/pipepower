package com.cornford.games.pipepower;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.cornford.games.pipepower.data.AppDataStore;
import com.cornford.games.pipepower.data.SoundValueContract;
import com.cornford.games.pipepower.data.SoundValueDbHelper;
import com.cornford.games.pipepower.graph.SoundGraphView;
import com.cornford.games.pipepower.storevalues.SoundValuesActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.ColorUtils;


public class MainActivity extends AppCompatActivity {

    private static final int SOUND_METER_DEF_WIDTH = 432;
    private static final int SOUND_METER_DEF_HEIGHT = 450;
    private static final int TOGGLE_BUTTON_DEF_WIDTH = 180;
    private static final int TOGGLE_BUTTON_DEF_HEIGHT = 180;
    private static final int DISPLAY_DEF_WIDTH = 480;
    private static final int DISPLAY_DEF_HEIGHT = 854;

    public static final int WARNING_LEVEL_PLAY = 1001;
    public static final int WARNING_LEVEL_STOP = 1002;

    private static final float AD_VIEW_HEIGHT = 50.0f;

    public static final int DB_LEVEL_LOW = 50;
    public static final int DB_LEVEL_MIDDLE = 65;
    public static final int DB_LEVEL_HIGH = 75;
    public static final int DB_LEVEL_VERY_HIGH = 85;
    private LinearLayout mainContainer;
    private ValueMeter valueMeter;
    private LinearLayout warningLayout;
    private TextView warningText;
    private TextView levelText;
    private ImageButton historyButton;
    private Timer timer;
    private SoundMeterView soundMeterView;
    private ToggleButton toggleButton;
    private ToggleButton playPauseButton;
    private TextView soundValueTextView;
    private TextView maxDbTextView;
    private AppLoop appLoop;
    private MainActivity activity;
    private int START_LISTEN_DELAY = 3;

    private static final int RATE_SHOW_1 = 0;
    private static final int RATE_SHOW_2 = 5;
    private static final int RATE_SHOW_3 = 15;

    private boolean isFirstStart = true;

    private String currentSoundPath;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    //private FirebaseAnalytics firebaseAnalytics;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length <= 0) {
            return;
        }
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        //firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //logScreenOpened();

        setContentView(R.layout.activity_main);
        valueMeter = new ValueMeter();
        mainContainer = findViewById(R.id.mainContainer);
        toggleButton = findViewById(R.id.toggleButton);
        playPauseButton = findViewById(R.id.recordButton);
        soundMeterView = findViewById(R.id.soundMeterView);
        soundValueTextView = findViewById(R.id.soundValueTextView);
        maxDbTextView = findViewById(R.id.maxDbTextView);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("DEE6B64D413427EA2E32503865D21D83").build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainContainer.requestLayout();
                    }
                });
            }
        });

        warningLayout = findViewById(R.id.warningLayout);
        warningText = findViewById(R.id.warningText);
        levelText = findViewById(R.id.levelText);
        historyButton = findViewById(R.id.open_history_button);
        //warningLayout.setVisibility(View.VISIBLE);

        float scaleParam = 1;
        float widthScale = ((float) getScreenWidth() / (float) DISPLAY_DEF_WIDTH);
        float heightScale = (((float) getScreenHeight() - AD_VIEW_HEIGHT) / (float) DISPLAY_DEF_HEIGHT);
        // берем наименьший. иначе может не влезть
        if (widthScale > heightScale) {
            scaleParam = heightScale;
        } else {
            scaleParam = widthScale;
        }

        ViewGroup.LayoutParams soundMeterParams = soundMeterView.getLayoutParams();
        soundMeterParams.width = (int) (SOUND_METER_DEF_WIDTH * scaleParam);
        soundMeterParams.height = (int) (SOUND_METER_DEF_HEIGHT * scaleParam);
        soundMeterView.setLayoutParams(soundMeterParams);

        ViewGroup.LayoutParams toggleButtonParams = toggleButton.getLayoutParams();
        toggleButtonParams.width = (int) (TOGGLE_BUTTON_DEF_WIDTH * scaleParam);
        toggleButtonParams.height = (int) (TOGGLE_BUTTON_DEF_HEIGHT * scaleParam);
        toggleButton.setLayoutParams(toggleButtonParams);

        final SoundGraphView soundGraphView = findViewById(R.id.soundGraphView);

        activity = this;
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Intent intent = new Intent(MainActivity.this, SoundValuesActivity.class);
                startActivity(intent);
            }
        });
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    currentSoundPath = FileUtils.createFile();
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    appLoop = new AppLoop(activity, soundMeterView, soundGraphView, valueMeter);
                                    valueMeter.start(currentSoundPath);
                                    soundMeterView.clearData();
                                    appLoop.setRunning(true);
                                    if (appLoop.getState() == Thread.State.NEW) {
                                        appLoop.start();

                                    }
                                    startRecord();
                                }
                            });
                        }
                    }, START_LISTEN_DELAY * 100);

                } else {
                    //valueMeter.stop();
                    stopWork();
                    if (getShownCount() == RATE_SHOW_1) {
                        if (!getIsNeverShow()) {
                            showRateThisAppDialog();
                        }
                    }
                    if (getShownCount() == RATE_SHOW_2) {
                        if (!getIsNeverShow()) {
                            showRateThisAppDialog();
                        }
                    }
                    if (getShownCount() == RATE_SHOW_3) {
                        if (!getIsNeverShow()) {
                            showRateThisAppDialog();
                            saveIsNeverShow();
                        }
                    }
                    if (isFirstStart) {
                        increaseShowCount();
                        isFirstStart = false;
                    }
                    if (getIsNeverShow()) {
                        showPromoIfNeeded(false);
                    }
                }
            }
        });
        playPauseButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                SoundPlayer.playSound(currentSoundPath);
            }
        });
    }

    /*private void logScreenOpened() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, getClass().getSimpleName());
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, getClass().getSimpleName());
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }*/

    public void setDbValueText(final String dbLevel) {
        soundValueTextView.setText(dbLevel);
        maxDbTextView.setText(soundMeterView.getMaxDb() + "");
        maxDbTextView.setTextColor(generateMaxDbColor(soundMeterView.getMaxDb()));
    }

    private int generateMaxDbColor(final int dbValue) {
        return ColorUtils.blendARGB(Color.parseColor("#F44FB2"), Color.parseColor("#6B00FE"), ((float)dbValue / 120.0F));
    }

    private void startRecord() {
        // start recording and stop when 15 sec left
        //recordManager.startRecordTest(FileUtils.createFile());
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        valueMeter.stopRecording();
                    }
                }).start();
            }
        }, Constants.getRecordingLength());

    }

    private int getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    private int getScreenHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    private void stopWork() {
        boolean retry = true;
        if (appLoop != null) appLoop.setRunning(false);
        valueMeter.stop();
        int maxDb = soundMeterView.getMaxDb();
        if (maxDb > 0) saveToDatabase(maxDb);
        setWarningLevel(WARNING_LEVEL_STOP, maxDb);
        while (retry) {
            try {
                appLoop.join();
                appLoop.interrupt();
                retry = false;
                soundMeterView.setData(0, true);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

    }

    public void setWarningLevel(int warningLevel, int dbLevel) {
        if (warningLevel == WARNING_LEVEL_STOP) {
            warningText.setText(getResources().getString(R.string.noise_level_max));
        } else {
            warningText.setText(getResources().getString(R.string.noise_level));
        }
        if (dbLevel >= 0 && dbLevel <= DB_LEVEL_LOW) {
            levelText.setText(R.string.low_level);
            //levelText.setTextColor(getResources().getColor(R.color.low_level_color));
        } else if (dbLevel > DB_LEVEL_LOW && dbLevel <= DB_LEVEL_MIDDLE) {
            levelText.setText(R.string.middle_level);
            //levelText.setTextColor(getResources().getColor(R.color.middle_level_color));
        } else if (dbLevel > DB_LEVEL_MIDDLE && dbLevel <= DB_LEVEL_HIGH) {
            //levelText.setTextColor(getResources().getColor(R.color.low_high_level_color));
            levelText.setText(R.string.low_high_level);
        } else if (dbLevel > DB_LEVEL_HIGH && dbLevel < DB_LEVEL_VERY_HIGH) {
            //levelText.setTextColor(getResources().getColor(R.color.high_level_color));
            levelText.setText(R.string.high_level);
        } else if (dbLevel > DB_LEVEL_VERY_HIGH) {
            //levelText.setTextColor(getResources().getColor(R.color.very_high_color));
            levelText.setText(R.string.very_high_level);
        }
        levelText.setTextColor(generateColor(dbLevel));
    }

    private int generateColor(final int dbValue) {
        return ColorUtils.blendARGB(Color.parseColor("#6B00FE"), Color.parseColor("#F44FB2"), ((float)dbValue / 120.0F));
    }

    private void showPromoIfNeeded(final boolean fromRateApp) {
        if (AppDataStore.getIsPromoShown(this)) {
            return;
        }
        if (fromRateApp) {
            if (getIsNeverShow()) {
                showPromoDialog();
            }
        } else {
            showPromoDialog();
        }
    }

    private void showPromoDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.promo_layout, null);
        builder.setView(view);
        final Dialog dialog = builder.create();

        final ImageView promoImageView = view.findViewById(R.id.promoImage);
        final ImageView closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final Button downloadButton = view.findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPromoApp();
                dialog.dismiss();
            }
        });

        Glide.with(this).load(R.raw.promo).into(promoImageView);
        dialog.setCancelable(false);
        dialog.show();
        AppDataStore.setIsPromoShown(this, true);
    }

    private void openPromoApp() {
        final String appPackageName = "com.capcorn.twozerofe";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void showRateThisAppDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.rate_app_dialog, null);
        builder.setView(view);
        final Button rateNow = view.findViewById(R.id.rate_now);
        final Dialog dialog = builder.create();
        rateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.cornford.games.pipepower"));
                if (!startIntent(intent)) {
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.cornford.games.pipepower"));
                    if (!startIntent(intent)) {
                        Toast.makeText(MainActivity.this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                    }
                }
                saveIsNeverShow();
                dialog.dismiss();
            }
        });
        final Button rateNotNow = view.findViewById(R.id.rate_not_now);
        rateNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showPromoIfNeeded(false);
                dialog.dismiss();
            }
        });
        final Button rateNever = view.findViewById(R.id.rate_never);
        rateNever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                saveIsNeverShow();
                showPromoIfNeeded(true);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private boolean startIntent(final Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    private void saveToDatabase(final int value) {
        final SoundValueDbHelper dbHelper = new SoundValueDbHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues contentValues = new ContentValues();
        contentValues.put(SoundValueContract.SoundValueEntry.COLUMN_NAME_DATE, System.currentTimeMillis());
        contentValues.put(SoundValueContract.SoundValueEntry.COLUMN_NAME_SOUND_VALUE, value);
        db.insert(SoundValueContract.SoundValueEntry.TABLE_NAME, null, contentValues);
        dbHelper.close();
    }

    private static final String IS_NEWER_SHOW = "IS_NEWER_SHOW";

    private boolean getIsNeverShow() { // не показывать больше никогда
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        return preferences.getBoolean(IS_NEWER_SHOW, false);
    }

    private void saveIsNeverShow() {
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().putBoolean(IS_NEWER_SHOW, true).apply();
    }

    private static final String SHOWN_COUNT = "SHOWN_COUNT";

    private int getShownCount() { // сколько раз было показано
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        return preferences.getInt(SHOWN_COUNT, 0);
    }

    private void increaseShowCount() {
        int shownCount = getShownCount();
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        shownCount++;
        preferences.edit().putInt(SHOWN_COUNT, shownCount).apply();
    }

}
