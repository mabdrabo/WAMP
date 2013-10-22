package backend;

public class WhereAreMyParts extends GenericSearchProblem {
	
	public Grid grid;
	
	public WhereAreMyParts() {}

	public void GenGrid() {
		this.grid = new Grid();
	}
	
	public boolean move(Part part, Operator direction) {
	// Continuous movement of the given part in the given direction obeying the stop conditions
		switch (direction) {
		
		case NORTH:
			break;

		case SOUTH:
			break;
			
		case EAST:
			break;
			
		case WEST:
			break;
			
		default:
			return false;
		}
		return false;
		
	}
	
	public void step(Part part, Operator direction) {
	// move the given part one step in the given direction
		
		switch (direction) {
		
		case NORTH:
			break;

		case SOUTH:
			break;
			
		case EAST:
			break;
			
		case WEST:
			break;
			
		default:
			break;
		}
	}
	
	public Object[] search(Grid grid, SearchStrategy strategy, boolean visualize) {
	// General Search
		int[][] moves = null;	// a representation of the sequence of moves to reach the goal (if possible)
		int cost = 0;	// the cost of the solution computed
		int expansions = 0;	// the number of nodes chosen for expansion during the search
		
		switch (strategy) {
		
		case BREADTH_FIRST:
			breadthFirst();
			break;
			
		case DEPTH_FIRST:
			depthFirst();
			break;
			
		case ITERATIVE_DEEPENING:
			iterativeDeepening();
			break;
			
		case GREEDY_1:
			greedy(1);
			break;
			
		case GREEDY_2:
			greedy(2);
			break;
			
		case A_STAR_1:
			AStar(1);
			break;
			
		case A_STAR_2:
			AStar(2);
			break;
			
		default:
			break;
		}
		
		Object[] return_list = {moves, cost, expansions};
		return return_list;
	}
	
	public void breadthFirst() {
		System.out.println("Breadth first");
	}
	
	public void depthFirst() {
		System.out.println("Depth first");
	}
	
	public void iterativeDeepening() {
		System.out.println("Iterative deepening");
	}
	
	public void greedy(int heuristic) {
		System.out.println("Greedy " + heuristic);
	}
	
	public void AStar(int heuristic) {
		System.out.println("A* " + heuristic);
	}
}
