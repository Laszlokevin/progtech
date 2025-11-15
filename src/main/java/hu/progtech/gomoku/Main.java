package hu.progtech.gomoku;

import hu.progtech.gomoku.ai.RandomAI;
import hu.progtech.gomoku.dao.ScoreDao;
import hu.progtech.gomoku.model.Board;
import hu.progtech.gomoku.model.Player;

import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final int DEFAULT_SIZE = 10;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Gomoku / Amőba - 4 in a row on a 10x10 board");
        System.out.print("Add meg a neved: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = "Player";

        Player human = new Player(name, 'x');
        Player ai = new Player("Computer", 'o');

        Board board = new Board(DEFAULT_SIZE, DEFAULT_SIZE);
        ScoreDao dao = new ScoreDao();
        RandomAI aiPlayer = new RandomAI();

        // Place human's starting piece on one of the middle squares automatically (spec)
        int startR = board.getRows() / 2 - 1;
        int startC = board.getCols() / 2 - 1;
        board.place(human.getSymbol(), startR, startC);
        System.out.println("Kezdőlépés automatikusan a középső négy mező egyikére került: " + toHumanPos(startR, startC));
        boolean humanTurn = false; // human already placed starting stone, so AI goes next per "X is human and X starts" but initial stone placed - to keep simple, we let AI move now.
        // Note: If you prefer that the human actively chooses the center, we can change logic to prompt and place human's first move.

        while (true) {
            System.out.println(board.toStringRepresentation());
            if (humanTurn) {
                System.out.print(human.getName() + " (" + human.getSymbol() + ") lépése (pl. e5): ");
                String line = scanner.nextLine().trim();
                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                    System.out.println("Kilépés...");
                    return;
                }
                int[] rc = parseMove(line, board);
                if (rc == null) {
                    System.out.println("Érvénytelen formátum vagy kívül eső mező.");
                    continue;
                }
                boolean ok = board.place(human.getSymbol(), rc[0], rc[1]);
                if (!ok) {
                    System.out.println("Érvénytelen lépés (nem üres vagy nincs szomszédja, vagy nem a középső indítás). Próbáld újra.");
                    continue;
                }
                if (board.checkWin(human.getSymbol(), rc[0], rc[1])) {
                    System.out.println(board.toStringRepresentation());
                    System.out.println("Gratulálok, nyertél! (" + human.getName() + ")");
                    dao.incrementWin(human.getName());
                    printHighScores(dao);
                    break;
                }
                humanTurn = false;
            } else {
                System.out.println("Gép lép...");
                int[] rc = aiPlayer.chooseMove(board);
                if (rc == null) {
                    System.out.println("Nincs érvényes lépés. Döntetlen.");
                    printHighScores(dao);
                    break;
                }
                board.place(ai.getSymbol(), rc[0], rc[1]);
                System.out.println("A gép lépése: " + toHumanPos(rc[0], rc[1]));
                if (board.checkWin(ai.getSymbol(), rc[0], rc[1])) {
                    System.out.println(board.toStringRepresentation());
                    System.out.println("A gép nyert!");
                    dao.incrementWin(ai.getName());
                    printHighScores(dao);
                    break;
                }
                humanTurn = true;
            }
        }
    }

    private static void printHighScores(ScoreDao dao) {
        System.out.println("High scores:");
        for (Map.Entry<String, Integer> e : dao.getAllScores().entrySet()) {
            System.out.printf("%s: %d%n", e.getKey(), e.getValue());
        }
    }

    private static String toHumanPos(int r, int c) {
        char col = (char) ('a' + c);
        int row = r + 1;
        return "" + col + row;
    }

    private static int[] parseMove(String s, Board board) {
        s = s.trim().toLowerCase();
        if (s.length() < 2) return null;
        char colChar = s.charAt(0);
        int col = colChar - 'a';
        String rowPart = s.substring(1).trim();
        int row;
        try {
            row = Integer.parseInt(rowPart) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        if (row < 0 || row >= board.getRows() || col < 0 || col >= board.getCols()) return null;
        return new int[]{row, col};
    }
}
