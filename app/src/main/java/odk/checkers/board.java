package odk.checkers;

/**
 * Created by 1 on 18.04.2016.
 */
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import java.lang.reflect.Array;

public class board extends View implements OnTouchListener {
    boolean again = false;
    private boolean aiFlag;
    Bitmap background;
    Paint blackSquare;
    private Handler gameoverHandler;
    Button hide;
    boolean isHidden = false;
    boolean move = false;
    private String moveMessage = new String();
    private boolean multiplayerFlag;
    private Context myContext;
    checker myGame;
    message mySender;
    Bitmap p1Piece;
    Bitmap p1king;
    Bitmap p2Piece;
    Bitmap p2king;
    int playerNum;
    Bitmap possiblePiece;
    Paint redSquare;
    Bitmap selectedGraphic;
    Rect[][] squares = ((Rect[][]) Array.newInstance(Rect.class, new int[]{10, 10}));
    int sx1;
    int sx2;
    int sy1;
    int sy2;
    int turn;
    int xOff;
    int xTotal;
    int yOff;
    int yTotal;

    public board(Context context, checker game, int x, int y, boolean flag, boolean mult, message sender, Handler h) {
        super(context);
        Log.i("-------", "boardClass Start");
        this.mySender = sender;
        this.aiFlag = flag;
        this.gameoverHandler = h;
        this.myContext = context;
        this.multiplayerFlag = mult;
        if (flag) {
            this.mySender = sender;
        }
        this.xTotal = x;
        this.yTotal = y;
        int i;
        if (x < y) {
            i = x / 10;
            this.xOff = i;
            this.yOff = i;
        } else {
            i = y / 10;
            this.xOff = i;
            this.yOff = i;
        }
        this.sx1 = (x - (this.xOff * 10)) / 2;
        this.sx2 = this.sx1 + (this.xOff * 10);
        this.sy1 = (y - (this.yOff * 10)) / 2;
        this.sy2 = this.sy1 + (this.yOff * 10);
        this.turn = 1;
        this.myGame = game;
        this.redSquare = new Paint();
        this.redSquare.setAntiAlias(true);
        this.redSquare.setColor(-1);
        this.blackSquare = new Paint();
        this.blackSquare.setAntiAlias(true);
        this.blackSquare.setColor(-16777216);
        for (int i2 = 0; i2 < this.squares.length; i2++) {
            for (int j = 0; j < this.squares[i2].length; j++) {
                this.squares[i2][j] = new Rect();
                this.squares[i2][j].set(this.sx1 + (this.xOff * i2), this.sy1 + (this.yOff * j), this.sx1 + (this.xOff * (i2 + 1)), this.sy1 + (this.yOff * (j + 1)));
            }
        }
        Resources res = getResources();

        //заставка
        this.background = BitmapFactory.decodeResource(res, R.drawable.background);
        this.background = Bitmap.createScaledBitmap(this.background, this.xTotal, this.yTotal, true);

        //игрока шашки
        this.p1Piece = BitmapFactory.decodeResource(res, R.drawable.black);
        this.p1Piece = Bitmap.createScaledBitmap(this.p1Piece, this.xOff, this.yOff, true);

        //куда можно ходить
        this.possiblePiece = BitmapFactory.decodeResource(res, R.drawable.move);
        this.possiblePiece = Bitmap.createScaledBitmap(this.possiblePiece, this.xOff, this.yOff, true);
        //компа шашки
        this.p2Piece = BitmapFactory.decodeResource(res, R.drawable.white);
        this.p2Piece = Bitmap.createScaledBitmap(this.p2Piece, this.xOff, this.yOff, true);

        //выбраная шашка
        this.selectedGraphic = BitmapFactory.decodeResource(res, R.drawable.selected);
        this.selectedGraphic = Bitmap.createScaledBitmap(this.selectedGraphic, this.xOff, this.yOff, true);

        //дамка игрока
        this.p1king = BitmapFactory.decodeResource(res, R.drawable.blackd1);
        this.p1king = Bitmap.createScaledBitmap(this.p1king, this.xOff, this.yOff, true);

        //дамка компа
        this.p2king = BitmapFactory.decodeResource(res, R.drawable.whited1);
        this.p2king = Bitmap.createScaledBitmap(this.p2king, this.xOff, this.yOff, true);

        if (isGameMultiplayer()) {
            this.playerNum = sender.getPlayerNumber();
        } else {
            this.playerNum = 1;
        }
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
        Log.i("-------", "boardClass End");
    }

