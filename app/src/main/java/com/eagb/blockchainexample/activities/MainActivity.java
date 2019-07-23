package com.eagb.blockchainexample.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.eagb.blockchainexample.R;
import com.eagb.blockchainexample.fragments.MoreInfoFragment;
import com.eagb.blockchainexample.fragments.PowFragment;
import com.eagb.blockchainexample.managers.SharedPreferencesManager;
import com.eagb.blockchainexample.utils.CipherUtils;
import com.eagb.blockchainexample.utils.Blockchain;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText editMessage;
    private ProgressDialog progressDialog;
    private Blockchain blockchain;
    private RecyclerView mRecyclerView;
    private SharedPreferencesManager prefs;
    private boolean isEncryptionActivated;

    private static final String TAG_POW = "prof_of_work";
    private static final String TAG_MORE_INFO = "more_info";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton btnSendData = findViewById(R.id.btn_send_data);
        editMessage = findViewById(R.id.edit_message);
        mRecyclerView = findViewById(R.id.content);

        prefs = new SharedPreferencesManager(this);
        isEncryptionActivated = prefs.getEncryptionStatus();

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // Use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // Setting the Progress Dialog
        showProgressDialog(getResources().getString(R.string.text_creating_blockchain));

        // Starting Blockchain request on a thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * Initializing Blockchain
                         * @param PROOF_OF_WORK = difficulty.
                         *                      Given some difficulty, the CPU will has to find a hash
                         *                      for the block starting with a given number of zeros.
                         *                      More Proof-of-Work will be harder to mine and will take longer time.
                         *                      Watch out!
                         */
                        blockchain = new Blockchain(getApplicationContext(), prefs.getPowValue());
                        mRecyclerView.setAdapter(blockchain.adapter);
                        cancelProgressDialog(progressDialog);
                    }
                });
            }
        }).start();

        btnSendData.setOnClickListener(this);
    }

    // Starting new request on a thread
    private void startBlockchain() {
        // Setting the Progress Dialog
        showProgressDialog(getResources().getString(R.string.text_mining_blocks));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (blockchain != null && editMessage.getText() != null && mRecyclerView.getAdapter() != null) {
                    String message = editMessage.getText().toString();

                    if (!message.equals("")) {

                        // Verification if encryption is activated
                        if (!isEncryptionActivated) {
                            // Broadcast data
                            blockchain.addBlock(blockchain.newBlock(message));
                        } else {
                            try {
                                // Broadcast data
                                blockchain.addBlock(blockchain.newBlock(CipherUtils.encryptIt(message)));
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), R.string.error_something_wrong, Toast.LENGTH_LONG).show();
                            }
                        }

                        // Validate block's data
                        System.out.println(getResources().getString(R.string.log_blockchain_valid) + " " + blockchain.isBlockChainValid());
                        if (blockchain.isBlockChainValid()) {
                            // Preparing data to insert to RecyclerView
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                            // Cleaning the EditText
                            editMessage.setText("");
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
        switch (view.getId()) {
            case R.id.btn_send_data:
                // Start new request on a UI thread
                startBlockchain();
                break;
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
        MenuItem checkable = menu.findItem(R.id.action_encrypt);
        checkable.setChecked(isEncryptionActivated);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml-v25.
        switch(item.getItemId()) {
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
            case R.id.action_pow:
                PowFragment powFragment = PowFragment.newInstance();
                powFragment.show(this.getSupportFragmentManager(), TAG_POW);
                break;
            case R.id.action_more:
                MoreInfoFragment moreInfoFragment = MoreInfoFragment.newInstance();
                moreInfoFragment.show(this.getSupportFragmentManager(), TAG_MORE_INFO);
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
