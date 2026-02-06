package com.zotov424.conwaysgame;

public class MapItem {
    public long id;
    public String name;
    public int width;
    public int height;

    public String cells;
    public String createdAt;
    public String updatedAt;

    public MapItem() {}

    public MapItem(long id, String name, int width, int height, String cells,
                   String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.cells = cells != null ? cells : "";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public int getCell(int col, int row) {
        if (cells == null || col < 0 || row < 0 || col >= width || row >= height)
            return 0;
        int idx = row * width + col;
        if (idx >= cells.length()) return 0;
        return cells.charAt(idx) == '1' ? 1 : 0;
    }


    public void setCell(int col, int row, int value) {
        if (cells == null) cells = "";
        int len = width * height;
        while (cells.length() < len) cells += "0";
        int idx = row * width + col;
        if (idx < 0 || idx >= len) return;
        StringBuilder sb = new StringBuilder(cells);
        sb.setCharAt(idx, value == 1 ? '1' : '0');
        cells = sb.toString();
    }
}
