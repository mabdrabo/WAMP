package backend;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

enum SearchStrategy {BF, DF, ID, GR1, GR2, AS1, AS2};
enum Operator {NORTH, SOUTH, EAST, WEST};
enum TempState {CLEAR, STOP, FAIL};

public class WhereAreMyParts extends GenericSearchProblem {
	
	public Grid grid;
	
	public WhereAreMyParts() {
		GenGrid();
		this.initial_state = this.grid;
		System.out.println(goal_test(this.initial_state));
	}

	public void GenGrid() {
		this.grid = new Grid();
	}
	
	public boolean move(Grid grid, Part part, Operator direction) {
	// Continuous movement of the given part in the given direction obeying the stop conditions
		boolean moved = false;
		while (checkForStopAndFail(part, direction) == TempState.CLEAR) {
			System.out.println("MOVE CLEAR");
			part.step(grid, direction);
			moved = true;
		}
		
		if (checkForStopAndFail(part, direction) == TempState.FAIL) {
			System.out.println("MOVE FAIL, reason: WIRE FENCE, GAMEOVER!");
			return moved;
		}
		
		if (checkForStopAndFail(part, direction) == TempState.STOP) {
			System.out.println("MOVE STOP, reason: OBSTACLE or ROBOT PART");
			return moved;
		}
		
		return moved;	
	}
	
	private TempState checkForStopAndFail(Part part, Operator direction) {
	//	A moving part should Stop if it's facing either an obstacle or an other robotic part
	//	A moving part should Fail if it's facing a wired fence
	//	Otherwise, the way is Clear to continue moving

		int i = 0;
		int j = 0;
		
		switch (direction) {
		
		case NORTH:
			i = part.location[0] - 1;
			j = part.location[1];
			break;

		case SOUTH:
			i = part.location[0] + 1;
			j = part.location[1];
			break;
			
		case EAST:
			i = part.location[0];
			j = part.location[1] + 1;
			break;
			
		case WEST:
			i = part.location[0];
			j = part.location[1] - 1;
			break;
		}
		if (i<0 || j<0 || i>=this.grid.height || j>=this.grid.width)
			return TempState.FAIL;
		else
			return (grid.grid[i][j] == "o" || grid.grid[i][j].contains("p"))? TempState.STOP : 
				(grid.grid[i][j] == "f")? TempState.FAIL :
					(grid.grid[i][j] == "e")? TempState.CLEAR : TempState.FAIL ; 
	}
	
	
	public Object[] search(WhereAreMyParts searchProblem, SearchStrategy strategy, boolean visualize) {		// General Search
		int[][] moves = null;	// a representation of the sequence of moves to reach the goal (if possible)
		int cost = 0;	// the cost of the solution computed
		int expansions = 0;	// the number of nodes chosen for expansion during the search

		Queue<Grid> nodes = new LinkedList<>();
		nodes.add((Grid) searchProblem.initial_state);

		while (true) {
			if (nodes.isEmpty()) {	// fail
				System.out.println("NO SOLUTION");
				break;
			}
			Grid node = nodes.poll();
			System.out.format("popped node: %s\n", node.toString());
			if (searchProblem.goal_test(node)) {	// success
				System.out.format("GOAL NODE!! %s\n", node);
				break;
			}

			switch (strategy) {
				case BF:
					System.out.format("Tree size before: %d\n", nodes.size());
					nodes = new LinkedList<Grid>(breadthFirst(nodes, node));
					System.out.format("Tree size after: %d\n", nodes.size());
					break;

				case GR1:
					greedy(1, nodes);
					break;

				default:
					break;
			}
//			if (nodes.size() > 100) break;
		}
		Object[] return_list = {moves, cost, expansions};
		System.out.println("Search Return List: " + Arrays.deepToString(return_list));
		return return_list;
	}

	public Queue<Grid> breadthFirst(Queue<Grid> nodes, Grid node) {
		System.out.println("Breadth first");
		for (Part part : node.parts) {
			for (Operator op : Operator.values()) {
				Grid grid_after_move = new Grid(node);	// create a copy of the current node
				if (move(grid_after_move, part, op)) {	// a move is possible within the rules
					nodes.add(grid_after_move);		// add that node to the end of the Q
					System.out.format("ADDED NODE: %s\n", grid_after_move);
				}
			}
		}
		return nodes;
	}
	
	
	public void greedy(int heuristic, Queue<Grid> nodes) {
		System.out.println("Greedy " + heuristic);
		switch (heuristic) {
		case 1:
			break;
			
		case 2:
			break;
		}
	}
	

	@Override
	public boolean goal_test(SearchTreeNode node) {
		Grid grid = (Grid) node;
		for (int x=0; x<grid.parts.size(); x++) {
			for (int z=0; z<grid.parts.size(); z++) {
				Part p1 = grid.parts.get(x);
				Part p2 = grid.parts.get(z);

				if (p1.location[0] != p2.location[0]
						|| p1.location[1] != p2.location[1]) {
					if ((p1.location[0] == p2.location[0]) &&
							(p1.location[1] == p2.location[1] + 1 || 
							p1.location[1] == p2.location[1] - 1)) {
						System.out.println("Horizontal match p1: " + p1.toString() + " p2: " + p2.toString());
						p1.linkPart(p2);
						grid.grid[p2.location[0]][p2.location[1]] = grid.grid[p1.location[0]][p1.location[1]];
//						grid.parts.remove(p2);
					}
					if ((p1.location[1] == p2.location[1]) &&
							(p1.location[0] == p2.location[0] + 1 || 
							p1.location[0] == p2.location[0] - 1)) {
						System.out.println("Vertical match p1: " + p1.toString() + " p2: " + p2.toString());
						p1.linkPart(p2);
						grid.grid[p2.location[0]][p2.location[1]] = grid.grid[p1.location[0]][p1.location[1]];
//						grid.parts.remove(p2);
					}
				}
				grid.parts.trimToSize();
			}
		}
//		if (grid.parts.size() > 1)
//			return false;
//		else
//			return true;
		for (Part part : grid.parts) {
			for (Part part2 : grid.parts) {
				if (!this.grid.grid[part.location[0]][part.location[1]].equals(this.grid.grid[part2.location[0]][part2.location[1]]))
					return false;
			}
		}
		return true;
	}

	@Override
	public void path_cost() {
		// TODO Auto-generated method stub
		
	}
}
