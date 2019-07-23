package com.eagb.blockchainexample.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.eagb.blockchainexample.managers.SharedPreferencesManager;

import androidx.annotation.Nullable;

public class RestorePowShortcutActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferencesManager prefs = new SharedPreferencesManager(this);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        prefs.setPowValue(SharedPreferencesManager.DEFAULT_PROOF_OF_WORK);
        startActivity(intent);
    }
}
