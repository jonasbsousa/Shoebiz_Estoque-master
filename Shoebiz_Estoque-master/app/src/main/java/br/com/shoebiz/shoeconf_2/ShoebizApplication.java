package br.com.shoebiz.shoeconf_2;

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.squareup.otto.Bus;

public class ShoebizApplication extends Application {
    private static final String TAG = "ShoebizApplication";
    private static ShoebizApplication instance = null;
    private final Bus bus = new Bus();

    public static ShoebizApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "ShoebizApplication.onCreate()");
        instance = this;

        Stetho.initializeWithDefaults(instance);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        Log.d(TAG, "ShoebizApplication.onTerminate()");
    }

    public Bus getBus() {
        return bus;
    }
}