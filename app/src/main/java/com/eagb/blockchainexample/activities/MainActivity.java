package com.eagb.blockchainexample.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.IntentSender;
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
import com.eagb.blockchainexample.managers.AppManager;
import com.eagb.blockchainexample.managers.SharedPreferencesManager;
import com.eagb.blockchainexample.utils.CipherUtils;
import com.eagb.blockchainexample.managers.BlockChainManager;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ContentMainBinding viewBindingContent;

    private ProgressDialog progressDialog;
    private BlockChainManager blockChain;
    private SharedPreferencesManager prefs;
    private boolean isEncryptionActivated;
    private boolean isDarkThemeActivated;
    private AppUpdateManager appUpdateManager;

    private static final int UPDATE_REQUEST_CODE = 1000;
    private static final String TAG_POW_DIALOG = "proof_of_work_dialog";
    private static final String TAG_MORE_INFO_DIALOG = "more_info_dialog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        managePowerTheme();

        // Setting the Night mode - must be done before calling super()
        super.onCreate(savedInstanceState);

        setUpView();
    }

    /**
     * Managing the power theme.
     */
    private void managePowerTheme() {
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
            setDarkMode();
        }
    }

    /**
     * Setting the dark mode.
     */
    private void setDarkMode() {
        if (isDarkThemeActivated) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }
    }

    /**
     * Setting up the view.
     */
    private void setUpView() {
        ActivityMainBinding viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        viewBindingContent = ContentMainBinding.bind(viewBinding.contentMain.getRoot());
        setContentView(viewBinding.getRoot());
        setSupportActionBar(viewBinding.toolbar);

        // Check a possible update from Play Store
        checkUpdate();

        isEncryptionActivated = prefs.getEncryptionStatus();

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        viewBindingContent.recyclerContent.setHasFixedSize(true);
        // Use a linear layout manager
        viewBindingContent.recyclerContent.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        viewBindingContent.btnSendData.setOnClickListener(this);

        // Starting Blockchain
        startBlockChain();
    }

    /**
     * Checks a possible update from Play Store.
     */
    private void checkUpdate() {
        // Creates instance of the manager
        appUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns an intent object that you use to check for an update
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update
                startTheUpdate(appUpdateManager, appUpdateInfo);
            }
        });

    }

    /**
     * Starting BlockChain.
     */
    private void startBlockChain() {
        // Setting the Progress Dialog
        showProgressDialog(getResources().getString(R.string.text_creating_block_chain));

        // Starting BlockChain request on a thread
        new Thread(() -> runOnUiThread(() -> {
            // Initializing BlockChain...
            // PROOF_OF_WORK = difficulty.
            // Given some difficulty, the CPU will has to find a hash for the block
            // starting with a given number of zeros.
            // More Proof-of-Work will be harder to mine and will take longer time.
            // Watch out!
            blockChain = new BlockChainManager(this, prefs.getPowValue());
            viewBindingContent.recyclerContent.setAdapter(blockChain.adapter);
            cancelProgressDialog(progressDialog);
        })).start();
    }

    /**
     * If an update exist, request for the update.
     *
     * @param appUpdateManager is the manager to start the flow for result
     * [AppUpdateManager.startUpdateFlowForResult].
     * @param appUpdateInfo gets the app info [AppUpdateInfo].
     */
    private void startTheUpdate(
            @NonNull AppUpdateManager appUpdateManager,
            @NonNull AppUpdateInfo appUpdateInfo
    ) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request
                    this,
                    // Include a request code to later monitor this update request
                    UPDATE_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Continue with the update if one exists
        resumeTheUpdate();
    }

    /**
     * Continue with the update if one exists.
     */
    private void resumeTheUpdate() {
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability() ==
                                    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                startTheUpdate(appUpdateManager, appUpdateInfo);
                            }

                        });
    }

    /**
     * Requesting new block from Blockchain.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void requestNewBlockFromBlockChain() {
        // Setting the Progress Dialog
        showProgressDialog(getResources().getString(R.string.text_mining_blocks));

        runOnUiThread(() -> {
            if (blockChain != null && viewBindingContent.editMessage.getText() != null && viewBindingContent.recyclerContent.getAdapter() != null) {
                String message = viewBindingContent.editMessage.getText().toString();

                if (!message.isEmpty()) {
                    // Verification if encryption is activated
                    verifyEncryption(message);

                    // Validate block's data
                    validateBlockData();
                } else {
                    printErrorEmptyData();
                }

                cancelProgressDialog(progressDialog);
            } else {
                printErrorSomethingWrong();
            }
        });
    }

    /**
     * Verify if the encryption is activated.
     *
     * @param message is the message to broadcast.
     */
    private void verifyEncryption(@NotNull String message) {
        if (!isEncryptionActivated) {
            // Broadcast data
            blockChain.addBlock(blockChain.newBlock(message));
        } else {
            try {
                // Broadcast data
                blockChain.addBlock(blockChain.newBlock(CipherUtils.encryptIt(message).trim()));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.error_something_wrong, Toast.LENGTH_LONG).show();
            }
        }
        viewBindingContent.recyclerContent.smoothScrollToPosition(blockChain.adapter.getItemCount() - 1);
    }

    /**
     * Validating block's data.
     */
    private void validateBlockData() {
        System.out.println(getResources().getString(R.string.log_block_chain_valid, blockChain.isBlockChainValid()));
        if (blockChain.isBlockChainValid()) {
            // Preparing data to insert to RecyclerView
            blockChain.adapter.notifyItemInserted(blockChain.adapter.getItemCount() - 1);
            // Cleaning the EditText
            viewBindingContent.editMessage.setText("");
        } else {
            printErrorBlockchainCorrupted();
        }
    }

    /**
     * Prints an error message when the block chain is corrupted.
     */
    private void printErrorBlockchainCorrupted() {
        Toast.makeText(this, R.string.error_block_chain_corrupted, Toast.LENGTH_LONG).show();
    }

    /**
     * Prints an error message when the EditText is empty.
     */
    private void printErrorEmptyData() {
        Toast.makeText(this, R.string.error_empty_data, Toast.LENGTH_LONG).show();
    }

    /**
     * Prints an error message when something goes wrong.
     */
    private void printErrorSomethingWrong() {
        Toast.makeText(this, R.string.error_something_wrong, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(@NonNull View view) {
        if (view.getId() == R.id.btn_send_data) {
            // Start new request on a UI thread
            requestNewBlockFromBlockChain();
        }
    }

    /**
     * Setting the Progress Dialog.
     *
     * @param loadingMessage is the message in [String].
     */
    private void showProgressDialog(@NonNull String loadingMessage) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(loadingMessage);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.show();
    }

    /**
     * Canceling the Progress Dialog.
     *
     * @param progressDialog is the Progress Dialog.
     */
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
        if (R.id.action_pow == item.getItemId()) {
            onPowOptionTapped();
        } else if (R.id.action_encrypt == item.getItemId()) {
            onEncryptionOptionTapped(item);
        } else if (R.id.action_dark == item.getItemId()) {
            onDarkThemeOptionTapped(item);
        } else if (R.id.action_more == item.getItemId()) {
            onMoreInfoOptionTapped();
        } else if (R.id.action_exit == item.getItemId()) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Opening the PoW dialog.
     */
    private void onPowOptionTapped() {
        PowFragment powFragment = PowFragment.newInstance();
        powFragment.show(this.getSupportFragmentManager(), TAG_POW_DIALOG);
    }

    /**
     * Setting the encryption status.
     *
     * @param item is the menu item.
     */
    private void onEncryptionOptionTapped(@NonNull MenuItem item) {
        isEncryptionActivated = !item.isChecked();
        item.setChecked(isEncryptionActivated);
        if (item.isChecked()) {
            Toast.makeText(this, R.string.text_encryption_activated, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.text_encryption_deactivated, Toast.LENGTH_SHORT).show();
        }
        prefs.setEncryptionStatus(isEncryptionActivated);
    }

    /**
     * Setting the dark theme status.
     *
     * @param item is the menu item.
     */
    private void onDarkThemeOptionTapped(@NonNull MenuItem item) {
        isDarkThemeActivated = !item.isChecked();
        item.setChecked(isDarkThemeActivated);
        prefs.setDarkTheme(isDarkThemeActivated);

        AppManager appManager = new AppManager(this);
        appManager.restartApp();
    }

    /**
     * Opening the More Info dialog.
     */
    private void onMoreInfoOptionTapped() {
        MoreInfoFragment moreInfoFragment = MoreInfoFragment.newInstance();
        moreInfoFragment.show(this.getSupportFragmentManager(), TAG_MORE_INFO_DIALOG);
    }
}
