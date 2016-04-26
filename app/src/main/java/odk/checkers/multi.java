package odk.checkers;

/**
 * Created by 1 on 18.04.2016.
 */
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class multi extends Activity {
    board myBoard;
    checker myGame;
    int sx1;
    int sx2;
    int sy1;
    int sy2;
    int xOff;
    int xTotal;
    int yOff;
    int yTotal;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("-------", "multiStart");
        getWindow().setFlags(1024, 1024);
        requestWindowFeature(1);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.myGame = new checker(1);
        this.myGame.clear();
        this.myGame.setup();
        int y = metrics.heightPixels;
        int x = metrics.widthPixels;
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
        message sender = new message("single player");
        Handler h = new Handler() {
            public void handleMessage(Message msg) {
                Log.i("------------", "handleMessage Start");
                String text = msg.getData().getString("1");
                String whatToDo = msg.getData().getString("CHECK");
                if (whatToDo.equals("GAME_OVER")) {
                    new Builder(multi.this).setMessage(text).setPositiveButton("Ok", new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            multi.this.finish();
                        }
                    }).show();
                } else if (whatToDo.equals("REFREASH")) {
                    multi.this.myBoard.invalidate();
                } /*else if (whatToDo.equals("SERVER_BUSY") && whatToDo.equals("GAME_OVER")) {
                    new Builder(multi.this).setMessage("Server is currently busy").setPositiveButton("Ok", new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            multi.this.finish();
                        }
                    }).show();
                }*/
                Log.i("------------", "handleMessage End");
            }
        };

        this.myBoard = new board(this, this.myGame, x, y, false, false, sender, h);
        //aiThread com_bac_checkers_aiThread = new aiThread(h, this.myGame, this.myBoard);
        RelativeLayout mlayout = new RelativeLayout(this);
        mlayout.addView(this.myBoard);
        setContentView(mlayout);
        this.myBoard.requestFocus();
    }

    public View IntitializeView(int layoutId) {
        Log.i("---------","Multi InitialView");
        View view = LayoutInflater.from(this).inflate(layoutId, null);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        return view;
    }

}

