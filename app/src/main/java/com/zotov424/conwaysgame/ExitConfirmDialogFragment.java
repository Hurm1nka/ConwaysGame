package com.zotov424.conwaysgame;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;


public class ExitConfirmDialogFragment extends DialogFragment {
    public static final String TAG = "ExitConfirmDialog";

    public interface ExitConfirmListener {
        void onDeleteMap();
        void onKeepMap();
    }

    private ExitConfirmListener listener;

    public void setExitConfirmListener(ExitConfirmListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_exit_confirm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.dialog_exit_no).setOnClickListener(v -> {
            if (listener != null) listener.onKeepMap();
            dismiss();
        });
        view.findViewById(R.id.dialog_exit_yes).setOnClickListener(v -> {
            if (listener != null) listener.onDeleteMap();
            dismiss();
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        if (d.getWindow() != null) {
            d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        return d;
    }
}
