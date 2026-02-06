package com.zotov424.conwaysgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


public class GameGridView extends View {


    public interface OnCellTouchedListener {
        void onCellTouched(int col, int row, int value);
    }

    private OnCellTouchedListener cellTouchedListener;
    private final Paint cellAlivePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint cellDeadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int cols = 5;
    private int rows = 5;
    private String cells = "";
    private float cellSize = 40f;
    private float padding = 2f;

    public GameGridView(Context context) {
        super(context);
        init();
    }

    public GameGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setClickable(true);
        setFocusable(true);
        cellAlivePaint.setStyle(Paint.Style.FILL);
        cellAlivePaint.setColor(ContextCompat.getColor(getContext(), R.color.cell_alive));
        cellDeadPaint.setStyle(Paint.Style.STROKE);
        cellDeadPaint.setStrokeWidth(1f);
        cellDeadPaint.setColor(ContextCompat.getColor(getContext(), R.color.cell_dead_stroke));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1f);
        gridPaint.setColor(ContextCompat.getColor(getContext(), R.color.cell_dead_stroke));
    }

    public void setMap(MapItem map) {
        if (map == null) {
            cols = rows = 5;
            cells = "";
        } else {
            cols = map.width;
            rows = map.height;
            cells = map.cells != null ? map.cells : "";
        }
        updateCellSize();
        requestLayout();
        invalidate();
    }

    public void setCells(int width, int height, String cellsStr) {
        cols = width;
        rows = height;
        cells = cellsStr != null ? cellsStr : "";
        updateCellSize();
        invalidate();
    }

    public void setOnCellTouchedListener(OnCellTouchedListener listener) {
        this.cellTouchedListener = listener;
    }


    private void updateCellSize() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0 || cols <= 0 || rows <= 0) return;
        float cellW = (float) w / cols;
        float cellH = (float) h / rows;
        cellSize = Math.min(cellW, cellH);
        padding = Math.max(1f, cellSize * 0.05f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateCellSize();
    }

    private int getCell(int col, int row) {
        if (cells == null || col < 0 || row < 0 || col >= cols || row >= rows) return 0;
        int idx = row * cols + col;
        if (idx >= cells.length()) return 0;
        return cells.charAt(idx) == '1' ? 1 : 0;
    }

    private int lastTouchCol = -1;
    private int lastTouchRow = -1;
    private int lastDrawnValue = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (cellTouchedListener == null || cols <= 0 || rows <= 0 || cellSize <= 0) return super.onTouchEvent(event);
        int col = (int) (event.getX() / cellSize);
        int row = (int) (event.getY() / cellSize);
        if (col < 0 || col >= cols || row < 0 || row >= rows) {
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                lastTouchCol = lastTouchRow = -1;
            }
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int current = getCell(col, row);
                lastDrawnValue = current == 1 ? 0 : 1;
                cellTouchedListener.onCellTouched(col, row, lastDrawnValue);
                lastTouchCol = col;
                lastTouchRow = row;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (col != lastTouchCol || row != lastTouchRow) {
                    cellTouchedListener.onCellTouched(col, row, lastDrawnValue);
                    lastTouchCol = col;
                    lastTouchRow = row;
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastTouchCol = lastTouchRow = -1;
                return true;
            default:
                return true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (cols <= 0 || rows <= 0) return;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float x = col * cellSize + padding / 2;
                float y = row * cellSize + padding / 2;
                float s = cellSize - padding;
                if (getCell(col, row) == 1) {
                    canvas.drawRect(x, y, x + s, y + s, cellAlivePaint);
                } else {
                    canvas.drawRect(x, y, x + s, y + s, cellDeadPaint);
                }
            }
        }
    }
}
