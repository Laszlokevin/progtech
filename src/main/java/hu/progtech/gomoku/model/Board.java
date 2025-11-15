package hu.progtech.gomoku.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game board for Gomoku.
 * The board supports customizable dimensions (5 <= M <= N <= 25).
 * Default is 10x10. The board tracks placed stones and checks for winning conditions.
 */
public class Board {
    private final int rows;
    private final int cols;
    private final char[][] grid;
    private final List<int[]> occupiedCells;
    private static final int WIN_LENGTH = 4;

    /**
     * Creates a new Board with the specified dimensions.
     *
     * @param rows number of rows (5 to 25)
     * @param cols number of columns (5 to 25)
     * @throws IllegalArgumentException if dimensions are out of valid range
     */
    public Board(int rows, int cols) {
        if (rows < 5 || rows > 25 || cols < 5 || cols > 25) {
            throw new IllegalArgumentException("Board dimensions must be between 5 and 25");
        }
        if (rows > cols) {
            throw new IllegalArgumentException("Rows cannot exceed columns");
        }
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
        this.occupiedCells = new ArrayList<>();
        initializeBoard();
    }

    /**
     * Creates a default 10x10 Board.
     */
    public Board() {
        this(10, 10);
    }

    private void initializeBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '.';
            }
        }
    }

    /**
     * Gets the number of rows.
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Gets the number of columns.
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Places the initial stone at the center of the board.
     *
     * @param marker the player's marker
     */
    public void placeInitialStone(char marker) {
        int centerRow = rows / 2;
        int centerCol = cols / 2;
        grid[centerRow][centerCol] = marker;
        occupiedCells.add(new int[]{centerRow, centerCol});
    }

    /**
     * Checks if a cell is empty.
     *
     * @param row the row index
     * @param col the column index
     * @return true if the cell is empty
     */
    public boolean isEmpty(int row, int col) {
        return grid[row][col] == '.';
    }

    /**
     * Checks if a position is valid (within board bounds).
     *
     * @param row the row index
     * @param col the column index
     * @return true if position is valid
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * Checks if a move is adjacent (including diagonally) to any existing stone.
     *
     * @param row the row index
     * @param col the column index
     * @return true if the move is adjacent to an existing stone
     */
    public boolean isAdjacentToExisting(int row, int col) {
        if (occupiedCells.isEmpty()) {
            return true; // First move is always valid
        }

        // Check all 8 directions
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isValidPosition(newRow, newCol) && !isEmpty(newRow, newCol)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Places a marker on the board at the specified position.
     *
     * @param row the row index
     * @param col the column index
     * @param marker the player's marker
     * @return true if the move was successful
     */
    public boolean placeMarker(int row, int col, char marker) {
        if (!isValidPosition(row, col)) {
            return false;
        }
        if (!isEmpty(row, col)) {
            return false;
        }
        if (!isAdjacentToExisting(row, col)) {
            return false;
        }

        grid[row][col] = marker;
        occupiedCells.add(new int[]{row, col});
        return true;
    }

    /**
     * Gets the marker at the specified position.
     *
     * @param row the row index
     * @param col the column index
     * @return the marker at the position, or '.' if empty
     */
    public char getMarker(int row, int col) {
        if (!isValidPosition(row, col)) {
            return '.';
        }
        return grid[row][col];
    }

    /**
     * Checks if there is a winner after the last move.
     *
     * @param row the row of the last move
     * @param col the column of the last move
     * @param marker the marker that was placed
     * @return true if this move created a winning condition
     */
    public boolean checkWinner(int row, int col, char marker) {
        // Check horizontal
        if (countConsecutive(row, col, 0, 1, marker) + countConsecutive(row, col, 0, -1, marker) - 1 >= WIN_LENGTH) {
            return true;
        }
        // Check vertical
        if (countConsecutive(row, col, 1, 0, marker) + countConsecutive(row, col, -1, 0, marker) - 1 >= WIN_LENGTH) {
            return true;
        }
        // Check diagonal (top-left to bottom-right)
        if (countConsecutive(row, col, 1, 1, marker) + countConsecutive(row, col, -1, -1, marker) - 1 >= WIN_LENGTH) {
            return true;
        }
        // Check diagonal (top-right to bottom-left)
        if (countConsecutive(row, col, 1, -1, marker) + countConsecutive(row, col, -1, 1, marker) - 1 >= WIN_LENGTH) {
            return true;
        }
        return false;
    }

    private int countConsecutive(int row, int col, int dRow, int dCol, char marker) {
        int count = 0;
        int r = row;
        int c = col;
        while (isValidPosition(r, c) && grid[r][c] == marker) {
            count++;
            r += dRow;
            c += dCol;
        }
        return count;
    }

    /**
     * Checks if the board is full (draw condition).
     *
     * @return true if the board is full
     */
    public boolean isFull() {
        return occupiedCells.size() >= rows * cols;
    }

    /**
     * Gets a list of all valid moves (empty cells adjacent to existing stones).
     *
     * @return list of valid moves as [row, col] arrays
     */
    public List<int[]> getValidMoves() {
        List<int[]> validMoves = new ArrayList<>();
        
        if (occupiedCells.isEmpty()) {
            // If board is empty, only center positions are valid
            int centerRow = rows / 2;
            int centerCol = cols / 2;
            validMoves.add(new int[]{centerRow, centerCol});
            return validMoves;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (isEmpty(i, j) && isAdjacentToExisting(i, j)) {
                    validMoves.add(new int[]{i, j});
                }
            }
        }
        return validMoves;
    }

    /**
     * Returns a string representation of the board.
     *
     * @return string representation of the board
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Column headers
        sb.append("   ");
        for (int j = 0; j < cols; j++) {
            sb.append((char) ('a' + j)).append(" ");
        }
        sb.append("\n");

        // Rows with row numbers
        for (int i = 0; i < rows; i++) {
            sb.append(String.format("%2d ", i + 1));
            for (int j = 0; j < cols; j++) {
                sb.append(grid[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
