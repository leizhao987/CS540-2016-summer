/****************************************************************
 * studPlayer.java
 * Implements MiniMax search with A-B pruning and iterative deepening search (IDS). The static board
 * evaluator (SBE) function is simple: the # of stones in studPlayer's
 * mancala minue the # in opponent's mancala.
 * -----------------------------------------------------------------------------------------------------------------
 * Licensing Information: You are free to use or extend these projects for educational purposes provided that
 * (1) you do not distribute or publish solutions, (2) you retain the notice, and (3) you provide clear attribution to UW-Madison
 *
 * Attribute Information: The Mancala Game was developed at UW-Madison.
 *
 * The initial project was developed by Chuck Dyer(dyer@cs.wisc.edu) and his TAs.
 *
 * Current Version with GUI was developed by Fengan Li(fengan@cs.wisc.edu).
 * Some GUI componets are from Mancala Project in Google code.
 */




//################################################################
// studPlayer class
//################################################################

public class lzhao47Player extends Player {


    /*Use IDS search to find the best move. The step starts from 1 and keeps incrementing by step 1 until
	 * interrupted by the time limit. The best move found in each step should be stored in the
	 * protected variable move of class Player.
     */
    public void move(GameState state)
    {
    	//select the first legal move to be safe 
    	for (int i=0; i<6; i++) 
    	{
    		if (!state.illegalMove(i)) 
    		{
    			move = i;
    			break;
    		}
    	}
    	
    	int maxDepth = 1;
    	while (true) {
    		GameState playState = new GameState(state);
    		int[] moveSBE = maxAction(playState, maxDepth);
    		move = moveSBE[0];
    		maxDepth++;
    	}
    	
    }

    // Return best move for max player. Note that this is a wrapper function created for ease to use.
	// In this function, you may do one step of search. Thus you can decide the best move by comparing the 
	// sbe values returned by maxSBE. This function should call minAction with 5 parameters.
    public int[] maxAction(GameState state, int maxDepth)
    {
    	int m = -1, s = Integer.MIN_VALUE;
    	if (maxDepth == 1) {
    		for (int i = 0; i < 6; i++) {
    			if (!state.illegalMove(i)) {
        			GameState childState = new GameState(state);
    				/* if (childState.applyMove(i) && !childState.gameOver()) {
    					int[] values = maxAction(childState, 1);
    					if (values[1] > s) {
    						s = values[1];
    						m = i;
    					}
    				} */
        			childState.applyMove(i);
    				if (sbe(childState) > s) {
    					s = sbe(childState);
    					m = i;
    				}
    			}
    		}
    		// state.applyMove(m);
        	return new int[]{m,s};
    	} else {
    		int v = Integer.MIN_VALUE, alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;
    		for (int i = 0; i < 6; i++) {
    			if (!state.illegalMove(i)) {
    				GameState childState = new GameState(state);
    				int value = childState.applyMove(i) ? maxAction(childState, 1, maxDepth, alpha, beta)
    						: minAction(childState, 1, maxDepth, alpha, beta);
    					// if (v >= beta) return new int[]{-1, v};
    					// alpha = Math.max(alpha, v);
    				if (value > v) {
						m = i;
						v = value;
						alpha = value;
					}
    			}
    		}
    		return new int[]{m,v};
    	}
    }
    
	//return sbe value related to the best move for max player
    public int maxAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
    {
    	if (state.gameOver() || currentDepth == maxDepth) return sbe(state);
    	
    	int v = Integer.MIN_VALUE;
    	for (int i = 0; i < 6; i++) {
    		if (!state.illegalMove(i)) {
    			GameState childState = new GameState(state);
    			int value = childState.applyMove(i) ? maxAction(childState, currentDepth+1, maxDepth, alpha, beta) 
    					: minAction(childState, currentDepth+1, maxDepth, alpha, beta);
    			v = Math.max(v, value);
    			if (v >= beta) return v;
    			alpha = Math.max(alpha, v);
    		}
    	}
    	return v;
    }
    
    //return sbe value related to the bset move for min player
    public int minAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
    {
    	if (state.gameOver() || currentDepth == maxDepth) return sbe(state);
    	
    	int v = Integer.MAX_VALUE;
    	for (int i = 7; i < 13; i++) {
    		if (!state.illegalMove(i)) {
    			GameState childState = new GameState(state);
    			int value = childState.applyMove(i) ? minAction(childState, currentDepth+1, maxDepth, alpha, beta)
    					: maxAction(childState, currentDepth+1, maxDepth, alpha, beta);
    			v = Math.min(v,  value);
    			if (alpha >= v) return v;
    			beta = Math.min(beta, v);
    		}
    	}
    	return v;
    }

    //the sbe function for game state. Note that in the game state, the bins for current player are always in the bottom row.
    private int sbe(GameState state)
    {
    	int stones = 0;
		for (int i = 0; i <= 6; ++i)
			stones += state.state[i];
		for (int j = 7; j <= 13; ++j)
			stones -= state.state[j];
    	return stones;
    }
    /*
    private int sbe(GameState state)
    {
    	return state.state[6] - state.state[13];
    }
    */
}

