package chess.piece;

import chess.board.Board;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(String color, int posX, int posY, int point, Board board) {
        super(color, posX, posY, point, board);
    }

    @Override
    public List<Integer[]> getSight() {
        List<Integer[]> queenSight = new ArrayList<>();
        queenSight.addAll(getCardinalMoves(Board.NO_LIMIT));
        queenSight.addAll(getDiagonalMoves(Board.NO_LIMIT));
        return queenSight;
    }

    @Override
    public List<Integer[]> getStandardMoves() {
        List<Integer[]> queenMoves = new ArrayList<>();
        queenMoves.addAll(getStandardCardinalMoves(Board.NO_LIMIT));
        queenMoves.addAll(getStandardDiagonalMoves(Board.NO_LIMIT));
        return queenMoves;
    }

    @Override
    public List<Integer[]> getLegalMoves() {
        if (isPinned()) {
            List<Integer[]> legalMoves = new ArrayList<>();
            if (pinnedValidation(1) || pinnedValidation(8)) {
                legalMoves.addAll(nullifyBlockage(getMovesUpLeft())
                        .subList(0, Math.min(Board.NO_LIMIT, nullifyBlockage(getMovesUpLeft()).size())));
                legalMoves.addAll(nullifyBlockage(getMovesDownRight())
                        .subList(0, Math.min(Board.NO_LIMIT, nullifyBlockage(getMovesDownRight()).size())));
            } else if (pinnedValidation(3) || pinnedValidation(6)) {
                legalMoves.addAll(nullifyBlockage(getMovesUpRight())
                        .subList(0, Math.min(Board.NO_LIMIT, nullifyBlockage(getMovesUpRight()).size())));
                legalMoves.addAll(nullifyBlockage(getMovesDownLeft())
                        .subList(0, Math.min(Board.NO_LIMIT, nullifyBlockage(getMovesDownLeft()).size())));
            } else if (pinnedValidation(2) || pinnedValidation(7)) {
                legalMoves.addAll(nullifyBlockage(getMovesUp())
                        .subList(0, Math.min(Board.NO_LIMIT, nullifyBlockage(getMovesUp()).size())));
                legalMoves.addAll(nullifyBlockage(getMovesDown())
                        .subList(0, Math.min(Board.NO_LIMIT, nullifyBlockage(getMovesDown()).size())));
            } else if (pinnedValidation(4) || pinnedValidation(5)) {
                legalMoves.addAll(nullifyBlockage(getMovesLeft())
                        .subList(0, Math.min(Board.NO_LIMIT, nullifyBlockage(getMovesLeft()).size())));
                legalMoves.addAll(nullifyBlockage(getMovesRight())
                        .subList(0, Math.min(Board.NO_LIMIT, nullifyBlockage(getMovesRight()).size())));
            }
            return legalMoves;
        }
        else return getStandardMoves();
    }

}
