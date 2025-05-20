package chess.gui;

import chess.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageAsset {

    public static Map<String, ImageIcon> pieceIcons = new HashMap<>();

    public static void anarcandyPieces() {
        pieceIcons.clear();
        pieceIcons.put("BLACKPawn", getImageIcon("anarcandy/Artboard 3.png"));
        pieceIcons.put("WHITEPawn", getImageIcon("anarcandy/Artboard 10.png"));
        pieceIcons.put("BLACKKing", getImageIcon("anarcandy/Artboard 1.png"));
        pieceIcons.put("WHITEKing", getImageIcon("anarcandy/Artboard 8.png"));
        pieceIcons.put("BLACKRook", getImageIcon("anarcandy/Artboard 5.png"));
        pieceIcons.put("WHITERook", getImageIcon("anarcandy/Artboard 12.png"));
        pieceIcons.put("BLACKKnight", getImageIcon("anarcandy/Artboard 2.png"));
        pieceIcons.put("WHITEKnight", getImageIcon("anarcandy/Artboard 9.png"));
        pieceIcons.put("BLACKBishop", getImageIcon("anarcandy/Artboard 6.png"));
        pieceIcons.put("WHITEBishop", getImageIcon("anarcandy/Artboard 7.png"));
        pieceIcons.put("BLACKQueen", getImageIcon("anarcandy/Artboard 4.png"));
        pieceIcons.put("WHITEQueen", getImageIcon("anarcandy/Artboard 11.png"));
    }

    public static void pixelPieces() {
        pieceIcons.clear();
        pieceIcons.put("BLACKPawn", getImageIcon("pixel/Artboard 9@4x.png"));
        pieceIcons.put("WHITEPawn", getImageIcon("pixel/Artboard 1@4x.png"));
        pieceIcons.put("BLACKKing", getImageIcon("pixel/Artboard 11@4x.png"));
        pieceIcons.put("WHITEKing", getImageIcon("pixel/Artboard 5@4x.png"));
        pieceIcons.put("BLACKRook", getImageIcon("pixel/Artboard 7@4x.png"));
        pieceIcons.put("WHITERook", getImageIcon("pixel/Artboard 2@4x.png"));
        pieceIcons.put("BLACKKnight", getImageIcon("pixel/Artboard 10@4x.png"));
        pieceIcons.put("WHITEKnight", getImageIcon("pixel/Artboard 4@4x.png"));
        pieceIcons.put("BLACKBishop", getImageIcon("pixel/Artboard 12@4x.png"));
        pieceIcons.put("WHITEBishop", getImageIcon("pixel/Artboard 6@4x.png"));
        pieceIcons.put("BLACKQueen", getImageIcon("pixel/Artboard 8@4x.png"));
        pieceIcons.put("WHITEQueen", getImageIcon("pixel/Artboard 3@4x.png"));
    }

    public static void cbrrunettPieces() {
        pieceIcons.clear();
        pieceIcons.put("BLACKPawn", getImageIcon("cbrrunett/black-pawn.png"));
        pieceIcons.put("WHITEPawn", getImageIcon("cbrrunett/white-pawn.png"));
        pieceIcons.put("BLACKKing", getImageIcon("cbrrunett/black-king.png"));
        pieceIcons.put("WHITEKing", getImageIcon("cbrrunett/white-king.png"));
        pieceIcons.put("BLACKRook", getImageIcon("cbrrunett/black-rook.png"));
        pieceIcons.put("WHITERook", getImageIcon("cbrrunett/white-rook.png"));
        pieceIcons.put("BLACKKnight", getImageIcon("cbrrunett/black-knight.png"));
        pieceIcons.put("WHITEKnight", getImageIcon("cbrrunett/white-knight.png"));
        pieceIcons.put("BLACKBishop", getImageIcon("cbrrunett/black-bishop.png"));
        pieceIcons.put("WHITEBishop", getImageIcon("cbrrunett/white-bishop.png"));
        pieceIcons.put("BLACKQueen", getImageIcon("cbrrunett/black-queen.png"));
        pieceIcons.put("WHITEQueen", getImageIcon("cbrrunett/white-queen.png"));
    }

    public static URL getImageURL(String fileName) {
        String image = Main.class.getPackageName().replace('.', '/') +
                "/asset/png/" + fileName;
        return Main.class.getClassLoader().getResource(image);
    }

    public static ImageIcon getImageIcon(String fileName) {
        URL imageURL = ImageAsset.getImageURL(fileName);
        if (imageURL != null) {
            ImageIcon originalIcon = new ImageIcon(imageURL);
            Image img = originalIcon.getImage();
            int width = 60;
            int height = 60;
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            AffineTransform at = AffineTransform.getScaleInstance((double) width / img.getWidth(null), (double) height / img.getHeight(null));
            g2d.drawImage(img, at, null);
            g2d.dispose();
            return new ImageIcon(resizedImage);
        } else {
            return null;
        }
    }

}
