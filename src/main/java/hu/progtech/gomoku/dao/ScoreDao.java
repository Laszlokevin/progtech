package hu.progtech.gomoku.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for persisting game scores to an H2 database.
 */
public class ScoreDao {
    private static final Logger logger = LoggerFactory.getLogger(ScoreDao.class);
    private static final String DB_URL = "jdbc:h2:./data/gomoku";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    /**
     * Creates a new ScoreDao and initializes the database.
     */
    public ScoreDao() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS scores (
                id INT AUTO_INCREMENT PRIMARY KEY,
                player_name VARCHAR(255) NOT NULL,
                result VARCHAR(20) NOT NULL,
                game_date TIMESTAMP NOT NULL
            )
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
            logger.info("Database initialized successfully");
        } catch (SQLException e) {
            logger.error("Failed to initialize database", e);
        }
    }

    /**
     * Saves a game result to the database.
     *
     * @param playerName the name of the player
     * @param result the game result (e.g., "WIN", "LOSS", "DRAW")
     */
    public void saveScore(String playerName, String result) {
        String insertSql = "INSERT INTO scores (player_name, result, game_date) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, playerName);
            pstmt.setString(2, result);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
            logger.info("Score saved: {} - {}", playerName, result);
        } catch (SQLException e) {
            logger.error("Failed to save score", e);
        }
    }

    /**
     * Retrieves all scores from the database, ordered by date descending.
     *
     * @return list of score records as String arrays [playerName, result, date]
     */
    public List<String[]> getAllScores() {
        List<String[]> scores = new ArrayList<>();
        String selectSql = "SELECT player_name, result, game_date FROM scores ORDER BY game_date DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {
            
            while (rs.next()) {
                String playerName = rs.getString("player_name");
                String result = rs.getString("result");
                String gameDate = rs.getTimestamp("game_date").toString();
                scores.add(new String[]{playerName, result, gameDate});
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve scores", e);
        }

        return scores;
    }

    /**
     * Retrieves the top N scores (wins only) from the database.
     *
     * @param limit the maximum number of records to retrieve
     * @return list of score records as String arrays [playerName, result, date]
     */
    public List<String[]> getTopScores(int limit) {
        List<String[]> scores = new ArrayList<>();
        String selectSql = "SELECT player_name, result, game_date FROM scores WHERE result = 'WIN' ORDER BY game_date DESC LIMIT ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String playerName = rs.getString("player_name");
                    String result = rs.getString("result");
                    String gameDate = rs.getTimestamp("game_date").toString();
                    scores.add(new String[]{playerName, result, gameDate});
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve top scores", e);
        }

        return scores;
    }
}
