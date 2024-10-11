package chess;

import controllers.ViewController;
import view.ChessBoardPanel;

import javax.swing.*;
import java.util.Stack;

/**
 * Class for managing undo commands.
 */
public class CommandManager {

    public static final Stack<Command> undos = new Stack<>(); // Undo stack

    /**
     * Prints the contents of the undo stack.
     */
    public static void printStack() {
        while (!undos.isEmpty()) {
            Command command = undos.pop();
            printCommandDetails(command);
        }
    }

    private static void printCommandDetails(Command command) {
        int startX = command.getStart().getX();
        int startY = command.getStart().getY();
        int destX = command.getDest().getX();
        int destY = command.getDest().getY();

        String movedPieceName = command.getMovedPiece().getName();
        String killedPieceName = (command.getKilledPiece() != null) ? command.getKilledPiece().getName() : "blank";

        System.out.printf("Moved: %s (%d, %d) %s (%d, %d)%n", movedPieceName, startX, startY, killedPieceName, destX, destY);
    }

    /**
     * Pops a command from the undo stack.
     * @return The most recent command if available; otherwise null.
     */
    public static Command popCommand() {
        return canUndo() ? undos.pop() : null;
    }

    /**
     * Checks if there are commands available to undo.
     * @return True if there are commands to undo; otherwise false.
     */
    public static boolean canUndo() {
        return !undos.isEmpty();
    }

    /**
     * Executes an undo command.
     * @param viewController The view controller managing the game state.
     * @param undoCommand The command to undo.
     * @return True if the undo was executed successfully; otherwise false.
     */
    public static boolean executeUndo(ViewController viewController, Command undoCommand) {
        if (undoCommand == null) {
            JOptionPane.showMessageDialog(null, "Can't Undo");
            return false;
        }

        restoreGameState(undoCommand);
        togglePlayerTurn(viewController);
        adjustPawnFirstStep(viewController, undoCommand);

        return true;
    }

    private static void restoreGameState(Command undoCommand) {
        // Restore the state of the game based on the undo command
        undoCommand.getStart().setPiece(undoCommand.getMovedPiece());
        undoCommand.getDest().setPiece(undoCommand.getKilledPiece());

        // Update piece positions
        undoCommand.getMovedPiece().setSquare(undoCommand.getStart());
        if (undoCommand.getKilledPiece() != null) {
            undoCommand.getKilledPiece().setSquare(undoCommand.getDest());
        }

        // Update button icons
        undoCommand.getStartButton().setIcon(undoCommand.getStartImage());
        undoCommand.getDestButton().setIcon(undoCommand.getDestImage());
    }

    private static void togglePlayerTurn(ViewController viewController) {
        String currentTurn = viewController.getGame().getPlayerTurn();
        viewController.getGame().setPlayerTurn(currentTurn == Player.BLACK ? Player.WHITE : Player.BLACK);
    }

    /**
     * Adjusts pawn's first step status after an undo operation.
     * @param viewController The view controller managing the game state.
     * @param undoCommand The command that was undone.
     */
    private static void adjustPawnFirstStep(ViewController viewController, Command undoCommand) {
        Piece piece = undoCommand.getMovedPiece();

        if (piece instanceof Pawn) {
            Pawn pawn = (Pawn) piece;

            // Reset pawn's first step status if it was its first move
            if (pawn.getFirstStep()) {
                pawn.setFirstStep(true);
                viewController.nextPlayer--;
                viewController.getChessBoardPanel().setTurn(viewController.nextPlayer);
            }
        }
    }

    /**
     * Creates a new game and initializes the chess board panel.
     * @param game The current game instance.
     * @param chessBoardPanel The panel displaying the chess board.
     * @param gameType The type of game to create (e.g., custom or standard).
     */
    public static void createNewGame(Game game, ChessBoardPanel chessBoardPanel, String gameType) {
        game = linkGame(game, gameType, chessBoardPanel);

        repaintChessBoard(game, chessBoardPanel);

        // Initialize player turn
        game.getVc().nextPlayer = 0;
        game.getVc().currPlayer = Player.BLACK;
        chessBoardPanel.setTurn(0);

        undos.clear(); // Clear all previous commands
    }

    private static void repaintChessBoard(Game game, ChessBoardPanel chessBoardPanel) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (game.getBoard().getSquare(i, j).hasChess()) {
                    chessBoardPanel.setImageIcon(chessBoardPanel.pieces[j][i], chessBoardPanel.getPieceImageIcon(i, j));
                } else {
                    chessBoardPanel.pieces[j][i].setIcon(null);
                }
            }
        }
    }

    /**
     * Links a new game instance with the ViewController and initializes it.
     * @param game The current game instance.
     * @param gameType The type of game to create (e.g., custom or standard).
     * @param chessBoardPanel The panel displaying the chess board.
     * @return The newly created game instance.
     */
    private static Game linkGame(Game game, String gameType, ChessBoardPanel chessBoardPanel) {
        ViewController vc = game.getVc();

        // Create custom or normal new game
        if (gameType.equals(Board.CUSTOM_BOARD)) {
            game = Game.customGame();
        } else {
            game = Game.newGame();
        }

        vc.setGame(game);
        game.setVc(vc);
        chessBoardPanel.setBoard(game.getBoard());

        return game;
    }
}