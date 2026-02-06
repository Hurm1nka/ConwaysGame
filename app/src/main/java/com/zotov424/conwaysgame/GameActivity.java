package com.zotov424.conwaysgame;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class GameActivity extends AppCompatActivity
        implements ExitConfirmDialogFragment.ExitConfirmListener, EditMapDialogFragment.EditMapListener {

    public static final String EXTRA_MAP_ID = "map_id";

    private static final long SIMULATION_INTERVAL_MS = 300;

    private MapsDbHelper dbHelper;
    private MapItem map;
    private GameGridView gameGrid;
    private TextView gameTitle;

    private String currentCells;
    private int gridWidth;
    private int gridHeight;

    private boolean simulationRunning;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable simulationStep = new Runnable() {
        @Override
        public void run() {
            if (!simulationRunning || map == null) return;
            currentCells = GameOfLifeEngine.nextGeneration(currentCells, gridWidth, gridHeight);
            gameGrid.setCells(gridWidth, gridHeight, currentCells);
            handler.postDelayed(this, SIMULATION_INTERVAL_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        dbHelper = new MapsDbHelper(this);
        long mapId = getIntent().getLongExtra(EXTRA_MAP_ID, -1);
        if (mapId < 0) {
            finish();
            return;
        }
        map = dbHelper.getMap(mapId);
        if (map == null) {
            finish();
            return;
        }

        loadStateFromMap();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(v -> finish());

        gameTitle = findViewById(R.id.game_title);
        updateTitle();

        gameGrid = findViewById(R.id.game_grid);
        gameGrid.setCells(gridWidth, gridHeight, currentCells);
        gameGrid.setOnCellTouchedListener((col, row, value) -> {
            if (simulationRunning || map == null) return;
            setCell(col, row, value);
        });
        gameGrid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gameGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                gameGrid.setCells(gridWidth, gridHeight, currentCells);
            }
        });

        ImageButton btnStart = findViewById(R.id.btn_start);
        ImageButton btnStep = findViewById(R.id.btn_step);
        ImageButton btnStop = findViewById(R.id.btn_stop);
        ImageButton btnEdit = findViewById(R.id.btn_edit);
        ImageButton btnDelete = findViewById(R.id.btn_delete);

        btnStart.setOnClickListener(v -> startSimulation());
        btnStep.setOnClickListener(v -> doOneStep());
        btnStop.setOnClickListener(v -> stopSimulation());

        btnEdit.setOnClickListener(v -> {
            if (simulationRunning) stopSimulation();
            EditMapDialogFragment d = EditMapDialogFragment.newInstance(map);
            d.setEditMapListener(this);
            d.show(getSupportFragmentManager(), EditMapDialogFragment.TAG);
        });

        btnDelete.setOnClickListener(v -> showExitDialog());
    }

    private void setCell(int col, int row, int value) {
        int idx = row * gridWidth + col;
        int len = gridWidth * gridHeight;
        if (idx < 0 || idx >= len) return;
        StringBuilder sb = new StringBuilder(currentCells);
        while (sb.length() < len) sb.append('0');
        if (sb.length() > len) sb.setLength(len);
        sb.setCharAt(idx, value == 1 ? '1' : '0');
        currentCells = sb.toString();
        gameGrid.setCells(gridWidth, gridHeight, currentCells);
    }

    private void loadStateFromMap() {
        gridWidth = map.width;
        gridHeight = map.height;
        currentCells = map.cells != null ? map.cells : "";
        int len = gridWidth * gridHeight;
        if (currentCells.length() < len) {
            StringBuilder sb = new StringBuilder(currentCells);
            while (sb.length() < len) sb.append('0');
            currentCells = sb.toString();
        } else if (currentCells.length() > len) {
            currentCells = currentCells.substring(0, len);
        }
    }

    private void doOneStep() {
        if (map == null) return;
        currentCells = GameOfLifeEngine.nextGeneration(currentCells, gridWidth, gridHeight);
        gameGrid.setCells(gridWidth, gridHeight, currentCells);
    }

    private void startSimulation() {
        if (simulationRunning) return;
        simulationRunning = true;
        handler.postDelayed(simulationStep, SIMULATION_INTERVAL_MS);
        Toast.makeText(this, R.string.start_sim, Toast.LENGTH_SHORT).show();
    }

    private void stopSimulation() {
        if (!simulationRunning) return;
        simulationRunning = false;
        handler.removeCallbacks(simulationStep);
        saveStateToDb();
        Toast.makeText(this, R.string.stop_sim, Toast.LENGTH_SHORT).show();
    }

    private void saveStateToDb() {
        if (map != null && currentCells != null) {
            dbHelper.updateMap(map.id, null, null, null, currentCells);
            map = dbHelper.getMap(map.id);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (simulationRunning) {
            stopSimulation();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(simulationStep);
        super.onDestroy();
    }

    private void updateTitle() {
        if (gameTitle != null && map != null) {
            gameTitle.setText(map.name + "  " + map.width + " × " + map.height);
        }
    }

    private void showExitDialog() {
        ExitConfirmDialogFragment d = new ExitConfirmDialogFragment();
        d.setExitConfirmListener(this);
        d.show(getSupportFragmentManager(), ExitConfirmDialogFragment.TAG);
    }

    @Override
    public void onDeleteMap() {
        if (map != null) {
            dbHelper.deleteMap(map.id);
        }
        finish();
    }

    @Override
    public void onKeepMap() {
        finish();
    }

    @Override
    public void onMapUpdated(long mapId, String name, int width, int height) {
        if (simulationRunning) stopSimulation();
        dbHelper.updateMap(mapId, name, width, height, null);
        map = dbHelper.getMap(mapId);
        if (map != null) {
            loadStateFromMap();
            updateTitle();
            gameGrid.setMap(map);
            gameGrid.setCells(gridWidth, gridHeight, currentCells);
            gameGrid.requestLayout();
            gameGrid.invalidate();
        }
    }
}
