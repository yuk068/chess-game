package chess.utility;

import chess.board.Board;
import chess.board.ChessGame;
import chess.chessbot.ChessBot;
import chess.chessbot.ChessGameBotVersusBot;
import chess.chessbot.ChessGameHumanVersusBot;
import chess.chessbot.DummyBot;
import chess.gui.*;

public class CmdHandler {

    private ChessGameGUI window;
    private ChessBoardPanel chessBoardPanel;
    private InfoTabPanel infoTab;

    public CmdHandler(ChessGameGUI window, ChessBoardPanel chessBoardPanel, InfoTabPanel infoTab) {
        this.window = window;
        this.chessBoardPanel = chessBoardPanel;
        this.infoTab = infoTab;
    }

    public void handleCommandLine(String cmdLine) {
        switch (cmdLine.split(" ")[0]) {
            case "/f":
            case "/flip":
                chessBoardPanel.flipBoard = !chessBoardPanel.flipBoard;
                chessBoardPanel.resetSelect();
                infoTab.updateConsoleLog("> Board flipped");
                window.update();
                break;
            case "/c":
            case "/coords":
                chessBoardPanel.showCoordinates = !chessBoardPanel.showCoordinates;
                infoTab.updateConsoleLog("> Coordinates " +
                        (chessBoardPanel.showCoordinates ? "on" : "off"));
                window.update();
                break;
            case "/ng":
            case "/newgame":
                infoTab.clearCaptureLog();
                infoTab.updateConsoleLog("> Starting new game...");
                SFX.playSound("game-start.wav", 6);
                window.attachGame(new ChessGame());
                break;
            case "/b":
            case "/bot":
                newGameWithBot(cmdLine);
                break;
            case "/clr":
            case "/cls":
            case "/clear":
                infoTab.clearConsoleLog();
                window.update();
                break;
            case "/n":
            case "/notation":
                chessBoardPanel.coordsInNotation = !chessBoardPanel.coordsInNotation;
                infoTab.updateConsoleLog("> Coordinates mode set to: " +
                        (chessBoardPanel.coordsInNotation ? "notation" : "debug coordinates"));
                window.update();
                break;
            case "/t":
            case "/theme":
                themePick(cmdLine);
                break;
            case "/ps":
            case "/pieceset":
                pieceSkinPick(cmdLine);
                break;
            case "/h":
            case "/help":
                getHelp(cmdLine);
                break;
            case "/cmd":
            case "/command":
                ChessGameHelpNote.showCmdLines();
                infoTab.updateConsoleLog("> Test out the commands, thanks!");
                window.update();
                window.infoTab.inputField.requestFocus();
                break;
            case "/note":
            case "/devnote":
                window.note.showNote();
                infoTab.updateConsoleLog("> Thanks for trying out my program!");
                window.update();
                break;
            case "/m":
            case "/mute":
                SFX.mute = !SFX.mute;
                infoTab.updateConsoleLog("> Sounds: " + (SFX.mute ? "off" : "on"));
                window.update();
                break;
            case "/q":
            case "/exit":
            case "/quit":
                System.exit(0);
                break;
            case "/gf":
            case "/gfen":
            case "/generatefen":
                infoTab.generateFen = !infoTab.generateFen;
                infoTab.updateConsoleLog("> FEN generation: " +
                        (infoTab.generateFen ? "on" : "off"));
                window.update();
                break;
            case "/lf":
            case "/lfen":
            case "/loadfen":
                String loadFromFen = cmdLine.substring(cmdLine.indexOf(' ') + 1);
                if (!FenHandler.validateFen(loadFromFen)) {
                    infoTab.updateConsoleLog("> Invalid FEN!");
                    window.update();
                    break;
                }
                try {
                    window.attachGame(FenHandler.initializeChessGameFromFen(loadFromFen));
                } catch (IllegalStateException e) {
                    infoTab.updateConsoleLog("> Position from FEN seems Illegal...");
                    window.update();
                    break;
                }
                infoTab.updateConsoleLog("> Setting up position from FEN");
                infoTab.clearCaptureLog();
                window.update();
                break;
            case "/vf":
            case "/vfen":
            case "/validatefen":
                String checkFen = cmdLine.substring(cmdLine.indexOf(' ') + 1);
                infoTab.updateConsoleLog("> Validation: " +
                        (FenHandler.validateFen(checkFen) ? "valid" : "invalid") + " FEN!");
                window.update();
                break;
            default:
                infoTab.updateConsoleLog("> Invalid command!");
                window.update();
                break;
        }
        SFX.playSound("half-life-button.wav", 4);
    }

    private void pieceSkinPick(String cmdLine) {
        if (cmdLine.split(" ").length < 2) {
            infoTab.updateConsoleLog("> Available sets: cburrnett, pixel, anarcandy");
        } else {
            switch (cmdLine.split(" ")[1]) {
                case "cbrnt":
                case "cburrnett":
                    ImageAsset.cbrrunettPieces();
                    infoTab.updateConsoleLog("> 'cburrnett' pieces loaded");
                    break;
                case "px":
                case "pixel":
                    ImageAsset.pixelPieces();
                    infoTab.updateConsoleLog("> 'pixel' pieces loaded");
                    break;
                case "ac":
                case "anarcandy":
                    ImageAsset.anarcandyPieces();
                    infoTab.updateConsoleLog("> 'anarcandy' pieces loaded");
                    break;
                default:
                    infoTab.updateConsoleLog("> Available sets: cburrnett, pixel, anarcandy");
                    break;
            }
        }
        window.update();
    }

