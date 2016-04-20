package odk.checkers;

/**
 * Created by 1 on 18.04.2016.
 */
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.Random;

/* compiled from: single */
class aiThread implements Runnable {
    private board myBoard;
    private checker myGame;
    Thread t = new Thread(this, "AI Thread");
    private Handler uiHandler;

    aiThread(Handler h, checker game, board board) {
        Log.i("-------","aiThread Start");
        this.myBoard = board;
        this.myGame = game;
        this.uiHandler = h;
        this.t.start();
        Log.i("-------", "aiThread End");
    }

    public void run() {
        boolean flag = true;
        while (flag) {
            if (this.myBoard.turn != this.myBoard.playerNum) {
                Bundle b;
                int tmp;
                String s;
                Message msg;
                if (ai(2)) {
                    b = new Bundle();
                    Message m = new Message();
                    b.putString("CHECK", "REFREASH");
                    m.setData(b);
                    this.uiHandler.sendMessage(m);
                    this.myBoard.turn = (this.myBoard.turn % 2) + 1;
                } else {
                    tmp = this.myBoard.turn;
                    this.myBoard.turn = (this.myBoard.turn % 2) + 1;
                    s = String.format("Player %d Can't Move, Player %d Wins!", new Object[]{Integer.valueOf(tmp), Integer.valueOf(this.myBoard.turn)});
                    b = new Bundle();
                    b.putString("1", s);
                    b.putString("CHECK", "GAME_OVER");
                    msg = new Message();
                    msg.setData(b);
                    this.uiHandler.sendMessage(msg);
                }
                if (this.myGame.gameover()) {
                    s = String.format("Player %d Wins!", new Object[]{Integer.valueOf((this.myBoard.playerNum % 2) + 1)});
                    b = new Bundle();
                    b.putString("1", s);
                    b.putString("CHECK", "GAME_OVER");
                    msg = new Message();
                    msg.setData(b);
                    this.uiHandler.sendMessage(msg);
                    flag = false;
                }
                if (!this.myGame.findMoves(this.myBoard.turn)) {
                    tmp = this.myBoard.turn;
                    this.myBoard.turn = (this.myBoard.turn % 2) + 1;
                    s = String.format("Player %d Can't Move, Player %d Wins!", new Object[]{Integer.valueOf(tmp), Integer.valueOf(this.myBoard.turn)});
                    b = new Bundle();
                    b.putString("1", s);
                    b.putString("CHECK", "GAME_OVER");
                    msg = new Message();
                    msg.setData(b);
                    this.uiHandler.sendMessage(msg);
                    flag = false;
                }
            }
        }
    }

    public boolean ai(int turn) {
        Random gen = new Random(System.currentTimeMillis());
        int toCol = -1;
        int toRow = -1;
        int fromCol = -1;
        int fromRow = -1;
        int score = -1336;
        if (!this.myGame.getAIMoves(turn)) {
            return false;
        }
        int i = 8;
        while (i > 0) {
            int j = 1;
            while (j < 9) {
                if (this.myGame.board[i][j].move_score > score) {
                    fromRow = i;
                    fromCol = j;
                    toRow = this.myGame.board[i][j].suggested_row;
                    toCol = this.myGame.board[i][j].suggested_col;
                    score = this.myGame.board[i][j].move_score;
                }
                if (this.myGame.board[i][j].move_score == score && ((this.myGame.board[fromRow][fromCol].isKing() || this.myGame.board[i][j].isKing() || i == fromRow) && gen.nextInt(2) == 1)) {
                    fromRow = i;
                    fromCol = j;
                    toRow = this.myGame.board[i][j].suggested_row;
                    toCol = this.myGame.board[i][j].suggested_col;
                    score = this.myGame.board[i][j].move_score;
                }
                j++;
            }
            i--;
        }
        Log.i("ai()", String.format("ai moving %d %d to %d %d", new Object[]{Integer.valueOf(fromRow), Integer.valueOf(fromCol), Integer.valueOf(toRow), Integer.valueOf(toCol)}));
        this.myGame.sRow = fromRow;
        this.myGame.sCol = fromCol;
        this.myGame.board[fromRow][fromCol].selected = true;
        this.myGame.board[toRow][toCol].move = true;
        Bundle b = new Bundle();
        Message m = new Message();
        b.putString("CHECK", "REFREASH");
        m.setData(b);
        this.uiHandler.sendMessage(m);
        do {
        } while (System.currentTimeMillis() - System.currentTimeMillis() < 850);
        this.myGame.resetHighlighted();
        while (this.myGame.aimove(turn, toRow, toCol)) {
            this.myGame.sRow = toRow;
            this.myGame.sCol = toCol;
            for (i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    if (this.myGame.board[i][j].move) {
                        toRow = i;
                        toCol = j;
                        break;
                    }
                }
            }
            this.myGame.board[this.myGame.sRow][this.myGame.sCol].selected = true;
            this.myGame.board[toRow][toCol].move = true;
            b = new Bundle();
            m = new Message();
            b.putString("CHECK", "REFREASH");
            m.setData(b);
            this.uiHandler.sendMessage(m);
            do {
            } while (System.currentTimeMillis() - System.currentTimeMillis() < 850);
            this.myGame.resetHighlighted();
        }
        return true;
    }
}

