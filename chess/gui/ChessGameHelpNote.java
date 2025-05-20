package chess.gui;

import javax.swing.*;
import java.awt.*;

public class ChessGameHelpNote {

    ChessGameGUI gui;
    private static JFrame cmdFrame;

    public ChessGameHelpNote(ChessGameGUI gui) {
        this.gui = gui;
    }

    public void showNote() {
        JOptionPane.showMessageDialog(gui,
                """
                        NOTE:
                        This is a TEST_BOT build, test them with /bot
                        
                        There is a very high likelihood of bugs
                        cause i'm not that good yet
                                                
                        <- Say hi to her btw
                                                
                        Please help me test the app extensively!
                                                
                        If you spot any bugs, feel free to report them to me!
                                                
                        Thanks, Much appreciated!
                                                
                        Spaghetti coded by Yuk
                        \n""", "Chess Game " + ChessGameGUI.VERSION, JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon(ImageAsset.getImageURL("hare.png")));
    }

    public static void showCmdLines() {
        if (cmdFrame == null || !cmdFrame.isDisplayable()) {
            synchronized (ChessGameHelpNote.class) {
                if (cmdFrame == null || !cmdFrame.isDisplayable()) {
                    cmdFrame = new JFrame("Chess Game " + ChessGameGUI.VERSION);
                    cmdFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    cmdFrame.setIconImage(new ImageIcon(ImageAsset.getImageURL("cbrrunett/white-pawn.png")).getImage());
                    cmdFrame.setSize(500, 600);
                    cmdFrame.setResizable(false);
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    int screenWidth = screenSize.width;
                    int screenHeight = screenSize.height;
                    cmdFrame.setLocation(screenWidth - screenHeight / 2 + screenHeight / 24, screenHeight / 5);

                    JScrollPane scrollPane = getCmdLinesText();
                    cmdFrame.add(scrollPane);
                    cmdFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                            cmdFrame = null;
                        }
                    });
                    cmdFrame.setVisible(true);
                }
            }
        }
    }

    private static JScrollPane getCmdLinesText() {
        JTextArea textArea = new JTextArea("""
                COMMAND LINES:
                Flipping board:     /f      /flip
                Show coordinates:   /c      /coords
                New game:           /ng     /newgame
                Clear console:      /clr    /cls        /clear
                Notation mode:      /n      /notation
                Exit the program:   /q      /quit       /exit
                Toggle sounds:      /m      /mute
                Help:               /h      /help       +[guide]
                
                Theme:              /t      /theme      +[theme]
                Piece set:          /ps     /pieceset   +[set]
                
                Command list:       /cmd    /command
                Yuk's note:         /note   /devnote
                
                Generate FEN:       /gf     /gfen       /generatefen
                
                Load FEN:
                /lf     /lfen       /loadfen            +[FEN]
                Validate FEN:
                /vf     /vfen       /validatefen        +[FEN]
                
                Bot (Test):
                Play against bot:   /bot        +[bot]
                Bot vs Bot:         /bot bot    +[bot]      +[delay_ms]
                
                (Timer WIP)
                Toggle timer:       /timer
                Timer operations:   /ts     /timerstart
                                    /tp     /timerpause
                                    /tr     /timerreset
                                    /tset   /timerset
                """);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        textArea.setBackground(Color.DARK_GRAY);
        textArea.setForeground(Color.WHITE);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        return scrollPane;
    }

}
