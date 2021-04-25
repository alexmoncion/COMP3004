package com.endgame.chess.Piece;

import com.endgame.chess.Game.PieceType;
import com.endgame.chess.Player.AbstractPlayer;

public class Bishop extends AbstractPiece {
    private static final int MAX_RANGE = 7;
    private static final int[][] OFFSETS = {
            {-1, -1}, // up left
            {-1, 1}, // up right
            {1, -1}, // down left
            {1, 1}, // down right
    };

    public Bishop(Bishop other) {
        super(other);
    }

    public Bishop(int row, int col, AbstractPlayer player) {
        super(row, col, player);
        type = PieceType.BISHOP;
        character = "B";
        offsets = OFFSETS;
        maxRange = MAX_RANGE;
    }

    public Bishop copyToBoard(AbstractPiece[][] board) {
        Bishop bishop = new Bishop(this);
        bishop.board = board;
        return bishop;
    }
}
