package com.endgame.chess;

import com.endgame.chess.Piece.AbstractPiece;

// this class is for developing chess logic without the overhead of loading the GUI
class Main {
    public static void main (String[] args){
        GameFactory gameFactory = GameFactory.getInstance();
        Game game = gameFactory.initGame("KQvK");
        AbstractPiece[][] board = game.getBoard();

        System.out.println(game.stringifyBoard());
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!board[i][j].isEmptyPiece()) {
                    System.out.println("This Piece " + board[i][j].getType() + " sees these pieces: ");
                    System.out.println(board[i][j].getPossibleSquares());
                    if (board[i][j].getType() == Game.PieceType.KING) {
                        System.out.println("Checkmate? " + game.isCheckmate(game.computer));
                    }
                }
            }
        }
        System.out.println("Stalemate? " + game.isStalemate(game.computer));
    }

}