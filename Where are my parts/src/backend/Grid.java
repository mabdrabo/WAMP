package backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Grid extends SearchTreeNode{
	//	Randomly generated rectangular grid of squares
	
	public ArrayList<Part> parts;
	public String[][] grid;
	public int width;
	public int height;
	int min = 5;
	int max = 10;
	
	public Grid() {
		this.width = random(min, max);
		this.height = random(min, max);
		this.grid = new String[height][width];
		
		System.out.println("width " + width + " height " + height);
		for (int i=0; i<height; i++)
			for (int j=0; j<width; j++)
				if (i==0 || j==0 || i==height-1 || j==width-1) {
					this.grid[i][j] = "o";		// TODO CHANGE TO f INSTED OF o THIS WAS JUST A TEST
				} else 
					this.grid[i][j] = "e";
		
//		int obstacles_count = random(0, this.width*this.height/2);
		int obstacles_count = 0;
		System.out.println("#obstacles:" + obstacles_count);
		for (int x=0; x<obstacles_count; x++) {
			int i = random(0, this.height-1);
			int j = random(0, this.width-1);
			this.grid[i][j] = "o";
		}
		
//		int parts_count = random(0, this.width*this.height/2);
		int parts_count = 5;
		System.out.println("#parts:" + parts_count);
		parts = new ArrayList<Part>();
		HashSet<Part> hashSetOfParts = new HashSet<>();	// used to remove duplicates while adding the randomly generated robotic parts
		for (int x=0; x<parts_count; x++) {
			int i = random(0, this.height-1);
			int j = random(0, this.width-1);
			if (this.grid[i][j].contains("p"))
				continue;
			else this.grid[i][j] = "p" + x;
			System.out.format("Part[%d][%d]\n", i, j);
			hashSetOfParts.add(new Part(i,j));
		}
		parts_count = hashSetOfParts.size();
		System.out.println("#parts (after removing dubs):" + parts_count);
		parts.addAll(hashSetOfParts);
		
	}
	
	public Grid(Grid grid) {
		this.parts = new ArrayList<Part>(grid.parts);
		this.width = grid.width;
		this.height = grid.height;
		this.grid = new String[grid.height][grid.width];
		this.grid = grid.grid;
	}
	
	
	public static int random(int min, int max) {
		return min + (int)(Math.random() * ((max - min) + 1));
	}
	
	public String toString() {
		return Arrays.deepToString(this.grid);
	}
}
