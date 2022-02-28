import java.util.Scanner;

public class Player {
  private final Scanner in = new Scanner(System.in);
  private final char type;
  private Long treeCount;

  public Player(char type) {
    this.type = type;
  }

  //Getting move from the human
    public int getMove(Board board) {
        if (type == 'X') // player
          return humanMove(board);
        else // AI
          return computerMove(board);
    }

    public int humanMove(Board board) {
      System.out.println("Please enter your move between 0-" + (board.getDim() - 1) +" and press Q/q to quit:");
      if(!in.hasNextInt())
        System.exit(1);
      int move = in.nextInt();
      if (move < 0 || move > board.getDim() - 1 || (board.getGridVal(0,move)!=' ') ) {
        System.out.println("Invalid move!");
        move=humanMove(board);
      }

      return move;
    }

    //calculating the move for computer
    public int computerMove(Board board)
    {
      this.treeCount = 0L;
      return evaluate(board, -1, 1);
    }

    private int evaluate(Board board, int alpha, int beta) {
      treeCount++;
      System.out.println("current nodes explored: " + treeCount + "\nalpha: " + alpha + "\nbeta: " + beta);

      if (board.getTotalMoves() == board.getDim() * board.getDim()) return 0;

      for(int x = 0; x < board.getDim(); x++)
        if(board.goodPlay(x) && board.checkWinningMove(x))
          return (board.getDim()* board.getDim()+1 - board.getTotalMoves())/2;
      System.out.println("got here - 1");
      int max = (board.getDim() * board.getDim()-1 - board.getTotalMoves())/2;
      System.out.println("max: " + max);
      if(beta > max) {
        beta = max;
        if(alpha >= beta) return beta;  // prune
      }
      System.out.println("got here - 2");
      for(int x = 0; x < board.getDim(); x++)
        if(board.goodPlay(x)) {
          Board tempBoard = new Board(board);
          tempBoard.makePlay(x);
          int score = -evaluate(tempBoard, -beta, -alpha);

          if(score >= beta) return score;
          if(score > alpha) alpha = score;
        }

      return alpha;
    }

}
