package hu.progtech.gomoku.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Board class.
 */
class BoardTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testBoardCreation() {
        assertNotNull(board);
        assertEquals(10, board.getRows());
        assertEquals(10, board.getCols());
    }

    @Test
    void testCustomBoardSize() {
        Board customBoard = new Board(8, 12);
        assertEquals(8, customBoard.getRows());
        assertEquals(12, customBoard.getCols());
    }

    @Test
    void testInvalidBoardSize() {
        assertThrows(IllegalArgumentException.class, () -> new Board(3, 5));
        assertThrows(IllegalArgumentException.class, () -> new Board(30, 30));
        assertThrows(IllegalArgumentException.class, () -> new Board(15, 10)); // rows > cols
    }

    @Test
    void testPlaceInitialStone() {
        board.placeInitialStone('x');
        assertEquals('x', board.getMarker(5, 5)); // Center of 10x10 board
    }

    @Test
    void testIsEmpty() {
        assertTrue(board.isEmpty(0, 0));
        board.placeInitialStone('x');
        assertFalse(board.isEmpty(5, 5));
    }

    @Test
    void testIsValidPosition() {
        assertTrue(board.isValidPosition(0, 0));
        assertTrue(board.isValidPosition(9, 9));
        assertFalse(board.isValidPosition(-1, 0));
        assertFalse(board.isValidPosition(0, 10));
        assertFalse(board.isValidPosition(10, 0));
    }

    @Test
    void testPlaceMarker() {
        board.placeInitialStone('x');
        assertTrue(board.placeMarker(5, 6, 'o')); // Adjacent to center
        assertEquals('o', board.getMarker(5, 6));
    }

    @Test
    void testPlaceMarkerOnOccupiedCell() {
        board.placeInitialStone('x');
        assertFalse(board.placeMarker(5, 5, 'o')); // Center is occupied
    }

    @Test
    void testPlaceMarkerNotAdjacent() {
        board.placeInitialStone('x');
        assertFalse(board.placeMarker(0, 0, 'o')); // Not adjacent to center
    }

    @Test
    void testIsAdjacentToExisting() {
        board.placeInitialStone('x');
        
        // Test all 8 directions from center (5,5)
        assertTrue(board.isAdjacentToExisting(4, 4)); // Top-left
        assertTrue(board.isAdjacentToExisting(4, 5)); // Top
        assertTrue(board.isAdjacentToExisting(4, 6)); // Top-right
        assertTrue(board.isAdjacentToExisting(5, 4)); // Left
        assertTrue(board.isAdjacentToExisting(5, 6)); // Right
        assertTrue(board.isAdjacentToExisting(6, 4)); // Bottom-left
        assertTrue(board.isAdjacentToExisting(6, 5)); // Bottom
        assertTrue(board.isAdjacentToExisting(6, 6)); // Bottom-right
        
        assertFalse(board.isAdjacentToExisting(0, 0)); // Far away
    }

    @Test
    void testCheckWinnerHorizontal() {
        board.placeInitialStone('x');
        board.placeMarker(5, 6, 'x');
        board.placeMarker(5, 7, 'x');
        board.placeMarker(5, 8, 'x');
        
        assertTrue(board.checkWinner(5, 8, 'x'));
    }

    @Test
    void testCheckWinnerVertical() {
        board.placeInitialStone('x');
        board.placeMarker(6, 5, 'x');
        board.placeMarker(7, 5, 'x');
        board.placeMarker(8, 5, 'x');
        
        assertTrue(board.checkWinner(8, 5, 'x'));
    }

    @Test
    void testCheckWinnerDiagonal() {
        board.placeInitialStone('x');
        board.placeMarker(6, 6, 'x');
        board.placeMarker(7, 7, 'x');
        board.placeMarker(8, 8, 'x');
        
        assertTrue(board.checkWinner(8, 8, 'x'));
    }

    @Test
    void testCheckWinnerAntiDiagonal() {
        board.placeInitialStone('x');
        board.placeMarker(6, 4, 'x');
        board.placeMarker(7, 3, 'x');
        board.placeMarker(8, 2, 'x');
        
        assertTrue(board.checkWinner(8, 2, 'x'));
    }

    @Test
    void testNoWinnerYet() {
        board.placeInitialStone('x');
        board.placeMarker(5, 6, 'x');
        board.placeMarker(5, 7, 'x');
        
        assertFalse(board.checkWinner(5, 7, 'x')); // Only 3 in a row
    }

    @Test
    void testIsFull() {
        assertFalse(board.isFull());
        
        // Fill the entire board
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getCols(); j++) {
                if (board.isEmpty(i, j)) {
                    // Place markers manually for testing
                    board.placeMarker(i, j, 'x');
                }
            }
        }
        
        assertTrue(board.isFull());
    }

    @Test
    void testGetValidMovesInitial() {
        List<int[]> moves = board.getValidMoves();
        assertEquals(1, moves.size());
        assertArrayEquals(new int[]{5, 5}, moves.get(0)); // Center
    }

    @Test
    void testGetValidMovesAfterInitial() {
        board.placeInitialStone('x');
        List<int[]> moves = board.getValidMoves();
        
        assertTrue(moves.size() > 0);
        // All moves should be adjacent to center
        for (int[] move : moves) {
            assertTrue(board.isAdjacentToExisting(move[0], move[1]));
        }
    }

    @Test
    void testToString() {
        String boardStr = board.toString();
        assertNotNull(boardStr);
        assertTrue(boardStr.contains("a")); // Column header
        assertTrue(boardStr.contains("1")); // Row number
        assertTrue(boardStr.contains(".")); // Empty cells
    }

    @Test
    void testGetMarker() {
        board.placeInitialStone('x');
        assertEquals('x', board.getMarker(5, 5));
        assertEquals('.', board.getMarker(0, 0));
        assertEquals('.', board.getMarker(-1, 0)); // Invalid position returns '.'
    }
}
