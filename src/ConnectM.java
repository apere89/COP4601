public class ConnectM {

  private final Board board;

  public ConnectM(int dim, int disk, int order) {
    board = new Board(dim,disk,order);
  }

  public static void main(String[] args) {

    if (!validateInput(args)) {
      usage();
      System.exit(1);
    }

    ConnectM connectM = new ConnectM(
            Integer.parseInt(args[0]),
            Integer.parseInt(args[1]),
            Integer.parseInt(args[2]));

    connectM.board.init();
    connectM.board.print();
  }

  public static boolean validateInput(String[] args) {
    boolean valid = args.length == 3; //validate we get 3 args
    // validate right values in args
    if (!args[0].matches("^\\b([3-9]|1[0])\\b")) { valid = false; }
    if (!args[1].matches("^\\b([1-9]|1[0])\\b")) { valid = false; }
    if (!args[2].matches("^\\b([0-1])\\b")) { valid = false; }
    if (Integer.parseInt(args[1]) > Integer.parseInt(args[0])) { valid = false; }

    return valid;
  }

  public static void usage() {
    System.out.println("Connect4 game. Enter valid inputs to start");
    System.out.println("usage: connectM [dimensions] [disks to connect] [Player or PC turn, 0 or 1]");
  }
}
