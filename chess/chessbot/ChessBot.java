package chess.chessbot;

import chess.board.ChessGame;

public interface ChessBot {


    boolean move();

    void attachGame(ChessGame game);

    String getColor();

}
