package backend;

import java.util.ArrayList;
import java.util.Arrays;

enum SearchStrategy {BF, DF, ID, GR1, GR2, AS1, AS2};
enum Operator {NORTH, EAST, SOUTH, WEST};
enum TempState {CLEAR, STOP, FAIL};

public class WhereAreMyParts extends GenericSearchProblem {
	
	public WhereAreMyParts() {
		this.initial_state = GenGrid();
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
	
	private TempState checkForStopAndFail1(Grid node, Part part, Operator direction) {
		// Testing for multiple parts move together
		TempState returnState = null;
		boolean move = false;
		int i=0, j=0, min_i=0, max_i=0, min_j=0, max_j=0;
		int[] correct = new int[] {0,0};
		
		for (int[] loc : part.linked_parts_locations) {
			if (loc[0] < min_i)
				min_i = loc[0];
			if (loc[0] > max_i)
				max_i = loc[0];
			if (loc[1] < min_j)
				min_j = loc[1];
			if (loc[1] > max_j)
				max_j = loc[1];
		}
		
//		ArrayList<int[]> parts_temp_loc = new ArrayList<int[]>(part.linked_parts_locations);
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

			if (min_i+i<0 || min_j+j<0 || max_i+i>=node.height || max_j+j>=node.width) {
				returnState = TempState.FAIL;
				move = false;
			}
			else {
				for (int[] loc : part.linked_parts_locations) {
					if (node.grid[loc[0]+i][loc[1]+j] == "f" || node.grid[loc[0]+i][loc[1]+j] == "f"
							|| node.grid[loc[0]+i][loc[1]+j] == "f" || node.grid[loc[0]+i][loc[1]+j] == "f") {
						returnState = TempState.FAIL;
						move = false;
					}
					
					if (node.grid[loc[0]+i][loc[1]+j] == "b" || (node.grid[loc[0]+i][loc[1]+j].contains("p") 
							&& !part.hasPart(new int[] {loc[0]+i, loc[1]+j}))) {
						returnState = TempState.STOP; 
					}
					else {
						if (node.grid[loc[0]+i][loc[1]+j] == "e") {
							move = true;
							returnState = TempState.CLEAR;
						}
					}
				}
			}
		} while(returnState == TempState.CLEAR);
		
		if (move) {
			ArrayList<int[]> old_locations = new ArrayList<int[]>(part.linked_parts_locations);
			for (int x=0; x<part.linked_parts_locations.size(); x++) {
				String tag = node.grid[part.linked_parts_locations.get(x)[0]][part.linked_parts_locations.get(x)[1]];
				part.linked_parts_locations.get(x)[0] += i + correct[0];
				part.linked_parts_locations.get(x)[1] += j + correct[1];
				node.grid[part.linked_parts_locations.get(x)[0]][part.linked_parts_locations.get(x)[1]] = tag;
			}
			for (int[] loc : old_locations) {
				if (!part.hasPart(loc)) {
					System.out.println("emptying i:" + loc[0] + " j:" + loc[1]);
					node.grid[loc[0]][loc[1]] = "e";
				}
			}
			System.out.println("moved from " + Arrays.deepToString(old_locations.toArray()) + "to " + Arrays.deepToString(part.linked_parts_locations.toArray()));
		}
		return returnState;
	}
	
	private TempState checkForStopAndFail(Grid node, Part part, Operator direction) {
	//	A moving part should Stop if it's facing either an obstacle or an other robotic part
	//	A moving part should Fail if it's facing a wired fence
	//	Otherwise, the way is Clear to continue moving
		TempState returnState = null;
		boolean move = false;
		int i = part.linked_parts_locations.get(0)[0];
		int j = part.linked_parts_locations.get(0)[1];
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
				returnState = TempState.FAIL;
				move = false;
			}
			else {
				if (node.grid[i][j] == "b" || node.grid[i][j].contains("p")) {
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
			System.out.format("old %d %d, new %d %d\n", part.linked_parts_locations.get(0)[0], part.linked_parts_locations.get(0)[1], i, j);
			String tag = node.grid[part.linked_parts_locations.get(0)[0]][part.linked_parts_locations.get(0)[1]];
			node.grid[part.linked_parts_locations.get(0)[0]][part.linked_parts_locations.get(0)[1]] = "e";
			part.linked_parts_locations.get(0)[0] = i;
			part.linked_parts_locations.get(0)[1] = j;
			node.grid[part.linked_parts_locations.get(0)[0]][part.linked_parts_locations.get(0)[1]] = tag;
		}
		return returnState;
	}


	public Object[] search(WhereAreMyParts searchProblem, SearchStrategy strategy, boolean visualize) {		// General Search
		ArrayList<String> moves = null;	// a representation of the sequence of moves to reach the goal (if possible)
		int cost = 0;	// the cost of the solution computed
		int expansions = 0;	// the number of nodes chosen for expansion during the search

		this.state_space.add(this.initial_state);

		while (true) {
			if (this.state_space.isEmpty()) {	// fail
				System.out.println("NO SOLUTION");
				break;
			}
			Grid node = (Grid) this.state_space.remove(0);
			moves = new ArrayList<String>(node.moves);
			cost = node.cost;
			expansions = node.expansions;
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
			if (this.state_space.size() > 300) {	// Threshold to avoid open loops
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
		for (int x=0 ; x<node.parts.size(); x++) {
			Part part = node.parts.get(x);
			for (Operator op : Operator.values()) {
				Grid new_node = new Grid(node);	// create a copy of the current node
				new_node.operator = op;
				new_node.parent = node;
				if (move(new_node, part, op)) {	// a move is possible within the rules
					new_node.expansions++;
					checkToLinkParts(node);
					new_node.moves.add(part.id + " " + op.toString());
					System.out.format("ADDED: %s\n", new_node);
					new_nodes.add(new_node);
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
	
	
	public void checkToLinkParts(SearchTreeNode node) {
		Grid grid = (Grid) node;
		for (int x=0; x<grid.parts.size(); x++) {
			Part p1 = grid.parts.get(x);
			for (int l=0; l<p1.linked_parts_locations.size(); l++) {
				int[] loc1 = p1.linked_parts_locations.get(l);
				for (int y=0; y<grid.parts.size(); y++) {
					Part p2 = grid.parts.get(y);
					if (!p1.equals(p2)) {
						for (int[] loc2 : p2.linked_parts_locations) {
							if (loc1[0]==loc2[0]) {
								if (loc1[1]==loc2[1]-1 || loc1[1]==loc2[1]+1) {
									System.out.println("Horizontal match p1: " + p1 + " p2: " + p2);
									p1.linkPart(grid, p2);
								}
							}
							if (loc1[1]==loc2[1]) {
								if (loc1[0]==loc2[0]-1 || loc1[0]==loc2[0]+1) {
									System.out.println("Vertical match p1: " + p1 + " p2: " + p2);
									p1.linkPart(grid, p2);
								}
							}
						}
					}
				}
			}
		}
	}
	

	@Override
	public boolean goal_test(SearchTreeNode node) {
		Grid grid = (Grid) node;
		System.out.println("# PARTS: " + grid.parts.size());
		if (grid.parts.size() > 1)
			return false;
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
