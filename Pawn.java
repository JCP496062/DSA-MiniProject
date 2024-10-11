package chess;

import java.util.logging.Level;

/**
 * This is the chess.Pawn object.
 *
 * From Wikipedia:
 * The pawn may move forward to the unoccupied square immediately in front of it on the same file;
 * or on its first move it may advance two squares along the same file provided both squares are unoccupied;
 * or it may move to a square occupied by an opponent's piece which is diagonally in front of it on an adjacent file, capturing that piece.
 */
public class Pawn extends Piece implements Moveable {

    final public static String _NAME = "pawn";

    private boolean firstStep = true; // This pawn has not been moved, so it may advance two squares.

    public Pawn(Player player, Square square) {
        setPlayer(player);
        setSquare(square);
        setName(Pawn._NAME);
        square.setPiece(this);
    }

    @Override
    public boolean move(Game game, Square dest) {
        // Check if it's the player's turn
        if (!this.getPlayer().getColor().equals(game.getPlayerTurn())) {
            System.out.println("Not Your Turn");
            return false;
        }

        // Check if the destination is valid and within bounds
        if (dest == null || game.getBoard().getSquare(dest.getX(), dest.getY()) == null) {
            Main.LOGGER.log(Level.INFO, "Destination Out Of Bound");
            return false;
        }

        // Validate if the move is legal
        if (this.canMove(game.getBoard(), this.getSquare(), dest)) {
            Square prevSquare = this.getSquare();
            this.setSquare(dest); // Move the pawn to the destination square
            dest.setPiece(this); // Place pawn on the destination square
            prevSquare.setNullPiece(); // Remove pawn from the previous square

            // If capturing an opponent's piece
            if (dest.hasChess() && !this.hasSameColorWith(dest.getPiece())) {
                System.out.println("Captured " + dest.getPiece().getName());
            }

            this.firstStep = false; // After the first move, it can't move two squares anymore
            return true;
        }

        printCanNotMoveToWarning(dest);
        return false;
    }

    @Override
    public boolean canMove(Board board, Square start, Square dest) {
        // Determine movement direction based on player color (black moves up, white moves down)
        int directionMultiplier = this.getPlayer().getColor().equals(Player.WHITE) ? 1 : -1;

        int startX = start.getX();
        int startY = start.getY();
        int destX = dest.getX();
        int destY = dest.getY();

        // Check if the pawn is trying to move vertically (one square forward)
        if (destY == startY) {
            // One square forward
            if (destX == startX + directionMultiplier && !dest.hasChess()) {
                return true; // Allow moving forward to an empty square
            }

            // Two squares forward on the first move
            if (firstStep && destX == startX + 2 * directionMultiplier && !dest.hasChess()) {
                Square intermediateSquare = board.getSquare(startX + directionMultiplier, startY);
                if (!intermediateSquare.hasChess()) {
                    return true; // Allow moving two squares forward if both squares are empty
                } else {
                    System.out.println("Pawn cannot move two squares; intermediate square is occupied.");
                }
            }
        }

        // Capture diagonally (one square forward and one square sideways)
        if (Math.abs(destY - startY) == 1 && destX == startX + directionMultiplier) {
            if (dest.hasChess() && !this.hasSameColorWith(dest.getPiece())) {
                return true; // Allow capturing an opponent's piece
            } else {
                System.out.println("Pawn cannot capture; destination does not have an opponent's piece.");
            }
        }

        // If none of the conditions are met, the move is invalid
        System.out.println("Invalid pawn move from (" + startX + ", " + startY + ") to (" + destX + ", " + destY + ").");
        return false;
    }

    @Override
    public boolean hasPieceOnThePath(Board board, Square dest) {
        // Check for pieces on the path if moving two squares forward on the first move
        if (firstStep && Math.abs(dest.getX() - getSquare().getX()) == 2) {
            int directionMultiplier = this.getPlayer().getColor().equals(Player.WHITE) ? -1 : 1;
            return board.getSquare(this.getX() + directionMultiplier, this.getY()).hasChess(); // Check the square in between
        }
        return false;
    }

    @Override
    public Direction getDirection(Square start, Square dest) {
        return super.getDirection(start, dest);
    }

    public void setFirstStep(boolean firstStep) {
        this.firstStep = firstStep;
    }

    public boolean getFirstStep() {
        return firstStep;
    }

    public int getMoveDistance(Square start, Square dest) {
        return Math.abs(start.getX() - dest.getX());
    }

}