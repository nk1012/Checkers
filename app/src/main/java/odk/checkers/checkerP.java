package odk.checkers;

import android.util.Log;

/**
 * Created by 1 on 18.04.2016.
 */
public class checkerP {
    public int col_location;
    private boolean king;
    public boolean move;
    public int move_score;
    public int oldCol;
    public int oldRow;
    public final int player;
    public int row_location;
    public boolean selected;
    public final char square;
    public int suggested_col;
    public int suggested_row;

    //
    public checkerP(int x, int r, int c) {
       // Log.i("-------", "checkerP Start");
        this.move = false;
        this.selected = false;
        this.row_location = r;
        this.col_location = c;
        this.suggested_row = -1;
        this.suggested_col = -1;
        this.move_score = -1337;
        if (x == 1) {
            this.square = 'r';
            this.player = x;
            this.king = false;
        } else if (x == 2) {
            this.square = 'r';
            this.player = x;
            this.king = false;
        } else if (x == -1) {
            this.square = 'r';
            this.player = x;
            this.king = false;
        } else {
            this.square = 'r';
            this.player = 0;
            this.king = false;
            Log.i("-------","ERROR: invalid build.");
            System.exit(2);
        }
       // Log.i("-------", "checkerP End");
    }

    //
    public checkerP(char c) {
        this.move = false;
        this.selected = false;
        this.move_score = -1337;
        if (c == 'r') {
            this.player = 0;
            this.square = c;
            this.king = false;
        } else if (c == 'b') {
            this.square = c;
            this.player = 0;
            this.king = false;
        } else {
            this.square = c;
            this.player = 0;
            this.king = false;
            System.out.println("ERROR: invalid build.");
            System.exit(2);
        }
    }

    //является шашка дамкой или нет
    public boolean isKing() {
        return this.king;
    }

    //вызывается когда шашка становится дамкой
    public void crown() {
        this.king = true;
        if (this.player != 1 && this.player != 2) {
            System.out.print("ERROR: Can't king this piece.");
            System.exit(1);
        }
    }
}
