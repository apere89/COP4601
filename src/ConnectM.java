import java.io.IOException;
import java.util.Scanner;

public class ConnectM {

    private final Board board;
    private static boolean isMultiplayer = false;

    public ConnectM(int dim, int disk, int order) {
        board = new Board(dim, disk, order);
    }

    //Main program starts here
    public static void main(String[] args) {

        if (!validateArgs(args)) {
            usage();
            System.exit(1);
        }

        try {
            //Multiplayer game
            if (isMultiplayer)
                multiplayerLoop(args);
            else
                //Against the computer
                gameLoop(args);
        } catch (IOException e) {
            System.out.println("Error in game loop: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    // function to play multiplayer game using UDP
    public static void multiplayerLoop(String[] args) throws InterruptedException {
        String address = args[3];
        int port = Integer.parseInt(args[4]);

        boolean isServer = Integer.parseInt(args[2]) == 0;

        ConnectM connectM = new ConnectM(
                Integer.parseInt(args[0]),
                Integer.parseInt(args[1]),
                Integer.parseInt(args[2]));

        if (isServer) {
            Player serverPlayer = new Player();
            GameServer server = new GameServer(address, port, connectM.board, serverPlayer);
            server.start();
            while (!server.shutdown) {
                Thread.sleep(2000);
            }

        } else {
            Player clientPlayer = new Player();
            GameClient client = new GameClient(address, port, connectM.board, clientPlayer);
            client.start();
            while (!client.shutdown) {
                Thread.sleep(2000);
            }
        }
    }

    //function to play against the computer
    public static void gameLoop(String[] args) throws IOException {
        clearScreen();
        Scanner in = new Scanner(System.in);
        int turn = 1; //setting turn for human to start
        int move; // store the human move
        Player player = new Player();
        Player computer = new Player();

        ConnectM connectM = new ConnectM(
                Integer.parseInt(args[0]),
                Integer.parseInt(args[1]),
                Integer.parseInt(args[2]));

        connectM.board.init();
        System.out.println("Initial Board:");
        connectM.board.print();

        //setting turn for computer to start
        if (connectM.board.getOrder() == 0) {
            turn = 2;
        }

        while (true) {

            if (turn == 1) {
                System.out.print("Your turn: ");
                move = player.getMove(connectM.board);
                connectM.board.updateBoard(move, turn);
                connectM.board.print();
                if (connectM.board.checkWinner(turn)) {
                    System.out.println("\nYou Won!!\n");
                    break;
                }
                if (connectM.board.isBoardFull()) {
                    System.out.println("\nBoard is full, its a draw!\n");
                    break;
                }
                turn = 2; //passing control to computer
            } else {
                //System.out.println("Computer turn");
                move = computer.computerMove(connectM.board);
                System.out.println("\nComputer move is: " + move);
                connectM.board.updateBoard(move, turn);
                connectM.board.print();
                if (connectM.board.checkWinner(turn)) {
                    System.out.println("\nComputer Won!!\n");
                    break;
                }
                if (connectM.board.isBoardFull()) {
                    System.out.println("\nBoard is full, its a draw!\n");
                    break;
                }
                turn = 1; //passing control to human
            }

        }
    }

    //function to validate input arguments
    public static boolean validateArgs(String[] args) {
        if (args == null) {
            return false;
        }
        if (args.length != 3 && args.length != 5) {
            return false;
        }

        boolean valid = true;

        // validate right values in args
        if (valid && !args[0].matches("^\\b([3-9]|1[0])\\b")) {
            valid = false;
        }
        if (valid && !args[1].matches("^\\b([1-9]|1[0])\\b")) {
            valid = false;
        }
        if (valid && !args[2].matches("^\\b([0-1])\\b")) {
            valid = false;
        }
        if (valid && Integer.parseInt(args[1]) > Integer.parseInt(args[0])) {
            valid = false;
        }

        if (args.length == 5) {
            if (!valid && args[3].matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
                valid = false;
            }
            if (!valid && args[4].matches(
                    "^((6553[0-5])|(655[0-2][0-9])|(65[0-4][0-9]{2})|(6[0-4][0-9]{3})|([1-5][0-9]{4})|([0-5]{0,5})|([0-9]{1,4}))$")) {
                valid = false;
            }
            if (valid)
                isMultiplayer = true;
        }

        return valid;
    }

    //Function to display the usage
    public static void usage() {
        System.out.println("ConnectM game. Enter valid inputs to start");
        System.out.println("usage: connectM [dimensions] [disks to connect] [Player or PC turn, 0 or 1]");
        System.out.println("usage: connectM [dimensions] [disks to connect] [0 - to start the server , 1 - for the client ][IP] [Port]");

    }

    //function to clear the screen
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}