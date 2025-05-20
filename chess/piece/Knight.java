package chess.piece;

import chess.board.Board;

import java.util.*;

public class Knight extends Piece {

    private static final Map<Integer, Integer[]> KNIGHT_OFFSETS = new HashMap<>();

    static {
        KNIGHT_OFFSETS.put(1, new Integer[]{-2, -1}); // Up-Left
        KNIGHT_OFFSETS.put(2, new Integer[]{-2, 1});  // Up-Right
        KNIGHT_OFFSETS.put(3, new Integer[]{-1, -2}); // Left-Up
        KNIGHT_OFFSETS.put(4, new Integer[]{-1, 2});  // Right-Up
        KNIGHT_OFFSETS.put(5, new Integer[]{1, -2});  // Left-Down
        KNIGHT_OFFSETS.put(6, new Integer[]{1, 2});   // Right-Down
        KNIGHT_OFFSETS.put(7, new Integer[]{2, -1});  // Down-Left
        KNIGHT_OFFSETS.put(8, new Integer[]{2, 1});   // Down-Right
    }

    public Knight(String color, int posX, int posY, int point, Board board) {
        super(color, posX, posY, point, board);
    }

    @Override
    public List<Integer[]> getSight() {
        // Knights can phase
        List<Integer[]> sight = new ArrayList<>();
        for (Map.Entry<Integer, Integer[]> entry : KNIGHT_OFFSETS.entrySet()) {
            int deltaX = entry.getValue()[0];
            int deltaY = entry.getValue()[1];
            sight.addAll(getMovesHelper(deltaX, deltaY, 1));
        }
        return sight;
    }

    @Override
    public List<Integer[]> getStandardMoves() {
        List<Integer[]> moves = getSight();
        List<Integer[]> invalidMoves = new ArrayList<>();

        for (Integer[] move : moves) {
            int x = move[0];
            int y = move[1];

            if (board.getSquares()[x][y].isOccupied()
                    && board.getSquares()[posX][posY].isOccupied()
                    && board.getSquares()[posX][posY].getPiece().isAlly(board.getSquares()[x][y])) {
                game.isProtected.add(new Integer[]{x, y});
                invalidMoves.add(move);
            }
        }
        moves.removeAll(invalidMoves);
        return moves;
    }

    @Override
    public List<Integer[]> getLegalMoves() {
        if (isPinned()) return Collections.emptyList();
        else return getStandardMoves();
    }

}
