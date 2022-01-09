import java.util.*;
public class minimax_917272594 extends AIModule
{
	public void getNextMove(final GameStateModule game)
	{   //makes a copy of the current game board
        int [][] currentBoard =  new int[game.getWidth()][game.getHeight()];
        for (int i = 0; i < game.getWidth(); i++){
            for(int j = 0; j < game.getHeight(); j++ ){
                currentBoard [i][j] = game.getAt(i, j);
            }
        }
        //calls MiniMax
        int [] move = MiniMax(game, currentBoard, 2, game.getActivePlayer());
        //System.out.println("Best Col is " + move[1]);
        chosenMove = move[1];
	}
    //MiniMax Returs array: [0] = score,  [1] = column
    private int [] MiniMax (final GameStateModule game, int [][] board, int depth, int Player){
        int [] validLoc = validMove(game, board); //gets an array of valid locations 
        //sets Max and Min players
        int maxPlayer = game.getActivePlayer();
        int minPlayer = -1;
        if (maxPlayer == 1) minPlayer = 2;
        else minPlayer = 1;
        
        if (depth == 0 || game.isGameOver()){
            int [] dZero = {-1,-1};
            dZero[0] = getScore(game, board, Player);
            return dZero;
        }

        if (Player == maxPlayer){
            int [] Max = {-999999, -1};
            for  (int x = 0; x < game.getWidth(); x++ ){
                if (validLoc[x] == 1){
                    int y = tempRow(game, board, x);
                    int [][] tempBoard = new int[game.getWidth()][game.getHeight()];
                    //makes copy of board
                    for (int i = 0; i < game.getWidth(); i++){
                        for(int j = 0; j < game.getHeight(); j++ ){
                            tempBoard [i][j] = board[i][j];
                        }
                    }
                    tempDrop(tempBoard, x, y, maxPlayer);
                    int [] newScore = MiniMax(game, tempBoard, depth -1 , minPlayer);
                    if (newScore[0] > Max[0]) {
                        Max[0] = newScore[0];
                        Max[1] = x;
                    }
                }
            }
            return Max;
        }
        //Mini
        else {
            int [] Mini = {999999,-1};
            for  (int x = 0; x < game.getWidth(); x++ ){
                if (validLoc[x] == 1){
                    int y = tempRow(game, board, x);
                    int [][] tempBoard = new int[game.getWidth()][game.getHeight()];
                    for (int i = 0; i < game.getWidth(); i++){
                        for(int j = 0; j < game.getHeight(); j++ ){
                            tempBoard [i][j] = board[i][j];
                        }
                    }
                    tempDrop(tempBoard, x, y, minPlayer);
                    int newScore [] = MiniMax(game, tempBoard, depth -1 , maxPlayer);
                    if (newScore[0] < Mini[0]) {
                        Mini[0] = newScore[0];
                        Mini[1] = x;
                    }
                }
            }
            return Mini;
        }
    }
    //MADE FIRST TO TEST EVAL FUNCTION
    /*private int bestMove (final GameStateModule game, int [][] board, int player){
        int [] validLoc = validMove(game, board);
        int topScore = -999999;
        int bestCol = 4;
        for (int x = 0; x < game.getWidth(); x++){
            if (validLoc[x] == 1) {
                int y = tempRow(game, board, x);
                int [][] tempBoard = new int[game.getWidth()][game.getHeight()];
                for (int i = 0; i < game.getWidth(); i++){
                    for(int j = 0; j < game.getHeight(); j++ ){
                        tempBoard [i][j] = board[i][j];
                    }
                }
                tempDrop(tempBoard, x, y, game.getActivePlayer());
                int score = getScore(game, tempBoard, game.getActivePlayer());
                if (score > topScore) {
                    topScore = score;
                    bestCol = x;
                }
            }
        }
        return bestCol;
    }*/

