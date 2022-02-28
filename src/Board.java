import java.lang.reflect.Array;
import java.util.Arrays;

public class Board {

    private final int DIM;
    private final int DISK;
    private final int ORDER;
    private char[][] grid;
    private int[] height;
    private int totalMoves;

    public static final char HUMAN = 'X';
    public static final char COMPUTER = 'O';

    public Board(int dim, int disk, int order) {
        this.DIM = dim;
        this.DISK = disk;
        this.ORDER = order;
        this.grid = new char[DIM][DIM];
        this.height = new int[DIM];
        this.totalMoves = 0;
    }

    public Board(Board board) {
        this(board.DIM, board.DISK, board.ORDER);
        this.totalMoves = board.totalMoves;
        this.height = board.height.clone();
        for (int i = 0; i < DIM; i++)
            this.grid[i] = board.grid[i].clone();
    }

    public int getDim() {
        return this.DIM;
    }

    public int getDisk() {
        return this.DISK;
    }

    public int getOrder() {
        return this.ORDER;
    }

    public int getGridVal(int row, int col) {
        return grid[row][col];
    }

    public void init() {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                grid[i][j] = ' ';
            }
        }
    }

    public void print() {

        for (int i = 0; i < DIM; i++) {
            System.out.print("  " + i + " ");
        }
        System.out.println("");
        for (int i = 0; i < DIM; i++) {
            printDivider();
            for (int j = 0; j < DIM; j++) {
                System.out.print("| " + grid[i][j]);
                if (j < DIM - 1)
                    System.out.print(" ");
                else
                    System.out.print(" |");
            }
            System.out.println();
            if (i == DIM - 1)
                printDivider();
        }
    }

    private void printDivider() {
        for (int i = 0; i < DIM; i++) {
            System.out.print("+---");
        }
        System.out.println("+");
    }

    //Update the board for both computer and human based on the move
    public void updateBoard(int move, int turn) {
        char val = turn == 1 ? HUMAN : COMPUTER;
        for (int i = DIM - 1; i >= 0; i--) {
            if (grid[i][move] == ' ') {
                grid[i][move] = val;
                break;
            }
        }

    }

    //Function to check if the board is full after a move
    public boolean isBoardFull() {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (grid[i][j] == ' ')
                    return false;
            }
        }
        return true;
    }

    //Function to check if there is a winner
    public boolean checkWinner(int turn) {
        char val = turn == 1 ? HUMAN : COMPUTER;
        int count = 0;

        //Checking horizontally
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (grid[i][j]!=' ' && grid[i][j] == val) {
                    count++;

                    if (count == DISK) {
                        return true;
                    }
                } else
                    count = 0;
            }
            count=0;
        }

        //Checking vertically
        count = 0;
        for (int j = 0; j < DIM; j++) {
            for (int i = 0; i < DIM; i++) {
                if (grid[i][j]!=' ' && grid[i][j] == val) {
                    count++;
                    if (count == DISK) {
                        return true;
                    }
                } else
                    count = 0;
            }
            count=0;
        }

        //checking diagonally top left to bottom right
        //System.out.println("diagonal check");
        int row,col;
        count=0;
        for(int i=0;i<DIM;i++) {
            for(int j=0;j<DIM;j++) {
                row=i;
                col=j;
                while (row < DIM && col < DIM) {

                    if (grid[row][col]!=' ' && grid[row][col] == val) {
                        count++;
                        //System.out.println("count is "+ count + " DISK is "+ DISK);
                        if(count == DISK)
                            return true;
                    }
                    else
                    {
                        count=0;
                    }
                    row++;
                    col++;
                }
                count=0;
            }

        }

        //checking diagonally top right to bottom left
        count=0;
        for(int i=DIM-1;i>=0;i--) {
            for(int j=DIM-1;j>=0;j--) {
                row=i;
                col=j;
                while (row >=0  && col <DIM ) {

                    if (grid[row][col]!=' ' && grid[row][col] == val) {
                        count++;
                        //System.out.println("count is "+ count + " DISK is "+ DISK);
                        if(count == DISK)
                            return true;
                    }
                    else
                    {
                        count=0;
                    }
                    row--;
                    col++;
                }
                count=0;
            }

        }
        return false;
    }

    public boolean goodPlay(int move) {
        return this.height[move] < DIM;
    }

    public void makePlay(int move) {
        char currentMark = 1 + totalMoves%2 == 0 ? 'O' : 'X';

        this.grid[DIM-1-height[move]][move] = currentMark;
        height[move]++;
        totalMoves++;
    }

    public boolean checkWinningMove(int move) {
        char currentMark = 1 + totalMoves%2 == 0 ? 'X' : 'O';

        if (height[move] >= 3
                && grid[move][height[move]-1] == currentMark
                && grid[move][height[move]-2] == currentMark
                && grid[move][height[move]-3] == currentMark)
            return true;

        for(int dy = -1; dy <=1; dy++) {
            int nb = 0;
            for(int dx = -1; dx <=1; dx += 2)
                for(int x = move+dx, y = height[move]+dx*dy; x >= 0 && x < DIM && y >= 0 && y < DIM && grid[x][y] == currentMark; nb++) {
                    x += dx;
                    y += dx*dy;
                }
            if(nb >= 3) return true;
        }
        return false;
    }

    public int getTotalMoves() { return totalMoves; }


}
