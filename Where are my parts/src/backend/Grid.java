package backend;

import java.util.ArrayList;
import java.util.Arrays;

public class Grid extends SearchTreeNode{
	//	Randomly generated rectangular grid of squares
	
	public ArrayList<Part> parts;
	ArrayList<String> moves = null;	// a representation of the sequence of moves to reach the goal (if possible)
	int cost = 0;	// the cost of the solution computed
	int expansions = 0;	// the number of nodes chosen for expansion during the search
	public int width;
	public int height;
	private int min = 5;
	private int max = 5;
	
	public Grid() {
		this.width = random(min, max);
		this.height = random(min, max);
		this.state = new String[height][width];
		this.moves = new ArrayList<String>();
		
		System.out.println("width " + width + " height " + height);
		for (int i=0; i<height; i++)
			for (int j=0; j<width; j++)
				if (i==0 || j==0 || i==height-1 || j==width-1) {
					this.state[i][j] = "b";		// TODO CHANGE TO f INSTED OF b, THIS WAS JUST A TEST
				} else 
					this.state[i][j] = "e";
		
//		int obstacles_count = random(0, this.width*this.height/2);	// TODO just for testing
		int obstacles_count = 0;
		System.out.println("#obstacles:" + obstacles_count);
		for (int x=0; x<obstacles_count; x++) {
			int i = random(0, this.height-1);
			int j = random(0, this.width-1);
			this.state[i][j] = "b";
		}
		
//		int parts_count = random(0, this.width*this.height/2);	// TODO just for testing
		int parts_count = 5;
//		System.out.println("#parts:" + parts_count);
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
	
	public Grid(Grid grid) {
		this.parts = new ArrayList<Part>(grid.parts);
		this.width = grid.width;
		this.height = grid.height;
		this.moves = new ArrayList<String>(grid.moves);
		this.expansions = grid.expansions;
		this.cost = grid.cost;
		this.state = new String[grid.height][grid.width];
		this.state = grid.state.clone();
	}
	
	
	public static int random(int min, int max) {
		return min + (int)(Math.random() * ((max - min) + 1));
	}
	
	public String toString() {
		return Arrays.deepToString(this.state);
	}
}
