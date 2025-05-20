package chess.chessbot;

import chess.board.ChessGame;
import chess.piece.Piece;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DummyBot implements ChessBot{

    public ChessGame game;
    public String botColor;
    public Random random;

    public DummyBot(String color) {
        this.botColor = color;
        random = new Random();
    }

    @Override
    public String getColor() {
        return botColor;
    }

    @Override
    public void attachGame(ChessGame game) {
        this.game = game;
    }

    @Override
    public boolean move() {
        Piece[] allyPieces = game.getBoard().getAllPiecesOfColor(botColor);
        int maxAllyPiece = allyPieces.length;
        int currentAllyPieceIndex = random.nextInt(maxAllyPiece);
        Set<Integer> generatedPiece = new HashSet<>();

        while (generatedPiece.size() < maxAllyPiece) {
            while (true) {
                if (!generatedPiece.contains(currentAllyPieceIndex)) {
                    generatedPiece.add(currentAllyPieceIndex);
                    break;
                } else {
                    currentAllyPieceIndex = random.nextInt(maxAllyPiece);
                }
            }
            if (!allyPieces[currentAllyPieceIndex].getLegalMoves().isEmpty()) {
                Piece currentPiece = allyPieces[currentAllyPieceIndex];

                Set<Integer> generatedMoves = new HashSet<>();
                int randomMoveIndex = random.nextInt(currentPiece.getLegalMoves().size());

                while (generatedMoves.size() < currentPiece.getLegalMoves().size()) {
                    while (true) {
                        if (!generatedMoves.contains(randomMoveIndex)) {
                            generatedMoves.add(randomMoveIndex);
                            break;
                        } else {
                            randomMoveIndex = random.nextInt(currentPiece.getLegalMoves().size());
                        }
                    }
                    Integer[] randomMove = currentPiece.getLegalMoves().get(randomMoveIndex);
                    boolean moveSuccess = game.moveOperation(currentPiece.posX, currentPiece.posY, randomMove[0], randomMove[1]);
                    if (moveSuccess) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
