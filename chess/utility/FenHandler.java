package chess.utility;

import chess.board.Board;
import chess.board.ChessGame;
import chess.board.Square;
import chess.gui.SFX;
import chess.piece.*;

public class FenHandler {

    private ChessGame game;

    public FenHandler(ChessGame game) {
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public static boolean validateFen(String fen) {
        String[] parts = fen.split(" ");

        if (parts.length < 6) return false;
        String board = parts[0];
        String side = parts[1];
        String castleRights = parts[2];
        String ep = parts[3];
        if (Integer.parseInt(parts[4]) > 100) return false;
        if (!side.equals("w") && !side.equals("b")) return false;

        if (!castleRights.matches(
                "KQkq|KQk|KQq|KQ-|Kkq|Kk|Kq|K-|Qkq|Qk|Qq|Q-|-kq|-k|-q|--|-")) return false;

        if (!ep.equals("-")) {
            if (ep.length() != 2) return false;
            if (!(ep.charAt(0) >= 'a' && ep.charAt(0) <= 'h')) return false;
            if (!((side.equals("w") && ep.charAt(1) == '6') || (side.equals("b") && ep.charAt(1) == '3')))
                return false;
        }

        if (board.isEmpty()) return false;
        if (board.split("/").length != 8) return false;
        for (char c : board.toCharArray()) {
            if (c != '/' && !Character.isDigit(c) && !isValidPieceCharacter(c)) return false;
        }

        String firstRank = fen.split(" ")[0].split("/")[0];
        String lastRank = fen.split(" ")[0].split("/")[7];
        if (firstRank.contains("p") || firstRank.contains("P")
                || lastRank.contains("p") || lastRank.contains("P")) return false;

        long wp = board.chars().filter(ch -> ch == 'P').count();
        long bp = board.chars().filter(ch -> ch == 'p').count();
        long wn = board.chars().filter(ch -> ch == 'N').count();
        long bn = board.chars().filter(ch -> ch == 'n').count();
        long wb = board.chars().filter(ch -> ch == 'B').count();
        long bb = board.chars().filter(ch -> ch == 'b').count();
        long wr = board.chars().filter(ch -> ch == 'R').count();
        long br = board.chars().filter(ch -> ch == 'r').count();
        long wq = board.chars().filter(ch -> ch == 'Q').count();
        long bq = board.chars().filter(ch -> ch == 'q').count();

        return wp <= 8 && bp <= 8 && wn <= 10 && bn <= 10 && wb <= 10 && bb <= 10
                && wr <= 10 && br <= 10 && wq <= 9 && bq <= 10
                && wp + wn + wb + wr + wq <= 15 && bp + bn + bb + br + bq <= 15;
    }

    public String generateFen() {
        StringBuilder fen = new StringBuilder();
        int countEmptySquare = 0;
        int countSquares = 0;
        int countRank = 0;
        for (Square square : game.getBoard().getAllSquares()) {
            countSquares++;
            if (!square.isOccupied()) {
                countEmptySquare++;
            } else {
                fen.append(countEmptySquare == 0 ? "" : countEmptySquare);
                countEmptySquare = 0;
                fen.append(getFenFromPiece(square.getPiece()));
            }
            if (countSquares >= 8) {
                fen.append(countEmptySquare == 0 ? "" : countEmptySquare);
                if (countRank < 7) {
                    fen.append("/");
                }
                countSquares = 0;
                countEmptySquare = 0;
                countRank++;
            }

        }
        if (game.turn) {
            fen.append(" w");
        } else {
            fen.append(" b");
        }
        fen.append(" ").append(getFenCastlingRights());
        fen.append(getFenEnPassant());
        fen.append(" ").append(game.fiftyMoveRuleCounter);
        fen.append(" ").append(game.halfmoves / 2 + 1);
        return fen.toString().trim();
    }

    private String getFenFromPiece(Piece piece) {
        String fenPiece = "";
        if (piece instanceof Pawn) {
            fenPiece = "p";
        } else if (piece instanceof Bishop) {
            fenPiece = "b";
        } else if (piece instanceof Knight) {
            fenPiece = "n";
        } else if (piece instanceof Rook) {
            fenPiece = "r";
        } else if (piece instanceof Queen) {
            fenPiece = "q";
        } else if (piece instanceof King) {
            fenPiece = "k";
        }
        return piece.getColor().equals(Board.WHITE) ? fenPiece.toUpperCase() : fenPiece;
    }

    private String getFenCastlingRights() {
        StringBuilder castlingRights = new StringBuilder();
        boolean whiteKingHasMoved = true;
        boolean blackKingHasMoved = true;
        boolean queenSideBlackRookHasMoved = true;
        boolean queenSideWhiteRookHasMoved = true;
        boolean kingSideBlackRookHasMoved = true;
        boolean kingSideWhiteRookHasMoved = true;
        for (Square square : game.getBoard().getAllSquares()) {
            if (square.isOccupied()) {
                if (square.getPiece() instanceof King && square.getPiece().getColor().equals(Board.WHITE)) {
                    whiteKingHasMoved = ((King) square.getPiece()).hasKingMoved;
                } else if (square.getPiece() instanceof King && square.getPiece().getColor().equals(Board.BLACK)) {
                    blackKingHasMoved = ((King) square.getPiece()).hasKingMoved;
                } else if (square.getPiece() instanceof Rook && square.getPiece().getColor().equals(Board.WHITE)) {
                    if (square.getPiece().posX == 7 && square.getPiece().posY == 0) {
                        queenSideWhiteRookHasMoved = ((Rook) square.getPiece()).hasRookMoved;
                    } else if (square.getPiece().posX == 7 && square.getPiece().posY == 7) {
                        kingSideWhiteRookHasMoved = ((Rook) square.getPiece()).hasRookMoved;
                    }
                } else if (square.getPiece() instanceof Rook && square.getPiece().getColor().equals(Board.BLACK)) {
                    if (square.getPiece().posX == 0 && square.getPiece().posY == 0) {
                        queenSideBlackRookHasMoved = ((Rook) square.getPiece()).hasRookMoved;
                    } else if (square.getPiece().posX == 0 && square.getPiece().posY == 7) {
                        kingSideBlackRookHasMoved = ((Rook) square.getPiece()).hasRookMoved;
                    }
                }
            }
        }
        if (!whiteKingHasMoved) {
            if (!kingSideWhiteRookHasMoved) {
                castlingRights.append("K");
            }
            if (!queenSideWhiteRookHasMoved) {
                castlingRights.append("Q");
            }
        }
        if (!castlingRights.toString().contains("K") && !castlingRights.toString().contains("Q")) {
            castlingRights.append("-");
        }
        if (!blackKingHasMoved) {
            if (!kingSideBlackRookHasMoved) {
                castlingRights.append("k");
            }
            if (!queenSideBlackRookHasMoved) {
                castlingRights.append("q");
            }
        }
        if (!castlingRights.toString().contains("k") && !castlingRights.toString().contains("q")) {
            castlingRights.append("-");
        }
        if (castlingRights.toString().isEmpty() || castlingRights.toString().equals("--")) return "-";
        return castlingRights.toString();
    }

    public String getFenEnPassant() {
        if (game.passantSquare[0] == -1) return " -";
        int x = game.passantSquare[0];
        int y = game.passantSquare[1];
        return " " + ChessGameUtility.convertToChessNotation(x, y).toLowerCase();
    }

    public static ChessGame initializeChessGameFromFen(String fen) throws IllegalStateException {
        boolean whiteKingHasMoved = true;
        boolean blackKingHasMoved = true;
        boolean queenSideBlackRookHasMoved = true;
        boolean queenSideWhiteRookHasMoved = true;
        boolean kingSideBlackRookHasMoved = true;
        boolean kingSideWhiteRookHasMoved = true;
        String[] fenSections = fen.split(" ");
        ChessGame gameFromFen = new ChessGame();
        for (Square square : gameFromFen.getBoard().getAllSquares()) square.setPiece(null);
        gameFromFen.fiftyMoveRuleCounter = Integer.parseInt(fenSections[4]);
        gameFromFen.halfmoves = Math.max(0, Integer.parseInt(fenSections[5]) / 2 - 1);
        gameFromFen.turn = fenSections[1].charAt(0) == 'b';
        char[] castlingRights = fenSections[2].toCharArray();
        for (char castling : castlingRights) {
            if (castling == 'K') {
                whiteKingHasMoved = false;
                kingSideWhiteRookHasMoved = false;
            }
            if (castling == 'Q') {
                whiteKingHasMoved = false;
                queenSideWhiteRookHasMoved = false;
            }
            if (castling == 'k') {
                blackKingHasMoved = false;
                kingSideBlackRookHasMoved = false;
            }
            if (castling == 'q') {
                whiteKingHasMoved = false;
                queenSideBlackRookHasMoved = false;
            }
        }
        gameFromFen.passantSquare = ChessGameUtility.convertFromChessNotation(fenSections[3]);
        int squareNumber = 0;
        String positionString = fenSections[0].replace("/", "");
        char[] position = fenSections[0].replace("/", "").toUpperCase().toCharArray();
        int positionPointer = 0;
        int emptySquare = 0;
        for (Square square : gameFromFen.getBoard().getAllSquares()) {
            if (emptySquare > 1) {
                emptySquare--;
            } else if (Character.isDigit(position[positionPointer])) {
                emptySquare = Integer.parseInt(String.valueOf(position[positionPointer]));
                squareNumber += emptySquare;
                positionPointer++;
            } else {
                Piece piece = getPieceFromFen(squareNumber, positionPointer, position, gameFromFen);
                if (piece != null && Character.isUpperCase(positionString.charAt(positionPointer))) {
                    piece.setColor(Board.WHITE);
                    if (piece instanceof Rook) {
                        if (piece.posX == 7 && piece.posY == 0) {
                            ((Rook) piece).hasRookMoved = queenSideWhiteRookHasMoved;
                        }
                        if (piece.posX == 7 && piece.posY == 7) {
                            ((Rook) piece).hasRookMoved = kingSideWhiteRookHasMoved;
                        }
                    }
                    if (piece instanceof King) {
                        ((King) piece).hasKingMoved = whiteKingHasMoved;
                    }
                    if (piece instanceof Pawn) {
                        ((Pawn) piece).refreshPawnOffset();
                        ((Pawn) piece).setCanBePassant(false);
                        if (piece.posX == 6) {
                            ((Pawn) piece).setFirstMove(true);
                        }
                    }
                } else if (piece != null && Character.isLowerCase(positionString.charAt(positionPointer))) {
                    piece.setColor(Board.BLACK);
                    if (piece instanceof Rook) {
                        if (piece.posX == 0 && piece.posY == 0) {
                            ((Rook) piece).hasRookMoved = queenSideBlackRookHasMoved;
                        }
                        if (piece.posX == 0 && piece.posY == 7) {
                            ((Rook) piece).hasRookMoved = kingSideBlackRookHasMoved;
                        }
                    }
                    if (piece instanceof King) {
                        ((King) piece).hasKingMoved = blackKingHasMoved;
                    }
                    if (piece instanceof Pawn) {
                        ((Pawn) piece).refreshPawnOffset();
                        ((Pawn) piece).setCanBePassant(false);
                        if (piece.posX == 1) {
                            ((Pawn) piece).setFirstMove(true);
                        }
                    }
                }
                square.setPiece(piece);
                square.getPiece().attachGame(gameFromFen);
                positionPointer++;
                squareNumber++;
            }
        }
        gameFromFen.validateChecking();
        gameFromFen.turn = !gameFromFen.turn;

        gameFromFen.getBoard().refreshProtecting();
        gameFromFen.getBoard().markPinnedPieces();

        if (gameFromFen.passantSquare[0] == 2) {
            gameFromFen.targetOfEnPassant =
                    (Pawn) gameFromFen.getBoard().
                            getSquareAt(gameFromFen.passantSquare[0] + 1, gameFromFen.passantSquare[1]).getPiece();
            gameFromFen.targetOfEnPassant.setCanBePassant(true);
        } else if (gameFromFen.passantSquare[0] == 5) {
            gameFromFen.targetOfEnPassant =
                    (Pawn) gameFromFen.getBoard().
                            getSquareAt(gameFromFen.passantSquare[0] - 1, gameFromFen.passantSquare[1]).getPiece();
            gameFromFen.targetOfEnPassant.setCanBePassant(true);
        } else {
            gameFromFen.targetOfEnPassant = null;
        }

        if (gameFromFen.getBoard().checkStalemate(gameFromFen.turn)) gameFromFen.stalemated = true;
        if (gameFromFen.getBoard().checkCheckmate(gameFromFen.turn)) gameFromFen.checkmated = true;
        gameFromFen.checkDraw();

        if (gameFromFen.stalemated || gameFromFen.checkmated
                || gameFromFen.drawByFiftyMoveRule || gameFromFen.drawByRepetition
                || gameFromFen.drawByInsufficientMaterial) gameFromFen.gameIsOver = true;

        gameFromFen.getBoard().capturedToString = "none";
        gameFromFen.capturedInfo = "none";

        gameFromFen.currentFEN = gameFromFen.fenHandler.generateFen();
        if (gameFromFen.gameIsOver) {
            SFX.playSound("game-end.wav", 5);
        }
        return gameFromFen;
    }

    private static Piece getPieceFromFen(int squareNumber, int positionPointer, char[] position, ChessGame gameFromFen) {
        int row = squareNumber / 8;
        int col = squareNumber % 8;
        Piece piece;
        switch (position[positionPointer]) {
            case 'K':
                piece = new King("-", row, col, Board.NO_LIMIT,
                        gameFromFen.getBoard(), true, true);
                break;
            case 'Q':
                piece = new Queen("-", row, col, 9, gameFromFen.getBoard());
                break;
            case 'R':
                piece = new Rook("-", row, col, 5, gameFromFen.getBoard(), true);
                break;
            case 'N':
                piece = new Knight("-", row, col, 3, gameFromFen.getBoard());
                break;
            case 'B':
                piece = new Bishop("-", row, col, 3, gameFromFen.getBoard());
                break;
            case 'P':
                piece = new Pawn("-", row, col, 1, gameFromFen.getBoard());
                break;
            default:
                piece = null;
        }
        return piece;
    }

    private static boolean isValidPieceCharacter(char c) {
        return "KkQqRrBbNnPp".indexOf(c) != -1;
    }

}
