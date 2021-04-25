package com.endgame.chess.Piece;

import com.endgame.chess.Game;
import com.endgame.chess.Game.Log;
import com.endgame.chess.Game.Color;
import com.endgame.chess.Game.PieceType;

import com.endgame.chess.Player.AbstractPlayer;

import java.util.ArrayList;

public abstract class AbstractPiece {
    int[][] offsets;
    Game.PieceType type;
    Color color;
    String character;
    Game game;
    AbstractPlayer player;
    AbstractPiece[][] board;
    boolean isCaptured;
    int row;
    int col;
    int maxRange;

    public AbstractPiece() {}

    // copy constructor

    public AbstractPiece(AbstractPiece other) {
        this.offsets = other.offsets;
        this.type = other.type;
        this.color = other.color;
        this.character = other.character;
        this.game = other.game;
        this.player = other.player;
        this.board = other.board;
        this.isCaptured = other.isCaptured;
        this.row = other.row;
        this.col = other.col;
        this.maxRange = other.maxRange;
    }

    public AbstractPiece(int row, int col, AbstractPlayer player) {
        this.row = row;
        this.col = col;
        this.player = player;
        this.game = player.getGame();
        this.board = player.getGame().getBoard();
        this.isCaptured = false;
        this.color = player.getColor();
    }

//    public abstract int getMaxRange();

    public abstract AbstractPiece copyToBoard(AbstractPiece[][] board);

    public boolean isCaptured() {
        return isCaptured;
    }

    public void setCaptured(boolean captured) {
        isCaptured = captured;
        row = -1;
        col = -1;
    }

    public PieceType getType() {
        return type;
    }

    public String getCharacter() {
        return player.getColor() == Color.WHITE ? character : character.toLowerCase();
    }

    public Color getColor() {
        return player.getColor();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void updatePosition(int toRow, int toCol) {
        row = toRow;
        col = toCol;
    }

    public String getRankAndFile() {
        //example: rowCol_to_coord(7,7) -> "h1"
        //         rowCol_to_coord(0,0) -> "a8"
        //         rowCol_to_coord(1,5) -> "f7"
        String coord = "";
        coord += String.valueOf((char) (col + 1 + 96));
        coord += Integer.toString(7 - row + 1);
        return coord;
    }


    @Override
    public String toString() {
        return getCharacter() + getRankAndFile();
    }

    public ArrayList<AbstractPiece> getPossibleSquares() {
        return getPossibleSquares(false);
    }

    public ArrayList<AbstractPiece> getAttackingSquares() {
        return getPossibleSquares(true);
    }

    public AbstractPlayer getPlayer() {
        return player;
    }

    public ArrayList<AbstractPiece> getPossibleSquares(boolean getAttacking) {
        ArrayList<AbstractPiece> possibleSquares = new ArrayList<>();
        for (int[] o : offsets) {
            for (int i = 1; i <= maxRange; i++) {
                int checkRow = row + (i * o[0]);
                int checkCol = col + (i * o[1]);
                if (checkRow > 7 || checkRow < 0 || checkCol > 7 || checkCol < 0)
                    break;
                AbstractPiece piece = board[checkRow][checkCol];
                if (isEmpty(piece)) {
                    possibleSquares.add(piece);
                } else {
                    if (getAttacking || isFoe(piece)) {
                        possibleSquares.add(piece);
                    }
                    break;
                }
            }
        }

        if (!getAttacking) {
            possibleSquares = filterMovesIntoCheck(possibleSquares);
        }

        return possibleSquares;
    }

    public ArrayList<AbstractPiece> filterMovesIntoCheck(ArrayList<AbstractPiece> possibleSquares) {
        ArrayList<AbstractPiece> filteredSquares = new ArrayList<>();
        for (AbstractPiece possibleSquare : possibleSquares) {
            if (!isMoveIntoCheck(possibleSquare)) {
                filteredSquares.add(possibleSquare);
            }
        }
        return filteredSquares;
    }

    public boolean isFoe(int toRow, int toCol) {
        return isFoe(board[toRow][toCol]);
    }

    public boolean isFoe(AbstractPiece target) {
        return (!target.isEmptyPiece()) && (color != target.getColor());
    }

    public boolean isEmptyPiece() {
        return type == PieceType.EMPTY;
    }

    public boolean isEmpty(int row, int col) {
        return board[row][col].getType() == PieceType.EMPTY;
    }

    public boolean isEmpty(AbstractPiece piece) {
        return piece.getType() == PieceType.EMPTY;
    }

    private boolean isMoveIntoCheck(AbstractPiece target) {
        // make moves on speculative board
        board = game.startSpeculation();

        King king = (King) game.findPlayerPiece(this.player, PieceType.KING);
        assert king != null;

        game.movePiece(row, col, target.row, target.col);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                AbstractPiece piece = board[row][col];
                if (isFoe(piece)) {
                    for (AbstractPiece attackedPiece : piece.getAttackingSquares()) {
                        if (attackedPiece == king) {
                            // restore board
                            board = game.endSpeculation();
                            return true;
                        }
                    }
                }
            }
        }
        // restore board
        board = game.endSpeculation();
        return false;
    }
}