    //Takes a set of 4
    private int eval_set (int [] set, int player){
        //initializing players
        int score = 0;
        int activePlayer = -1;
        int oppPlayer = -1;
        if (player == 1) {
            activePlayer = 1;
            oppPlayer = 2;
        }
        else {
            activePlayer = 2;
            oppPlayer = 1;
        }
        //Count the # of peices in a set of 4
        int emptyCount = 0;
        int activeCount = 0;
        int oppCount = 0;
        for (int i=0; i < set.length; i++ ){
            if (set[i] == activePlayer){
                activeCount++;
            }
            else if (set[i] == oppPlayer){
                oppCount++;
            }
        
            else {
                emptyCount++;
            }
        }
        
        if (activeCount == 4 ) score+=100;//probly useless in the minimax implementation
        else if (activeCount == 3  && emptyCount == 1) score +=5; 
        else if (activeCount == 2  && emptyCount == 2) score +=2; 
        if (oppCount == 3 && emptyCount ==1) score -= 4; 
        //interesting issue with this value: if AI is P1 it wont block a 4 stack on the 0th or 6th Col
        // if I change it to -7 it will but will lose to MonteCarlo (will need to further tweek these scores for a more perfect AI)
        else if (oppCount == 2  && emptyCount == 2) score -= 1;
        return score;

    }
    private int getScore (final GameStateModule game, int [][] board, int player){
        int score = 0;
        
        // Score Center Column
        int centCount = 0;
        for (int j = 0; j < game.getHeight(); j++ ){
            if(board[3][j] == player){
                centCount++;
            }
        }
        //weight of 3 per coin in the center
        score += centCount * 3 ;
        //Horizontal
        for (int j = 0; j < game.getHeight(); j++){
            for(int i=0; i < game.getWidth()-3; i++){
                //create horz set:
                int[] horz_set = new int[4];
                for(int x=0; x<4; x++){
                    horz_set[x] = board[i+x][j];
                }
                score += eval_set(horz_set, player);
            }
        }
        //Vertical
        for (int i = 0; i < game.getWidth(); i++){
            for(int j=0; j<game.getHeight()-3; j++){
                //create horz set:
                int[] vert_set = new int[4];
                for(int y=0; y<4; y++){
                    vert_set[y] = board[i][j+y];
                }
                score += eval_set(vert_set, player);
            }
        }
        //Pos Diag
        for (int j = 0; j < game.getHeight()-3; j++){
            for(int i=0; i<game.getWidth()-3; i++){
                //create diag set
                int [] posDiag_set = new int[4];
                for (int x = 0; x < 4 ; x++){
                    int y = x;
                    posDiag_set[y] = board[i+x][j+y];
                }
                score += eval_set(posDiag_set, player);
            }
        }
        //neg Diag
        for (int j = 0; j < game.getHeight()-3; j++){
            for(int i=0; i<game.getWidth()-3; i++){
                //create diag set
                int [] negDiag_set = new int[4];
                for (int x = 0; x < 4 ; x++){
                    int y = x;
                    negDiag_set[y] = board[i+x][j+3-y];
                }
                score += eval_set(negDiag_set, player);
            }
        }
        return score;
    }
    
    //returns array of valid moves in a given board: 1 = col is valid
    private int[] validMove (final GameStateModule game, int [][] board){
        int [] validLoc = new int [game.getWidth()]; 
        for (int i = 0; i< game.getWidth(); i++){
            if (game.canMakeMove(i)) validLoc[i] = 1;
            else validLoc[i] = 0;
        }
        return validLoc;
    }
    //adds a piece to my temp board
    private void tempDrop (int [][] board, int x, int y, int player){
        board[x][y] = player;
    }
    //gets the first empty slot in a given column 
    private int tempRow (final GameStateModule game, int [][] board, int column){
        for (int j = 0; j < game.getHeight(); j++){
            if (board[column][j] == 0) return j;
        }
        return 3;
    }   
}
