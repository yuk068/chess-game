package chess.piece;

import chess.board.Board;

import java.util.*;

public class Pawn extends Piece {

    private boolean canBePassant;

    private boolean firstMove;
    private final Map<Integer, Integer[]> pawnOffset = new HashMap<>();
    /*
        1: left
        2: right
        3: advance
     */

    public Pawn(String color, int posX, int posY, int point, Board board) {
        super(color, posX, posY, point, board);
        refreshPawnOffset();
    }

    public void refreshPawnOffset() {
        if (color.equals(Board.BLACK)) {
            pawnOffset.put(1, new Integer[]{1, -1});
            pawnOffset.put(2, new Integer[]{1, 1});
            pawnOffset.put(3, new Integer[]{1, 0});
            if (posX == 1) firstMove = true;
        } else {
            pawnOffset.put(1, new Integer[]{-1, -1});
            pawnOffset.put(2, new Integer[]{-1, 1});
            pawnOffset.put(3, new Integer[]{-1, 0});
            if (posX == 6) firstMove = true;
        }
    }

    public boolean isFirstMove() {
        return firstMove;
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    public boolean canBePassant() {
        return canBePassant;
    }

    public void setCanBePassant(boolean canBePassant) {
        this.canBePassant = canBePassant;
    }

    public void nullifyFirstMove() {
        if (firstMove) firstMove = false;
    }

    public boolean canPromote() {
        if (color.equals(Board.WHITE) && posX == 0) {
            return true;
        } else return color.equals(Board.BLACK) && posX == 7;
    }

    @Override
    public List<Integer[]> getSight() {
        List<Integer[]> pawnSight = new ArrayList<>(getMovesHelper(pawnOffset.get(1)[0], pawnOffset.get(1)[1], 1));
        pawnSight.addAll(getMovesHelper(pawnOffset.get(2)[0], pawnOffset.get(2)[1], 1));
        return pawnSight;
    }

    @Override
    public List<Integer[]> getStandardMoves() {
        List<Integer[]> pawnSight = getSight();
        List<Integer[]> pawnMoves = new ArrayList<>(getMovesHelper(pawnOffset.get(3)[0], pawnOffset.get(3)[1], firstMove ? 2 : 1));
        int target = 0;
        for (Integer[] move : pawnMoves) {
            if (!withinBoard(move[0], move[1])) pawnMoves.remove(target);
            target++;
        }
        if (!pawnMoves.isEmpty() && board.getSquares()[pawnMoves.get(0)[0]][pawnMoves.get(0)[1]].isOccupied()) {
            if (!pawnMoves.isEmpty()) {
                pawnMoves.clear();
            }
        } else if (pawnMoves.size() > 1 && board.getSquares()[pawnMoves.get(1)[0]][pawnMoves.get(1)[1]].isOccupied()) {
            pawnMoves.remove(pawnMoves.size() - 1);
        }

        int count = 0;
        if (pawnSight.isEmpty()) return pawnMoves;
        if ((pawnSight.size() == 1 && board.getSquares()[pawnSight.get(count)[0]][pawnSight.get(count)[1]].isOccupied()
                && board.getSquares()[posX][posY].isOccupied()
                && board.getSquares()[posX][posY].getPiece()
                .isEnemy(board.getSquares()[pawnSight.get(count)[0]][pawnSight.get(count)[1]]))
                || (pawnSight.get(count)[0].equals(game.passantSquare[0]))
                && pawnSight.get(count)[1].equals(game.passantSquare[1])) {
            pawnMoves.add(pawnSight.get(count));
            return pawnMoves;
        }
        while (count < pawnSight.size()) {
            if (board.getSquares()[pawnSight.get(count)[0]][pawnSight.get(count)[1]].isOccupied()
                    && board.getSquares()[posX][posY].isOccupied()
                    && board.getSquares()[posX][posY].getPiece()
                    .isEnemy(board.getSquares()[pawnSight.get(count)[0]][pawnSight.get(count)[1]])
                    || (pawnSight.get(count)[0].equals(game.passantSquare[0]))
                    && pawnSight.get(count)[1].equals(game.passantSquare[1])) {
                pawnMoves.add(pawnSight.get(count));
            }
            count++;
        }
        return pawnMoves;
    }

    @Override
    public List<Integer[]> getLegalMoves() {
        if (isPinned()) {
            if (pinnedValidation(1) || pinnedValidation(6)) {
                List<Integer[]> pawnSight = getMovesHelper(pawnOffset.get(1)[0], pawnOffset.get(1)[1], 1);
                if (board.getSquareAt(pawnSight.get(0)[0], pawnSight.get(0)[1]).isOccupied()
                        || (pawnSight.get(0)[0].equals(game.passantSquare[0]))
                        && pawnSight.get(0)[1].equals(game.passantSquare[1])) {
                    return pawnSight;
                }
            } else if (pinnedValidation(3) || pinnedValidation(8)) {
                List<Integer[]> pawnSight = getMovesHelper(pawnOffset.get(2)[0], pawnOffset.get(2)[1], 1);
                if (board.getSquareAt(pawnSight.get(0)[0], pawnSight.get(0)[1]).isOccupied()
                    || (pawnSight.get(0)[0].equals(game.passantSquare[0]))
                        && pawnSight.get(0)[1].equals(game.passantSquare[1])) {
                    return pawnSight;
                }
            } else if (pinnedValidation(2) || pinnedValidation(7)) {
                List<Integer[]> pawnMoves = new ArrayList<>(getMovesHelper(pawnOffset.get(3)[0], pawnOffset.get(3)[1], firstMove ? 2 : 1));
                int target = 0;
                for (Integer[] move : pawnMoves) {
                    if (!withinBoard(move[0], move[1])) pawnMoves.remove(target);
                    target++;
                }
                if (!pawnMoves.isEmpty() && board.getSquares()[pawnMoves.get(0)[0]][pawnMoves.get(0)[1]].isOccupied()) {
                    if (!pawnMoves.isEmpty()) {
                        pawnMoves.clear();
                    }
                } else if (pawnMoves.size() > 1 && board.getSquares()[pawnMoves.get(1)[0]][pawnMoves.get(1)[1]].isOccupied()) {
                    pawnMoves.remove(pawnMoves.size() - 1);
                }
                return pawnMoves;
            } else {
                return Collections.emptyList();
            }
        }
        return getStandardMoves();
    }

}
