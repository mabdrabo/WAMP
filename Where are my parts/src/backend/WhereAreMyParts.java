package backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

enum SearchStrategy {BF, DF, ID, GR1, GR2, AS1, AS2};
enum Operator {NORTH, SOUTH, EAST, WEST};
enum TempState {CLEAR, STOP, FAIL};

public class WhereAreMyParts extends GenericSearchProblem {

	ArrayList<Grid> closed_states;
	public int expansions = 0;	// the number of nodes chosen for expansion during the search
	public Grid goalNode;
	private int depthLimit = 0; 
	
	public WhereAreMyParts() {
		this.state_space = new ArrayList<SearchTreeNode>();
		this.closed_states = new ArrayList<Grid>();
		this.initial_state = GenGrid();
		this.state_space.add(this.initial_state);
		this.closed_states.add((Grid) this.initial_state);
	}
	
	public WhereAreMyParts(Grid node) {
		this.initial_state = node;
		this.state_space = new ArrayList<SearchTreeNode>();
		this.closed_states = new ArrayList<Grid>();
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
		
		if (move && (Math.abs(i) + Math.abs(j) + correct[0] + correct[1]) > 0) {
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
			int cost = path_cost(part, i, j, correct);
			System.out.println("ADDED COST: " + cost);
			node.cost +=  cost;
			node.replacePart(part.id, part);
			checkToLinkParts(node);
		}
		return move;
	}
	
	public ArrayList<Grid> expand(Grid node) {
		ArrayList<Grid> new_nodes = new ArrayList<Grid>();
		for (int x=0 ; x<node.parts.size(); x++) {
			System.out.println(".............NEW PART................");
			for (Operator op : Operator.values()) {
				Part part = node.parts.get(x).clone();
				Part clone = node.parts.get(x).clone();
				Grid new_node = node.clone();	// create a clone of the current node
				if (move(new_node, part, op)) {	// a move is possible within the rules
					if (!new_node.in(this.closed_states)) {
						this.closed_states.add(new_node);
						this.expansions++;
						new_node.depth++;
						new_node.operator = op;
						new_node.parent = node.clone();
						System.out.println("DEPTH: " + new_node.depth);
						new_node.moves.add(clone + " " + op.toString());
						System.out.format("ADDED: %s after moving %s %s\n", new_node, part, op.toString());
						new_nodes.add(new_node);
					} else { new_node = null; System.out.println("closed state not added"); }
				} else new_node = null;
			}
		}
		System.out.println("new nodes: " + new_nodes);
		return new_nodes;
	}
	
