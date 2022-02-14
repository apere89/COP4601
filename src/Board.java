public class Board {

  private final int DIM;
  private final int DISK;
  private final int ORDER;
  private final char[][] board;

  private final char PLAYER = 'X';
  private final char COMPUTER = 'O';

  public Board(int dim, int disk, int order) {
    this.DIM = dim;
    this.DISK = disk;
    this.ORDER = order;
    this.board = new char[DIM][DIM];
  }

  public void init() {
    for (int i = 0; i < DIM; i++) {
      for (int j = 0; j < DIM; j++) {
        board[i][j] = '.';
      }
    }
  }

  public void print() {
    for (int i = 0; i < DIM; i++) {
      System.out.print("|");
      for (int j = 0; j < DIM; j++) {
        System.out.print(board[i][j]);
        if (j != DIM-1)
          System.out.print(" ");
      }
      System.out.println("|");
    }
  }

}
