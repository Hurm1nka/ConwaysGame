package com.zotov424.conwaysgame;


public final class GameOfLifeEngine {

    private GameOfLifeEngine() {}


    public static String nextGeneration(String cells, int width, int height) {
        if (width <= 0 || height <= 0) return cells != null ? cells : "";
        int len = width * height;
        if (cells == null || cells.length() < len) {
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) sb.append(i < cells.length() && cells.charAt(i) == '1' ? '1' : '0');
            cells = sb.toString();
        }
        StringBuilder next = new StringBuilder(len);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int neighbors = countNeighbors(cells, width, height, col, row);
                int idx = row * width + col;
                char c = idx < cells.length() ? cells.charAt(idx) : '0';
                if (c == '1') {
                    next.append(neighbors == 2 || neighbors == 3 ? '1' : '0');
                } else {
                    next.append(neighbors == 3 ? '1' : '0');
                }
            }
        }
        return next.toString();
    }

    private static int countNeighbors(String cells, int width, int height, int col, int row) {
        int count = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int c = col + dx, r = row + dy;
                if (c < 0 || c >= width || r < 0 || r >= height) continue;
                int idx = r * width + c;
                if (idx < cells.length() && cells.charAt(idx) == '1') count++;
            }
        }
        return count;
    }
}
