package com.zotov424.conwaysgame;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CreateMapDialogFragment extends DialogFragment {
    public static final String TAG = "CreateMapDialog";

    public interface CreateMapListener {
        void onMapCreated(String name, int width, int height);
    }

    private CreateMapListener listener;

    public void setCreateMapListener(CreateMapListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_create_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText nameEt = view.findViewById(R.id.dialog_create_name);
        EditText widthEt = view.findViewById(R.id.dialog_create_width);
        EditText heightEt = view.findViewById(R.id.dialog_create_height);
        ImageButton cancelBtn = view.findViewById(R.id.dialog_create_cancel);
        ImageButton confirmBtn = view.findViewById(R.id.dialog_create_confirm);

        cancelBtn.setOnClickListener(v -> dismiss());

        confirmBtn.setOnClickListener(v -> {
            String name = nameEt.getText() != null ? nameEt.getText().toString().trim() : "";
            String wStr = widthEt.getText() != null ? widthEt.getText().toString().trim() : "";
            String hStr = heightEt.getText() != null ? heightEt.getText().toString().trim() : "";
            if (TextUtils.isEmpty(name)) {
                nameEt.setError(getString(R.string.name));
                return;
            }
            int w, h;
            try {
                w = Integer.parseInt(wStr);
                h = Integer.parseInt(hStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Width and Height must be numbers", Toast.LENGTH_SHORT).show();
                return;
            }
            if (w < 1 || w > 500 || h < 1 || h > 500) {
                Toast.makeText(requireContext(), "Width and Height: 1–500", Toast.LENGTH_SHORT).show();
                return;
            }
            if (listener != null) {
                listener.onMapCreated(name, w, h);
            }
            dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
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
