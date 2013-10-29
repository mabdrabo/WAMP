package backend;

import java.util.ArrayList;
import java.util.Arrays;

enum SearchStrategy {BF, DF, ID, GR1, GR2, AS1, AS2};
enum Operator {NORTH, EAST, SOUTH, WEST};
enum TempState {CLEAR, STOP, FAIL};

public class WhereAreMyParts extends GenericSearchProblem {
	
	public WhereAreMyParts() {
		GenGrid();
		this.initial_state = GenGrid();
		this.state_space.add(this.initial_state);
	}

	public Grid GenGrid() {
		return new Grid();
	}
	
	public boolean move(Grid node, Part part, Operator direction) {
	// Continuous movement of the given part in the given direction obeying the stop conditions
		TempState check = checkForStopAndFail(node, part, direction);
		
		switch (check) {
		case FAIL:
			System.out.println("MOVE FAIL, reason: WIRE FENCE");
			return false;
		case STOP:
			System.out.println("MOVE STOP, reason: OBSTACLE or ROBOT PART");
			return true;
		default:
			break;
		}
		return false;
	}
	
	private TempState checkForStopAndFail(Grid node, Part part, Operator direction) {
	//	A moving part should Stop if it's facing either an obstacle or an other robotic part
	//	A moving part should Fail if it's facing a wired fence
	//	Otherwise, the way is Clear to continue moving
		TempState returnState = null;
		boolean move = false;
		int i = part.location[0];
		int j = part.location[1];
		int[] correct = new int[] {0,0};

		do {
			switch (direction) {
			
			case NORTH:
				i--;
				correct[0] = 1;
				break;
	
			case SOUTH:
				i++;
				correct[0] = -1;
				break;
				
			case EAST:
				j++;
				correct[1] = -1;
				break;
				
			case WEST:
				j--;
				correct[1] = 1;
				break;
			}
			if (i<0 || j<0 || i>=node.height || j>=node.width || node.grid[i][j] == "f") {
				returnState = TempState.FAIL;	// TODO return to FAIL
				move = false;
			}
			else {
				if (node.grid[i][j] == "o" || node.grid[i][j].contains("p")) {
					returnState = TempState.STOP; 
				}
				else {
					if (node.grid[i][j] == "e") {
						move = true;
						returnState = TempState.CLEAR;
					}
				}
			}
		} while (returnState == TempState.CLEAR);
		
		i+=correct[0];
		j+=correct[1];
		if (move) {
			System.out.format("old %d %d, new %d %d", part.location[0], part.location[1], i, j);
			String tag = node.grid[part.location[0]][part.location[1]];
			node.grid[part.location[0]][part.location[1]] = "e";
			part.location[0] = i;
			part.location[1] = j;
			node.grid[part.location[0]][part.location[1]] = tag;
		}
		return returnState;
	}
	
	
	public Object[] search(WhereAreMyParts searchProblem, SearchStrategy strategy, boolean visualize) {		// General Search
		int[][] moves = null;	// a representation of the sequence of moves to reach the goal (if possible)
		int cost = 0;	// the cost of the solution computed
		int expansions = 0;	// the number of nodes chosen for expansion during the search

		while (true) {
			if (this.state_space.isEmpty()) {	// fail
				System.out.println("NO SOLUTION");
				break;
			}
			Grid node = (Grid) this.state_space.remove(0);
			System.out.format("popped node: %s\n", node.toString());
			if (searchProblem.goal_test(node)) {	// success
				System.out.format("GOAL NODE!! %s\n", node);
				System.out.println(node.parts);
				break;
			}

			switch (strategy) {
				case BF:
					breadthFirst(node);
					break;
				case DF:
					depthFirst(node);
					break;
				case GR1:
					greedy(1);
					break;

				default:
					break;
			}
			if (this.state_space.size() > 1000) {	// Threshold to avoid open loops
				System.out.println("NO SOLUTION!");
				break;
			}
		}
		Object[] return_list = {moves, cost, expansions};
		System.out.println("Search Return List: " + Arrays.deepToString(return_list));
		return return_list;
	}
	
	public ArrayList<Grid> expand(Grid node) {
		ArrayList<Grid> new_nodes = new ArrayList<>();
		for (Part part : node.parts) {
			for (Operator op : Operator.values()) {
				Grid new_node = new Grid(node);	// create a copy of the current node
				if (move(new_node, part, op)) {	// a move is possible within the rules
					new_node.operator = op;
					new_node.parent = node;
//					if (!Arrays.deepToString(new_node.grid).equals(Arrays.deepToString(((Grid) new_node.parent).grid)))
//						if (!Arrays.deepToString(new_node.grid).equals(Arrays.deepToString(((Grid) new_node.parent.parent).grid))) {
							System.out.format("ADDED: %s\n", new_node);
							new_nodes.add(new_node);
//						}
				}
			}
		}
		return new_nodes;
	}

	public void breadthFirst(Grid node) {
		System.out.println("Breadth first");
		this.state_space.addAll(expand(node));		// add that node to the end of the Q
	}
	
	public void depthFirst(Grid node) {
		System.out.println("Depth first");
		this.state_space.addAll(0, expand(node));		// add that node to the start of the Q
	}
	
	public void greedy(int heuristic) {
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
				if (!grid.grid[part.location[0]][part.location[1]].equals(grid.grid[part2.location[0]][part2.location[1]]))
					return false;
			}
		}
		return true;
	}

	public Operator opposite(Operator direction) {
		switch (direction) {
		case NORTH:
			return Operator.SOUTH;
		case SOUTH:
			return Operator.NORTH;
		case EAST:
			return Operator.WEST;
		case WEST:
			return Operator.EAST;
		default:
			return null;
		}
	}
	
	@Override
	public void path_cost() {
		// TODO Auto-generated method stub
		
	}
}
