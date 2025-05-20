package chess.piece;

import chess.board.Board;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    public boolean hasRookMoved;

    public Rook(String color, int posX, int posY, int point, Board board, boolean hasRookMoved) {
        super(color, posX, posY, point, board);
        this.hasRookMoved = hasRookMoved;
    }

    public boolean hasRookMoved() {
        return hasRookMoved;
    }

    @Override
    public List<Integer[]> getSight() {
        return getCardinalMoves(Board.NO_LIMIT);
    }

    @Override
    public List<Integer[]> getStandardMoves() {
        return getStandardCardinalMoves(Board.NO_LIMIT);
    }

    @Override
    public List<Integer[]> getLegalMoves() {
        if (isPinned()) {
            List<Integer[]> legalMoves = new ArrayList<>();
            if (pinnedValidation(2) || pinnedValidation(7)) {
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
        return getStandardCardinalMoves(Board.NO_LIMIT);
    }

}
