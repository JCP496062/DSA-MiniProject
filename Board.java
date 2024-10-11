package chess;

import java.util.ArrayList;

public class Board {

    public final static String BLANK_BOARD = "blank";
    public final static String START_BOARD = "start";
    public final static String CUSTOM_BOARD = "custom";
    public static int NUM_PIECES = 16;

    public Square[][] board;

    public Board(String option) {
        board = new Square[8][8]; // Corrected to 8x8 for a standard chessboard

        if (option.equals(BLANK_BOARD)) {
            initBlankBoard();
        } else if (option.equals(START_BOARD)) {
            this.initBoard(new Player(Player.WHITE), new Player(Player.BLACK));
        } else if (option.equals(CUSTOM_BOARD)) {
            this.initCustomBoard(new Player(Player.WHITE), new Player(Player.BLACK));
        }
    }

    private void initBoard(Player player1, Player player2) {
        initBlankBoard();


        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = null;

                if (i == 0 && j == 4) { // White King
                    piece = new King(player1, board[i][j]);
                } else if (i == 7 && j == 4) { // Black King
                    piece = new King(player2, board[i][j]);
                }

                // Initialize white pawns
                if (i == 1) {
                    piece = new Pawn(player1, board[i][j]);
                }

                // Initialize black pawns
                if (i == 6) {
                    piece = new Pawn(player2, board[i][j]);
                }

                // Initialize major pieces
                Player currPlayer;
                if (i == 0 || i == 7) {
                    currPlayer = (i == 0) ? player1 : player2;

                    if (j == 0 || j == 7) {
                        piece = new Rook(currPlayer, board[i][j]);
                    } else if (j == 1 || j == 6) {
                        piece = new Knight(currPlayer, board[i][j]);
                    } else if (j == 2 || j == 5) {
                        piece = new Bishop(currPlayer, board[i][j]);
                    } else if (j == 3) {
                        piece = new Queen(currPlayer, board[i][j]);
                    } else {
                        piece = new King(currPlayer, board[i][j]);
                    }
                }

                board[i][j].setPiece(piece);
            }
        }
    }

    private void initCustomBoard(Player player1, Player player2) {
        initBlankBoard();
        initBoard(player1, player2);
        getSquare(1, 7).setPiece(new Witch(player1, getSquare(1, 7)));
        getSquare(1, 0).setPiece(new Vampire(player1, getSquare(1, 0)));

        getSquare(6, 7).setPiece(new Witch(player2, getSquare(6, 7)));
        getSquare(6, 0).setPiece(new Vampire(player2, getSquare(6, 0)));
    }

    private void initBlankBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Square(null, i, j);
            }
        }
    }

    public boolean doNotContain(Square square) {
        return square.getX() > 7 || square.getX() < 0 || square.getY() > 7 || square.getY() < 0;
    }

    public Square getSquare(int x, int y) {
        if (x > 7 || x < 0 || y > 7 || y < 0) {
            return null;
        }
        return board[x][y];
    }

    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            System.out.println();
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j].getPiece();
                if (piece == null) {
                    System.out.print("blank   ");
                } else {
                    System.out.print(piece.getName() + " (" + piece.getPlayer().getColor() + ")  ");
                }
            }
        }
    }

    public boolean checkMate(Game game, Piece tryKillKingPiece, King king) {
        if (!isKingInCheck(game, king)) {
            return false; // King is not in check, no checkmate
        }

        // Check if king can escape
        ArrayList<Square> kingMoves = king.generatePossibleMoves(game);
        for (Square dest : kingMoves) {
            if (canMoveWithoutCheck(game, king, dest)) {
                return false; // King can escape, no checkmate
            }
        }

        // Check if other pieces can capture the checking piece or block the check
        ArrayList<Piece> pieces = findSameColorPieces(king.getPlayer().getColor());
        for (Piece piece : pieces) {
            ArrayList<Square> pieceMoves = piece.generatePossibleMoves(game);
            for (Square dest : pieceMoves) {
                Piece capturedPiece = dest.getPiece();
                Square start = piece.getSquare();

                piece.move(game, dest);
                if (!isKingInCheck(game, king)) {
                    // Undo the move and return false (no checkmate)
                    piece.move(game, start);
                    dest.setPiece(capturedPiece);
                    return false;
                }
                // Undo the move
                piece.move(game, start);
                dest.setPiece(capturedPiece);
            }
        }

        return true; // No valid moves left, checkmate
    }

    public King checkKing(Game game) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j].getPiece();
                if (piece instanceof King) {
                    System.out.println("Found a king at: " + i + ", " + j);
                    King king = (King) piece;
                    if (isKingInCheck(game, king)) {
                        return king;
                    }
                }
            }
        }
        return null;
    }

    private boolean isKingInCheck(Game game, King king) {
        Player opponent = game.getOpponent(king.getPlayer());
        System.out.println("King of: " + king.getPlayer());
        System.out.println("Opponent is: " + opponent);

        Board board = game.getBoard(); // Fetch the board

        // Loop through all squares on the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square currentSquare = board.getSquare(i, j); // Get the current square
                Piece piece = currentSquare.getPiece(); // Get the piece on the square

                if (piece != null && !piece.getPlayer().equals(king.getPlayer())) { // If it's an opponent's piece
                    System.out.println("Found opponent's piece: " + piece.getName());

                    // Get all possible moves of the opponent's piece
                    ArrayList<Square> possibleMoves = piece.generatePossibleMoves(game);

                    // Check if any of the possible moves would put the king in check
                    for (Square move : possibleMoves) {
                        if (move.equals(king.getSquare())) {
                            System.out.println("King is in check by: " + piece.getName());
                            return true; // King is in check
                        }
                    }
                }
            }
        }
        return false; // King is not in check
    }
    public boolean isMoveSafe(Game game, Piece piece, Square dest) {
        // Backup the current state
        Square originalSquare = piece.getSquare();
        Piece capturedPiece = dest.getPiece(); // Might be null

        // Temporarily make the move
        dest.setPiece(piece);
        originalSquare.setNullPiece();
        piece.setSquare(dest);

        // Check if the player's king is in check after the move
        boolean kingInCheck = isKingInCheck(game, piece.getPlayer().getKing());

        // Revert the move
        originalSquare.setPiece(piece);
        dest.setPiece(capturedPiece);
        piece.setSquare(originalSquare);

        // Return false if the move leaves the king in check
        return !kingInCheck;
    }


    private boolean canMoveWithoutCheck(Game game, King king, Square move) {
        Piece targetPiece = move.getPiece();
        Square originalSquare = king.getSquare();

        king.move(game, move);
        boolean stillInCheck = isKingInCheck(game, king);

        king.move(game, originalSquare);

        // Restore the target square's original state
        move.setPiece(targetPiece);

        return !stillInCheck;
    }

    public ArrayList<Piece> findSameColorPieces(String color) {
        ArrayList<Piece> pieces = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = getSquare(i,j).getPiece();

                if(piece != null && piece.getPlayer().getColor().equals(color)) {
                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }

    public King[] findKing(Game game) {
        King[] kings = new King[2];
        int count=0;

        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                Piece piece=game.getBoard().getSquare(i,j).getPiece();
                if(piece instanceof King){
                    kings[count]=(King)piece;
                    count++;
                }
            }
        }
        return kings;
    }

    // New method to retrieve all pieces on the board
    public ArrayList<Piece> getPieces() {
        ArrayList<Piece> allPieces = new ArrayList<>();
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                Piece piece=board[i][j].getPiece();
                if(piece!=null){
                    allPieces.add(piece);
                }
            }
        }
        return allPieces;
    }
}