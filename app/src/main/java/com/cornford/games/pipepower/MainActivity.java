package com.cornford.games.pipepower;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cornford.games.pipepower.storevalues.SoundValuesActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

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
    private AppLoop appLoop;
    private MainActivity activity;
    private int START_LISTEN_DELAY = 3;

    private static final int RATE_SHOW_1 = 0;
    private static final int RATE_SHOW_2 = 5;
    private static final int RATE_SHOW_3 = 15;

    private boolean isFirstStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        valueMeter = new ValueMeter();
        mainContainer = (LinearLayout) findViewById(R.id.mainContainer);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        soundMeterView = (SoundMeterView) findViewById(R.id.soundMeterView);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("FBF9D9A996CCF942022738FDB7816B1E").build();
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

        warningLayout = (LinearLayout) findViewById(R.id.warningLayout);
        warningText = (TextView) findViewById(R.id.warningText);
        levelText = (TextView) findViewById(R.id.levelText);
        historyButton = (ImageButton) findViewById(R.id.open_history_button);
        warningLayout.setVisibility(View.VISIBLE);
        /*if(isRussianLocale()){
            warningLayout.setVisibility(View.VISIBLE);
        }else{
            warningLayout.setVisibility(View.GONE);
        }*/

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
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    appLoop = new AppLoop(activity, soundMeterView, valueMeter);
                                    valueMeter.start();
                                    soundMeterView.clearData();
                                    appLoop.setRunning(true);
                                    if (appLoop.getState() == Thread.State.NEW) {
                                        appLoop.start();

                                    }
                                }
                            });
                        }
                    }, START_LISTEN_DELAY * 100);

                } else {
                    //valueMeter.stop();
                    stopWork();
                    if (getShownCount() == RATE_SHOW_1) {
                        if (getIsNeverShow() == false) {
                            showRateThisAppDialog();
                        }
                    }
                    if (getShownCount() == RATE_SHOW_2) {
                        if (getIsNeverShow() == false) {
                            showRateThisAppDialog();
                        }
                    }
                    if (getShownCount() == RATE_SHOW_3) {
                        if (getIsNeverShow() == false) {
                            showRateThisAppDialog();
                            saveIsNeverShow();
                        }
                    }
                    if (isFirstStart == true) {
                        increaseShowCount();
                        isFirstStart = false;
                    }
                    //showRateThisAppDialog();
                    //saveToDatabase(String.valueOf(soundMeterView.getMaxDb()));
                }
            }
        });
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

    private boolean isRussianLocale() {
        String lang = Locale.getDefault().getLanguage();
        if (lang.equals("ru")) {
            return true;
        } else {
            return false;
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
            levelText.setTextColor(getResources().getColor(R.color.low_level_color));
        } else if (dbLevel > DB_LEVEL_LOW && dbLevel <= DB_LEVEL_MIDDLE) {
            levelText.setText(R.string.middle_level);
            levelText.setTextColor(getResources().getColor(R.color.middle_level_color));
        } else if (dbLevel > DB_LEVEL_MIDDLE && dbLevel <= DB_LEVEL_HIGH) {
            levelText.setTextColor(getResources().getColor(R.color.low_high_level_color));
            levelText.setText(R.string.low_high_level);
        } else if (dbLevel > DB_LEVEL_HIGH && dbLevel < DB_LEVEL_VERY_HIGH) {
            levelText.setTextColor(getResources().getColor(R.color.high_level_color));
            levelText.setText(R.string.high_level);
        } else if (dbLevel > DB_LEVEL_VERY_HIGH) {
            levelText.setTextColor(getResources().getColor(R.color.very_high_color));
            levelText.setText(R.string.very_high_level);
        }
    }

    private void showRateThisAppDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.rate_app_dialog, null);
        builder.setView(view);
        final Button rateNow = (Button) view.findViewById(R.id.rate_now);
        final Dialog dialog = builder.create();
        rateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.cornford.games.pipepower"));
                if (!startIntent(intent)) {
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.cornford.games.pipepower"));
                    saveIsNeverShow();
                    if (!startIntent(intent)) {
                        Toast.makeText(MainActivity.this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
            }
        });
        final Button rateNotNow = (Button) view.findViewById(R.id.rate_not_now);
        rateNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dialog.dismiss();
            }
        });
        final Button rateNever = (Button) view.findViewById(R.id.rate_never);
        rateNever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                saveIsNeverShow();
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
