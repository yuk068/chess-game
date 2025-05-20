package chess.board;

import chess.piece.Piece;

public class Square {

    private Piece piece;
    private String shade;

    protected int posX;
    protected int posY;

    public Square() {
    }

    public Square(Piece piece, String shade) {
        this.piece = piece;
        this.shade = shade;
    }

    public void setPosition(int x, int y) {
        posX = x;
        posY = y;
    }

    public Integer[] getPosition() {
        return new Integer[]{posX, posY};
    }

    public String getShade() {
        return shade;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Piece replacePiece(Square another) {
        Piece oldPiece = another.getPiece();
        another.setPiece(piece);
        another.getPiece().setPosition(another.posX, another.posY);
        piece = null;
        return oldPiece;
    }

    public boolean isOccupied() {
        return piece != null;
    }

    public boolean isSameSquare(Square another) {
        if (!isOccupied() && !another.isOccupied()) {
            return true;
        }
        if (!isOccupied() && another.isOccupied()) {
            return false;
        }
        if (isOccupied() && !another.isOccupied()) {
            return false;
        }
        Piece thisPiece = getPiece();
        Piece anotherPiece = another.getPiece();
        return thisPiece.toString().equals(anotherPiece.toString());
    }

}
