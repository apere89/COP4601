import java.io.IOException;
import java.util.Scanner;

public class ConnectM {

    private final Board board;

    public ConnectM(int dim, int disk, int order) {
        board = new Board(dim, disk, order);
    }

    public static void main(String[] args) {

        if (!validateArgs(args)) {
            usage();
            System.exit(1);
        }

        try {
            gameLoop(args);
        } catch (IOException e) {
            System.out.println("Error in game loop: " + e.getMessage());
        }
    }

    public static void gameLoop(String[] args) throws IOException {

        Scanner in = new Scanner(System.in);
        int turn = 1; //setting turn for human to start
        int move; // store the human move
        Player player=new Player();
        Player computer=new Player();

        ConnectM connectM = new ConnectM(
                Integer.parseInt(args[0]),
                Integer.parseInt(args[1]),
                Integer.parseInt(args[2]));

        connectM.board.init();
        connectM.board.print();

        //setting turn for computer to start
        if (connectM.board.getOrder() == 0) {
            turn = 2;
        }

        while (true) {
         //   clearScreen();
            if (turn == 1) {
                System.out.println("Human turn");
                move=player.getMove(connectM.board);
                connectM.board.updateBoard(move,turn);
                connectM.board.print();
                if(connectM.board.checkWinner(turn))
                {
                    System.out.println("You won!");
                    break;
                }
                if(connectM.board.isBoardFull())
                {
                    System.out.println("Board is full, its a draw!");
                    break;
                }
                turn = 2; //passing control to computer
            } else {
                System.out.println("Computer turn");
                move=computer.computerMove(connectM.board);
                connectM.board.updateBoard(move,turn);
                connectM.board.print();
                if(connectM.board.checkWinner(turn))
                {
                    System.out.println("Computer won!");
                    break;
                }
                if(connectM.board.isBoardFull())
                {
                    System.out.println("Board is full, its a draw!");
                    break;
                }
                turn = 1; //passing control to human
            }

        }
    }

    public static boolean validateArgs(String[] args) {
        if (args == null) { return false; }
        boolean valid = args.length == 3; //validate we get 3 args
        // validate right values in args
        if (valid && !args[0].matches("^\\b([3-9]|1[0])\\b")) { valid = false; }
        if (valid && !args[1].matches("^\\b([1-9]|1[0])\\b")) { valid = false; }
        if (valid && !args[2].matches("^\\b([0-1])\\b")) { valid = false; }
        if (valid && Integer.parseInt(args[1]) > Integer.parseInt(args[0])) { valid = false; }

        return valid;
    }

    public static void usage() {
        System.out.println("Connect4 game. Enter valid inputs to start");
        System.out.println("usage: connectM [dimensions] [disks to connect] [Player or PC turn, 0 or 1]");
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
