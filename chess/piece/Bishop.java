package chess.piece;

import chess.board.Board;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(String color, int posX, int posY, int point, Board board) {
        super(color, posX, posY, point, board);
    }

    @Override
    public List<Integer[]> getSight() {
        return getDiagonalMoves(Board.NO_LIMIT);
    }

    @Override
    public List<Integer[]> getStandardMoves() {
        return getStandardDiagonalMoves(Board.NO_LIMIT);
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
            }
            return legalMoves;
        }
        return getStandardDiagonalMoves(Board.NO_LIMIT);
    }

}
