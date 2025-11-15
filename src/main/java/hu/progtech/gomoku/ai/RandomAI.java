package hu.progtech.gomoku.ai;

import hu.progtech.gomoku.model.Board;
import java.util.List;
import java.util.Random;

/**
 * A simple AI that makes random valid moves.
 */
public class RandomAI {
    private final Random random;

    /**
     * Creates a new RandomAI with a default random number generator.
     */
    public RandomAI() {
        this.random = new Random();
    }

    /**
     * Creates a new RandomAI with a specific random number generator.
     * Useful for testing with a seeded random.
     *
     * @param random the random number generator to use
     */
    public RandomAI(Random random) {
        this.random = random;
    }

    /**
     * Chooses a random valid move from the board.
     *
     * @param board the game board
     * @return an array [row, col] representing the chosen move, or null if no valid moves
     */
    public int[] chooseMove(Board board) {
        List<int[]> validMoves = board.getValidMoves();
        
        if (validMoves.isEmpty()) {
            return null;
        }

        int index = random.nextInt(validMoves.size());
        return validMoves.get(index);
    }
}
