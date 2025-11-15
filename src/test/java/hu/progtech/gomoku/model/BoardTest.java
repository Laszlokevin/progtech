package hu.progtech.gomoku.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    void testPlaceAndWinHorizontal() {
        Board b = new Board(10, 10);

        // place first in middle
        assertTrue(b.place('x', 4, 4));

        // place adjacent stones to build 4 in a row horizontally
        assertTrue(b.place('x', 4, 5));
        assertTrue(b.place('x', 4, 6));
        assertTrue(b.place('x', 4, 7));

        // last placed (4,7) should make 4 in a row: check win
        assertTrue(b.checkWin('x', 4, 7));
    }

    @Test
    void testInvalidFirstPlaceNotInMiddle() {
        Board b = new Board(10, 10);
        // trying to place first stone outside the central 4 squares must fail
        assertFalse(b.place('x', 0, 0));
    }

    @Test
    void testAdjacencyRequirement() {
        Board b = new Board(10, 10);
        assertTrue(b.place('x', 4, 4));
        // position far not adjacent should be invalid
        assertFalse(b.place('o', 1, 1));
    }
}
