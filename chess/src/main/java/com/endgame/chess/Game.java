package com.endgame.chess;

import com.endgame.chess.Piece.AbstractPiece;
import com.endgame.chess.Piece.Empty;
import com.endgame.chess.Piece.King;
import com.endgame.chess.Player.AbstractPlayer;
import com.endgame.chess.Player.Computer;
import com.endgame.chess.Player.Human;
import com.endgame.chess.Player.NoPlayer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class Game implements GameScenario {
    public Computer computer;
    public Computer computer2;
    public Human human;
    public NoPlayer nobody;
    public AbstractPlayer white;
    public AbstractPlayer black;
    public AbstractPlayer activePlayer;
    public AbstractPlayer waitingPlayer;

    private PropertyChangeSupport subject;
    private ArrayList<AbstractPiece[][]> boards;
    private AbstractPiece[][] board;
    public boolean gameOver;
    public AbstractPlayer winner;
    public String scenarioName;

    public Game() {
        // make this a subject in the observer pattern
        subject = new PropertyChangeSupport(this);

        boards = new ArrayList<>();
        board = new AbstractPiece[8][8];
        human = new Human(this, Color.WHITE);
        computer = new Computer(this, Color.BLACK);
//        computer2 = new Computer(this, Color.WHITE);
        nobody = new NoPlayer(this);

//        white = computer2;
        white = human;
        black = computer;
        activePlayer = white; // white goes first
        waitingPlayer = black;

        initBoard();
    }

    public void attachObserver(PropertyChangeListener pcl) {
        subject.addPropertyChangeListener(pcl);
    }

    public void removeObserver(PropertyChangeListener pcl) {
        subject.removePropertyChangeListener(pcl);
    }

    // check if a player is stalemated
    boolean isStalemate(AbstractPlayer player) {
        King king = (King) findPlayerPiece(player, PieceType.KING);
        if (!king.getPossibleSquares().isEmpty() || king.isInCheck()) {
            return false;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                AbstractPiece piece = board[row][col];
                if (!piece.isEmptyPiece() && piece.getPlayer() == player && piece != king) {
                    if (!piece.getPossibleSquares().isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    // check if a player is checkmated
    boolean isCheckmate(AbstractPlayer player) {
        King king = (King) findPlayerPiece(player, PieceType.KING);
        // not in check?
        if (!king.isInCheck()) return false;
        // if we can legally move something, it's not checkmate!
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                AbstractPiece piece = board[row][col];
                if (!piece.isEmptyPiece() && piece.getPlayer() == player) {
                    if (!piece.getPossibleSquares().isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    String stringifyBoard() {
        String o = "Board (depth " + boards.indexOf(board) + ")\n";

        for (int row = 0; row < 8; row++) {
            o += (8 - row) + " ";
            for (int col = 0; col < 8; col++) {
                o += board[row][col].getCharacter() + " ";
            }
            o += "\n";
        }
        o += "  A B C D E F G H";
        return o;
    }

    public void checkGameOver() {
        boolean checkmate = isCheckmate(waitingPlayer);
        boolean stalemate = isStalemate(waitingPlayer);

        gameOver = checkmate || stalemate;

        if (checkmate) winner = activePlayer;
        if (stalemate) winner = nobody;
    }

    public void nextTurn() {
        // this is effectively notify() from the observer pattern
        subject.firePropertyChange("board", null, board);

        AbstractPlayer previousPlayer;

        checkGameOver();
        if (gameOver) {
            subject.firePropertyChange("gameOver", false, true);
            return;
        }

        // switch active player
        previousPlayer = activePlayer;
        activePlayer = waitingPlayer;
        waitingPlayer = previousPlayer;

        // wait a non-zero amount of time before making next move to avoid blocking the ui thread!
        // wait longer for computers to give the appearance of movement
        int delay = activePlayer.isHuman() ? 1 : 20;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                activePlayer.makeMove();
            }
        }, delay);
    }

    @Override
    public void initBoard() {
        // build empty board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = new Empty(row, col, nobody);
            }
        }
        boards.add(board);
    }

    public AbstractPiece[][] getBoard() {
        return board;
    }

    public Stream<AbstractPiece> boardStream() {
        ArrayList<AbstractPiece> b = new ArrayList<>();
        for (AbstractPiece[] row : board) {
            b.addAll(Arrays.asList(row));
        }
        return b.stream();
    }

    public void addPiece(AbstractPiece piece) {
        board[piece.getRow()][piece.getCol()] = piece;
    }

    private void removePiece(AbstractPiece piece) {
        board[piece.getRow()][piece.getCol()] = new Empty(piece.getRow(), piece.getCol(), nobody);
        piece.setCaptured(true);
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        AbstractPiece piece = board[fromRow][fromCol];
        movePiece(piece, toRow, toCol);
    }

    private void movePiece(AbstractPiece piece, int toRow, int toCol) {
        AbstractPiece dest = board[toRow][toCol];
        movePiece(piece, dest);
    }

    public void movePiece(AbstractPiece piece, AbstractPiece dest) {
        Log.i("moving", piece.toString() + " to " + dest.toString());

        int toRow = dest.getRow();
        int toCol = dest.getCol();

        if (!dest.isEmptyPiece()) {
            Log.i("removing", dest.toString());
            removePiece(board[dest.getRow()][dest.getCol()]);
        }
        board[piece.getRow()][piece.getCol()] = new Empty(piece.getRow(), piece.getCol(), nobody);
        board[toRow][toCol] = piece;
        piece.updatePosition(toRow, toCol);
    }

    public AbstractPiece findPlayerPiece(AbstractPlayer player, PieceType pieceType) {
        AbstractPiece piece = boardStream()
                .filter(p -> (p.getColor() == player.getColor()))
                .filter(p -> (p.getType() == pieceType))
                .findFirst()
                .orElse(null);

        return piece;
    }

    // save the existing board state, and start working with a cloned version
    public AbstractPiece[][] startSpeculation() {
        AbstractPiece[][] specBoard = new AbstractPiece[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                specBoard[row][col] = board[row][col].copyToBoard(specBoard);
            }
        }
        // push spec board
        boards.add(specBoard);
        // use spec board
        board = specBoard;
        return board;
    }

    // restore the previous board state
    public AbstractPiece[][] endSpeculation() {
        // pop current board
        boards.remove(boards.size() - 1);
        // get previous board
        board = boards.get(boards.size() - 1);
        return board;
    }

    public void shuffleBoard() {
        AbstractPiece[] pieces = boardStream()
                .filter(p -> (!p.isEmptyPiece()))
                .toArray(AbstractPiece[]::new);
        for (int i = 0; i < 100; i++) {
            for (AbstractPiece p : pieces) {
                AbstractPiece[] squares = p.getPossibleSquares().stream()
                        .filter(AbstractPiece::isEmptyPiece)
                        .toArray(AbstractPiece[]::new);
                if (squares.length > 0) {
                    int j = squares.length > 1 ? ThreadLocalRandom.current().nextInt(0, squares.length) : 0;
                    movePiece(p, squares[j]);
                }
            }
        }
    }

    public enum Color {
        WHITE,
        BLACK,
        NONE
    }

    public enum PieceType {
        KING,
        QUEEN,
        ROOK,
        BISHOP,
        KNIGHT,
        PAWN,
        EMPTY
    }

    public static class Log {
        public static void i(String a, String b) {
            System.out.println(a + " " + b);
        }

    }
}