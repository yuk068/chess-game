package chess.board;

import chess.utility.ChessGameUtility;
import chess.gui.SFX;
import chess.piece.*;

import javax.swing.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Board {

    public static final int DEFAULT_NUM_ROW = 8;
    public static final int DEFAULT_NUM_COL = 8;
    public static final String DARK = "DARK";
    public static final String LIGHT = "LIGHT";
    public static final String BLACK = "BLACK";
    public static final String WHITE = "WHITE";

    public static final int NO_LIMIT = 10000;

    private ChessGame game;
    private Square[][] squares;
    private final int numRow;
    private final int numCol;
    public boolean pawnPromoted;

    public String capturedToString;

    public Board() {
        numRow = DEFAULT_NUM_ROW;
        numCol = DEFAULT_NUM_COL;
        squares = new Square[DEFAULT_NUM_ROW][DEFAULT_NUM_COL];
        initializeBoard();
        initializeDefaultPieces();
    }

    public void attachAndInitializeDefaultPieces(ChessGame game) {
        this.game = game;
        initializeDefaultPieces();
    }

    public Square getSquareAt(int x, int y) {
        return squares[x][y];
    }

    public Square[][] getSquares() {
        return squares;
    }

    public Square[] getAllSquares() {
        Square[] squareArray = new Square[64];
        int count = 0;
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                squareArray[count++] = squares[i][j];
            }
        }
        return squareArray;
    }

    public void setSquares(Square[][] squares) {
        for (Piece piece : getAllPiecesOfColor(WHITE)) {
            piece.setBoard(this);
        }
        for (Piece piece : getAllPiecesOfColor(BLACK)) {
            piece.setBoard(this);
        }
        this.squares = squares;
    }

    public int getNumRow() {
        return numRow;
    }

    public int getNumCol() {
        return numCol;
    }

    public void initializeBoard() {
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                squares[i][j] =
                        new Square(null, (i + j) % 2 == 0 ? LIGHT : DARK);
                squares[i][j].setPosition(i, j);
            }
        }
    }

    public void initializeDefaultPieces() {
        // Queens
        squares[0][3].setPiece(new Queen(BLACK, 0, 3, 9, this));
        squares[7][3].setPiece(new Queen(WHITE, 7, 3, 9, this));
        // Rooks
        squares[0][0].setPiece(new Rook(BLACK, 0, 0, 5, this, false));
        squares[0][7].setPiece(new Rook(BLACK, 0, 7, 5, this, false));
        squares[7][0].setPiece(new Rook(WHITE, 7, 0, 5, this, false));
        squares[7][7].setPiece(new Rook(WHITE, 7, 7, 5, this, false));

        // Knights
        squares[0][1].setPiece(new Knight(BLACK, 0, 1, 3, this));
        squares[0][6].setPiece(new Knight(BLACK, 0, 6, 3, this));
        squares[7][1].setPiece(new Knight(WHITE, 7, 1, 3, this));
        squares[7][6].setPiece(new Knight(WHITE, 7, 6, 3, this));

        // Bishops
        squares[0][2].setPiece(new Bishop(BLACK, 0, 2, 3, this));
        squares[0][5].setPiece(new Bishop(BLACK, 0, 5, 3, this));
        squares[7][2].setPiece(new Bishop(WHITE, 7, 2, 3, this));
        squares[7][5].setPiece(new Bishop(WHITE, 7, 5, 3, this));
        // Kings
        squares[0][4].setPiece(new King(BLACK, 0, 4, NO_LIMIT, this, false, false));
        squares[7][4].setPiece(new King(WHITE, 7, 4, NO_LIMIT, this, false, false));

        // Pawns
        for (int col = 0; col < numCol; col++) {
            squares[1][col].setPiece(new Pawn(BLACK, 1, col, 1, this));
            squares[6][col].setPiece(new Pawn(WHITE, 6, col, 1, this));
        }
        attachGameAllPieces();
    }

    public Piece[] getAllPiecesOfColor(String color) {
        int count = 0;
        Piece[] pieces = new Piece[16];
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                if (squares[i][j].isOccupied() && squares[i][j].getPiece().getColor().equals(color)) {
                    pieces[count++] = squares[i][j].getPiece();
                }
            }
        }
        return Arrays.copyOf(pieces, count);
    }

    public Piece[] getAllPieces() {
        Piece[] blackPieces = getAllPiecesOfColor(BLACK);
        Piece[] whitePieces = getAllPiecesOfColor(WHITE);
        Piece[] allPieces = new Piece[blackPieces.length + whitePieces.length];
        System.arraycopy(blackPieces, 0, allPieces, 0, blackPieces.length);
        System.arraycopy(whitePieces, 0, allPieces, blackPieces.length, whitePieces.length);
        return allPieces;
    }

    public void move(Square current, Square another) {
        if (current.isOccupied() && ChessGameUtility.haveThisMove(current.getPiece().getLegalMoves(), another.getPosition())) {
            Piece captured;
            if (current.getPiece() instanceof Pawn) {
                if (((Pawn) current.getPiece()).isFirstMove()) {
                    if (Math.abs(current.posX - another.posX) == 2) {
                        ((Pawn) current.getPiece()).setCanBePassant(true);
                    }
                }
                ((Pawn) current.getPiece()).nullifyFirstMove();
                if (another.posX == game.passantSquare[0] && another.posY == game.passantSquare[1]) {
                    captured = game.targetOfEnPassant;
                    squares[game.targetOfEnPassant.posX][game.targetOfEnPassant.posY].setPiece(null);
                    squares[current.posX][current.posY].replacePiece(another);
                } else {
                    captured = squares[current.posX][current.posY].replacePiece(another);
                }
            } else {
                captured = squares[current.posX][current.posY].replacePiece(another);
            }

            if (another.getPiece() instanceof Rook) ((Rook) another.getPiece()).hasRookMoved = true;

            if (another.getPiece() instanceof King) {
                if (!((King) another.getPiece()).hasKingMoved) {
                    if (!((King) another.getPiece()).castled && !((King) another.getPiece()).hasKingMoved) {
                        if (another.getPiece().getColor().equals(WHITE)) {
                            if (another.getPosition()[0] == 7 && another.getPosition()[1] == 2) {
                                getSquares()[7][0].replacePiece(getSquares()[7][3]);
                                ((King) another.getPiece()).castled = true;
                                SFX.playSound("castle_1.wav", 5);
                            } else if (another.getPosition()[0] == 7 && another.getPosition()[1] == 6) {
                                getSquares()[7][7].replacePiece(getSquares()[7][5]);
                                ((King) another.getPiece()).castled = true;
                                SFX.playSound("castle_1.wav", 5);
                            }
                        } else {
                            if (another.getPosition()[0] == 0 && another.getPosition()[1] == 2) {
                                getSquares()[0][0].replacePiece(getSquares()[0][3]);
                                ((King) another.getPiece()).castled = true;
                                SFX.playSound("castle_1.wav", 5);
                            } else if (another.getPosition()[0] == 0 && another.getPosition()[1] == 6) {
                                getSquares()[0][7].replacePiece(getSquares()[0][5]);
                                ((King) another.getPiece()).castled = true;
                                SFX.playSound("castle_1.wav", 5);
                            }
                        }
                    }
                    ((King) another.getPiece()).hasKingMoved = true;
                }
            }

            if (another.getPiece() instanceof Pawn && ((Pawn) another.getPiece()).canPromote()) {
                Piece prevPawn = another.getPiece();
                Piece newPiece;
                if ((game.bot != null && game.bot.getColor().equals(prevPawn.getColor()))
                    || (game.anotherBot != null && game.anotherBot.getColor().equals(prevPawn.getColor()))) {
                    newPiece = new Queen(prevPawn.getColor(), prevPawn.posX, prevPawn.posY, 9, this);
                } else {
                    String[] options = {"Queen", "Rook", "Bishop", "Knight"};
                    int choice = JOptionPane.showOptionDialog(null, "Choose promotion piece:", "Promotion",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                    newPiece = switch (choice) {
                        case 1 -> new Rook(prevPawn.getColor(), prevPawn.posX, prevPawn.posY, 5, this, true);
                        case 2 -> new Bishop(prevPawn.getColor(), prevPawn.posX, prevPawn.posY, 3, this);
                        case 3 -> new Knight(prevPawn.getColor(), prevPawn.posX, prevPawn.posY, 3, this);
                        default -> new Queen(prevPawn.getColor(), prevPawn.posX, prevPawn.posY, 9, this);
                    };
                }
                another.setPiece(newPiece);
                pawnPromoted = true;
            }

            if (captured != null) {
                this.capturedToString = captured.toString();
            } else {
                this.capturedToString = "none";
            }
        }
    }

    public Integer[] getKingPosition(String color) {
        Integer[] kingPosition = new Integer[2];
        for (Piece piece : getAllPiecesOfColor(color)) {
            if (piece instanceof King) {
                kingPosition[0] = piece.posX;
                kingPosition[1] = piece.posY;
            }
        }
        return kingPosition;
    }

    public boolean noPawnsLeft() {
        for (Piece piece : getAllPieces()) {
            if (piece instanceof Pawn) {
                return false;
            }
        }
        return true;
    }

    public int calculateValueExcludingPawn(String color) {
        int totalValue = 0;
        for (Piece piece : getAllPieces()) {
            if (piece.getColor().equals(color)) {
                if (piece instanceof Queen || piece instanceof Rook
                        || piece instanceof Knight || piece instanceof Bishop) {
                    totalValue += piece.getPoint();
                }
            }
        }
        return totalValue;
    }

    public int calculateTotalValue(String color) {
        int totalValue = 0;
        for (Piece piece : getAllPieces()) {
            if (piece.getColor().equals(color)) {
                if (piece instanceof Queen || piece instanceof Rook || piece instanceof Pawn
                        || piece instanceof Knight || piece instanceof Bishop) {
                    totalValue += piece.getPoint();
                }
            }
        }
        return totalValue;
    }

    public boolean kingInCheck(boolean turn) {
        List<Integer[]> allMoves = new ArrayList<>();
        for (Piece piece : getAllPiecesOfColor(turn ? WHITE : BLACK)) {
            if (piece instanceof Pawn) {
                allMoves.addAll(piece.getSight());
            } else {
                allMoves.addAll(piece.getStandardMoves());
            }
        }
        return ChessGameUtility.haveThisMove(allMoves, getKingPosition(!turn ? WHITE : BLACK));
    }

    public boolean checkStalemate(boolean turn) {
        List<Integer[]> allAllyMoves = new ArrayList<>();
        for (Piece piece : getAllPiecesOfColor(turn ? WHITE : BLACK)) {
            allAllyMoves.addAll(piece.getLegalMoves());
        }
        return allAllyMoves.isEmpty() && (turn ? !game.whiteKingInCheck : !game.blackKingInCheck);
    }

    @SuppressWarnings("unchecked")
    public boolean checkCheckmate(boolean turn) {
        if (turn && !game.whiteKingInCheck) return false;
        if (!turn && !game.blackKingInCheck) return false;

        String colorBeingChecked = turn ? WHITE : BLACK;
        String opposingColor = turn ? BLACK : WHITE;

        if (doubleChecked(turn)
                && getSquareAt(getKingPosition(colorBeingChecked)[0], getKingPosition(colorBeingChecked)[1])
                .getPiece().getLegalMoves().isEmpty()) {
            return true;
        }
        List<Integer[]> enemyMoves = new ArrayList<>();
        List<Integer[]> directionalMoves = new ArrayList<>();
        List<Integer> direction = new ArrayList<>();
        boolean directionalPiece;

        for (Piece enemy : getAllPiecesOfColor(opposingColor)) {
            directionalPiece = false;
            enemyMoves.clear();
            directionalMoves.clear();
            direction.clear();
            if (enemy instanceof Queen) {
                IntStream.rangeClosed(1, 8).forEach(direction::add);
                directionalPiece = true;
            } else if (enemy instanceof Bishop) {
                direction.add(1);
                direction.add(3);
                direction.add(6);
                direction.add(8);
                directionalPiece = true;
            } else if (enemy instanceof Rook) {
                direction.add(2);
                direction.add(4);
                direction.add(5);
                direction.add(7);
                directionalPiece = true;
            }
            if (directionalPiece) {
                for (int i = 1; i <= direction.size(); i++) {
                    enemyMoves.clear();
                    directionalMoves.clear();
                    enemyMoves.add(new Integer[]{enemy.posX, enemy.posY});
                    String methodName = "getMoves" + Piece.DIRECTION_MAP.get(direction.get(i - 1));
                    Method method;
                    try {
                        method = enemy.getClass().getMethod(methodName);
                        directionalMoves = (List<Integer[]>) method.invoke(enemy);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    enemyMoves.addAll(enemy.nullifyBlockage(directionalMoves)
                            .subList(0, Math.min(Board.NO_LIMIT, enemy.nullifyBlockage(directionalMoves).size())));
                    if (ChessGameUtility.haveThisMove(enemyMoves, getKingPosition(colorBeingChecked))) {
                        for (Piece ally : getAllPiecesOfColor(colorBeingChecked)) {
                            if (ChessGameUtility.containsAny(enemyMoves, ally.getLegalMoves())) {
                                return false;
                            }
                        }
                        if (getSquareAt(getKingPosition(colorBeingChecked)[0], getKingPosition(colorBeingChecked)[1])
                                .getPiece().getLegalMoves().isEmpty()) {
                            return true;
                        }
                    }
                }
            } else {
                enemyMoves.add(new Integer[]{enemy.posX, enemy.posY});
                if (ChessGameUtility.haveThisMove(enemy.getSight(), getKingPosition(colorBeingChecked))) {
                    for (Piece ally : getAllPiecesOfColor(colorBeingChecked)) {
                        if (ChessGameUtility.containsAny(enemyMoves, ally.getLegalMoves())) {
                            return false;
                        }
                    }
                    if (game.targetOfEnPassant != null && game.targetOfEnPassant.getColor().equals(opposingColor)
                            && (game.targetOfEnPassant.posX == enemy.posX && game.targetOfEnPassant.posY == enemy.posY)) {
                        for (Piece allyPawns : getAllPiecesOfColor(colorBeingChecked)) {
                            if (allyPawns instanceof Pawn && ChessGameUtility.haveThisMove(allyPawns.getLegalMoves(), game.passantSquare)) {
                                return false;
                            }
                        }
                    }
                    if (getSquareAt(getKingPosition(colorBeingChecked)[0], getKingPosition(colorBeingChecked)[1])
                            .getPiece().getLegalMoves().isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean doubleChecked(boolean turn) {
        String colorBeingChecked = turn ? WHITE : BLACK;
        String opposingColor = turn ? BLACK : WHITE;
        int checkingPiece = 0;

        for (Piece piece : getAllPiecesOfColor(opposingColor)) {
            List<Integer[]> allMoves = new ArrayList<>();
            if (piece instanceof Pawn) {
                allMoves.addAll(piece.getSight());
            } else {
                allMoves.addAll(piece.getStandardMoves());
            }
            if (ChessGameUtility.haveThisMove(allMoves, getKingPosition(colorBeingChecked))) {
                checkingPiece++;
            }
        }
        return checkingPiece >= 2;
    }

    public void refreshPins() {
        for (Piece piece : getAllPieces()) {
            piece.setPinning(false);
            piece.setPinned(false);
        }

    }

    @SuppressWarnings("unchecked")
    public void pinningHelper(Piece piece, int direction) {
        List<Integer[]> between;
        String opposingColor = piece.getColor().equals(WHITE) ? BLACK : WHITE;
        try {
            String methodName = "getMoves" + Piece.DIRECTION_MAP.get(direction);
            Method method = piece.getClass().getMethod(methodName);
            between = (List<Integer[]>) method.invoke(piece);
            if (ChessGameUtility.haveThisMove(between, getKingPosition(opposingColor))) {
                between = ChessGameUtility.trimMoves((List<Integer[]>) method.invoke(piece), getKingPosition(opposingColor), false);
                between.removeIf(move -> !getSquareAt(move[0], move[1]).isOccupied());
                if (between.size() == 1
                        && getSquareAt(between.get(0)[0], between.get(0)[1]).getPiece().getColor().equals(opposingColor)) {
                    getSquareAt(between.get(0)[0], between.get(0)[1]).getPiece().setPinned(true);
                    piece.setPinning(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void markPinnedPieces() {
        refreshPins();
        for (Piece piece : getAllPieces()) {
            if (piece instanceof Queen) {
                IntStream.rangeClosed(1, 8).forEach(i -> pinningHelper(piece, i));
            } else if (piece instanceof Rook) {
                pinningHelper(piece, 2);
                pinningHelper(piece, 4);
                pinningHelper(piece, 5);
                pinningHelper(piece, 7);
            } else if (piece instanceof Bishop) {
                pinningHelper(piece, 1);
                pinningHelper(piece, 3);
                pinningHelper(piece, 6);
                pinningHelper(piece, 8);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void protectingHelper(Piece piece, int direction, List<Integer[]> isProtecting) {
        List<Integer[]> between;
        int target = 0;
        boolean protecting;
        try {
            String methodName = "getMoves" + Piece.DIRECTION_MAP.get(direction);
            Method method = piece.getClass().getMethod(methodName);
            between = (List<Integer[]>) method.invoke(piece);
            if (between.isEmpty()) return;
            protecting = between.stream()
                    .anyMatch(move -> getSquareAt(move[0], move[1]).isOccupied());
            if (protecting) {
                for (Integer[] move : between) {
                    if (getSquareAt(move[0], move[1]).isOccupied()
                            && getSquareAt(move[0], move[1]).getPiece().isEnemy(getSquareAt(piece.posX, piece.posY))) {
                        return;
                    }
                    if (getSquareAt(move[0], move[1]).isOccupied()
                            && getSquareAt(move[0], move[1]).getPiece().isAlly(getSquareAt(piece.posX, piece.posY))) {
                        target++;
                        isProtecting.add(between.get(target - 1));
                        return;
                    }
                    target++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshProtecting() {
        game.isProtected.clear();
        List<Integer[]> protecting = new ArrayList<>();
        List<Integer[]> currentSight = new ArrayList<>();
        for (Piece piece : getAllPieces()) {
            currentSight.clear();
            if (piece instanceof Knight || piece instanceof Pawn) {
                currentSight.addAll(piece.getSight());
                for (Integer[] sight : currentSight) {
                    if (getSquareAt(sight[0], sight[1]).isOccupied()
                            && getSquareAt(sight[0], sight[1]).getPiece().isAlly(getSquareAt(piece.posX, piece.posY))) {
                        protecting.add(new Integer[]{sight[0], sight[1]});
                    }
                }
            } else if (piece instanceof Queen) {
                IntStream.rangeClosed(1, 8).forEach(i -> protectingHelper(piece, i, protecting));
            } else if (piece instanceof Rook) {
                protectingHelper(piece, 2, protecting);
                protectingHelper(piece, 4, protecting);
                protectingHelper(piece, 5, protecting);
                protectingHelper(piece, 7, protecting);
            } else if (piece instanceof Bishop) {
                protectingHelper(piece, 1, protecting);
                protectingHelper(piece, 3, protecting);
                protectingHelper(piece, 6, protecting);
                protectingHelper(piece, 8, protecting);
            } else if (piece instanceof King) {
                List<Integer[]> kingSight = piece.getSight();
                for (Integer[] sight : kingSight) {
                    if (getSquareAt(sight[0], sight[1]).isOccupied()
                            && getSquareAt(sight[0], sight[1]).getPiece().isAlly(getSquareAt(piece.posX, piece.posY))) {
                        protecting.add(new Integer[]{sight[0], sight[1]});
                    }
                }
            }
        }
        protecting.addAll(ChessGameUtility.removeDuplicates(protecting));
        game.isProtected.addAll(protecting);
    }

    public Square[][] copyOfBoard(Square[][] board) {
        Board copiedBoard = new Board();
        copiedBoard.attachAndInitializeDefaultPieces(game);
        Square[][] copy = new Square[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                copy[i][j] =
                        new Square(null, (i + j) % 2 == 0 ? Board.LIGHT : Board.DARK);
                copy[i][j].setPosition(i, j);
                if (board[i][j].isOccupied()) {
                    Piece piece = board[i][j].getPiece();
                    if (piece instanceof Pawn) {
                        copy[i][j].setPiece(new Pawn(piece.getColor(), i, j, 1, copiedBoard));
                        ((Pawn) copy[i][j].getPiece()).setFirstMove(((Pawn) board[i][j].getPiece()).isFirstMove());
                        ((Pawn) copy[i][j].getPiece()).setCanBePassant(((Pawn) board[i][j].getPiece()).canBePassant());
                    }
                    if (piece instanceof Knight)
                        copy[i][j].setPiece(new Knight(piece.getColor(), i, j, 3, copiedBoard));
                    if (piece instanceof Bishop)
                        copy[i][j].setPiece(new Bishop(piece.getColor(), i, j, 3, copiedBoard));
                    if (piece instanceof Queen)
                        copy[i][j].setPiece(new Queen(piece.getColor(), i, j, 9, copiedBoard));
                    if (piece instanceof Rook)
                        copy[i][j].setPiece(new Rook(piece.getColor(), i, j, 5, copiedBoard, ((Rook) piece).hasRookMoved));
                    if (piece instanceof King) {
                        copy[i][j].setPiece(new King(piece.getColor(), i, j, Board.NO_LIMIT, copiedBoard, ((King) piece).hasKingMoved, ((King) piece).castled));
                    }
                    copy[i][j].getPiece().setPinned(piece.isPinned());
                    copy[i][j].getPiece().setPinning(piece.isPinning());
                    copy[i][j].getPiece().attachGame(game);
                }
            }
        }
        copiedBoard.setSquares(copy);
        attachGameAllPieces();
        return copiedBoard.getSquares();
    }

    private void attachGameAllPieces() {
        for (Piece piece : getAllPieces()) {
            piece.attachGame(game);
        }
    }

}
