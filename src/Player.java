import java.util.Scanner;

public class Player {

    //Function to get move from the human
    public int getMove(Board board) {
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter your move between 0-" + (board.getDim() - 1) + " and press Q/q to quit:");
        if (!in.hasNextInt())
            System.exit(1);
        int move = in.nextInt();
        if (move < 0 || move > board.getDim() - 1 || (board.getGridVal(0, move) != ' ')) {
            System.out.println("Invalid move!");
            move = getMove(board);
        }
        return move;
    }

    //Function to check if there is a winner
    public boolean winner_check(Board board) {

        return (board.checkWinner(1) == true || board.checkWinner(2) == true);

    }

    //Function to get move from computer
    public int computerMove(Board board) {
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        double alpha_temp;
        int move = -1;
        int depth = 4;
        for (int i = 0; i < board.getDim(); i++) {
            Board temp_board = new Board(board); //using a copy constructor to clone
            if (temp_board.getGridVal(0, i) == ' ') //check if its valid column
            {
                temp_board.updateBoard(i, 2);
                alpha_temp = minvalue(temp_board, depth - 1, alpha, beta);
                if (alpha_temp > alpha) {
                    alpha = alpha_temp;
                    move = i;
                }
            }
        }
        //System.out.println("move "+ move);
        return move;
    }

    //Function to get beta value
    public double minvalue(Board board, int depth, double alpha, double beta) {
        if (depth == 0 || winner_check(board)) {
            return heuristic_function(board);
        }

        for (int i = 0; i < board.getDim(); i++) {
            Board temp_board = new Board(board);
            if (temp_board.getGridVal(0, i) == ' ') //check if its valid column
            {
                temp_board.updateBoard(i, 1);
                beta = Math.min(beta, maxvalue(temp_board, depth - 1, alpha, beta));
                if (alpha >= beta) {
                    return Double.NEGATIVE_INFINITY;
                }

            }

        }
        return beta;
    }

    //Function to get alpha value
    public double maxvalue(Board board, int depth, double alpha, double beta) {
        if (depth == 0 || winner_check(board)) {
            return heuristic_function(board);
        }

        for (int i = 0; i < board.getDim(); i++) {
            Board temp_board = new Board(board);
            if (temp_board.getGridVal(0, i) == ' ') //check if its valid column
            {
                temp_board.updateBoard(i, 2);
                alpha = Math.max(alpha, minvalue(temp_board, depth - 1, alpha, beta));
                if (alpha >= beta) {
                    return Double.POSITIVE_INFINITY;
                }

            }

        }
        return alpha;

    }

    //Heuristic Evaluation function
    public double heuristic_function(Board board) {
        double eval = 0;
        int self_win = 0;
        int opp_win = 0;
        self_win = calc_winning_chances(board, 'O'); //computer
        opp_win = calc_winning_chances(board, 'X');//human

        eval =(double) (self_win - opp_win);

        if (winner_check(board)) {
            if (board.checkWinner(2)) {
                eval = 100000000000000.0;  //if AI is winning, we maximize the eval
            } else if (board.checkWinner(1)) {
                eval =-100000000000000.0; //if AI is losing, we minimize the eval
            }
        }
        // board.print();
        //System.out.println("self winning moves: " + self_win);
        // System.out.println("opp winning moves: " + opp_win);
        //  System.out.println("evaluation: " + eval);

        return eval;
    }

    //Function to calculate the winning chances in a given board for a given player
    public int calc_winning_chances(Board board, char val) {

        int win_count = 0;
        int count = 0;
        int row,col;
        //Checking horizontally
        for (int i = 0; i < board.getDim(); i++) {
            for (int j = 0; j < board.getDim() -1; j++) {
                for (int k = j; k < board.getDim(); k++) {

                    if (board.getGridVal(i, k) == ' ' || board.getGridVal(i, k) == val) {
                        count++;
                    } else {
                        break;
                    }

                    if (count == board.getDisk()) {
                        win_count++;
                        break;
                    }
                }
                count = 0;
            }
        }

        //Checking vertically
        count = 0;
        for (int i = 0; i < board.getDim()-1 ; i++) {
            for (int j = 0; j < board.getDim(); j++) {
                for (int k = i; k < board.getDim(); k++) {
                    if (board.getGridVal(k, j) == ' ' || board.getGridVal(k, j) == val) {
                        count++;
                    } else
                        break;

                    if (count == board.getDisk()) {
                        win_count++;
                        break;

                    }

                }
                count = 0;
            }
        }

        //checking diagonally top left to bottom right

        count = 0;
        for (int i = 0; i < board.getDim(); i++) {
            for (int j = 0; j < board.getDim(); j++) {
                for (row = i,col=j;( row < board.getDim() && col < board.getDim());row++,col++) {
                    if (board.getGridVal(row, col) == ' ' || board.getGridVal(row, col) == val) {
                        count++;
                        if (count == board.getDisk()) {
                            win_count++;
                        }
                    } else {
                        count = 0;
                    }
                }
                count = 0;
            }

        }

        //checking diagonally top right to bottom left
        count = 0;
        for (int i = board.getDim() - 1; i >= 0; i--) {
            for (int j = board.getDim() - 1; j >= 0; j--) {
                for (row = i,col=j; (row >= 0 && col < board.getDim());row--,col++) {
                    if (board.getGridVal(row, col) == ' ' || board.getGridVal(row, col) == val) {
                        count++;
                        if (count == board.getDisk()) {
                            win_count++;
                        }
                    } else {
                        count = 0;
                    }
                }
                count = 0;
            }

        }


        return win_count;
    }


}
