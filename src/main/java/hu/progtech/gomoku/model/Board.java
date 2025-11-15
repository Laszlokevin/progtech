package hu.progtech.gomoku.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Board {
    private final int rows;
    private final int cols;
    private final char[][] grid;
    public static final char EMPTY = '.';

    public Board(int rows, int cols) {
        if (rows < 5 || cols < 5 || rows > 25 || cols > 25 || cols > rows) {
            throw new IllegalArgumentException("Invalid board size. Must satisfy 5 <= M <= N <= 25 (cols <= rows).");
        }
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = EMPTY;
            }
        }
    }

    public boolean isEmpty(int r, int c) {
        return grid[r][c] == EMPTY;
    }

    public char get(int r, int c) {
        return grid[r][c];
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    /**
     * Place a symbol. Returns true if successful.
     * Validates: inside bounds, empty, and if there's at least one already existing stone, the new stone
     * must touch (chebyshev distance <= 1) at least one existing stone. The very first stone is allowed anywhere in the middle per spec
     */
    public boolean place(char symbol, int r, int c) {
        Objects.checkIndex(r, rows);
        Objects.checkIndex(c, cols);
        if (!isEmpty(r, c)) return false;

        // If board is empty (no stones yet), placement must be in one of middle squares per spec.
        if (isBoardEmpty()) {
            int midR = rows / 2 - 1; // choose lower-middle for even
            int midC = cols / 2 - 1;
            int midR2 = rows / 2;
            int midC2 = cols / 2;
            // allowed middle squares: (midR, midC), (midR, midC2), (midR2, midC), (midR2, midC2) for even sizes
            boolean allowed = (r == midR || r == midR2) && (c == midC || c == midC2);
            if (!allowed) return false;
            grid[r][c] = symbol;
            return true;
        }

        // otherwise, require adjacency (including diagonals) to at least one existing stone
        boolean adjacent = false;
        for (int dr = -1; dr <= 1 && !adjacent; dr++) {
            for (int dc = -1; dc <= 1 && !adjacent; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = r + dr, nc = c + dc;
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !isEmpty(nr, nc)) {
                    adjacent = true;
                }
            }
        }
        if (!adjacent) return false;

        grid[r][c] = symbol;
        return true;
    }

    private boolean isBoardEmpty() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) if (!isEmpty(r, c)) return false;
        }
        return true;
    }

    /**
     * Check if placing symbol at (r,c) caused a win (4 in a row)
     */
    public boolean checkWin(char symbol, int r, int c) {
        // Check 4 directions: horizontal, vertical, diag1 (\), diag2 (/)
        return countInDirection(symbol, r, c, 0, 1) >= 4    // horizontal
                || countInDirection(symbol, r, c, 1, 0) >= 4 // vertical
                || countInDirection(symbol, r, c, 1, 1) >= 4 // diag \
                || countInDirection(symbol, r, c, 1, -1) >= 4; // diag /
    }

    /**
     * Count continuous same-symbol pieces through (r,c) combining both sides.
     */
    private int countInDirection(char symbol, int r, int c, int dr, int dc) {
        int count = 1;
        // forward
        int nr = r + dr, nc = c + dc;
        while (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == symbol) {
            count++;
            nr += dr; nc += dc;
        }
        // backward
        nr = r - dr; nc = c - dc;
        while (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == symbol) {
            count++;
            nr -= dr; nc -= dc;
        }
        return count;
    }

    public List<int[]> getValidMoves() {
        List<int[]> res = new ArrayList<>();
        boolean anyStone = !isBoardEmpty();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!isEmpty(r, c)) continue;
                if (!anyStone) {
                    // empty board: only middle squares allowed
                    int midR = rows / 2 - 1;
                    int midC = cols / 2 - 1;
                    int midR2 = rows / 2;
                    int midC2 = cols / 2;
                    boolean allowed = (r == midR || r == midR2) && (c == midC || c == midC2);
                    if (allowed) res.add(new int[]{r, c});
                } else {
                    boolean adjacent = false;
                    for (int dr = -1; dr <= 1 && !adjacent; dr++) {
                        for (int dc = -1; dc <= 1 && !adjacent; dc++) {
                            if (dr == 0 && dc == 0) continue;
                            int nr = r + dr, nc = c + dc;
                            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !isEmpty(nr, nc)) {
                                adjacent = true;
                            }
                        }
                    }
                    if (adjacent) res.add(new int[]{r, c});
                }
            }
        }
        return res;
    }

    public String toStringRepresentation() {
        StringBuilder sb = new StringBuilder();
        // header: column letters a b c ...
        sb.append("   ");
        for (int c = 0; c < cols; c++) {
            sb.append((char)('a' + c)).append(' ');
        }
        sb.append('\n');
        for (int r = 0; r < rows; r++) {
            sb.append(String.format("%2d ", r + 1));
            for (int c = 0; c < cols; c++) {
                sb.append(grid[r][c]).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
