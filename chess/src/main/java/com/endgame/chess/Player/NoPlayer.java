package com.endgame.chess.Player;

import com.endgame.chess.Game;

public class NoPlayer extends AbstractPlayer {
    public NoPlayer(Game game) {
        super(game, Game.Color.NONE, false);
    }
    @Override
    public void makeMove() {
        // do nothing
    }
}
