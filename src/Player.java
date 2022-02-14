import java.util.Scanner;

public class Player {

    //Getting move from the human
    public int getMove(Board board) {
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter your move between 0-" + (board.getDim() - 1) +" and press Q/q to quit:");
        if(!in.hasNextInt())
            System.exit(1);
        int move = in.nextInt();
        if (move < 0 || move > board.getDim() - 1 || (board.getGridVal(0,move)!=' ') ) {
            System.out.println("Invalid move!");
            move=getMove(board);
        }

        return move;
    }

    //calculating the move for computer
    public int computerMove(Board board)
    {
        //Added for testing --need to be replaced
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter your move between 0-" + (board.getDim() - 1) +" and press Q/q to quit:");
        if(!in.hasNextInt())
            System.exit(1);
        int move = in.nextInt();
        if (move < 0 || move > board.getDim() - 1 || (board.getGridVal(0,move)!=' ') ) {
            System.out.println("Invalid move!");
            move=getMove(board);
        }

        return move;
    }

}
