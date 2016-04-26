package odk.checkers;

/**
 * Created by 1 on 18.04.2016.
 */

import android.util.Log;
import java.lang.reflect.Array;
import java.util.Random;

public class checker {
    public checkerP[][] board = ((checkerP[][]) Array.newInstance(checkerP.class, new int[]{12, 12}));

    // возможные ходы (4 - т.к. максимум ходов может быть 4) (2 - т.к это позиция клетки в которую делается ход)
    private int[][] choices = ((int[][]) Array.newInstance(Integer.TYPE, new int[]{4, 2}));
    //количетво возможных ходов
    private int numOfChoices = 0;
    private final int colSize = 12;
    private int count1 = 15; //начальное количетсво шашек 1 игрока
    private int count2 = 15; //начальное количетсво шашек 2 игрока

    private int playerNumber; //номер игрока
    private final int rowSize = 12;
    public int sCol;//устанавливается когда игрок нажимает на шашку (т.е. столбец выбранной шашки)
    public int sRow;//устанавливается когда игрок нажимает на шашку (т.е. строка выбранной шашки)

    public checker(int num) {
        Log.i("-------","checker Start");
        if (num < 1 || num > 2) {
            System.exit(1);
        }
        this.playerNumber = num;
        Log.i("-------","checker End");
    }

    //создает клетки без шашек
    public void clear() {
       //создает 0 и 11 строку и столбец
        int i = 0;
        while (i < this.board.length) {
            int j = 0;
            while (j < this.board[i].length) {
                if (i == 0 || i == this.board.length - 1 || j == 0 || j == this.board.length - 1) {
                    this.board[i][j] = new checkerP(-1, -1, -1);
                }
                j++;
            }
            i++;
        }
        // просто создает клетки и задает их цвет
        for (i = 1; i < this.board.length - 1; i++) {
            int j = 1;
            while (j < this.board[i].length - 1) {
                if (i % 2 == 1 && j % 2 == 0) {
                    this.board[i][j] = new checkerP('r');
                } else if (i % 2 == 0 && j % 2 == 1) {
                    this.board[i][j] = new checkerP('r');
                } else {
                    this.board[i][j] = new checkerP('b');
                }
                j++;
            }
        }
    }

    // вызывается при нажатии на шашку
    public void resetHighlighted() {
        Log.i("-------","checkerResetHighlighted Start");
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                checkerP com_bac_checkers_checkerP = this.board[i][j];
                this.board[i][j].move = false;
                com_bac_checkers_checkerP.selected = false;
            }
        }
    }


    // первоначально устанавливаются шашки
    public void setup() {
        int i;

        for (i = 1; i < 4; i++) {
            int j = 1;
            while (j < this.board[i].length - 1) {
                if (i % 2 == 1 && j % 2 == 0) {
                    this.board[i][j] = new checkerP((this.playerNumber % 2) + 1, i, j);
                } else if (i % 2 == 0 && j % 2 == 1) {
                    this.board[i][j] = new checkerP((this.playerNumber % 2) + 1, i, j);
                }
                j++;
            }
        }
        for (i = 8; i < this.board.length - 1; i++) {
            int j = 1;
            while (j < this.board[i].length - 1) {
                if (i % 2 == 0 && j % 2 == 1) {
                    this.board[i][j] = new checkerP(this.playerNumber, i, j);
                } else if (i % 2 == 1 && j % 2 == 0) {
                    this.board[i][j] = new checkerP(this.playerNumber, i, j);
                }
                j++;
            }
        }
    }
