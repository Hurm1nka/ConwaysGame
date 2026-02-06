package com.zotov424.conwaysgame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MapsDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "conways.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_MAPS = "maps";

    public MapsDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + TABLE_MAPS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "width INTEGER NOT NULL, "
                + "height INTEGER NOT NULL, "
                + "cells TEXT NOT NULL DEFAULT '', "
                + "created_at TEXT NOT NULL DEFAULT (datetime('now')), "
                + "updated_at TEXT NOT NULL DEFAULT (datetime('now'))"
                + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAPS);
        onCreate(db);
    }

    public List<MapItem> getAllMaps() {
        List<MapItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLE_MAPS, null, null, null, null, null, "updated_at DESC")) {
            while (c.moveToNext()) {
                list.add(cursorToMap(c));
            }
        }
        return list;
    }

    public MapItem getMap(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLE_MAPS, null, "id = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            if (c.moveToFirst()) return cursorToMap(c);
        }
        return null;
    }

    public long insertMap(String name, int width, int height, String cells) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("name", name != null ? name.trim() : "Unnamed");
        v.put("width", width);
        v.put("height", height);
        v.put("cells", cells != null ? cells : "");
        return db.insert(TABLE_MAPS, null, v);
    }

    public int updateMap(long id, String name, Integer width, Integer height, String cells) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        if (name != null) v.put("name", name.trim());
        if (width != null) v.put("width", width);
        if (height != null) v.put("height", height);
        MapItem old = getMap(id);
        if (cells != null) {
            v.put("cells", cells);
        } else if (old != null) {
            int newW = width != null ? width : old.width;
            int newH = height != null ? height : old.height;
            String newCells = resizeCells(old.cells, old.width, old.height, newW, newH);
            v.put("cells", newCells);
        }
        v.put("updated_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        return db.update(TABLE_MAPS, v, "id = ?", new String[]{String.valueOf(id)});
    }

    private static String resizeCells(String oldCells, int oldW, int oldH, int newW, int newH) {
        int newLen = newW * newH;
        StringBuilder sb = new StringBuilder(newLen);
        for (int i = 0; i < newLen; i++) {
            int row = i / newW, col = i % newW;
            if (col < oldW && row < oldH) {
                int idx = row * oldW + col;
                sb.append((oldCells != null && idx < oldCells.length() && oldCells.charAt(idx) == '1') ? '1' : '0');
            } else {
                sb.append('0');
            }
        }
        return sb.toString();
    }

    public boolean deleteMap(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_MAPS, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    private static MapItem cursorToMap(Cursor c) {
        return new MapItem(
            c.getLong(c.getColumnIndexOrThrow("id")),
            c.getString(c.getColumnIndexOrThrow("name")),
            c.getInt(c.getColumnIndexOrThrow("width")),
            c.getInt(c.getColumnIndexOrThrow("height")),
            c.getString(c.getColumnIndexOrThrow("cells")),
            c.getString(c.getColumnIndexOrThrow("created_at")),
            c.getString(c.getColumnIndexOrThrow("updated_at"))
        );
    }
}
