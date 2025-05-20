package chess.chessbot;

import chess.board.ChessGame;
import chess.gui.ChessGameGUI;

public class ChessGameHumanVersusBot extends ChessGame {

    public ChessGameGUI game;

    public ChessGameHumanVersusBot(ChessBot chessBot) {
        super();
        bot = chessBot;
    }

}
