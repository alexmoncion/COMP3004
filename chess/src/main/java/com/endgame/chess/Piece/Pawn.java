package com.endgame.chess.Piece;

import com.endgame.chess.Game.PieceType;
import com.endgame.chess.Player.AbstractPlayer;

import java.util.ArrayList;

abstract public class Pawn extends AbstractPiece {
    private static final int MAX_RANGE = 1;
    @Override
    public ArrayList<AbstractPiece> getPossibleSquares() {
        // TODO: implement
        return null;
    };

    public Pawn(int row, int col, AbstractPlayer player) {
        super(row, col, player);
        type = PieceType.PAWN;
        character = "P";
    }
}
