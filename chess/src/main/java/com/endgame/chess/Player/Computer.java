package com.endgame.chess.Player;

import com.endgame.chess.Game;
import com.endgame.chess.Game.Color;
import com.endgame.chess.Game.Log;
import com.endgame.chess.Piece.AbstractPiece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Computer extends AbstractPlayer {
    public Computer(Game game, Color color) {
        super(game, color, false);
    }

    public void makeMove() {
        moveCount++;

        // get randomly ordered list of computer's pieces
        ArrayList<AbstractPiece> computerPieces = game.boardStream()
                .filter(p -> (p.getPlayer() == this))
                .collect(Collectors.toCollection(ArrayList::new));
        ;
        Collections.shuffle(computerPieces);

        // capture a random enemy piece if possible
        for (AbstractPiece piece : computerPieces) {
            AbstractPiece[] enemies = piece.getPossibleSquares().stream()
                    .filter(p -> !p.isEmptyPiece())
                    .toArray(AbstractPiece[]::new);
            if (enemies.length > 0) {
                Log.i("ai", "found something to eat!");
                int i = enemies.length > 1 ? ThreadLocalRandom.current().nextInt(0, enemies.length) : 0;
                game.movePiece(piece, enemies[i]);
                game.nextTurn();
                return;
            }
        }

        // move a random piece
        Log.i("ai", "found nothing to eat :(");
        for (AbstractPiece piece : computerPieces) {
            ArrayList<AbstractPiece> squares = piece.getPossibleSquares();
            if (squares.size() > 0) {
                int i = squares.size() > 1 ? ThreadLocalRandom.current().nextInt(0, squares.size()) : 0;
                game.movePiece(piece, squares.get(i));
                game.nextTurn();
                return;
            }
        }

        // this can only happen if you call makeMove on an invalid board state
        Log.i("ai", "no valid moves! ;(");
        game.nextTurn();
    }
}
