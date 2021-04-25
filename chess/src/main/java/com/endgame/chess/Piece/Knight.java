package com.endgame.chess.Piece;

import com.endgame.chess.Game.PieceType;
import com.endgame.chess.Player.AbstractPlayer;

public class Knight extends AbstractPiece {
    private static final int MAX_RANGE = 1;
    private static final int[][] OFFSETS = {
            {-2, 1}, // up 2 right 1
            {-1, 2}, // up 1 right 2
            {1, 2}, // down 1 right 2
            {2, 1}, // down 2 right 1
            {2, -1}, // down 2 left 1
            {1, -2}, // down 1 left 2
            {-1, -2}, // up 1 left 2
            {-2, -1} // up 2 left 1
    };

    public Knight(Knight other) {
        super(other);
    }

    public Knight copyToBoard(AbstractPiece[][] board) {
        Knight knight = new Knight(this);
        knight.board = board;
        return knight;
    }

    public Knight(int row, int col, AbstractPlayer player) {
        super(row, col, player);
        type = PieceType.KNIGHT;
        character = "N";
        offsets = OFFSETS;
        maxRange = MAX_RANGE;
    }
}
