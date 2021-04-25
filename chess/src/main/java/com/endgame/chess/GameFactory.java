package com.endgame.chess;

import com.endgame.chess.Piece.Bishop;
import com.endgame.chess.Piece.King;
import com.endgame.chess.Piece.Knight;
import com.endgame.chess.Piece.Queen;
import com.endgame.chess.Piece.Rook;

import java.util.HashMap;
import java.util.Map;

public class GameFactory {
    private static GameFactory INSTANCE = new GameFactory();

    private GameFactory() {}

    public static GameFactory getInstance() {
        return INSTANCE;
    }

    public Game initGame(String scenarioName) {
        switch (scenarioName) {
            default:
            case "KQvK":
                return new KQvKGameScenario();
            case "KRvK":
                return new KRvKGameScenario();
            case "KBBvK":
                return new KBBvKGameScenario();
            case "KBNvK":
                return new KBNvKGameScenario();
            case "KQvKR":
                return new KQvKRGameScenario();
        }
    }

    public static Map<String, String> scenarioNames = new HashMap<String, String>() {{
        put("KQvK", "King & Queen");
        put("KRvK", "King & Rook");
        put("KBBvK", "Two Bishops");
        put("KBNvK", "Bishop & Knight");
        put("KQvKR", "Queen vs Rook");
    }};

    private static class KQvKGameScenario extends Game implements GameScenario {
        @Override
        public void initBoard() {
            super.initBoard();
            scenarioName = scenarioNames.get("KQvK");
            addPiece(new King(2, 2, white));
            addPiece(new Queen(4, 4, white));
            addPiece(new King(6, 4, black));
        }
    }

    private static class KRvKGameScenario extends Game implements GameScenario {
        @Override
        public void initBoard() {
            super.initBoard();
            scenarioName = scenarioNames.get("KRvK");
            addPiece(new King(2, 2, white));
            addPiece(new Rook(4, 4, white));
            addPiece(new King(6, 4, black));
        }
    }


    private static class KBBvKGameScenario extends Game implements GameScenario {
        @Override
        public void initBoard() {
            super.initBoard();
            scenarioName = scenarioNames.get("KBBvK");
            addPiece(new King(0, 0, white));
            addPiece(new Bishop(2, 2, white));
            addPiece(new Bishop(2, 3, white));
            addPiece(new King(6, 6, black));
        }
    }

    private static class KBNvKGameScenario extends Game implements GameScenario {
        @Override
        public void initBoard() {
            super.initBoard();
            scenarioName = scenarioNames.get("KBNvK");
            addPiece(new King(0, 0, white));
            addPiece(new Bishop(2, 2, white));
            addPiece(new Knight(3, 4, white));
            addPiece(new King(6, 6, black));
        }
    }

    private static class KQvKRGameScenario extends Game implements GameScenario {
        @Override
        public void initBoard() {
            super.initBoard();
            scenarioName = scenarioNames.get("KQvKR");
            addPiece(new King(0, 0, white));
            addPiece(new Queen(2, 2, white));
            addPiece(new King(5, 5, black));
            addPiece(new Rook(3, 3, black));
        }
    }
}


