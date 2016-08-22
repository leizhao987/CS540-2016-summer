import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public AStarSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main a-star search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {

		// FILL THIS METHOD

		// explored list is a Boolean array that indicates if a state associated with a given position in the maze has already been explored. 
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

		PriorityQueue<StateFValuePair> frontier = new PriorityQueue<StateFValuePair>();

		// TODO initialize the root state and add
		// to frontier list
		State init = new State(maze.getPlayerSquare(), null, 0, 0);
		double fValue = Math.abs(maze.getPlayerSquare().X - maze.getGoalSquare().X)
					  + Math.abs(maze.getPlayerSquare().Y - maze.getGoalSquare().Y);
		StateFValuePair sf = new StateFValuePair(init, fValue);
		frontier.add(sf);
		
		while (!frontier.isEmpty()) {
			// TODO return true if a solution has been found
			// TODO maintain the cost, noOfNodesExpanded (a.k.a. noOfNodesExplored),
			// maxDepthSearched, maxSizeOfFrontier during
			// the search
			// TODO update the maze if a solution found
			// use frontier.poll() to extract the minimum stateFValuePair.
			// use frontier.add(...) to add stateFValue pairs
			
			maxSizeOfFrontier = Math.max(maxSizeOfFrontier, frontier.size());
			
			StateFValuePair n = frontier.poll();
			explored[n.getState().getX()][n.getState().getY()] = true;
			
			noOfNodesExpanded++;
			maxDepthSearched = Math.max(maxDepthSearched, n.getState().getDepth());
			
			if (n.getState().isGoal(maze)) {
				cost = n.getState().getGValue();
				State s = n.getState().getParent();
				while (s.getParent() != null) {
					maze.setOneSquare(s.getSquare(), '.');
					s = s.getParent();
				}
				return true;
			}
			
			ArrayList<State> successors = n.getState().getSuccessors(explored, maze);
			
			for (State s : successors) {
				 if (explored[s.getX()][s.getY()])
					continue;
				double fvalue = s.getGValue() + Math.abs(s.getX() - maze.getGoalSquare().X)
							  + Math.abs(s.getY() - maze.getGoalSquare().Y);
				StateFValuePair sp = new StateFValuePair(s, fvalue);
				boolean isExist = false;
				StateFValuePair existQ = null;
				for (StateFValuePair q : frontier) {
					if (q.getState().getSquare().equals(s.getSquare())) {
						isExist = true;
						existQ = q;
					}
				}
				if (!isExist)
					frontier.add(sp);
				else if (s.getGValue() < existQ.getState().getGValue()) {
					frontier.remove(existQ);
					frontier.add(sp);
					// noOfNodesExpanded++;
				}
			}
			
		}

		return false;
	}

}
