package com.eagb.blockchainexample.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.eagb.blockchainexample.R;
import com.eagb.blockchainexample.databinding.ActivityMainBinding;
import com.eagb.blockchainexample.databinding.ContentMainBinding;
import com.eagb.blockchainexample.fragments.MoreInfoFragment;
import com.eagb.blockchainexample.fragments.PowFragment;
import com.eagb.blockchainexample.managers.SharedPreferencesManager;
import com.eagb.blockchainexample.utils.CipherUtils;
import com.eagb.blockchainexample.managers.BlockchainManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ContentMainBinding viewBindingContent;

    private ProgressDialog progressDialog;
    private BlockchainManager blockchain;
    private SharedPreferencesManager prefs;
    private boolean isEncryptionActivated, isDarkThemeActivated;

    private static final String TAG_POW_DIALOG = "proof_of_work_dialog";
    private static final String TAG_MORE_INFO_DIALOG = "more_info_dialog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        prefs = new SharedPreferencesManager(this);
        isDarkThemeActivated = prefs.isDarkTheme();

        PowerManager powerManager = (PowerManager)getSystemService(POWER_SERVICE);
        boolean isPowerSaveMode = false;

        if (powerManager != null) {
            isPowerSaveMode = powerManager.isPowerSaveMode();
        }
        if (isPowerSaveMode) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            if (isDarkThemeActivated) {
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        // Setting the Night mode - must be done before calling super()
        super.onCreate(savedInstanceState);
        ActivityMainBinding viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        viewBindingContent = ContentMainBinding.bind(viewBinding.contentMain.getRoot());
        setContentView(viewBinding.getRoot());
        setSupportActionBar(viewBinding.toolbar);

        isEncryptionActivated = prefs.getEncryptionStatus();

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        viewBindingContent.recyclerContent.setHasFixedSize(true);
        // Use a linear layout manager
        viewBindingContent.recyclerContent.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // Setting the Progress Dialog
        showProgressDialog(getResources().getString(R.string.text_creating_blockchain));

        // Starting Blockchain request on a thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Initializing Blockchain...
                        // PROOF_OF_WORK = difficulty.
                        // Given some difficulty, the CPU will has to find a hash for the block
                        // starting with a given number of zeros.
                        // More Proof-of-Work will be harder to mine and will take longer time.
                        // Watch out!
                        blockchain = new BlockchainManager(getApplicationContext(), prefs.getPowValue());
                        viewBindingContent.recyclerContent.setAdapter(blockchain.adapter);
                        cancelProgressDialog(progressDialog);
                    }
                });
            }
        }).start();

        viewBindingContent.btnSendData.setOnClickListener(this);
    }

    // Starting new request on a thread
    private void startBlockchain() {
        // Setting the Progress Dialog
        showProgressDialog(getResources().getString(R.string.text_mining_blocks));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (blockchain != null && viewBindingContent.editMessage.getText() != null && viewBindingContent.recyclerContent.getAdapter() != null) {
                    String message = viewBindingContent.editMessage.getText().toString();

                    if (!message.isEmpty()) {

                        // Verification if encryption is activated
                        if (!isEncryptionActivated) {
                            // Broadcast data
                            blockchain.addBlock(blockchain.newBlock(message));
                        } else {
                            try {
                                // Broadcast data
                                blockchain.addBlock(blockchain.newBlock(CipherUtils.encryptIt(message).trim()));
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), R.string.error_something_wrong, Toast.LENGTH_LONG).show();
                            }
                        }
                        viewBindingContent.recyclerContent.scrollToPosition(blockchain.adapter.getItemCount() - 1);

                        // Validate block's data
                        System.out.println(getResources().getString(R.string.log_blockchain_valid, blockchain.isBlockChainValid()));
                        if (blockchain.isBlockChainValid()) {
                            // Preparing data to insert to RecyclerView
                            viewBindingContent.recyclerContent.getAdapter().notifyDataSetChanged();
                            // Cleaning the EditText
                            viewBindingContent.editMessage.setText("");
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_blockchain_corrupted, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.error_empty_data, Toast.LENGTH_LONG).show();
                    }

                    cancelProgressDialog(progressDialog);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_something_wrong, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(@NonNull View view) {
        if (view.getId() == R.id.btn_send_data) {
            // Start new request on a UI thread
            startBlockchain();
        }
    }

    // Setting the Progress Dialog
    private void showProgressDialog(@NonNull String loadingMessage) {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(loadingMessage);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.show();
    }

    private void cancelProgressDialog(@Nullable ProgressDialog progressDialog) {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem checkEncrypt = menu.findItem(R.id.action_encrypt);
        checkEncrypt.setChecked(isEncryptionActivated);

        MenuItem checkTheme = menu.findItem(R.id.action_dark);
        checkTheme.setChecked(isDarkThemeActivated);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_pow:
                PowFragment powFragment = PowFragment.newInstance();
                powFragment.show(this.getSupportFragmentManager(), TAG_POW_DIALOG);
                break;

            case R.id.action_encrypt:
                isEncryptionActivated = !item.isChecked();
                item.setChecked(isEncryptionActivated);
                if (item.isChecked()) {
                    Toast.makeText(this, R.string.text_encryption_activated, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.text_encryption_deactivated, Toast.LENGTH_SHORT).show();
                }
                prefs.setEncryptionStatus(isEncryptionActivated);
                return true;

            case R.id.action_dark:
                isDarkThemeActivated = !item.isChecked();
                item.setChecked(isDarkThemeActivated);
                prefs.setDarkTheme(isDarkThemeActivated);
                Intent intent = this.getPackageManager().getLaunchIntentForPackage(this.getPackageName());
                startActivity(intent);
                finish();
                return true;

            case R.id.action_more:
                MoreInfoFragment moreInfoFragment = MoreInfoFragment.newInstance();
                moreInfoFragment.show(this.getSupportFragmentManager(), TAG_MORE_INFO_DIALOG);
                break;

            case R.id.action_exit:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }
}
