package chess.piece;

import chess.board.Board;
import chess.board.ChessGame;
import chess.board.Square;
import chess.utility.ChessGameUtility;

import java.lang.reflect.Method;
import java.util.*;

public abstract class Piece {

    public ChessGame game;
    protected Board board;
    protected String color;
    private boolean isPinned;
    private boolean pinning;
    private final int point;

    public int posX;
    public int posY;

    public static final Map<Integer, String> DIRECTION_MAP = new HashMap<>();

    static {
        DIRECTION_MAP.put(2, "Up");
        DIRECTION_MAP.put(7, "Down");
        DIRECTION_MAP.put(4, "Left");
        DIRECTION_MAP.put(5, "Right");
        DIRECTION_MAP.put(1, "UpLeft");
        DIRECTION_MAP.put(3, "UpRight");
        DIRECTION_MAP.put(6, "DownLeft");
        DIRECTION_MAP.put(8, "DownRight");
    }

    public Piece(String color, int posX, int posY, int point, Board board) {
        this.posX = posX;
        this.posY = posY;
        this.color = color;
        this.point = point;
        this.board = board;
        isPinned = false;
        pinning = false;
    }

    public void attachGame(ChessGame game) {
        this.game = game;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setPosition(int x, int y) {
        posX = x;
        posY = y;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getPoint() {
        return point;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isPinning() {
        return pinning;
    }

    public void setPinning(boolean pinning) {
        this.pinning = pinning;
    }

    public boolean isAlly(Square another) {
        return another.isOccupied() && another.getPiece().getColor().equals(color);
    }

    public boolean isEnemy(Square another) {
        return another.isOccupied() && !another.getPiece().getColor().equals(color);
    }

    public abstract List<Integer[]> getSight();

    public abstract List<Integer[]> getStandardMoves();

    public abstract List<Integer[]> getLegalMoves();

    /* Direction:
    1 2 3
    4   5
    6 7 8
     */
    public boolean withinBoard(int x, int y) {
        return x >= 0 && y >= 0 && x < board.getNumRow() && y < board.getNumCol();
    }

    public List<Integer[]> getMovesHelper(int deltaX, int deltaY, int limit) {
        List<Integer[]> moves = new ArrayList<>();
        int x = posX;
        int y = posY;
        while (limit > 0) {
            x += deltaX;
            y += deltaY;
            if (!withinBoard(x, y)) break;
            moves.add(new Integer[]{x, y});
            limit--;
        }
        moves = ChessGameUtility.removeMove(moves, posX, posY);
        return moves;
    }

    public List<Integer[]> getMovesUp() {
        return getMovesHelper(-1, 0, Board.NO_LIMIT);
    }

    public List<Integer[]> getMovesDown() {
        return getMovesHelper(1, 0, Board.NO_LIMIT);
    }

    public List<Integer[]> getMovesLeft() {
        return getMovesHelper(0, -1, Board.NO_LIMIT);
    }

    public List<Integer[]> getMovesRight() {
        return getMovesHelper(0, 1, Board.NO_LIMIT);
    }

    public List<Integer[]> getMovesUpLeft() {
        return getMovesHelper(-1, -1, Board.NO_LIMIT);
    }

    public List<Integer[]> getMovesUpRight() {
        return getMovesHelper(-1, 1, Board.NO_LIMIT);
    }

    public List<Integer[]> getMovesDownLeft() {
        return getMovesHelper(1, -1, Board.NO_LIMIT);
    }

    public List<Integer[]> getMovesDownRight() {
        return getMovesHelper(1, 1, Board.NO_LIMIT);
    }

    public List<Integer[]> getCardinalMoves(int limit) {
        List<Integer[]> cardinalMoves = new ArrayList<>();
        cardinalMoves.addAll(getMovesUp().subList(0, Math.min(limit, getMovesUp().size())));
        cardinalMoves.addAll(getMovesDown().subList(0, Math.min(limit, getMovesDown().size())));
        cardinalMoves.addAll(getMovesLeft().subList(0, Math.min(limit, getMovesLeft().size())));
        cardinalMoves.addAll(getMovesRight().subList(0, Math.min(limit, getMovesRight().size())));
        return cardinalMoves;
    }

    public List<Integer[]> getDiagonalMoves(int limit) {
        List<Integer[]> diagonalMoves = new ArrayList<>();
        diagonalMoves.addAll(getMovesUpLeft().subList(0, Math.min(limit, getMovesUpLeft().size())));
        diagonalMoves.addAll(getMovesUpRight().subList(0, Math.min(limit, getMovesUpRight().size())));
        diagonalMoves.addAll(getMovesDownLeft().subList(0, Math.min(limit, getMovesDownLeft().size())));
        diagonalMoves.addAll(getMovesDownRight().subList(0, Math.min(limit, getMovesDownRight().size())));
        return diagonalMoves;
    }

    public List<Integer[]> nullifyBlockage(List<Integer[]> moves) {
        List<Integer[]> movesWithBlockage;
        int toKeep = 0;
        for (Integer[] move : moves) {
            if (isAlly(board.getSquares()[move[0]][move[1]])) {
                break;
            } else if (isEnemy(board.getSquares()[move[0]][move[1]])) {
                toKeep++;
                break;
            }
            toKeep++;
        }
        movesWithBlockage = moves.subList(0, Math.max(0, toKeep));
        return movesWithBlockage;
    }

    public List<Integer[]> getStandardCardinalMoves(int limit) {
        List<Integer[]> moves = new ArrayList<>();
        moves.addAll(nullifyBlockage(getMovesUp())
                .subList(0, Math.min(limit, nullifyBlockage(getMovesUp()).size())));
        moves.addAll(nullifyBlockage(getMovesDown())
                .subList(0, Math.min(limit, nullifyBlockage(getMovesDown()).size())));
        moves.addAll(nullifyBlockage(getMovesLeft())
                .subList(0, Math.min(limit, nullifyBlockage(getMovesLeft()).size())));
        moves.addAll(nullifyBlockage(getMovesRight())
                .subList(0, Math.min(limit, nullifyBlockage(getMovesRight()).size())));
        return moves;
    }

    public List<Integer[]> getStandardDiagonalMoves(int limit) {
        List<Integer[]> moves = new ArrayList<>();
        moves.addAll(nullifyBlockage(getMovesUpLeft())
                .subList(0, Math.min(limit, nullifyBlockage(getMovesUpLeft()).size())));
        moves.addAll(nullifyBlockage(getMovesUpRight())
                .subList(0, Math.min(limit, nullifyBlockage(getMovesUpRight()).size())));
        moves.addAll(nullifyBlockage(getMovesDownLeft())
                .subList(0, Math.min(limit, nullifyBlockage(getMovesDownLeft()).size())));
        moves.addAll(nullifyBlockage(getMovesDownRight())
                .subList(0, Math.min(limit, nullifyBlockage(getMovesDownRight()).size())));
        return moves;
    }

    @SuppressWarnings("unchecked")
    public boolean pinnedValidation(int direction) {
        List<Integer[]> between;
        try {
            String methodName = "getMoves" + Piece.DIRECTION_MAP.get(direction);
            Method method = getClass().getMethod(methodName);
            between = (List<Integer[]>) method.invoke(this);
            for (Integer[] move : between) {
                if (board.getSquareAt(move[0], move[1]).isOccupied()
                        && board.getSquareAt(move[0], move[1]).getPiece().isPinning()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return getColor() + " " + getClass().getSimpleName();
    }

}
