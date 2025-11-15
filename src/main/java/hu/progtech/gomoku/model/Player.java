package hu.progtech.gomoku.model;

/**
 * Represents a player in the Gomoku game.
 * Each player has a name and a symbol (marker) used on the board.
 */
public class Player {
    private final String name;
    private final char marker;

    /**
     * Creates a new Player with the given name and marker.
     *
     * @param name the player's name
     * @param marker the player's marker symbol ('x' or 'o')
     */
    public Player(String name, char marker) {
        this.name = name;
        this.marker = marker;
    }

    /**
     * Gets the player's name.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player's marker symbol.
     *
     * @return the player's marker
     */
    public char getMarker() {
        return marker;
    }

    @Override
    public String toString() {
        return name + " (" + marker + ")";
    }
}