	public Object[] search(GenericSearchProblem searchProblem, SearchStrategy strategy, boolean visualize) {		// General Search
		switch(strategy){
	        case ID:  return iterativeDeepening(searchProblem, strategy, visualize);

	        default: 
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
						goalNode = node.clone();
						Object[] return_list = {node.moves, node.cost, this.expansions};
						System.out.println("Search Return List: " + Arrays.deepToString(return_list));
						return return_list;
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
						case GR2:
							greedy(2,node);
							break;
						case AS1:
							Astar(1,node);
							break;
						case AS2:
							Astar(2,node);
							break;
						default:
							break;
					}
	        	}
	        	return null;
	    }
	}
	
	public void breadthFirst(Grid node) {
		System.out.println("Breadth first");
		this.state_space.addAll(expand(node));		// add the node's children to the end of the Q
	}

	public void depthFirst(Grid node) {
		System.out.println("Depth first");
		this.state_space.addAll(0, expand(node));		// add the node's children to the start of the Q
	}
	
	public void greedy(int heuristic,Grid node) {
		System.out.println("Greedy " + heuristic);
		ArrayList<Grid> children = expand(node);
		for(int u=0; u<children.size(); u++){
			ArrayList<int[]> goalPart = children.get(u).parts.get(0).linked_parts_locations;
			ArrayList<Integer> partsHeuristicValues = new ArrayList<Integer>(); 
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
			
			if(heuristic == 1){
				for(int z=0; z<partsHeuristicValues.size(); z++){
					nodeHeuristicValue += partsHeuristicValues.get(z);
				}
				children.get(u).heuristicValue = (int)(nodeHeuristicValue/partsHeuristicValues.size());
				children.get(u).heuristicPluscost = children.get(u).heuristicValue;
			}
			else{
				int finalMinHeuristic = partsHeuristicValues.get(0);
				for(int z=1; z<partsHeuristicValues.size(); z++){
					if(partsHeuristicValues.get(z) < finalMinHeuristic){
						finalMinHeuristic = partsHeuristicValues.get(z);
					}
				}
				children.get(u).heuristicValue = finalMinHeuristic;
				children.get(u).heuristicPluscost = children.get(u).heuristicValue;
			}
		}
		
		System.out.println("------------ HEURISTICS---------------");
		for(int t = 0; t< children.size(); t++){
			System.out.println("node "+t+": "+children.get(t).heuristicValue);
		}
		
		this.state_space.addAll(children);
		System.out.println("---------COMPARISON_-----------------------");
		for(int t = 0; t< this.state_space.size(); t++){
			Grid g = (Grid) this.state_space.get(t);
			System.out.println("Node "+t+": "+g.heuristicPluscost );
		}
		Collections.sort(this.state_space);
		System.out.println("----------------------------------------");
		for(int t = 0; t< this.state_space.size(); t++){
			Grid g = (Grid) this.state_space.get(t);
			System.out.println("Node "+t+": "+g.heuristicPluscost);
		}
		
	}
	
	public void Astar(int heuristic,Grid node) {
		System.out.println("Astar " + heuristic);
		ArrayList<Grid> children = expand(node);
		for(int u=0; u<children.size(); u++){
			ArrayList<int[]> goalPart = children.get(u).parts.get(0).linked_parts_locations;
			ArrayList<Integer> partsHeuristicValues = new ArrayList<Integer>(); 
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

			if(heuristic == 1){
				for(int z=0; z<partsHeuristicValues.size(); z++){
					nodeHeuristicValue += partsHeuristicValues.get(z);
				}
				children.get(u).heuristicValue = (int)(nodeHeuristicValue/partsHeuristicValues.size());
				children.get(u).heuristicPluscost = children.get(u).heuristicValue + children.get(u).cost;
			}
			else{
				int finalMinHeuristic = partsHeuristicValues.get(0);
				for(int z=1; z<partsHeuristicValues.size(); z++){
					if(partsHeuristicValues.get(z) < finalMinHeuristic){
						finalMinHeuristic = partsHeuristicValues.get(z);
					}
				}
				children.get(u).heuristicValue = finalMinHeuristic;
				children.get(u).heuristicPluscost = children.get(u).heuristicValue + children.get(u).cost;
			}
		}
		
		System.out.println("------------ HEURISTICS---------------");
		for(int t = 0; t< children.size(); t++){
			System.out.println("node "+t+": "+children.get(t).heuristicValue);
		}
		
		this.state_space.addAll(children);
		Collections.sort(this.state_space);
	}
		
	public Object[] iterativeDeepeningSearch(GenericSearchProblem searchProblem, SearchStrategy strategy, boolean visualize, int maxDepth) { 
	        Grid lastEditedNode = null;
	        while (true) {  // Threshold to avoid open loops
				if (this.state_space.isEmpty()) {  // fail
					System.out.println("NO SOLUTION");
					break;
				}
			   System.out.println("############POP############");
			   Grid node = (Grid) this.state_space.remove(0);
			   lastEditedNode = node;
			   System.out.format("popped node: %s\n", node.toString());
			   
			   if (searchProblem.goal_test(node)) { // success
				    System.out.format("GOAL NODE!! %s\n", node);
				    System.out.println(node.parts);
				    Object[] return_list = {node.moves, node.cost};
				    System.out.println("Search Return List: " + Arrays.deepToString(return_list));
				    return return_list;
			   }
	   
	         if(node.depth < maxDepth){
	             System.out.println("Iterative deepening");
	             this.state_space.addAll(0, expand(node));
	         }
	         
	       }
	        /*
	        if(lastEditedNode == null){ //Case for state space initially empty
	            Object [] o = new Object[1];
	            o[0] = null;
	            return o;
	        }
	        */
	        if(expand(lastEditedNode).isEmpty()){
	            Object [] o = new Object[1];
	            o[0] = null;
	            return o;
	        }
	        else{
	            return null;
	        }
	 }




	 public Object[] iterativeDeepening(GenericSearchProblem searchProblem, SearchStrategy strategy, boolean visualize){
		 Object[] ret = null;
	     int i = 0;
	            Grid temp = (Grid) this.state_space.remove(0);
	            System.out.println("HELLLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
	            System.out.println(temp);
	     while(ret == null){
	    	 if(state_space.isEmpty()){
	    		 System.out.println("BYEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
	    		 this.state_space.add(0, temp); 
	    	 }
	          System.out.println("AAAAAAAAAA"+this.state_space);
	                 ret = iterativeDeepeningSearch(searchProblem, strategy, visualize, i);
	                 i++;
	     }
	     if(ret.length == 1 && ret[0]==null){
	         return null;
	     }
	     else{
	         return ret;
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
	
	public int path_cost(Part part, int i, int j, int[] correct) {
		return (Math.abs(i + j + correct[0] + correct[1])) * part.linked_parts_locations.size();
	}
}
