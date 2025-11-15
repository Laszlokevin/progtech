package hu.progtech.gomoku;

import hu.progtech.gomoku.ai.RandomAI;
import hu.progtech.gomoku.dao.ScoreDao;
import hu.progtech.gomoku.model.Board;
import hu.progtech.gomoku.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Main class for the Gomoku game.
 * Provides a command-line interface for playing the game.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        logger.info("Starting Gomoku game");
        System.out.println("=================================");
        System.out.println("   Gomoku / Amőba Game (10x10)");
        System.out.println("=================================");
        System.out.println();

        // Ask for player name
        System.out.print("Enter your name: ");
        String playerName = scanner.nextLine().trim();
        if (playerName.isEmpty()) {
            playerName = "Player";
        }

        Player human = new Player(playerName, 'x');
        Player ai = new Player("Computer", 'o');

        Board board = new Board();
        RandomAI randomAI = new RandomAI();
        ScoreDao scoreDao = new ScoreDao();

        System.out.println("\nGame rules:");
        System.out.println("- You play as 'x', computer plays as 'o'");
        System.out.println("- First stone is placed in the center automatically");
        System.out.println("- Each move must be adjacent (including diagonally) to existing stones");
        System.out.println("- Win by getting 4 in a row (horizontal, vertical, or diagonal)");
        System.out.println("- Enter moves as: column letter + row number (e.g., e5, f6)");
        System.out.println("- Columns: a-j, Rows: 1-10");
        System.out.println();

        // Place initial stone for human player
        board.placeInitialStone(human.getMarker());
        System.out.println("Initial stone placed at center for " + human.getName());
        System.out.println();
        System.out.println(board);

        boolean gameRunning = true;
        boolean humanTurn = false; // AI goes first after initial stone

        while (gameRunning) {
            if (humanTurn) {
                // Human's turn
                System.out.println(human.getName() + "'s turn (" + human.getMarker() + ")");
                boolean validMove = false;
                
                while (!validMove) {
                    System.out.print("Enter your move (e.g., e5): ");
                    String input = scanner.nextLine().trim().toLowerCase();
                    
                    if (input.length() < 2) {
                        System.out.println("Invalid input. Please use format: column letter + row number (e.g., e5)");
                        continue;
                    }

                    char colChar = input.charAt(0);
                    String rowStr = input.substring(1);
                    
                    if (colChar < 'a' || colChar > 'j') {
                        System.out.println("Invalid column. Use letters a-j.");
                        continue;
                    }

                    int col = colChar - 'a';
                    int row;
                    
                    try {
                        row = Integer.parseInt(rowStr) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid row number. Use numbers 1-10.");
                        continue;
                    }

                    if (row < 0 || row >= board.getRows()) {
                        System.out.println("Invalid row. Use numbers 1-" + board.getRows() + ".");
                        continue;
                    }

                    if (!board.isEmpty(row, col)) {
                        System.out.println("That position is already occupied!");
                        continue;
                    }

                    if (!board.isAdjacentToExisting(row, col)) {
                        System.out.println("Move must be adjacent to existing stones!");
                        continue;
                    }

                    board.placeMarker(row, col, human.getMarker());
                    validMove = true;
                    System.out.println();
                    System.out.println(board);

                    if (board.checkWinner(row, col, human.getMarker())) {
                        System.out.println("🎉 Congratulations! " + human.getName() + " wins!");
                        scoreDao.saveScore(human.getName(), "WIN");
                        gameRunning = false;
                    } else if (board.isFull()) {
                        System.out.println("Game is a draw!");
                        scoreDao.saveScore(human.getName(), "DRAW");
                        gameRunning = false;
                    }
                }
            } else {
                // AI's turn
                System.out.println(ai.getName() + "'s turn (" + ai.getMarker() + ")");
                int[] move = randomAI.chooseMove(board);
                
                if (move == null) {
                    System.out.println("AI has no valid moves. Game is a draw!");
                    scoreDao.saveScore(human.getName(), "DRAW");
                    gameRunning = false;
                } else {
                    int row = move[0];
                    int col = move[1];
                    board.placeMarker(row, col, ai.getMarker());
                    
                    char colChar = (char) ('a' + col);
                    System.out.println("AI plays: " + colChar + (row + 1));
                    System.out.println();
                    System.out.println(board);

                    if (board.checkWinner(row, col, ai.getMarker())) {
                        System.out.println("Computer wins! Better luck next time.");
                        scoreDao.saveScore(human.getName(), "LOSS");
                        gameRunning = false;
                    } else if (board.isFull()) {
                        System.out.println("Game is a draw!");
                        scoreDao.saveScore(human.getName(), "DRAW");
                        gameRunning = false;
                    }
                }
            }

            humanTurn = !humanTurn;
        }

        // Display high scores
        System.out.println("\n=== Recent Games ===");
        List<String[]> scores = scoreDao.getAllScores();
        if (scores.isEmpty()) {
            System.out.println("No games recorded yet.");
        } else {
            int count = 0;
            for (String[] score : scores) {
                System.out.println(score[0] + " - " + score[1] + " - " + score[2]);
                count++;
                if (count >= 10) break; // Show only last 10 games
            }
        }

        scanner.close();
        logger.info("Game ended");
    }
}
