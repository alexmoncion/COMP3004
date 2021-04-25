package com.endgame.chess.Piece;

import com.endgame.chess.Game.PieceType;
import com.endgame.chess.Player.AbstractPlayer;

public class King extends AbstractPiece {
    private static final int MAX_RANGE = 1;
    private static final int[][] OFFSETS = new int[][]{
            {-1, -1}, // up left
            {-1, 1}, // up right
            {1, -1}, // down left
            {1, 1}, // down right
            {-1, 0}, // up
            {1, 0}, // down
            {0, -1}, // left
            {0, 1}, // right
    };

    public King(King other) {
        super(other);
    }

    public King(int row, int col, AbstractPlayer player) {
        super(row, col, player);
        type = PieceType.KING;
        character = "K";
        offsets = OFFSETS;
        maxRange = MAX_RANGE;
    }

    public King copyToBoard(AbstractPiece[][] board) {
        King king = new King(this);
        king.board = board;
        return king;
    }

    public boolean isInCheck() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                AbstractPiece attackingPiece = board[i][j];
                if (isFoe(attackingPiece)) {
                    for (AbstractPiece attackedSquare : attackingPiece.getAttackingSquares()) {
                        if (attackedSquare == this) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
