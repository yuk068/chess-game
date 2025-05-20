package chess.gui;

import chess.board.ChessGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChessGameGUI extends JFrame {

    public static final String VERSION = "TEST_BOT v1.4.0";

    private JPanel mainPanel;
    private ChessBoardPanel chessBoardPanel;
    public InfoTabPanel infoTab;
    public ChessGame game;
    public boolean gameWithBot;
    public boolean botVsBot;
    public Timer botTimer;

    public ChessGameHelpNote note;

    public ChessGameGUI() {
        super("Chess Game " + VERSION);
        ImageAsset.pixelPieces();
        ChessGameTheme.chessDotComTheme();
        botTimer = null;
        gameWithBot = false;
        botVsBot = false;
        mainPanel = new JPanel(new BorderLayout());

        SFX.playSound("game-start.wav", 6);
        setIconImage(new ImageIcon(ImageAsset.getImageURL("cbrrunett/white-pawn.png")).getImage());

        game = new ChessGame();

        note = new ChessGameHelpNote(this);

        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        mainPanel.setBackground(ChessGameTheme.paddingColor);

        chessBoardPanel = new ChessBoardPanel(game, false, this);
        mainPanel.add(chessBoardPanel, BorderLayout.WEST);
        infoTab = new InfoTabPanel(game, chessBoardPanel, this);
        mainPanel.add(infoTab, BorderLayout.EAST);
        chessBoardPanel.attachConsole(infoTab);

        mainPanel.setBorder(BorderFactory.createLineBorder(ChessGameTheme.paddingColor, 20));

        getContentPane().add(mainPanel);

        setVisible(true);
        setLocationRelativeTo(null);
        addHotKeys();
        update();
    }

    public void playGameBotVersusBot(int delayMs) {
        delayMs = Math.min(delayMs, 5000);
        botTimer = new Timer((Math.abs(delayMs) == 0 ? 50 : Math.abs(delayMs)), (e) -> {
            if (!game.gameIsOver) {
                game.getBot().move();
                update();
                game.getAnotherBot().move();
                update();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        botTimer.setRepeats(true);
        botTimer.start();
    }

    public void cycleTheme() {
        ChessGameTheme.cycleTheme();
        update();
    }

    public void attachGame(ChessGame game) {
        this.game = game;
        chessBoardPanel.attachGame(game);
        infoTab.attachGame(game);
        update();
    }

    public void update() {
        mainPanel.setBackground(ChessGameTheme.paddingColor);
        mainPanel.setBorder(BorderFactory.createLineBorder(ChessGameTheme.paddingColor, 20));
        mainPanel.requestFocusInWindow();
        chessBoardPanel.update();
        infoTab.update();
    }

    public void addHotKeys() {
        mainPanel.setFocusable(true);
        mainPanel.requestFocusInWindow();
        mainPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SLASH -> infoTab.inputField.requestFocus();
                    case KeyEvent.VK_T -> cycleTheme();
                }
            }
        });
    }

}

