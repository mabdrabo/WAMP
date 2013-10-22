package backend;

import java.util.ArrayList;

public class Grid {
	//	Randomly generated rectangular grid of squares
	
	public ArrayList<Part> parts;
	public char[][] grid;
	public int width;
	public int height;
	int min = 10;
	int max = 25;
	
	public Grid() {
		this.width = random(min, max);
		this.height = random(min, max);
		this.grid = new char[height][width];
		
		System.out.println("width " + width + " height " + height);
		for (int i=0; i<height; i++)
			for (int j=0; j<width; j++)
				if (i==0 || j==0 || i==height-1 || j==width-1) {
					System.out.println("i " + i + " j " + j);
					this.grid[i][j] = 'f';
				}
		
		int obstacles_count = random(0, this.width*this.height/2);
		for (int x=0; x<obstacles_count; x++) {
			int i = random(0, this.height-1);
			int j = random(0, this.width-1);
			this.grid[i][j] = 'o';
		}
		
		int parts_count = random(0, this.width*this.height/2);
		parts = new ArrayList<Part>();
		for (int x=0; x<parts_count; x++) {
			int i = random(0, this.height-1);
			int j = random(0, this.width-1);
			this.grid[i][j] = 'p';
			parts.add(new Part(i,j));
		}
		
		for (int x=0; x<parts.size(); x++) {
			for (int z=0; z<parts.size(); z++) {
				Part p1 = parts.get(x);
				Part p2 = parts.get(z);

				if (p1.location[0] != p2.location[0]
						&& p1.location[0] != p2.location[0]) {
					if ((p1.location[0] == p2.location[0]) &&
							(p1.location[1] == p2.location[1] + 1 || 
							p1.location[1] == p2.location[1] - 1)) {
						p1.linkPart(p2);
						parts.remove(p2);
					}
					else
						if ((p1.location[1] == p2.location[1]) &&
								(p1.location[0] == p2.location[0] + 1 || 
								p1.location[0] == p2.location[0] - 1)) {
							p1.linkPart(p2);
							parts.remove(p2);
						}
				}
			}
		}
	}
	
	public static int random(int min, int max) {
		return min + (int)(Math.random() * ((max - min) + 1));
	}
}
