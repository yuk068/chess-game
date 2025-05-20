package chess.gui;

import chess.board.Board;
import chess.board.ChessGame;
import chess.board.Square;
import chess.piece.King;
import chess.utility.ChessGameUtility;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class ChessBoardPanel extends JPanel {

    private ChessGame game;
    private InfoTabPanel console;
    private ChessGameGUI window;

    public boolean showCoordinates;
    public boolean coordsInNotation;
    public boolean flipBoard;
    private Square selectedSquare;
    private int fromX, fromY, toX, toY;

    public ChessBoardPanel(ChessGame game, boolean flipped, ChessGameGUI gui) {
        this.game = game;
        this.window = gui;
        showCoordinates = false;
        coordsInNotation = true;
        flipBoard = flipped;
        setLayout(new GridLayout(8, 8));
        setBorder(getCompoundBorder());
        setBackground(ChessGameTheme.panelBorderColor);
        createChessBoard(game.getBoard().getSquares());
        addSquareMouseListeners();
    }

    public void attachGame(ChessGame game) {
        fromX = -1;
        fromY = -1;
        toX = -1;
        toY = -1;
        selectedSquare = null;
        this.game = game;
    }

    public void attachConsole(InfoTabPanel infoTabPanel) {
        this.console = infoTabPanel;
    }

    public void resetSelect() {
        fromX = -1;
        fromY = -1;
        toX = -1;
        toY = -1;
        selectedSquare = null;
        update();
    }

    public void update() {
        update(game.getBoard().getSquares());
    }

    public void createChessBoard(Square[][] board) {
        removeAll();
        revalidate();
        repaint();
        int squareSize = 67;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square square = board[i][j];
                JPanel squarePanel = new JPanel(new BorderLayout());
                squarePanel.setPreferredSize(new Dimension(squareSize, squareSize));
                squarePanel.setBackground((i + j) % 2 == 0 ?
                        ChessGameTheme.lightSquareColor : ChessGameTheme.darkSquareColor);

                if (square.isOccupied()) {
                    String piece = square.getPiece().getColor()
                            + square.getPiece().getClass().getSimpleName();
                    ImageIcon icon = ImageAsset.pieceIcons.get(piece);
                    if (icon != null) {
                        JLabel pieceLabel = new JLabel(icon);
                        squarePanel.add(pieceLabel, BorderLayout.CENTER);
                    }
                }

                if (showCoordinates) {
                    JLabel coordinateLabel = new JLabel(coordsInNotation ?
                            ChessGameUtility.convertToChessNotation(i, j) : i + "" + j);
                    squarePanel.add(coordinateLabel, BorderLayout.NORTH);
                }

                add(squarePanel);
            }
        }
    }

    public void update(final Square[][] board) {
        SwingUtilities.invokeLater(() -> {
            for (Component component : getComponents()) {
                JPanel squarePanel = (JPanel) component;
                int index = getComponentZOrder(squarePanel);

                int row;
                int col;
                if (flipBoard) {
                    row = 7 - (index / 8);
                    col = 7 - (index % 8);
                } else {
                    row = index / 8;
                    col = index % 8;
                }

                Square square = board[row][col];
                updateSquare(squarePanel, square);
                squarePanel.setBackground((row + col)
                        % 2 == 0 ? ChessGameTheme.lightSquareColor : ChessGameTheme.darkSquareColor);
            }
            setBackground(ChessGameTheme.panelBorderColor);
            setBorder(getCompoundBorder());
            revalidate();
            repaint();
        });
    }

    private void updateSquare(JPanel squarePanel, Square square) {
        squarePanel.setBorder(null);
        squarePanel.removeAll();
        if (square.isOccupied() && square.getPiece() instanceof King) {
            if (square.getPiece().getColor().equals(Board.WHITE) && game.whiteKingInCheck) {
                Border border = BorderFactory.createLineBorder(ChessGameTheme.checkColor, 5);
                squarePanel.setBorder(border);
            } else if (square.getPiece().getColor().equals(Board.BLACK) && game.blackKingInCheck) {
                Border border = BorderFactory.createLineBorder(ChessGameTheme.checkColor, 5);
                squarePanel.setBorder(border);
            }
        }
        if (square.isOccupied()) {
            String piece = square.getPiece().getColor() + square.getPiece().getClass().getSimpleName();
            ImageIcon icon = ImageAsset.pieceIcons.get(piece);
            if (icon != null) {
                JLabel pieceLabel = new JLabel(icon);
                squarePanel.add(pieceLabel, BorderLayout.CENTER);
            }
        }
        if (showCoordinates) {
            int index = getComponentZOrder(squarePanel);
            int row;
            int col;
            if (flipBoard) {
                row = 7 - (index / 8);
                col = 7 - (index % 8);
            } else {
                row = index / 8;
                col = index % 8;
            }
            JLabel coordinateLabel = new JLabel(coordsInNotation ?
                    ChessGameUtility.convertToChessNotation(row, col) : row + "" + col);
            squarePanel.add(coordinateLabel, BorderLayout.NORTH);
        }
    }

    private class SquareClickListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (!game.gameIsOver) {
                JPanel clickedPanel = (JPanel) e.getSource();
                int index = getComponentZOrder(clickedPanel);

                int row;
                int col;
                if (flipBoard) {
                    row = 7 - (index / 8);
                    col = 7 - (index % 8);
                } else {
                    row = index / 8;
                    col = index % 8;
                }

                Square clickedSquare = game.getBoard().getSquares()[row][col];

                if (selectedSquare == null) {
                    selectedSquare = clickedSquare;
                    fromX = row;
                    fromY = col;
                    clickedPanel.setBackground(ChessGameTheme.selectColor);
                } else {
                    toX = row;
                    toY = col;
                    boolean moveSuccess = game.moveOperation(fromX, fromY, toX, toY);

                    if (moveSuccess) {
                        fromX = fromY = toX = toY = -1;
                        if (window.gameWithBot && !game.gameIsOver) {
                            Timer timer = new Timer(250, (func) -> {
                                window.game.getBot().move();
                                update(game.getBoard().getSquares());
                                window.update();
                            });
                            timer.setRepeats(false);
                            timer.start();
                        }
                    } else {
                        SFX.playSound("illegal.wav", 4);
                        update(game.getBoard().getSquares());
                        window.update();
                    }
                    JPanel selectedPanel = getSquarePanel(selectedSquare);
                    Objects.requireNonNull(selectedPanel).setBackground((selectedSquare.getPosition()[0] + selectedSquare.getPosition()[1])
                            % 2 == 0 ? ChessGameTheme.lightSquareColor : ChessGameTheme.darkSquareColor);
                    selectedSquare = null;
                    window.update();
                }
            }
        }

        private JPanel getSquarePanel(Square square) {
            for (Component component : getComponents()) {
                JPanel squarePanel = (JPanel) component;
                int index = getComponentZOrder(squarePanel);

                int row;
                int col;
                if (flipBoard) {
                    row = 7 - (index / 8);
                    col = 7 - (index % 8);
                } else {
                    row = index / 8;
                    col = index % 8;
                }
                if (row == square.getPosition()[0] && col == square.getPosition()[1]) {
                    return squarePanel;
                }
            }
            return null;
        }
    }

    private void addSquareMouseListeners() {
        Component[] squares = getComponents();
        for (Component square : squares) {
            JPanel squarePanel = (JPanel) square;
            squarePanel.addMouseListener(new ChessBoardPanel.SquareClickListener());
        }
    }

    private CompoundBorder getCompoundBorder() {
        Border lineBorder = BorderFactory.createLineBorder(ChessGameTheme.panelBorderColor, 6);
        Border emptyBorder = BorderFactory.createEmptyBorder(0, 2, 0, 2);
        return new CompoundBorder(emptyBorder, lineBorder);
    }

}
