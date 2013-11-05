package backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

enum SearchStrategy {BF, DF, ID, GR1, GR2, AS1, AS2};
enum Operator {NORTH, EAST, SOUTH, WEST};
enum TempState {CLEAR, STOP, FAIL};

public class WhereAreMyParts extends GenericSearchProblem {

	ArrayList<Grid> closed_states;
	public int expansions = 0;	// the number of nodes chosen for expansion during the search
	
	public WhereAreMyParts() {
		this.state_space = new ArrayList<SearchTreeNode>();
		this.closed_states = new ArrayList<Grid>();
		this.initial_state = GenGrid();
		this.state_space.add(this.initial_state);
		this.closed_states.add((Grid) this.initial_state);
	}

	public Grid GenGrid() {
		return new Grid();
	}

	private boolean move(Grid node, Part part, Operator direction) {
		TempState moveState = null;
		boolean move = false;
		int i=0, j=0, min_i=node.height, max_i=0, min_j=node.width, max_j=0;
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
				moveState = TempState.FAIL;
				System.out.println("out of bounds if " + part + "moves " + direction.toString());
				move = false;
			}
			else {
				for (int[] loc : part.linked_parts_locations) {
					if (node.state[loc[0]+i][loc[1]+j] == "f") {
						moveState = TempState.FAIL;
						System.out.println("fence ahead of " + part + "when move " + direction.toString());
						move = false;
						break;
					}
					if (node.state[loc[0]+i][loc[1]+j] == "b"){
						moveState = TempState.STOP;
						System.out.println("obstacle ahead of " + part + "when move " + direction.toString());
						break;
					}
					if (node.state[loc[0]+i][loc[1]+j].contains("p")) {
						if (!part.hasPart(new int[] {loc[0]+i, loc[1]+j})) {
							System.out.println("part ahead of " + part + "when move " + direction.toString());
							moveState = TempState.STOP;
							break;
						}
					}				
					if (node.state[loc[0]+i][loc[1]+j] == "e") {
						move = true;
						moveState = TempState.CLEAR;
					}
				}
			}
		} while(moveState == TempState.CLEAR);
		
		if (move) {
			ArrayList<int[]> old_locations = part.clone().linked_parts_locations;
			for (int x=0; x<part.linked_parts_locations.size(); x++) {
				String tag = node.state[part.linked_parts_locations.get(x)[0]][part.linked_parts_locations.get(x)[1]];
				part.linked_parts_locations.get(x)[0] += i + correct[0];
				part.linked_parts_locations.get(x)[1] += j + correct[1];
				node.state[part.linked_parts_locations.get(x)[0]][part.linked_parts_locations.get(x)[1]] = tag;
			}
			for (int[] loc : old_locations) {
				if (!part.hasPart(loc)) {
					node.state[loc[0]][loc[1]] = "e";
				}
			}
			System.out.println("moved from " + Arrays.deepToString(old_locations.toArray()) + " to " + Arrays.deepToString(part.linked_parts_locations.toArray()));
			node.cost += (Math.abs(i) + Math.abs(j) + correct[0] + correct[1]) * part.linked_parts_locations.size(); 
			node.replacePart(part.id, part);
			checkToLinkParts(node);
		}
		return move;
	}
	
	public Object[] search(GenericSearchProblem searchProblem, SearchStrategy strategy, boolean visualize) {		// General Search

		while (true) {		// Threshold to avoid open loops
			if (this.state_space.isEmpty()) {		// fail
				System.out.println("NO SOLUTION");
				break;
			}
			System.out.println("############POP############");
			Grid node = (Grid) this.state_space.remove(0);
			System.out.format("popped node: %s\n", node.toString());
			if (searchProblem.goal_test(node)) {	// success
				System.out.format("GOAL NODE!! %s\n", node);
				System.out.println(node.parts);
				Object[] return_list = {node.moves, node.cost, this.expansions};
				System.out.println("Search Return List: " + Arrays.deepToString(return_list));
				break;
//				Grid g = node;
//				while(true) {
//					System.out.println(g + " " + g.operator);
//					if (g.equals(this.initial_state)) {
//						break;
//					}
//					g = (Grid) g.parent;
//				}
			}
			switch (strategy) {
				case BF:
					breadthFirst(node);
					break;
				case DF:
					depthFirst(node);
					break;
				case GR1:
					greedy(1,node);
					break;

				default:
					break;
			}
		}
		return null;
	}

	
	public ArrayList<Grid> expand(Grid node, int bound) {	// -1 bound means no bound
		ArrayList<Grid> new_nodes = new ArrayList<Grid>();
		for (int x=0 ; x<node.parts.size(); x++) {
			System.out.println(".............NEW PART................");
			for (Operator op : Operator.values()) {
				Part part = node.parts.get(x).clone();
				Grid new_node = node.clone();	// create a clone of the current node
				if (move(new_node, part, op)) {	// a move is possible within the rules
					if (!new_node.in(this.closed_states)) {
						this.closed_states.add(new_node);
						new_node.operator = op;
						new_node.parent = node.clone();
						this.expansions++;
						new_node.depth++;
						System.out.println("DEPTH: " + new_node.depth);
						new_node.moves.add(part.id + " " + op.toString());
						System.out.format("ADDED: %s after moving %s %s\n", new_node, part, op.toString());
						new_nodes.add(new_node);
					} else { new_node = null; System.out.println("closed state not added"); }
				} else new_node = null;
			}
		}
		System.out.println("new nodes: " + new_nodes);
		return new_nodes;
	}

	public void breadthFirst(Grid node) {
		System.out.println("Breadth first");
		this.state_space.addAll(expand(node, -1));		// add that node to the end of the Q
	}

	public void depthFirst(Grid node) {
		System.out.println("Depth first");
		this.state_space.addAll(0, expand(node, -1));		// add that node to the start of the Q
	}

	public void iterativeDeepening(Grid node) {
		
	}
	
	public void greedy(int heuristic,Grid node) {
		System.out.println("Greedy " + heuristic);
		switch (heuristic) {
		case 1:
			ArrayList<Grid> children = expand(node, -1);
			for(int u=0; u<children.size(); u++){
				ArrayList<int[]> goalPart = children.get(u).parts.get(0).linked_parts_locations;
				ArrayList<Integer> partsHeuristicValues = null; 
				partsHeuristicValues.add(0,0);
				int minSoFar = -1;
				int heuristicValue;
				int nodeHeuristicValue = 0;
				for(int i=1; i<children.get(u).parts.size(); i++){
					for(int j=0; j<children.get(u).parts.get(i).linked_parts_locations.size(); j++){
						int [] partCoord = children.get(u).parts.get(i).linked_parts_locations.get(j);
						for(int k=0; k<goalPart.size();k++){
							int [] goalCoord = children.get(u).parts.get(0).linked_parts_locations.get(k);
							if(minSoFar == -1){
								heuristicValue = Math.abs(partCoord[0]-goalCoord[0]) + Math.abs(partCoord[1]-goalCoord[1]);
								minSoFar = heuristicValue;
							}
							else{
								heuristicValue = Math.abs(partCoord[0]-goalCoord[0]) + Math.abs(partCoord[1]-goalCoord[1]);
								if(heuristicValue < minSoFar){
									minSoFar = heuristicValue;
								}
							}
						}
					}
					partsHeuristicValues.add(i,minSoFar);
				}
				
				for(int z=0; z<partsHeuristicValues.size(); z++){
					nodeHeuristicValue += partsHeuristicValues.get(z);
				}
				children.get(u).heuristicValue = (int) nodeHeuristicValue/partsHeuristicValues.size();
			}
			
			break;
			
		case 2:
			break;
		}
	}

	public void checkToLinkParts(SearchTreeNode node) {
		boolean linked = false;
		Grid grid = (Grid) node;
		do {
			for (int x=0; x<grid.parts.size(); x++) {
				Part p1 = grid.parts.get(x);
				for (int l=0; l<p1.linked_parts_locations.size(); l++) {
					int[] loc1 = p1.linked_parts_locations.get(l);
					for (int y=0; y<grid.parts.size(); y++) {
						Part p2 = grid.parts.get(y);
						if (!p1.equals(p2)) {
							for (int[] loc2 : p2.linked_parts_locations) {
								if (loc1[0]==loc2[0] || loc1[1]==loc2[1]) {
									if (loc1[0]==loc2[0]) {
										if (loc1[1]==loc2[1]-1 || loc1[1]==loc2[1]+1) {
											System.out.println("Horizontal match: " + p1 + " " + p2);
											p1.linkPart(grid, p2);
											linked = true;
										}
									}
									if (loc1[1]==loc2[1]) {
										if (loc1[0]==loc2[0]-1 || loc1[0]==loc2[0]+1) {
											System.out.println("Vertical match: " + p1 + " " + p2);
											p1.linkPart(grid, p2);
											linked = true;
										}
									}
								} else linked = false;
							}
						}
					}
				}
			}
		} while (linked && grid.parts.size() > 1);
		System.out.println("# PARTS: " + grid.parts.size());
	}

	@Override
	public boolean goal_test(SearchTreeNode node) {
		Grid grid = (Grid) node;
		checkToLinkParts(grid);
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
	
	public void path_cost() {
		
	}
}
