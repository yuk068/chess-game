package chess.utility;

import chess.board.ChessGame;

public class ChessTimer {

    private ChessGame game;
    private int blacksSecondsLeft;
    private int whiteSecondsLeft;
    private boolean running;

    public ChessTimer(ChessGame game, int initialTimeInSeconds) {
        this.game = game;
        this.blacksSecondsLeft = initialTimeInSeconds;
        this.whiteSecondsLeft = initialTimeInSeconds;
        this.running = false;
    }

    public void start() {
        running = true;
        Thread timerThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateTimer();
                printTimer();
                if (whiteSecondsLeft <= 0 || blacksSecondsLeft <= 0) {
                    stop();
                    System.out.println((whiteSecondsLeft == 0 ? "BLACK" : "WHITE") + " won on time!");
                }
            }
        });
        timerThread.start();
    }


    public void stop() {
        running = false;
    }

    private void updateTimer() {
        if (game.turn) {
            whiteSecondsLeft--;
        } else {
            blacksSecondsLeft--;
        }
    }

    private void printTimer() {
        int whiteSeconds = whiteSecondsLeft;
        int blackSeconds = blacksSecondsLeft;
        int whiteMinutes = whiteSeconds / 60;
        int blackMinutes = blackSeconds / 60;
        whiteSeconds %= 60;
        blackSeconds %= 60;
        System.out.printf("White: %02d:%02d ; ", whiteMinutes, whiteSeconds);
        System.out.printf("Black: %02d:%02d\n", blackMinutes, blackSeconds);
    }


}
