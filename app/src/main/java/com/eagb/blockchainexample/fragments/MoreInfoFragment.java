package com.eagb.blockchainexample.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eagb.blockchainexample.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MoreInfoFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private ImageButton btnClose;
    private TextView txtHeart;
    private LinearLayout linearCheckBlockchain, linearCheckWhitePaper, linearCheckWhiteBook1,
            linearCheckWhiteBook2, linearCheckRepo, linearCheckPost, linearCheckWeb;

    public MoreInfoFragment() {
        // Required empty public constructor
    }

    public static MoreInfoFragment newInstance() {
        return new MoreInfoFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_more_info, container, false);

        btnClose = rootView.findViewById(R.id.btn_close);
        linearCheckBlockchain = rootView.findViewById(R.id.linear_check_blockchain);
        linearCheckWhitePaper = rootView.findViewById(R.id.linear_check_white_paper);
        linearCheckWhiteBook1 = rootView.findViewById(R.id.linear_check_book_1);
        linearCheckWhiteBook2 = rootView.findViewById(R.id.linear_check_book_2);
        linearCheckRepo = rootView.findViewById(R.id.linear_check_repo);
        linearCheckPost = rootView.findViewById(R.id.linear_check_post);
        linearCheckWeb = rootView.findViewById(R.id.linear_check_web);
        txtHeart = rootView.findViewById(R.id.txt_heart);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnClose.setOnClickListener(this);
        linearCheckBlockchain.setOnClickListener(this);
        linearCheckWhitePaper.setOnClickListener(this);
        linearCheckWhiteBook1.setOnClickListener(this);
        linearCheckWhiteBook2.setOnClickListener(this);
        linearCheckRepo.setOnClickListener(this);
        linearCheckPost.setOnClickListener(this);
        linearCheckWeb.setOnClickListener(this);
        txtHeart.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(mContext);
        if (dialog.getWindow() != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        return dialog;
    }

    @Override
    public void onClick(@NonNull View view) {
        Intent intent;
        String url;
        switch (view.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.linear_check_blockchain:
                // Checking a Blockchain tutorial
                url = "https://www.guru99.com/blockchain-tutorial.html";
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.linear_check_white_paper:
                // Checking the White Paper of Bitcoin
                url = "https://bitcoin.org/bitcoin.pdf";
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.linear_check_book_1:
                // Checking Blockchain Revolution book
                url = "https://www.amazon.com/dp/1101980141/ref=cm_sw_em_r_mt_dp_U_amjmDbR0D5S46";
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.linear_check_book_2:
                // Checking The Science of the Blockchain book
                url = "https://www.amazon.com/dp/1544232101/ref=cm_sw_em_r_mt_dp_U_wnjmDbKXPKTCP";
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.linear_check_repo:
                // Checking the official repo to fork
                url = "https://github.com/MilesBellum/BlockchainExample";
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.linear_check_post:
                // Checking the official Medium post
                url = "https://medium.com/@eagb";
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.linear_check_web:
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

        mContext = null;
    }
}
