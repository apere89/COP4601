public class Game {

  private final Board board;

  public Game(int row, int col, int numPlayers) {
    board = new Board(row,col,numPlayers);
  }

  public static void main(String[] args) {

    if (!validateInput(args)) {
      usage();
      System.exit(1);
    }

    Game theGame = new Game(
            Integer.parseInt(args[0]),
            Integer.parseInt(args[1]),
            Integer.parseInt(args[2]));

    theGame.board.initBoard();
    theGame.board.printBoard();
  }

  public static boolean validateInput(String[] args) {
    boolean valid = args.length == 3;

    if (!args[0].matches("^[1-9]\\d*$")) { valid = false; }

    return valid;
  }

  public static void usage() {
    System.out.println("Connect4 game. Enter valid inputs to start");
    System.out.println("usage: connectFour [rows] [col] [players]");
  }
}
