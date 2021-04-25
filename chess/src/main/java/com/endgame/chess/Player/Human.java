package com.endgame.chess.Player;

import com.endgame.chess.Game;
import com.endgame.chess.Game.Color;

public class Human extends AbstractPlayer {
    public Human(Game game, Color color) {
        super(game, color, true);
    }

    @Override
    public void makeMove() {
        moveCount++;
        // wait for input from human
    }
}
