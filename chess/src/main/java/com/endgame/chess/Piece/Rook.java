package com.endgame.chess.Piece;

import com.endgame.chess.Game.PieceType;
import com.endgame.chess.Player.AbstractPlayer;

public class Rook extends AbstractPiece {
    private static final int MAX_RANGE = 7;
    private static final int[][] OFFSETS = {
            {-1, 0}, // up
            {1, 0}, // down
            {0, -1}, // left
            {0, 1}, // right
    };

    public Rook(Rook other) {
        super(other);
    }

    public Rook(int row, int col, AbstractPlayer player) {
        super(row, col, player);
        type = PieceType.ROOK;
        character = "R";
        offsets = OFFSETS;
        maxRange = MAX_RANGE;
    }

    public Rook copyToBoard(AbstractPiece[][] board) {
        Rook rook = new Rook(this);
        rook.board = board;
        return rook;
    }
}