    private void themePick(String cmdLine) {
        if (cmdLine.split(" ").length < 2) {
            infoTab.updateConsoleLog("> Available theme: chessdotcom, coral, coffee, candy, metal");
        } else {
            switch (cmdLine.split(" ")[1]) {
                case "crl":
                case "coral":
                    ChessGameTheme.coralTheme();
                    infoTab.updateConsoleLog("> Changing to 'coral' theme");
                    break;
                case "cdc":
                case "chessdotcom":
                    ChessGameTheme.chessDotComTheme();
                    infoTab.updateConsoleLog("> Changing to 'chessdotcom' theme");
                    break;
                case "cf":
                case "coffee":
                    ChessGameTheme.coffeeTheme();
                    infoTab.updateConsoleLog("> Changing to 'coffee' theme");
                    break;
                case "cd":
                case "candy":
                    ChessGameTheme.candyTheme();
                    infoTab.updateConsoleLog("> Changing to 'candy' theme");
                    break;
                case "mtl":
                case "metal":
                    ChessGameTheme.metalTheme();
                    infoTab.updateConsoleLog("> Changing to 'metal' theme");
                    break;
                default:
                    infoTab.updateConsoleLog("> Available theme: chessdotcom, coral, coffee, candy, metal");
                    break;
            }
        }
        window.update();
    }

    private void getHelp(String cmdLine) {
        if (cmdLine.split(" ").length < 2) {
            infoTab.updateConsoleLog("> Available guides: FEN, EnPassant, Bot");
        } else {
            switch (cmdLine.split(" ")[1].toLowerCase()) {
                case "fen":
                    infoTab.updateConsoleLog("""
                            FEN - A compact way to store chess
                            position, kind of like a hashCode function
                            for chess board""");
                    break;
                case "ep":
                case "enpassant":
                    infoTab.updateConsoleLog("""
                            En Passant - Special pawn capture when
                            an opponent's pawn moves two squares
                            forward, allowing the capturing pawn
                            to take it as if it had moved only
                            one square""");
                    break;
                case "b":
                case "bot":
                    infoTab.updateConsoleLog("""
                            Bot - Only available bot right now is
                            'dummy', which makes move 100%
                            randomly, you can play against it as
                            white or make it play it self by using
                            command:
                            /bot bot dummy +[delay_ms]""");
                    break;
                default:
                    infoTab.updateConsoleLog("> Available guides: FEN, EnPassant, Bot");
                    break;
            }
        }
        window.update();
    }

    private void newGameWithBot(String cmdLine) {
        if (cmdLine.split(" ").length < 2) {
            infoTab.updateConsoleLog("> Available bots: dummy");
        } else {
            switch (cmdLine.split(" ")[1].toLowerCase()) {
                case "d":
                case "dummy":
//                    if (!cmdLine.split(" ")[2].equalsIgnoreCase(Board.WHITE)
//                            && !cmdLine.split(" ")[2].equalsIgnoreCase(Board.BLACK)) {
//                        break;
//                    }
                    infoTab.clearCaptureLog();
                    infoTab.updateConsoleLog("> Starting new game with bot 'dummy' as black");
                    SFX.playSound("game-start.wav", 6);
                    ChessBot dummy = new DummyBot(Board.BLACK);
                    ChessGame gameDummy = new ChessGameHumanVersusBot(dummy);
                    dummy.attachGame(gameDummy);
                    window.gameWithBot = true;
                    window.attachGame(gameDummy);
                    break;
                case "bot":
                    if (cmdLine.split(" ").length < 3) {
                        infoTab.updateConsoleLog("> Invalid /bot syntax");
                        break;
                    } else {
                        switch (cmdLine.split(" ")[2].toLowerCase()) {
                            case "dummy":
                                try {
                                    infoTab.clearCaptureLog();
                                    int delayMs = Integer.parseInt(cmdLine.split(" ")[3]);
                                    delayMs = Math.min(delayMs, 5000);
                                    infoTab.updateConsoleLog("> Starting new game 'dummy' vs 'dummy' with delay: " + (Math.abs(delayMs) == 0 ? 50 : Math.abs(delayMs)) + "ms");
                                    SFX.playSound("game-start.wav", 6);
                                    ChessBot anotherDummy = new DummyBot(Board.WHITE);
                                    ChessBot aDummy = new DummyBot(Board.BLACK);
                                    ChessGame dummyGame = new ChessGameBotVersusBot(aDummy, anotherDummy);
                                    aDummy.attachGame(dummyGame);
                                    anotherDummy.attachGame(dummyGame);
                                    window.botVsBot = true;
                                    window.attachGame(dummyGame);
                                    window.playGameBotVersusBot(Integer.parseInt(cmdLine.split(" ")[3]));
                                    break;
                                } catch (Exception e) {
                                    infoTab.updateConsoleLog("> Invalid /bot syntax");
                                    break;
                                }
                            default:
                                infoTab.updateConsoleLog("> Available bots: dummy");
                                break;
                        }
                    }
            }
        }
        window.update();
    }

}
