package chess.gui;

import java.awt.*;

public class ChessGameTheme {

    public static Font defaultBoldFont = new Font("Helvetica", Font.BOLD, 14);
    public static Font defaultFont = new Font("Helvetica", Font.PLAIN, 14);
    public static Color lightSquareColor;
    public static Color darkSquareColor;
    public static Color selectColor;
    public static Color textAreaColor;
    public static Color panelBorderColor;
    public static Color paddingColor;
    public static Color checkColor = new Color(204, 0, 0);

    public static final int themeNumber = 5;
    public static int currentTheme;

    public static void cycleTheme() {
        if (currentTheme == 5) {
            currentTheme = 1;
        } else {
            currentTheme++;
        }
        switch (currentTheme) {
            case 1 -> coralTheme();
            case 2 -> chessDotComTheme();
            case 3 -> coffeeTheme();
            case 4 -> candyTheme();
            case 5 -> metalTheme();
        }

    }

    public static void coralTheme() {
        lightSquareColor = new Color(177,228,185);
        darkSquareColor = new Color(112,162,163);
        selectColor = new Color(51, 204, 255);
        textAreaColor = new Color(112,162,163);
        panelBorderColor = new Color(0, 77, 77);
        paddingColor = new Color(0, 128, 128);
        currentTheme = 1;
    }

    public static void chessDotComTheme() {
        lightSquareColor = new Color(238,238,210);
        darkSquareColor = new Color(118,150,86);
        selectColor = new Color(255,252,4);
        textAreaColor = new Color(118,150,86);
        panelBorderColor = new Color(48,44,44);
        paddingColor = new Color(72,68,68);
        currentTheme = 2;
    }

    public static void coffeeTheme() {
        lightSquareColor = new Color(196,172,140);
        darkSquareColor = new Color(100,76,52);
        selectColor = new Color(196,164,84);
        textAreaColor = new Color(100,76,52);
        panelBorderColor = new Color(50, 38, 26);
        paddingColor = new Color(118, 89, 61);
        currentTheme = 3;
    }

    public static void candyTheme() {
        lightSquareColor = new Color(255, 153, 230).darker();
        darkSquareColor = new Color(179, 0, 71).darker();
        selectColor = new Color(255, 51, 133);
        textAreaColor = new Color(115, 38, 77);
        panelBorderColor = new Color(48,44,44);
        paddingColor = new Color(72,68,68);
        currentTheme = 4;
    }

    public static void metalTheme() {
        lightSquareColor = Color.WHITE;
        darkSquareColor = Color.GRAY;
        selectColor = Color.GREEN;
        textAreaColor = Color.DARK_GRAY;
        panelBorderColor = Color.DARK_GRAY.darker();
        paddingColor = Color.GRAY.darker();
        currentTheme = 5;
    }

}
