package odk.checkers;

/**
 * Created by 1 on 18.04.2016.
 */
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class message {
    private static final int BUSY = 6;
    private static final int GAME_START = 2;
    private static final int INVALID_MSG = -1;
    private static final int I_QUIT = 4;
    private static final int JOIN_ACK = 1;
    private static final int MOVE = 3;
    private static final int OTHER_QUIT = 5;
    private DatagramSocket clientSocket;
    private int gameId;
    private Handler handler;
    private InetAddress local;
    private int localport;
    private int playerNumber;
    private int port;
    private byte[] receiveData;
    private DatagramPacket receivePacket;
    private byte[] sendData;
    private DatagramPacket sendPacket;
    private InetAddress serverAddress;

    public message(Handler h) {
        Log.i("-------","message");
        this.sendData = new byte[1024];
        this.receiveData = new byte[1024];
        this.handler = h;
        try {
            this.clientSocket = new DatagramSocket();
            this.serverAddress = InetAddress.getByName("98.23.161.9");
            this.port = 10000;
            String sentence = "JOIN_REQ";
            this.sendData = sentence.getBytes();
            this.sendPacket = new DatagramPacket(this.sendData, sentence.length(), this.serverAddress, this.port);
            this.clientSocket.send(this.sendPacket);
            this.receivePacket = new DatagramPacket(this.receiveData, this.receiveData.length);
            this.clientSocket.receive(this.receivePacket);
            sentence = new String(this.receivePacket.getData()).substring(0, this.receivePacket.getLength());
            String[] msg = sentence.split(" ");
            Log.i("RECIEVED", sentence);
            if (whatIsMessage(msg) == JOIN_ACK) {
                this.gameId = Integer.parseInt(msg[JOIN_ACK]);
                this.playerNumber = Integer.parseInt(msg[GAME_START]);
            } else {
                Log.i("message", "ERROR FOR JOIN_ACK");
                System.exit(JOIN_ACK);
            }
            this.clientSocket.receive(this.receivePacket);
            sentence = new String(this.receivePacket.getData()).substring(0, this.receivePacket.getLength());
            msg = sentence.split(" ");
            Log.i("RECIEVED", sentence);
            if (whatIsMessage(msg) != GAME_START) {
                Log.i("message", "ERROR FOR GAME_START");
                System.exit(JOIN_ACK);
            }
        } catch (Exception e) {
            System.exit(JOIN_ACK);
        }
    }

    public message(String s) {
        this.sendData = new byte[1024];
        this.receiveData = new byte[1024];
    }

    public void quit() {
        Object[] objArr = new Object[GAME_START];
        objArr[0] = Integer.valueOf(this.gameId);
        objArr[JOIN_ACK] = Integer.valueOf(this.playerNumber);
        String s = String.format("QUIT %d %d", objArr);
        this.sendData = s.getBytes();
        this.sendPacket = new DatagramPacket(this.sendData, s.length(), this.serverAddress, this.port);
        try {
            this.clientSocket.send(this.sendPacket);
        } catch (IOException ioe) {
            Log.i("send error", ioe.getMessage());
            System.exit(GAME_START);
        }
    }

    public void send(String msg) {
        String s = "MOVE " + String.valueOf(this.gameId) + " " + String.valueOf(this.playerNumber) + " + " + msg;
        this.sendData = s.getBytes();
        this.sendPacket = new DatagramPacket(this.sendData, s.length(), this.serverAddress, this.port);
        try {
            this.clientSocket.send(this.sendPacket);
        } catch (IOException ioe) {
            Log.i("send error", ioe.getMessage());
            System.exit(GAME_START);
        }
    }

    public int whatIsMessage(String[] msg) {
        int rval = INVALID_MSG;
        int id;
        int num;
        if (msg.length == MOVE) {
            id = Integer.parseInt(msg[JOIN_ACK]);
            num = Integer.parseInt(msg[GAME_START]);
            if (msg[0].equals("JOIN_ACK") && id > 0 && (num == JOIN_ACK || num == GAME_START)) {
                return JOIN_ACK;
            }
            if (msg[0].equals("GAME_START") && id == this.gameId && num == this.playerNumber) {
                return GAME_START;
            }
            if (msg[0].equals("Quit") && id == this.gameId && num == this.playerNumber) {
                return I_QUIT;
            }
            if (!msg[0].equals("Quit") || id != this.gameId || num == this.playerNumber) {
                return INVALID_MSG;
            }
            if (num == JOIN_ACK || num == GAME_START) {
                return OTHER_QUIT;
            }
            return INVALID_MSG;
        } else if (msg.length == GAME_START) {
            if (msg[0].equals("BUSY")) {
                return BUSY;
            }
            return INVALID_MSG;
        } else if (msg.length % OTHER_QUIT != MOVE) {
            return INVALID_MSG;
        } else {
            id = Integer.parseInt(msg[JOIN_ACK]);
            num = Integer.parseInt(msg[GAME_START]);
            if (msg[0].equals("MOVE") && id == this.gameId && num != this.playerNumber && (num == JOIN_ACK || num == GAME_START)) {
                rval = MOVE;
            }
            for (int i = I_QUIT; i < msg.length; i += JOIN_ACK) {
                if (i % OTHER_QUIT != MOVE) {
                    int x = Integer.parseInt(msg[i]);
                    if (x < JOIN_ACK || x > 8) {
                        rval = INVALID_MSG;
                    }
                }
            }
            return rval;
        }
    }

    public String receive() {
        try {
            this.clientSocket.receive(this.receivePacket);
        } catch (IOException ioe) {
            Log.i("send error", ioe.getMessage());
            System.exit(GAME_START);
        }
        String sentence = new String(this.receivePacket.getData()).substring(0, this.receivePacket.getLength());
        String[] msg = sentence.split(" ");
        Log.i("RECIEVED", sentence);
        int message = whatIsMessage(msg);
        if (message == I_QUIT) {
            this.clientSocket.close();
        }
        if (message == OTHER_QUIT) {
            Bundle b = new Bundle();
            b.putString("CHECK", "GAME_OVER");
            Object[] objArr = new Object[JOIN_ACK];
            objArr[0] = Integer.valueOf(this.playerNumber);
            b.putString("1", String.format("Other player quit, %d wins", objArr));
            Message m = new Message();
            m.setData(b);
            this.handler.sendMessage(m);
        }
        if (whatIsMessage(msg) != MOVE) {
            return "NOT VALID";
        }
        return sentence;
    }

    public int getPlayerNumber() {
        return this.playerNumber;
    }
}

