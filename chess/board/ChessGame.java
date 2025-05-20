package chess.board;

import chess.chessbot.ChessBot;
import chess.utility.ChessGameUtility;
import chess.utility.FenHandler;
import chess.gui.SFX;
import chess.piece.Pawn;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChessGame {

    public ChessBot bot;
    public ChessBot anotherBot;

    public boolean drawByInsufficientMaterial;
    public int fiftyMoveRuleCounter;
    public boolean drawByRepetition;
    public boolean drawByFiftyMoveRule;
    public boolean gameIsOver;
    public int whitePoints;
    public int blackPoints;

    public Pawn targetOfEnPassant;
    public List<Integer[]> isProtected = new ArrayList<>();
    public Integer[] passantSquare = new Integer[]{-1, -1};

    public FenHandler fenHandler;
    public LinkedList<Square[][]> boardStates;

    private Board board;
    public String currentFEN;
    public int halfmoves;
    public boolean turn;
    public boolean blackKingInCheck;
    public boolean whiteKingInCheck;

    public boolean stalemated;
    public boolean checkmated;

    public String capturedInfo;

    public ChessGame() {
        board = new Board();
        board.attachAndInitializeDefaultPieces(this);
        turn = true;
        drawByRepetition = false;
        drawByInsufficientMaterial = false;
        drawByFiftyMoveRule = false;
        stalemated = false;
        checkmated = false;
        blackKingInCheck = false;
        whiteKingInCheck = false;
        gameIsOver = false;
        capturedInfo = "none";
        boardStates = new LinkedList<>();
        pushState(board.copyOfBoard(getBoard().getSquares()));
        halfmoves = 0;
        fiftyMoveRuleCounter = 0;
        targetOfEnPassant = null;
        fenHandler = new FenHandler(this);
        currentFEN = fenHandler.generateFen();
    }

    public ChessBot getBot() {
        return bot;
    }

    public ChessBot getAnotherBot() {
        return anotherBot;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public boolean moveOperation(int fromX, int fromY, int toX, int toY) {
        if (!validateTurn(fromX, fromY)) return false;

        Square[][] currentSquares = getBoard().getSquares();
        Square[][] sample = board.copyOfBoard(getBoard().getSquares());

        try {
            getBoard().move(currentSquares[fromX][fromY], currentSquares[toX][toY]);
            capturedInfo = board.capturedToString;
            if (ChessGameUtility.isSameBoard(sample, getBoard().getSquares())) throw new IllegalStateException();

            validateChecking();
            halfmoves++;
            fiftyMoveRuleCounter++;
            turn = !turn;
            // After moving successfully:
            pushState(board.copyOfBoard(getBoard().getSquares()));
            board.refreshProtecting();
            board.markPinnedPieces();

            if (targetOfEnPassant != null) {
                passantSquare = new Integer[]{-1, -1};
                targetOfEnPassant.setCanBePassant(false);
                targetOfEnPassant = null;
            }

            if (board.getSquareAt(toX, toY).getPiece() instanceof Pawn) {
                fiftyMoveRuleCounter = 0;
                if (((Pawn) board.getSquareAt(toX, toY).getPiece()).canBePassant()) {
                    targetOfEnPassant = (Pawn) board.getSquareAt(toX, toY).getPiece();
                    passantSquare = new Integer[]{targetOfEnPassant.posX
                            + (targetOfEnPassant.getColor().equals(Board.WHITE) ? 1 : -1), targetOfEnPassant.posY};
                }
            }

            if (!capturedInfo.equals("none")) {
                fiftyMoveRuleCounter = 0;
                SFX.playSound("capture.wav", 5);
            } else SFX.playSound("move-self.wav", 4);

            if (board.pawnPromoted) {
                SFX.playSound("promote.wav", 6);
                board.pawnPromoted = false;
            }

            // Only generate FEN at this point
            currentFEN = fenHandler.generateFen();

            // check game over operation
            if (board.checkStalemate(turn)) stalemated = true;
            if (board.checkCheckmate(turn)) checkmated = true;
            checkDraw();

            if (stalemated || checkmated
                    || drawByFiftyMoveRule || drawByRepetition
                    || drawByInsufficientMaterial) gameIsOver = true;

            if (!turn) whiteKingInCheck = false;
            else blackKingInCheck = false;

            whitePoints = 39 - board.calculateTotalValue(Board.BLACK);
            blackPoints = 39 - board.calculateTotalValue(Board.WHITE);

            if (gameIsOver) {
                SFX.playSound("game-end.wav", 6);
            }

            return true;
        } catch (IllegalStateException e) {
            board.setSquares(sample);
            capturedInfo = "none";
            board.pawnPromoted = false;
            return false;
        }
    }

    public void checkDraw() {
        if (boardStates.size() == 9) {
            if (ChessGameUtility.isSameBoard(boardStates.get(0), boardStates.get(4))
                    && ChessGameUtility.isSameBoard(boardStates.get(4), boardStates.get(8))
                    && ChessGameUtility.isSameBoard(boardStates.get(0), boardStates.get(8))
                    && ChessGameUtility.isSameBoard(boardStates.get(2), boardStates.get(6))) {
                drawByRepetition = true;
            }
        }
        if (board.calculateValueExcludingPawn(Board.WHITE) <= 3
                && board.calculateValueExcludingPawn(Board.BLACK) <= 3 && board.noPawnsLeft()) {
            drawByInsufficientMaterial = true;
        }
        if (fiftyMoveRuleCounter >= 100) {
            drawByFiftyMoveRule = true;
        }
    }

    public void pushState(Square[][] state) {
        if (boardStates.size() >= 9) {
            boardStates.removeFirst();
        }
        boardStates.addLast(state);
    }

    private boolean validateTurn(int fromX, int fromY) {
        if (!getBoard().getSquares()[fromX][fromY].isOccupied()) {
            return false;
        }
        if (turn && !getBoard().getSquares()[fromX][fromY].getPiece().getColor().equals(Board.WHITE)) {
            return false;
        } else return turn || getBoard().getSquares()[fromX][fromY].getPiece().getColor().equals(Board.BLACK);
    }

    public void validateChecking() throws IllegalStateException {
        if (getBoard().kingInCheck(!turn)) {
            throw new IllegalStateException();
        }
        if (getBoard().kingInCheck(turn)) {
            SFX.playSound("move-check.wav", 6);
            if (getBoard().kingInCheck(!turn)) {
                throw new IllegalStateException();
            }
            if (turn) blackKingInCheck = true;
            else whiteKingInCheck = true;
        } else {
            if (!turn) blackKingInCheck = false;
            else whiteKingInCheck = false;
        }
    }

    public String getGameOverState() {
        if (gameIsOver) {
            if (checkmated) {
                return "Checkmate for " + (!turn ? Board.WHITE : Board.BLACK);
            } else if (stalemated) {
                return "Stalemate";
            } else if (drawByRepetition) {
                return "Repetition";
            } else if (drawByInsufficientMaterial) {
                return "Insufficient material";
            } else if (drawByFiftyMoveRule) {
                return "Fifty-move rule";
            }
        }
        return "";
    }

}
