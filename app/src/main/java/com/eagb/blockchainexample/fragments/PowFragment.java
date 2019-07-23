package com.eagb.blockchainexample.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.eagb.blockchainexample.R;
import com.eagb.blockchainexample.managers.SharedPreferencesManager;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class PowFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private ImageButton btnClose;
    private TextInputEditText editSetPow;
    private Button btnApplyChange;

    private SharedPreferencesManager prefs;

    public PowFragment() {
        // Required empty public constructor
    }

    public static PowFragment newInstance() {
        return new PowFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_pow, container, false);

        prefs = new SharedPreferencesManager(mContext);

        btnClose = rootView.findViewById(R.id.btn_close);
        btnApplyChange = rootView.findViewById(R.id.btn_continue);
        editSetPow = rootView.findViewById(R.id.edit_set_pow);
        editSetPow.setText(String.valueOf(prefs.getPowValue()));

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnClose.setOnClickListener(this);
        btnApplyChange.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(mContext);
        if (dialog.getWindow() != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return dialog;
    }

    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()){
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.btn_continue:
                if (editSetPow.getText() != null) {
                    String pow = editSetPow.getText().toString();
                    prefs.setPowValue(Integer.valueOf(pow));
                    if (getActivity() != null) {
                        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        dismiss();
                    }
                }
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mContext = null;
    }

}
