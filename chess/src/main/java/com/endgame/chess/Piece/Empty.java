package com.endgame.chess.Piece;

import com.endgame.chess.Game.PieceType;
import com.endgame.chess.Player.NoPlayer;

import java.util.ArrayList;

public class Empty extends AbstractPiece {
    @Override
    public ArrayList<AbstractPiece> getPossibleSquares() {
        return null;
    }

    public Empty(Empty other) {
        super(other);
    }

    @Override
    public Empty copyToBoard(AbstractPiece[][] board) {
        Empty empty = new Empty(this);
        empty.board = board;
        return empty;
    }

    public Empty(int row, int col, NoPlayer player) {
        super(row, col, player);
        type = PieceType.EMPTY;
        character = ".";
    }
}
