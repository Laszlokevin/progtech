package hu.progtech.gomoku.ai;

import hu.progtech.gomoku.model.Board;

import java.util.List;
import java.util.Random;

public class RandomAI {
    private final Random rnd = new Random();

    public int[] chooseMove(Board board) {
        List<int[]> valid = board.getValidMoves();
        if (valid.isEmpty()) return null;
        return valid.get(rnd.nextInt(valid.size()));
    }
}
