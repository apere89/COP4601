public class Board {

  private final int ROW;
  private final int COL;
  private final int NUMPLAYERS;
  private final char[][] board;

  private final char PLAYER = 'X';
  private final char COMPUTER = 'O';

  public Board(int row, int col, int numPLayers) {
    this.ROW = row;
    this.COL = col;
    this.NUMPLAYERS = numPLayers;
    this.board = new char[row][col];
  }

  public void initBoard() {
    for (int i = 0; i < ROW; i++) {
      for (int j = 0; j < COL; j++) {
        board[i][j] = '.';
      }
    }
  }

  public void printBoard() {
    for (int i = 0; i < ROW; i++) {
      System.out.print("|");
      for (int j = 0; j < COL; j++) {
        System.out.print(board[i][j]);
        if (j != COL-1)
          System.out.print(" ");
      }
      System.out.println("|");
    }
  }

}
