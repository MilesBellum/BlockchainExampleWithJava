package com.eagb.blockchainexample.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.eagb.blockchainexample.BuildConfig;
import com.eagb.blockchainexample.R;
import com.eagb.blockchainexample.databinding.FragmentMoreInfoBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MoreInfoFragment extends DialogFragment implements View.OnClickListener {

    private FragmentMoreInfoBinding viewBinding;

    private Context mContext;

    public MoreInfoFragment() {
        // Required empty public constructor
    }

    public static MoreInfoFragment newInstance() {
        return new MoreInfoFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mContext = context.getApplicationContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewBinding = FragmentMoreInfoBinding.inflate(getLayoutInflater(), container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String appVersion = "v." + BuildConfig.VERSION_NAME + " - Build " + BuildConfig.VERSION_CODE;
        viewBinding.txtAppVersion.setText(appVersion);

        viewBinding.btnClose.setOnClickListener(this);
        viewBinding.llCheckBlockchain.setOnClickListener(this);
        viewBinding.llCheckWhitePaper.setOnClickListener(this);
        viewBinding.llCheckBook1.setOnClickListener(this);
        viewBinding.llCheckBook2.setOnClickListener(this);
        viewBinding.llCheckRepo.setOnClickListener(this);
        viewBinding.llCheckWeb.setOnClickListener(this);
        viewBinding.txtHeart.setOnClickListener(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void onClick(@NonNull View view) {
        Intent intent;
        String url;

        switch (view.getId()) {
            case R.id.btn_close:
                dismiss();
                break;

            case R.id.ll_check_blockchain:
                // Checking a Blockchain tutorial
                url = "https://www.guru99.com/blockchain-tutorial.html";
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;

            case R.id.ll_check_white_paper:
                // Checking the White Paper of Bitcoin
                url = "https://bitcoin.org/bitcoin.pdf";
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;

            case R.id.ll_check_book_1:
                // Checking Blockchain Revolution book
                url = "https://www.amazon.com/dp/1101980141/ref=cm_sw_em_r_mt_dp_U_amjmDbR0D5S46";
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;

            case R.id.ll_check_book_2:
                // Checking The Science of the Blockchain book
                url = "https://www.amazon.com/dp/1544232101/ref=cm_sw_em_r_mt_dp_U_wnjmDbKXPKTCP";
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;

            case R.id.ll_check_repo:
                // Checking the official repo to fork
                url = "https://github.com/MilesBellum/BlockchainExample";
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;

            case R.id.ll_check_web:
                // Checking the official web site
                url = "https://eagb-corp.web.app";
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;

            case R.id.txt_heart:
                // Showing an EasterEgg
                Toast.makeText(mContext, R.string.text_thank_you, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        viewBinding = null;
        mContext = null;
    }
}
