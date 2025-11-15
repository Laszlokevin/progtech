package hu.progtech.gomoku.ai;

import hu.progtech.gomoku.model.Board;

import java.util.List;
import java.util.Random;

public class RandomAI {
    private final Random rnd = new Random();

    /**
     * Choose a random valid move. Returns int[]{r,c} or null if no move.
     */
    public int[] chooseMove(Board board) {
        List<int[]> valid = board.getValidMoves();
        if (valid.isEmpty()) return null;
        return valid.get(rnd.nextInt(valid.size()));
    }
}
