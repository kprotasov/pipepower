package com.cornford.games.pipepower;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by kprotasov on 26.07.2016.
 */
public class AppApplication extends Application{

    private Tracker tracker;

    synchronized public Tracker getTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker("UA-81326163-1");
        }
        return tracker;
    }

}
