package chess.piece;

import chess.board.Board;
import chess.utility.ChessGameUtility;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class King extends Piece {

    public boolean castled;
    public boolean hasKingMoved;

    public King(String color, int posX, int posY, int point, Board board, boolean hasKingMoved, boolean castled) {
        super(color, posX, posY, point, board);
        this.hasKingMoved = hasKingMoved;
        this.castled = castled;
    }

    @Override
    public List<Integer[]> getSight() {
        List<Integer[]> kingSight = new ArrayList<>();
        kingSight.addAll(getCardinalMoves(1));
        kingSight.addAll(getDiagonalMoves(1));
        return kingSight;
    }

    @Override
    public List<Integer[]> getStandardMoves() {
        List<Integer[]> kingMoves = new ArrayList<>();
        kingMoves.addAll(getStandardCardinalMoves(1));
        kingMoves.addAll(getStandardDiagonalMoves(1));
        kingMoves = ChessGameUtility.removeMoves(kingMoves, otherKingSight());
        if (canCastle(true)) kingMoves.add(new Integer[]{color.equals(Board.WHITE) ? 7 : 0, 6});
        if (canCastle(false)) kingMoves.add(new Integer[]{color.equals(Board.WHITE) ? 7 : 0, 2});

        List<Integer[]> otherEnemyMovesExceptKing = new ArrayList<>();
        for (int i = 0; i < board.getNumRow(); i++) {
            for (int j = 0; j < board.getNumCol(); j++) {
                if (board.getSquares()[i][j].isOccupied()
                        && !(board.getSquares()[i][j].getPiece() instanceof King)
                        && !board.getSquares()[i][j].getPiece().getColor().equals(color)) {
                    if (board.getSquares()[i][j].getPiece() instanceof Pawn) {
                        otherEnemyMovesExceptKing.addAll(board.getSquares()[i][j].getPiece().getSight());
                    } else {
                        otherEnemyMovesExceptKing.addAll(board.getSquares()[i][j].getPiece().getStandardMoves());
                    }
                }
            }
        }
        kingMoves = ChessGameUtility.removeMoves(kingMoves, otherEnemyMovesExceptKing);
        kingMoves = ChessGameUtility.removeMoves(kingMoves, game.isProtected);
        return kingMoves;
    }

    public List<Integer[]> otherKingSight() {
        List<Integer[]> otherKingSight = new ArrayList<>();
        for (int i = 0; i < board.getNumRow(); i++) {
            for (int j = 0; j < board.getNumCol(); j++) {
                if (board.getSquares()[i][j].isOccupied()
                        && board.getSquares()[i][j].getPiece() instanceof King
                        && !board.getSquares()[i][j].getPiece().getColor().equals(color)) {
                    otherKingSight.addAll(board.getSquares()[i][j].getPiece().getSight());
                    break;
                }
            }
        }
        return otherKingSight;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Integer[]> getLegalMoves() {
        List<Integer[]> kingMoves = getStandardMoves();
        List<Integer[]> enemyAxis = new ArrayList<>();
        List<Integer[]> directionalMoves = new ArrayList<>();
        List<Integer> direction = new ArrayList<>();
        boolean directionPiece;
        if ((color.equals(Board.WHITE) && game.whiteKingInCheck)
                || (color.equals(Board.BLACK) && game.blackKingInCheck)) {
            String opposingColor = color.equals(Board.WHITE) ? Board.BLACK : Board.WHITE;
            for (Piece enemy : board.getAllPiecesOfColor(opposingColor)) {
                directionPiece = false;
                direction.clear();
                if (enemy instanceof Queen) {
                    IntStream.rangeClosed(1, 8).forEach(direction::add);
                    directionPiece = true;
                } else if (enemy instanceof Bishop) {
                    direction.add(1);
                    direction.add(3);
                    direction.add(6);
                    direction.add(8);
                    directionPiece = true;
                } else if (enemy instanceof Rook) {
                    direction.add(2);
                    direction.add(4);
                    direction.add(5);
                    direction.add(7);
                    directionPiece = true;
                }
                if (directionPiece) {
                    for (int i = 1; i <= direction.size(); i++) {
                        enemyAxis.clear();
                        directionalMoves.clear();
                        String methodName = "getMoves" + Piece.DIRECTION_MAP.get(direction.get(i - 1));
                        Method method;
                        try {
                            method = enemy.getClass().getMethod(methodName);
                            directionalMoves = (List<Integer[]>) method.invoke(enemy);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        enemyAxis.addAll(enemy.nullifyBlockage(directionalMoves)
                                .subList(0, Math.min(Board.NO_LIMIT, enemy.nullifyBlockage(directionalMoves).size())));
                        if (ChessGameUtility.haveThisMove(enemyAxis, new Integer[]{posX, posY})) {
                            enemyAxis.clear();
                            enemyAxis.addAll(directionalMoves);
                            kingMoves = ChessGameUtility.removeMoves(kingMoves, enemyAxis);
                            if (ChessGameUtility.haveThisMove(getSight(), new Integer[]{enemy.posX, enemy.posY})
                                    && !ChessGameUtility.haveThisMove(game.isProtected, new Integer[]{enemy.posX, enemy.posY})) {
                                kingMoves.add(new Integer[]{enemy.posX, enemy.posY});
                            }
                            return ChessGameUtility.removeDuplicates(kingMoves);
                        }
                    }
                }
            }
        }
        return ChessGameUtility.removeDuplicates(kingMoves);
    }

    public boolean canCastle(boolean kingSide) {
        if (hasKingMoved) return false;
        if (color.equals(Board.WHITE) && game.whiteKingInCheck) return false;
        if (color.equals(Board.BLACK) && game.blackKingInCheck) return false;
        List<Integer[]> enemyMoves = new ArrayList<>();
        String opposingColor = color.equals(Board.WHITE) ? Board.BLACK : Board.WHITE;

        for (Piece enemy : board.getAllPiecesOfColor(opposingColor)) {
            if (enemy instanceof King || enemy instanceof Knight || enemy instanceof Pawn) {
                enemyMoves.addAll(enemy.getSight());
            } else {
                enemyMoves.addAll(enemy.getStandardMoves());
            }
        }
        List<Integer[]> checkKingSide = kingSide ? getMovesRight() : getMovesLeft();
        int lastIndex = checkKingSide.size() - 1;
        if (!board.getSquareAt(checkKingSide.get(lastIndex)[0], checkKingSide.get(lastIndex)[1]).isOccupied()) {
            return false;
        } else {
            Piece checkRook = board.getSquareAt(checkKingSide.get(lastIndex)[0], checkKingSide.get(lastIndex)[1]).getPiece();
            if (!(checkRook instanceof Rook)) return false;
            if (checkRook.getColor().equals(opposingColor)) return false;
            if (((Rook) checkRook).hasRookMoved) return false;
        }
        checkKingSide.remove(lastIndex);
        for (Integer[] move : checkKingSide) {
            if (board.getSquares()[move[0]][move[1]].isOccupied()) return false;
            if (ChessGameUtility.haveThisMove(enemyMoves, move)) return false;
        }
        return true;
    }

}
