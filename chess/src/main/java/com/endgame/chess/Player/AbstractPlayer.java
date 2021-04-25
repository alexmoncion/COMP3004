package com.endgame.chess.Player;

import com.endgame.chess.Game;
import com.endgame.chess.Game.Color;

public abstract class AbstractPlayer {
    protected Color color;
    private boolean isHuman;
    public int moveCount;
    Game game;

    public AbstractPlayer(Game game, Color color, boolean isHuman) {
        this.game = game;
        this.color = color;
        this.isHuman = isHuman;
    }

    public Game getGame() {
        return game;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public Color getColor() {
        return color;
    }

    public int getMoveCount() { return moveCount; }

    public abstract void makeMove();
}
