import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Depth-First Search (DFS)
 * 
 * You should fill the search() method of this class.
 */
public class DepthFirstSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public DepthFirstSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main depth first search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		// FILL THIS METHOD

		// explored list is a 2D Boolean array that indicates if a state associated with a given position in the maze has already been explored.
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

		// Stack implementing the Frontier list
		LinkedList<State> stack = new LinkedList<State>();

		State init = new State(maze.getPlayerSquare(),null,0,0);
		stack.push(init);
		
		while (!stack.isEmpty()) {
			// TODO return true if find a solution
			// TODO maintain the cost, noOfNodesExpanded (a.k.a. noOfNodesExplored),
			// maxDepthSearched, maxSizeOfFrontier during
			// the search
			// TODO update the maze if a solution found
			// use stack.pop() to pop the stack.
			// use stack.push(...) to elements to stack
			
			maxSizeOfFrontier = Math.max(maxSizeOfFrontier, stack.size());

			State n = stack.pop();
			explored[n.getX()][n.getY()] = true;
			
			noOfNodesExpanded++;
			maxDepthSearched = Math.max(maxDepthSearched, n.getDepth());
			
			if (n.isGoal(maze)) {
				cost = n.getGValue();
				n = n.getParent();
				while (n.getParent() != null) {
					maze.setOneSquare(n.getSquare(), '.');
					n = n.getParent();
				}
				return true;
			}
			
			ArrayList<State> successors = n.getSuccessors(explored, maze);
			for (State s : successors) {
				if (!explored[s.getX()][s.getY()]) stack.push(s); //cycle checking
				/* State t = s;
				boolean isExist = false;
				while (t.getParent() != null) {
					t = t.getParent();
					if (t.getSquare().equals(s.getSquare())) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					stack.push(s);
				} */
			}
			
		}

		return false;
	}
}
