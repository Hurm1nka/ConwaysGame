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


public class EditMapDialogFragment extends DialogFragment {
    public static final String TAG = "EditMapDialog";
    private static final String ARG_MAP_ID = "map_id";
    private static final String ARG_NAME = "name";
    private static final String ARG_WIDTH = "width";
    private static final String ARG_HEIGHT = "height";

    public interface EditMapListener {
        void onMapUpdated(long mapId, String name, int width, int height);
    }

    private EditMapListener listener;

    public static EditMapDialogFragment newInstance(MapItem map) {
        EditMapDialogFragment f = new EditMapDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_MAP_ID, map.id);
        args.putString(ARG_NAME, map.name);
        args.putInt(ARG_WIDTH, map.width);
        args.putInt(ARG_HEIGHT, map.height);
        f.setArguments(args);
        return f;
    }

    public void setEditMapListener(EditMapListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args == null) { dismiss(); return; }
        long mapId = args.getLong(ARG_MAP_ID, -1);
        String name = args.getString(ARG_NAME, "");
        int width = args.getInt(ARG_WIDTH, 10);
        int height = args.getInt(ARG_HEIGHT, 10);

        EditText nameEt = view.findViewById(R.id.dialog_edit_name);
        EditText widthEt = view.findViewById(R.id.dialog_edit_width);
        EditText heightEt = view.findViewById(R.id.dialog_edit_height);
        nameEt.setText(name);
        widthEt.setText(String.valueOf(width));
        heightEt.setText(String.valueOf(height));

        ImageButton cancelBtn = view.findViewById(R.id.dialog_edit_cancel);
        ImageButton confirmBtn = view.findViewById(R.id.dialog_edit_confirm);

        cancelBtn.setOnClickListener(v -> dismiss());

        confirmBtn.setOnClickListener(v -> {
            String newName = nameEt.getText() != null ? nameEt.getText().toString().trim() : "";
            String wStr = widthEt.getText() != null ? widthEt.getText().toString().trim() : "";
            String hStr = heightEt.getText() != null ? heightEt.getText().toString().trim() : "";
            if (TextUtils.isEmpty(newName)) {
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
            EditMapListener target = listener != null ? listener : (getActivity() instanceof EditMapListener ? (EditMapListener) getActivity() : null);
            if (target != null) {
                target.onMapUpdated(mapId, newName, w, h);
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
