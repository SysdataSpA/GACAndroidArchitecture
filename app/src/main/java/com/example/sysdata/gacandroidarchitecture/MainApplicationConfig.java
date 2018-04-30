package com.example.sysdata.gacandroidarchitecture;

import android.content.Context;

public class MainApplicationConfig {

    private static final MainApplicationConfig sInstance = new MainApplicationConfig();

    public static synchronized MainApplicationConfig getInstance() {
        return sInstance;
    }

    public synchronized void onCreate(Context context) {
        // do nothing
    }

}

