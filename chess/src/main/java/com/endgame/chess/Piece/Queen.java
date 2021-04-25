package com.endgame.chess.Piece;

import com.endgame.chess.Game.PieceType;
import com.endgame.chess.Player.AbstractPlayer;

public class Queen extends AbstractPiece {
    private static final int MAX_RANGE = 7;
    private static final int[][] OFFSETS = {
            {-1, -1}, // up left
            {-1, 1}, // up right
            {1, -1}, // down left
            {1, 1}, // down right
            {-1, 0}, // up
            {1, 0}, // down
            {0, -1}, // left
            {0, 1}, // right
    };

    public Queen(Queen other) {
        super(other);
    }

    public Queen copyToBoard(AbstractPiece[][] board) {
        Queen queen = new Queen(this);
        queen.board = board;
        return queen;
    }

    public Queen(int row, int col, AbstractPlayer player) {
        super(row, col, player);
        type = PieceType.QUEEN;
        character = "Q";
        offsets = OFFSETS;
        maxRange = MAX_RANGE;
    }
}