//проверяет найден хоть один ход или нет
    public boolean findMoves(int turn) {
        Log.i("-------","checker FindMoves Start");
        boolean rval = false;
        int i = 1;
        while (i < this.board.length) {
            int j = 1;
            while (j < this.board.length) {
                if (this.board[i][j].player == turn && genChoices(turn, i, j)) {
                    rval = true;
                }
                j++;
            }
            i++;
        }
        resetHighlighted();
        Log.i("-------", "checker FindMoves End");
        return rval;
    }

    // находит возможные ходы шашек
    //playerNum - номер игорока который ходит;  r - строка;  c - столбец выбраной клетки
    public boolean genChoices(int playerNum, int r, int c) {
        boolean flag = false;

        //обнуляем все шаги
        for (int i = 0; i < this.choices.length; i++) {
            for (int j = 0; j < this.choices[i].length; j++) {
                this.choices[i][j] = 0;
            }
        }
        this.numOfChoices = 0;
        if (playerNum != this.board[r][c].player) {
            return false;
        }

        // находит ходы для шашек которые находятся снизу на доске (черных) или белой, если шашка является дамкой
        if (playerNum == this.playerNumber || this.board[r][c].isKing()) {

            //проверяет свободна ли ближайшая клетка справа по диагонали
            if (this.board[r - 1][c + 1].player == 0) {
                // если да - увеличиваем кол-во возможных ходов. Записываем этот ход
                flag = true;
                this.board[r - 1][c + 1].move = true;
                this.choices[this.numOfChoices][0] = r - 1;
                this.choices[this.numOfChoices][1] = c + 1;
                this.numOfChoices++;
            }
            // проверяет есть ли в ближайшей справа клетке по диагонали шашка противника и что следующая клетка свободна (т.е. можно срубить шашку противника)
            if (this.board[r - 1][c + 1].player != playerNum && this.board[r - 1][c + 1].player > 0 && this.board[r - 2][c + 2].player == 0) {
                flag = true;
                this.board[r - 2][c + 2].move = true;
                this.choices[this.numOfChoices][0] = r - 2;
                this.choices[this.numOfChoices][1] = c + 2;
                this.numOfChoices++;
            }

            //проверяет свободна ли ближайшая клетка слева по диагонали
            if (this.board[r - 1][c - 1].player == 0) {
                flag = true;
                this.board[r - 1][c - 1].move = true;
                this.choices[this.numOfChoices][0] = r - 1;
                this.choices[this.numOfChoices][1] = c - 1;
                this.numOfChoices++;
            }

            // проверяет есть ли в ближайшей слева клетке по диагонали шашка противника и что следующая клетка свободна (т.е. можно срубить шашку противника)
            if (this.board[r - 1][c - 1].player != playerNum && this.board[r - 1][c - 1].player > 0 && this.board[r - 2][c - 2].player == 0) {
                flag = true;
                this.board[r - 2][c - 2].move = true;
                this.choices[this.numOfChoices][0] = r - 2;
                this.choices[this.numOfChoices][1] = c - 2;
                this.numOfChoices++;
            }
        }

        // если данная шашка не дамка и номер игрока 1(т.е. который играет черными то выходим из этой функции)
        if (playerNum != (this.playerNumber % 2) + 1 && !this.board[r][c].isKing()) {
            return flag;
        }
        //проверяет ходы для второго игрока (верхней части доски) - белые
        //свободна ли справа вниз на 1 клетку подиагонали клетка
        if (this.board[r + 1][c + 1].player == 0) {
            flag = true;
            this.board[r + 1][c + 1].move = true;
            this.choices[this.numOfChoices][0] = r + 1;
            this.choices[this.numOfChoices][1] = c + 1;
            this.numOfChoices++;
        }

        //проверяет есть ли в ближайшей слева клетке по диагонали шашка противника и что следующая клетка свободна (т.е. можно срубить шашку противника)
        if (this.board[r + 1][c + 1].player != playerNum && this.board[r + 1][c + 1].player > 0 && this.board[r + 2][c + 2].player == 0) {
            flag = true;
            this.board[r + 2][c + 2].move = true;
            this.choices[this.numOfChoices][0] = r + 2;
            this.choices[this.numOfChoices][1] = c + 2;
            this.numOfChoices++;
        }

        //свободна ли слева вниз на 1 клетку подиагонали клетка
        if (this.board[r + 1][c - 1].player == 0) {
            flag = true;
            this.board[r + 1][c - 1].move = true;
            this.choices[this.numOfChoices][0] = r + 1;
            this.choices[this.numOfChoices][1] = c - 1;
            this.numOfChoices++;
        }

        //
        if (this.board[r + 1][c - 1].player == playerNum || this.board[r + 1][c - 1].player <= 0 || this.board[r + 2][c - 2].player != 0) {
            return flag;
        }

        //
        this.board[r + 2][c - 2].move = true;
        this.choices[this.numOfChoices][0] = r + 2;
        this.choices[this.numOfChoices][1] = c - 2;
        this.numOfChoices++;
        return true;
    }

    public boolean movePiece(int playerNum, int row, int col) {
        boolean again = false;//меняем клетку в которой шашка и пустую клетку, которые нажал игрок
        checkerP tmp = this.board[row][col];
        this.board[row][col] = this.board[this.sRow][this.sCol];
        this.board[this.sRow][this.sCol] = tmp;//если бъем шашку
        if (row - this.sRow == 2 || this.sRow - row == 2) {
            int r;
            int c;
            if (this.sRow > row) {
                r = row + 1;
            } else {
                r = row - 1;
            }
            if (this.sCol > col) {
                c = col + 1;
            } else {
                c = col - 1;
            }
            this.board[r][c] = new checkerP('r');
            if (playerNum == 1) {
                this.count2--;
            } else {
                this.count1--;
            }
            resetHighlighted();
            if (jumpGen(playerNum, row, col)) {
                this.board[row][col].selected = true;
                this.sRow = row;
                this.sCol = col;
                again = true;
            }
        } else {
            resetHighlighted();
        }//делаем дамками шашки при достижении последних строк доски
        if (row == 1 && this.board[row][col].player == this.playerNumber) {
            this.board[row][col].crown();
        } else if (row == 10 && this.board[row][col].player == (this.playerNumber % 2) + 1) {
            this.board[row][col].crown();
        }
        return again;
    }

    public boolean aimove(int playerNum, int row, int col) {
        boolean again = false;
        checkerP tmp = this.board[row][col];
        this.board[row][col] = this.board[this.sRow][this.sCol];
        this.board[this.sRow][this.sCol] = tmp;
        Log.i("AI", String.format("%d %d", new Object[]{Integer.valueOf(row), Integer.valueOf(col)}));
        Log.i("AI", String.format("%d %d", new Object[]{Integer.valueOf(this.sRow), Integer.valueOf(this.sCol)}));
        if (row - this.sRow == 2 || this.sRow - row == 2) {
            int r;
            int c;
            if (this.sRow > row) {
                r = row + 1;
            } else {
                r = row - 1;
            }
            if (this.sCol > col) {
                c = col + 1;
            } else {
                c = col - 1;
            }
            this.board[r][c] = new checkerP('r');
            if (playerNum == 1) {
                this.count2--;
            } else {
                this.count1--;
            }
            resetHighlighted();
            if (jumpGen(playerNum, row, col)) {
                this.board[row][col].selected = true;
                this.sRow = row;
                this.sCol = col;
                again = true;
            }
        } else {
            resetHighlighted();
        }
        if (row == 1 && this.board[row][col].player == this.playerNumber) {
            this.board[row][col].crown();
        } else if (row == 8 && this.board[row][col].player == (this.playerNumber % 2) + 1) {
            this.board[row][col].crown();
        }
        return again;
    }

    public boolean jumpGen(int playerNum, int r, int c) {
        boolean flag = false;
        if (playerNum == this.playerNumber || this.board[r][c].isKing()) {
            if (this.board[r - 1][c + 1].player > 0 && this.board[r - 1][c + 1].player != playerNum && this.board[r - 2][c + 2].player == 0) {
                flag = true;
                this.board[r - 2][c + 2].move = true;
            }
            if (this.board[r - 1][c - 1].player > 0 && this.board[r - 1][c - 1].player != playerNum && this.board[r - 2][c - 2].player == 0) {
                flag = true;
                this.board[r - 2][c - 2].move = true;
            }
        }
        if (playerNum != (this.playerNumber % 2) + 1 && !this.board[r][c].isKing()) {
            return flag;
        }
        if (this.board[r + 1][c + 1].player > 0 && this.board[r + 1][c + 1].player != playerNum && this.board[r + 2][c + 2].player == 0) {
            flag = true;
            this.board[r + 2][c + 2].move = true;
        }
        if (this.board[r + 1][c - 1].player <= 0 || this.board[r + 1][c - 1].player == playerNum || this.board[r + 2][c - 2].player != 0) {
            return flag;
        }
        this.board[r + 2][c - 2].move = true;
        return true;
    }

    public boolean gameover() {
        if (this.count1 <= 0) {
            System.out.println("White wins!");
            return true;
        } else if (this.count2 > 0) {
            return false;
        } else {
            System.out.println("Black wins!");
            return true;
        }
    }

    public boolean getAIMoves(int pnum) {
        boolean rval = false;
        int enemy = (pnum % 2) + 1;
        Random gen = new Random(System.currentTimeMillis());
        if (pnum < 1 || pnum > 2) {
            Log.i("getAIMoves", "Invalid player number ERROR");
            System.exit(2);
        }
        int i = 1;
        while (i < 9) {
            int j = 1;
            while (j < 9) {
                if (this.board[i][j].player == pnum) {
                    this.board[i][j].move_score = -1337;
                    int lower = 1;
                    if (this.board[i][j].isKing()) {
                        lower = -2;
                    }
                    int x = lower;
                    while (x <= 2) {
                        int y = -2;
                        while (y <= 2) {
                            if (!(x == 0 || y == 0 || ((x != y && (-x) != y) || i + x <= 0 || i + x >= 9 || j + y <= 0 || j + y >= 9))) {
                                int score = calculateMoveScore(i, j, i + x, j + y);
                                int randNum = gen.nextInt(3);
                                if (score > this.board[i][j].move_score || (score == this.board[i][j].move_score && randNum == 0)) {
                                    rval = true;
                                    this.board[i][j].move_score = score;
                                    this.board[i][j].suggested_row = i + x;
                                    this.board[i][j].suggested_col = j + y;
                                }
                            }
                            y++;
                        }
                        x++;
                    }
                }
                j++;
            }
            i++;
        }
        return rval;
    }

    public int calculateMoveScore(int fromRow, int fromCol, int toRow, int toCol) {
        int score = 1;
        int rowChange = toRow - fromRow;
        int colChange = toCol - fromCol;
        Log.i("calculateMoveScore", String.format("%d %d : %d %d : %d %d : %d", new Object[]{Integer.valueOf(fromRow), Integer.valueOf(fromCol), Integer.valueOf(toRow), Integer.valueOf(toCol), Integer.valueOf(rowChange), Integer.valueOf(colChange), Integer.valueOf(this.playerNumber)}));
        int ally = (this.playerNumber % 2) + 1;
        int enemy = this.playerNumber;
        boolean isJump = false;
        if (this.board[toRow][toCol].player != 0) {
            return -1337;
        }
        if (rowChange < -1 || rowChange > 1) {
            isJump = true;
            if (this.board[fromRow + (rowChange / 2)][fromCol + (colChange / 2)].player != enemy) {
                return -1337;
            }
        }
        if ((this.board[fromRow + 1][fromCol + 1].player == enemy &&
                this.board[fromRow - 1][fromCol - 1].player == 0) ||
                (
                        (this.board[fromRow + 1][fromCol - 1].player == enemy
                                &&
                        this.board[fromRow - 1][fromCol + 1].player == 0)
                        ||
                        (
                           (this.board[fromRow - 1][fromCol + 1].player == enemy
                                   &&
                           this.board[fromRow - 1][fromCol + 1].isKing()
                                   &&
                           this.board[fromRow + 1][fromCol - 1].player == 0)

                            ||
                                   (this.board[fromRow - 1][fromCol - 1].player == enemy
                                   &&
                                        this.board[fromRow - 1][fromCol - 1].isKing()
                                   &&
                                   this.board[fromRow + 1][fromCol + 1].player == 0)
                        )
                )
          )
        {
            score = 1 + 1;
        }
        if ((this.board[fromRow + 1][fromCol + 1].player == ally && this.board[fromRow + 2][fromCol + 2].player == enemy) || ((this.board[fromRow + 1][fromCol - 1].player == ally && this.board[fromRow + 2][fromCol - 2].player == enemy) || ((this.board[fromRow - 1][fromCol + 1].player == ally && this.board[fromRow - 2][fromCol + 2].player == enemy && this.board[fromRow - 2][fromCol + 2].isKing()) || (this.board[fromRow - 1][fromCol - 1].player == ally && this.board[fromRow - 2][fromCol - 2].player == enemy && this.board[fromRow - 2][fromCol - 2].isKing())))) {
            score--;
        }
        if (!this.board[fromRow][fromCol].isKing() && toRow == 8) {
            score += 8;
        }
        if (isJump && Math.abs(rowChange) == 2) {
            boolean jumpAgain;
            do {
                score += 3;
                jumpAgain = false;
                if (this.board[(rowChange / 2) + toRow][(colChange / 2) + toCol].player == enemy && this.board[toRow + rowChange][toCol + colChange].player == 0) {
                    jumpAgain = true;
                    toRow += rowChange;
                    toCol += colChange;
                    continue;
                } else if (this.board[(rowChange / 2) + toRow][toCol - (colChange / 2)].player == enemy && this.board[toRow + rowChange][toCol - colChange].player == 0) {
                    jumpAgain = true;
                    toRow += rowChange;
                    toCol -= colChange;
                    continue;
                } else if (this.board[fromRow][fromCol].isKing() && this.board[toRow - (rowChange / 2)][(colChange / 2) + toCol].player == enemy && this.board[toRow - rowChange][toCol + colChange].player == 0) {
                    jumpAgain = true;
                    toRow -= rowChange;
                    toCol += colChange;
                    continue;
                }
            } while (jumpAgain);
        } else if (Math.abs(rowChange) == 1) {
            if (this.board[toRow + rowChange][toCol + colChange].player == enemy) {
                score -= 2;
            } else if (this.board[toRow + rowChange][toCol - colChange].player == enemy && this.board[toRow - rowChange][toCol + colChange].player == 0) {
                score -= 2;
            } else if (this.board[toRow - rowChange][toCol + colChange].player == enemy && this.board[toRow + rowChange][toCol - colChange].player == 0) {
                score -= 2;
            } else {
                if (this.board[toRow + rowChange][toCol + colChange].player == ally && this.board[(rowChange * 2) + toRow][(colChange * 2) + toCol].player == enemy) {
                    score++;
                }
                if (this.board[toRow + rowChange][toCol - colChange].player == ally && this.board[(rowChange * 2) + toRow][toCol - (colChange * 2)].player == enemy) {
                    score++;
                }
                if (this.board[toRow - rowChange][toCol + colChange].player == ally && this.board[toRow - (rowChange * 2)][(colChange * 2) + toCol].player == enemy) {
                    score++;
                }
            }
        }
        return score;
    }
}

