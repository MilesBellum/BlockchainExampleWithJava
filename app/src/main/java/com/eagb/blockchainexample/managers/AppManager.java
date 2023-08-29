package com.eagb.blockchainexample.managers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class AppManager {

    private final Context context;

    public AppManager(Context context) {
        this.context = context;
    }

    public void restartApp() {
        PackageManager manager = context.getPackageManager();
        Intent intent = manager.getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            Intent mainIntent = Intent.makeRestartActivityTask(intent.getComponent());
            context.startActivity(mainIntent);
            Runtime.getRuntime().exit(0);
        }
    }
}
