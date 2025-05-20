package chess.gui;

import chess.board.ChessGame;
import chess.utility.CmdHandler;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class InfoTabPanel extends JPanel {

    private ChessGame game;
    private ChessBoardPanel chessBoardPanel;
    private ChessGameGUI window;
    public CmdHandler cmdHandler;

    private JTextArea consoleTab;
    private JTextArea infoTab;
    private DefaultCaret infoTabCaret;
    private DefaultCaret consoleTabCaret;
    private JScrollPane infoTabScrollPane;
    private JScrollPane consoleScrollPane;
    public JTextField inputField;
    private List<JPanel> separatorList;
    private StringBuilder infoLog;
    private StringBuilder consoleLog;
    private StringBuilder captureLog;
    public boolean infoTabAutoScroll;
    public boolean generateFen;

    public InfoTabPanel(ChessGame game, ChessBoardPanel chessBoardPanel, ChessGameGUI gui) {
        this.game = game;
        this.chessBoardPanel = chessBoardPanel;
        this.window = gui;
        captureLog = new StringBuilder();
        consoleLog = new StringBuilder();
        cmdHandler = new CmdHandler(gui, chessBoardPanel, this);
        separatorList = new LinkedList<>();
        infoTabAutoScroll = true;
        generateFen = false;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ChessGameTheme.panelBorderColor);

        initializeInputField();
        initializeConsole();
        initializeInfoTab();

        consoleScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        infoTabScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        infoTabCaret = (DefaultCaret) infoTab.getCaret();
        infoTabCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        add(getSeparator("GAME INFO", 25));
        add(infoTabScrollPane);
        add(getSeparator("", 6));
        JPanel consoleTitleAndCopyFenButton = getSeparator("CONSOLE", 20);
        consoleTitleAndCopyFenButton.add(getCopyFenButton(), BorderLayout.EAST);
        add(consoleTitleAndCopyFenButton);
        add(getSeparator("", 6));
        add(consoleScrollPane, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
        setBorder(getCompoundBorder());
        setPreferredSize(new Dimension(320, 600));
    }

    public void attachGame(ChessGame game) {
        this.game = game;
    }

    public void update() {
        updateInfoTab();
        setBackground(ChessGameTheme.panelBorderColor);
        this.setBorder(getCompoundBorder());
        updateSeparator();
        consoleTab.setBackground(ChessGameTheme.textAreaColor);
        infoTab.setBackground(ChessGameTheme.textAreaColor);
        consoleScrollPane.setBackground(ChessGameTheme.textAreaColor);
        infoTabScrollPane.setBackground(ChessGameTheme.textAreaColor);
        consoleTab.setText(consoleLog.toString());

        infoTab.setText(infoLog.toString());
        inputField.setBackground(ChessGameTheme.lightSquareColor);
        inputField.setForeground(Color.BLACK);
    }

    public void initializeInfoTab() {
        infoTab = new JTextArea();
        infoTab.setEditable(false);
        infoTab.setLineWrap(true);
        infoTab.setWrapStyleWord(true);
        infoTab.setBackground(ChessGameTheme.textAreaColor);
        infoTab.setForeground(Color.WHITE);
        infoTab.setFont(ChessGameTheme.defaultFont);
        infoTab.setText("");

        infoTabScrollPane = new JScrollPane(infoTab);
        infoTabScrollPane.setBackground(ChessGameTheme.textAreaColor);
        infoTabScrollPane.setPreferredSize(new Dimension(320, 200));
    }

    public void initializeConsole() {
        consoleTab = new JTextArea();
        consoleTab.setEditable(false);
        consoleTab.setLineWrap(true);
        consoleTab.setWrapStyleWord(true);
        consoleTab.setBackground(ChessGameTheme.textAreaColor);
        consoleTab.setForeground(Color.WHITE);
        consoleTab.setFont(ChessGameTheme.defaultFont);
        clearConsoleLog();
        consoleScrollPane = new JScrollPane(consoleTab);
        consoleScrollPane.setBackground(ChessGameTheme.textAreaColor);
        consoleScrollPane.setPreferredSize(new Dimension(320, 300));
    }

    public void initializeInputField() {
        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(320, 20));
        inputField.setBackground(ChessGameTheme.lightSquareColor);
        inputField.setForeground(Color.BLACK);
        inputField.setFont(ChessGameTheme.defaultFont);

        inputField.addActionListener(e -> {
            String userInput = inputField.getText().trim();
            cmdHandler.handleCommandLine(userInput);
            inputField.setText("");
        });
    }

    private void updateInfoTab() {
        infoLog = new StringBuilder();

        infoLog.append("Halfmoves: ").append(game.halfmoves).append("; Moves: ").append((game.halfmoves / 2) + 1);

        if (game.turn) infoLog.append(" - White's turn\n\n");
        else infoLog.append(" - Black's turn\n\n");

        infoLog.append("WHITE + ").append(game.whitePoints).append("\n");
        infoLog.append("BLACK - ").append(game.blackPoints).append("\n");
        infoLog.append("OVERALL: ").append((game.whitePoints - game.blackPoints > 0 ? "+" : ""))
                .append(game.whitePoints - game.blackPoints).append("\n\n");

        if (game.gameIsOver) {
            infoLog.append(game.getGameOverState()).append("\n\n");
        }

        if (generateFen) {
            infoLog.append("FEN:\n").
                    append(window.game.currentFEN).append("\n\n");
        }

        infoLog.append("CAPTURED:\n");
        if (game.capturedInfo != null && !game.capturedInfo.equals("none")) {
            captureLog.append(game.capturedInfo).append("\n");
            game.capturedInfo = "none";
        }

        infoLog.append(captureLog.toString());
    }

    private void updateSeparator() {
        for (JPanel separator : separatorList) {
            separator.setBackground(ChessGameTheme.panelBorderColor);
            separator.getComponent(0).setForeground(ChessGameTheme.selectColor);
        }
    }

    private JPanel getSeparator(String titleString, int size) {
        JPanel separator = new JPanel();
        separator.setLayout(new BorderLayout());
        separator.setPreferredSize(new Dimension(320, size));
        JLabel title = new JLabel(titleString);
        title.setFont(ChessGameTheme.defaultBoldFont);
        title.setForeground(ChessGameTheme.selectColor);
        separator.add(title);
        separator.setBackground(ChessGameTheme.panelBorderColor);
        separatorList.add(separator);
        return separator;
    }

    private CompoundBorder getCompoundBorder() {
        Border lineBorder = BorderFactory.createLineBorder(ChessGameTheme.panelBorderColor, 6);
        Border emptyBorder = BorderFactory.createEmptyBorder(0, 6, 7, 6);
        return new CompoundBorder(emptyBorder, lineBorder);
    }

    public void updateConsoleLog(String content) {
        consoleLog.append(content).append("\n");
        int lineCount = consoleLog.toString().split("\n").length;
        if (lineCount > 25) {
            int indexToRemove = consoleLog.indexOf("\n", consoleLog.indexOf("\n") + 1);
            consoleLog.delete(0, indexToRemove + 1);
        }
    }

    public void clearConsoleLog() {
        consoleLog = new StringBuilder("> /cmd for list of commands\n");
    }

    public void clearCaptureLog() {
        captureLog = new StringBuilder();
    }


    private JButton getCopyFenButton() {
        JButton copyButton = new JButton("Copy FEN to Clipboard");
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String textToCopy = game.currentFEN;
                StringSelection selection = new StringSelection(textToCopy);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, null);
            }
        });
        copyButton.setPreferredSize(new Dimension(150, 10));
        copyButton.setFont(new Font("Helvetica", Font.BOLD, 10));
        return copyButton;
    }

}
