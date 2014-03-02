package com.theagilemonkeys.meets;

import android.util.Log;

import com.octo.android.robospice.Jackson2GoogleHttpClientSpiceService;

import roboguice.util.temp.Ln;

/**
 * Created by kloster on 22/11/13.
 */
public class MeetsSpiceService extends Jackson2GoogleHttpClientSpiceService {
    @Override
    public void onCreate() {
        super.onCreate();

        // Logging really causes the app to chug with this many requests
        Ln.getConfig().setLoggingLevel(Log.ERROR);
    }


    @Override
    public int getThreadCount() {
        return 8;
    }
}
