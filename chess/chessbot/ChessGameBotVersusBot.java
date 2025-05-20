package chess.chessbot;

import chess.board.ChessGame;
import chess.gui.ChessGameGUI;

public class ChessGameBotVersusBot extends ChessGame {

    public ChessGameGUI game;

    public ChessGameBotVersusBot(ChessBot chessBot, ChessBot anotherChessBot) {
        super();
        bot = chessBot;
        anotherBot = anotherChessBot;
    }

}
