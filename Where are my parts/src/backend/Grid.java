package backend;

import java.util.ArrayList;
import java.util.Arrays;

public class Grid extends SearchTreeNode {
	//	Randomly generated rectangular grid of squares
	
	public ArrayList<Part> parts;
	ArrayList<String> moves = null;	// a representation of the sequence of moves to reach the goal (if possible)
	public int cost = 0;	// the cost of the solution computed
	public int costPlusHeuristic = 0;	// the value of cost + heuristic value (used for both Greedy (heuristic only) and A*)
	public int width;
	public int height;
	public int heuristicValue;
	public int heuristicPluscost;
	private int min = 6;
	private int max = 6;
	
	public Grid() {
		this.width = random(min, max);
		this.height = random(min, max);
		this.state = new String[height][width];
		this.moves = new ArrayList<String>();
		
		System.out.println("width " + width + " height " + height);
		for (int i=0; i<height; i++)
			for (int j=0; j<width; j++)
				if (i==0 || j==0 || i==height-1 || j==width-1) {
					this.state[i][j] = "f";		// TODO CHANGE TO f INSTED OF b, THIS WAS JUST A TEST
				} else 
					this.state[i][j] = "e";
		
		int obstacles_count = random(0, this.width*this.height/2);
//		int obstacles_count = 0;	// TODO just for testing
		System.out.println("#obstacles:" + obstacles_count);
		for (int x=0; x<obstacles_count; x++) {
			int i = random(0, this.height-1);
			int j = random(0, this.width-1);
			this.state[i][j] = "b";
		}
		
		int parts_count = random(0, this.width*this.height/2);
//		int parts_count = 5;		// TODO just for testing
		System.out.println("#parts:" + parts_count);
		parts = new ArrayList<Part>();
		for (int x=0; x<parts_count; x++) {
			int i = random(0, this.height-1);
			int j = random(0, this.width-1);
			if (this.state[i][j].contains("p"))
				continue;
			this.parts.add(new Part(i,j));
			this.state[i][j] = "p" + this.parts.get(parts.size()-1).id;
		}
	}
	
	public Grid(boolean clone) {
	}
	
	@Override
	public Grid clone() {
		Grid grid = new Grid(true);
		grid.cost = this.cost;
		grid.depth = this.depth;
		grid.width = this.width;
		grid.height = this.height;
		grid.state = new String[height][width];
		for (int i=0; i<this.height; i++)
			for (int j=0; j<this.width; j++)
				grid.state[i][j] = this.state[i][j];
		grid.moves = new ArrayList<String>();
		for (int i=0; i<this.moves.size(); i++)
			grid.moves.add(new String (this.moves.get(i)));
		grid.parts = new ArrayList<Part>();
		for (int i=0; i<this.parts.size(); i++)
			grid.parts.add(this.parts.get(i).clone());
		if (this.parent != null)
			grid.parent = ((Grid) this.parent).clone();
		return grid;
	}
	
	public void replacePart(int id, Part part) {
		for (int i=0; i<this.parts.size(); i++)
			if (this.parts.get(i).id == id) {
				this.parts.remove(i);
				this.parts.add(part);
			}
	}
	
	public boolean in(ArrayList<Grid> closed_states) {
		for (int i=0; i<closed_states.size(); i++)
			if (this.equals(closed_states.get(i)))
				return true;
		return false;
	}
	
	public static int random(int min, int max) {
		return min + (int)(Math.random() * ((max - min) + 1));
	}
	
	
	@Override
	public boolean equals(Object grid) {
		return Arrays.deepToString(this.state).equals(Arrays.deepToString(((Grid) grid).state));
	}
	
	@Override
	public String toString() {
		return Arrays.deepToString(this.state);
	}

	@Override
	public int compareTo(SearchTreeNode node) {
		Grid grid = (Grid) node;
		return (this.costPlusHeuristic > grid.costPlusHeuristic)? 1 : (this.costPlusHeuristic < grid.costPlusHeuristic)? -1 : 0; 
	}
}
