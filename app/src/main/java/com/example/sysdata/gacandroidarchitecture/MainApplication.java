package com.example.sysdata.gacandroidarchitecture;

import android.app.Application;
import android.os.StrictMode;

/**
 * Created on 26/02/18.
 *
 * @author Umberto Marini
 */
public class MainApplication extends Application {

    /**
     * Instance of the app Application used to access various shared fields useful about everywhere in the app
     */
    private static MainApplication sInstance;

    /**
     * Returns the Singleton instance of this configuration
     *
     * @return the instance of this Application
     */
    public static synchronized MainApplicationConfig getConfiguration() {
        return MainApplicationConfig.getInstance();
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        /*
        boolean isDev = !"release".equals(BuildConfig.BUILD_TYPE);
        boolean isRooted = false;
        boolean isTampered = false;
        // if we are in prodRelease and we have an emulator attached, this could mean that somebody has made a backup and is running
        // the apk on an emulator: we have to stop it.
        boolean prodEmulator = ("release".equals(BuildConfig.BUILD_TYPE) && "prod".equals(BuildConfig.FLAVOR) && DeviceUtil.isEmulator());
        if (BuildConfig.DETECT_ROOT_ENABLED) {
            RootBeer rootBeer = new RootBeer(this);
            isRooted = rootBeer.checkForSuBinary() || rootBeer.checkSuExists() || rootBeer.checkForRootNative();
        }
        if (BuildConfig.TAMPERING_DETECTION_ENABLED) {
            TamperUtils tamperUtils = new TamperUtils(this);
            isTampered = tamperUtils.isTampered();
        }
        if (!isDev && (isRooted || isTampered || prodEmulator)) {
            // notify user app cannot be started
            if (!isLockedSemaphore()) {
                String notificationTitle = getString(R.string.common_warning);
                String notificationMessage = getString(R.string.device_tampered_or_rooted_error_android);
                NotificationManager.appendNotification(getApplicationContext(), notificationTitle, notificationMessage, true);
            }
            if (BuildConfig.IS_ROOT_FORBIDDEN || isTampered || prodEmulator) {
                stopService(new Intent(getApplicationContext(), FirebaseInstanceIdService.class));
                closeApp();
                Process.killProcess(Process.myPid());
                System.exit(0);
            } else {
                MainApplicationConfig.getInstance().onCreate();
            }
        } else {
            MainApplicationConfig.getInstance().onCreate();
        }
        */

        MainApplicationConfig.getInstance().onCreate(this);
    }

    /**
     * Returns the Singleton instance of this Application
     *
     * @return the instance of this Application
     */
    public static synchronized MainApplication getInstance() {
        return sInstance;
    }
}
