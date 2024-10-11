package chess;

public class Player {

    public static String WHITE = "white";
    public static String BLACK = "black";

    private String color; // Either "white" or "black"
    private King king;    // The player's king piece
    private static Player currentPlayer; // Static field to track the current player

    // Constructor for initializing the player with their color
    public Player(String color) {
        this.color = color;
    }

    // Method to get the player's color
    public String getColor() {
        return color;
    }

    // Method to get the player's king piece
    public King getKing() {
        return king;
    }

    // Method to set the player's king piece
    public void setKing(King king) {
        this.king = king;
    }

    // Static method to get the current player
    public static Player getCurrentPlayer() {
        return currentPlayer;
    }

    // Static method to set the current player
    public static void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    // Method to get the opponent based on the current player
    public Player getOpponent() {
        // Check the color of the current player to determine the opponent
        if (currentPlayer.getColor().equals(WHITE)) {
            return new Player(BLACK); // Return a new Player instance for the black player
        } else {
            return new Player(WHITE); // Return a new Player instance for the white player
        }
    }
}