    //отрисовка доски и шашек на ней
    public void onDraw(Canvas canvas) {
        int i;
        int j;
        canvas.drawBitmap(this.background, 0.0f, 0.0f, null);
        Paint p = new Paint();
        p.setAntiAlias(true);
        for (i = 0; i < this.squares.length; i++) {
            for (j = 0; j < this.squares[i].length; j++) {
/*                Log.i("--------------",Integer.toString(i));
                Log.i("--------------",Integer.toString(j));*/
                if (this.myGame.board[i + 1][j + 1].square == 'r') {
                    p.setColor(Color.BLACK);
                } else if (this.myGame.board[i + 1][j + 1].square == 'b') {
                    p.setColor(Color.WHITE);
                } else {
                    System.exit(4);
                }
                canvas.drawRect(this.squares[i][j], p);
            }
        }
        for (i = 1; i < this.myGame.board.length - 1; i++) {
            for (j = 1; j < this.myGame.board[i].length - 1; j++) {
                if (this.myGame.board[i][j].isKing()) {
                    if (this.myGame.board[i][j].player == 1) {
                        canvas.drawBitmap(this.p1king, (float) (this.sx1 + ((j - 1) * this.xOff)), (float) (this.sy1 + ((i - 1) * this.yOff)), null);
                    } else if (this.myGame.board[i][j].player == 2) {
                        canvas.drawBitmap(this.p2king, (float) (this.sx1 + ((j - 1) * this.xOff)), (float) (this.sy1 + ((i - 1) * this.yOff)), null);
                    }
                } else if (this.myGame.board[i][j].player == 1) {
                    canvas.drawBitmap(this.p1Piece, (float) (this.sx1 + ((j - 1) * this.xOff)), (float) (this.sy1 + ((i - 1) * this.yOff)), null);
                } else if (this.myGame.board[i][j].player == 2) {
                    canvas.drawBitmap(this.p2Piece, (float) (this.sx1 + ((j - 1) * this.xOff)), (float) (this.sy1 + ((i - 1) * this.yOff)), null);
                }
                if (this.myGame.board[i][j].selected) {
                    canvas.drawBitmap(this.selectedGraphic, (float) (this.sx1 + ((j - 1) * this.xOff)), (float) (this.sy1 + ((i - 1) * this.yOff)), null);
                }
                if (this.myGame.board[i][j].move) {
                    canvas.drawBitmap(this.possiblePiece, (float) (this.sx1 + ((j - 1) * this.xOff)), (float) (this.sy1 + ((i - 1) * this.yOff)), null);
                }
            }
        }
    }

    public boolean isGameMultiplayer() {
        return this.multiplayerFlag;
    }

    public boolean iscpuPlaying() {
        return this.aiFlag;
    }

    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() != 0) {
            return super.onTouchEvent(event);
        }
        if ((isGameMultiplayer() || iscpuPlaying()) && this.playerNum != this.turn) {
            return super.onTouchEvent(event);
        }
        float x = event.getX();
        float y = event.getY();
        if (x >= ((float) this.sx1) && y >= ((float) this.sy1) && x <= ((float) this.sx2) && y <= ((float) this.sy2)) {
            int col = ((int) ((x - ((float) this.sx1)) / ((float) this.xOff))) + 1;
            int row = ((int) ((y - ((float) this.sy1)) / ((float) this.yOff))) + 1;
            if (this.myGame.board[row][col].selected && !this.again) {
                this.myGame.board[row][col].selected = false;
                this.move = false;
                this.myGame.resetHighlighted();
            } else if (this.myGame.board[row][col].move) {
                if (isGameMultiplayer()) {
                    this.moveMessage += String.format("%d %d %d %d", new Object[]{Integer.valueOf(this.myGame.sRow), Integer.valueOf(this.myGame.sCol), Integer.valueOf(row), Integer.valueOf(col)});
                }
                if (this.myGame.movePiece(this.turn, row, col)) {
                    this.again = true;
                    this.move = true;
                    this.moveMessage += " + ";
                } else {
                    this.move = false;
                    this.again = false;
                    if (isGameMultiplayer()) {
                        this.mySender.send(this.moveMessage);
                        this.moveMessage = new String();
                    }
                    String s;
                    Bundle b;
                    Message msg;
                    if (this.myGame.gameover()) {
                        s = String.format("Game Over Player %d Wins!", new Object[]{Integer.valueOf(this.turn)});
                        b = new Bundle();
                        b.putString("1", s);
                        b.putString("CHECK", "GAME_OVER");
                        msg = new Message();
                        msg.setData(b);
                        this.gameoverHandler.sendMessage(msg);
                        return true;
                    }
                    this.turn = (this.turn % 2) + 1;
                    if (!this.myGame.findMoves(this.turn)) {
                        int tmp = this.turn;
                        this.turn = (this.turn % 2) + 1;
                        s = String.format("Player %d Can't Move, Player %d Wins!", new Object[]{Integer.valueOf(tmp), Integer.valueOf(this.turn)});
                        b = new Bundle();
                        b.putString("1", s);
                        b.putString("CHECK", "GAME_OVER");
                        msg = new Message();
                        msg.setData(b);
                        this.gameoverHandler.sendMessage(msg);
                        return true;
                    }
                }
            } else if (this.turn == this.myGame.board[row][col].player && !this.again) {
                if (this.myGame.genChoices(this.turn, row, col)) {
                    this.myGame.resetHighlighted();
                }
                if (this.myGame.genChoices(this.turn, row, col)) {
                    this.myGame.board[row][col].selected = true;
                    this.myGame.sRow = row;
                    this.myGame.sCol = col;
                    this.move = true;
                } else {
                    this.move = false;
                }
            }
        }
        invalidate();
        return true;
    }

    private boolean gameIsMultiplayer() {
        return false;
    }

    public void refreshUI() {
        invalidate();
    }
}
