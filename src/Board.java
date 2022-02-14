public class Board {

    private final int DIM;
    private final int DISK;
    private final int ORDER;
    private final char[][] grid;

    private final char HUMAN = 'X';
    private final char COMPUTER = 'O';

    public Board(int dim, int disk, int order) {
        this.DIM = dim;
        this.DISK = disk;
        this.ORDER = order;
        this.grid = new char[DIM][DIM];
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
            System.out.print("  " + i + "\t");
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

        //checking diagonally left top to right bottom


        return false;
    }


}
