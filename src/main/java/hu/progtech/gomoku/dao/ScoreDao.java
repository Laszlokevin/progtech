package hu.progtech.gomoku.dao;

import java.io.File;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple DAO using embedded H2 to persist player wins.
 * Database URL is file-based under ./data/gomoku by default.
 */
public class ScoreDao {
    private final String dbUrl;

    public ScoreDao() {
        File f = new File("data");
        if (!f.exists()) f.mkdirs();
        dbUrl = "jdbc:h2:./data/gomoku;AUTO_SERVER=FALSE";
        init();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, "sa", "");
    }

    private void init() {
        try (Connection c = getConnection();
             Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS scores (name VARCHAR PRIMARY KEY, wins INT)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void incrementWin(String playerName) {
        try (Connection c = getConnection()) {
            // upsert
            PreparedStatement ps = c.prepareStatement(
                    "MERGE INTO scores (name, wins) KEY(name) VALUES (?, COALESCE((SELECT wins FROM scores WHERE name=?),0) + 1)");
            ps.setString(1, playerName);
            ps.setString(2, playerName);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> getAllScores() {
        Map<String, Integer> result = new LinkedHashMap<>();
        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT name, wins FROM scores ORDER BY wins DESC")) {
            while (rs.next()) {
                result.put(rs.getString("name"), rs.getInt("wins"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
