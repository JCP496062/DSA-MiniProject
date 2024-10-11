package chess;

/**
 * This is chess.King object
 * From Wikipedia,
 * The chess.King moves one square in any direction.
 */
public class King extends Piece {

    final public static String _NAME = "king";

    private Piece checkPiece;

    // Constructor
    public King(Player player, Square square) {
        setPlayer(player);
        setSquare(square);
        setName(King._NAME);
        square.setPiece(this);
    }

    @Override
    public boolean canMove(Board board, Square start, Square dest) {
        // Check if the destination square is occupied by player's own piece
        // and if the destination square is out of bound
        if (!passBasicMoveRules(board, start, dest)) {
            return false;
        }

        float dfX = Math.abs(start.getX() - dest.getX());
        float dfY = Math.abs(start.getY() - dest.getY());

        if (getDirection(start, dest) == Direction.UP ||
                getDirection(start, dest) == Direction.DOWN ||
                getDirection(start, dest) == Direction.RIGHT ||
                getDirection(start, dest) == Direction.LEFT) {
            return Math.abs(start.getX() - dest.getX()) + Math.abs(start.getY() - dest.getY()) == 1;
        } else {
            return dfX == 1 && dfY == 1;
        }
    }

    // King does not need this method since it can only move 1 square
    @Override
    public boolean hasPieceOnThePath(Board board, Square dest) {
        return false;
    }

    public void setCheckPiece(Piece checkPiece) {
        this.checkPiece = checkPiece;
    }

    public Piece getCheckPiece() {
        return checkPiece;
    }

    // Check if the king is in check
    public boolean isInCheck(Board board) {
        Square kingSquare = this.getSquare();
        for (Piece piece : board.getPieces()) {
            if (piece.getPlayer() != this.getPlayer() && piece.canMove(board, piece.getSquare(), kingSquare)) {
                return true; // The king is in check
            }
        }
        return false; // The king is not in check
    }

    // Check if the king is in checkmate
    public boolean isCheckmate(Board board) {
        if (!isInCheck(board)) {
            return false; // Not in check, so can't be checkmate
        }

        // Get all possible moves for the king
        for (Direction direction : Direction.values()) {
            Square newSquare = getSquare().getAdjacentSquare(direction);
            if (newSquare != null && canMove(board, getSquare(), newSquare)) {
                King tempKing = new King(this.getPlayer(), newSquare);
                if (!tempKing.isInCheck(board)) {
                    return false; // The king can move to a safe square
                }
            }
        }

        return true; // No legal moves available, it's checkmate
    }
}