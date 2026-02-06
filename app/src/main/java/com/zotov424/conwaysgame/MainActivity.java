package com.zotov424.conwaysgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MapListAdapter.OnMapClickListener, CreateMapDialogFragment.CreateMapListener {

    private RecyclerView recycler;
    private MapListAdapter adapter;
    private MapsDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new MapsDbHelper(this);
        recycler = findViewById(R.id.recycler_maps);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MapListAdapter();
        adapter.setOnMapClickListener(this);
        recycler.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_create);
        fab.setOnClickListener(v -> showCreateMapDialog());

        loadMaps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMaps();
    }

    private void loadMaps() {
        List<MapItem> list = dbHelper.getAllMaps();
        adapter.setItems(list);
    }

    private void showCreateMapDialog() {
        CreateMapDialogFragment d = new CreateMapDialogFragment();
        d.setCreateMapListener(this);
        d.show(getSupportFragmentManager(), CreateMapDialogFragment.TAG);
    }

    @Override
    public void onMapClick(MapItem map) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(GameActivity.EXTRA_MAP_ID, map.id);
        startActivity(i);
    }

    @Override
    public void onMapCreated(String name, int width, int height) {
        try {
            int len = width * height;
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) sb.append('0');
            long id = dbHelper.insertMap(name, width, height, sb.toString());
            if (id > 0) {
                loadMaps();
                MapItem created = dbHelper.getMap(id);
                if (created != null) {
                    Intent i = new Intent(this, GameActivity.class);
                    i.putExtra(GameActivity.EXTRA_MAP_ID, id);
                    startActivity(i);
                }
            }
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                .setMessage(e.getMessage())
                .setPositiveButton(android.R.string.ok, null)
                .show();
        }
    }
}
