package com.cornford.games.pipepower.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppDataStore {

    /*
    private static final String IS_NEWER_SHOW = "IS_NEWER_SHOW";

    private boolean getIsNeverShow() { // не показывать больше никогда
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        return preferences.getBoolean(IS_NEWER_SHOW, false);
    }

    private void saveIsNeverShow() {
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().putBoolean(IS_NEWER_SHOW, true).apply();
    }
     */

    private static final String PREFERENCES_NAME = "PIPE_POWER_PREFERENCES_FILE";
    private static final String IS_PROMO_SHOWN = "IS_PROMO_SHOWN";

    public static boolean getIsPromoShown(final Activity activity) {
        final SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(IS_PROMO_SHOWN, false);
    }

    public static void setIsPromoShown(final Activity activity, final boolean isShown) {
        final SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(IS_PROMO_SHOWN, isShown).apply();
    }

}
