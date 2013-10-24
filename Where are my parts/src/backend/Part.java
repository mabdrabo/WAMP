package backend;

import java.util.ArrayList;

public class Part {
	
	int[] location;
	ArrayList<Part> linked_parts;

	public Part(int i, int j) {
		location = new int[2];
		location[0] = i;
		location[1] = j;
		linked_parts = new ArrayList<>();
	}
	
	public void linkPart(Part part) {
		this.linked_parts.add(part);
	}
	
	public void step(Grid grid, Operator direction) {
		String tag = grid.grid[this.location[0]][this.location[1]];  
		grid.grid[this.location[0]][this.location[1]] = "e";
		switch (direction) {
		
		case NORTH:
			this.location[0] = this.location[0] - 1;
			for (Part part : linked_parts)
				part.location[0] = part.location[0] - 1;
			break;

		case SOUTH:
			this.location[0] = this.location[0] + 1;
			for (Part part : linked_parts)
				part.location[0] = part.location[0] + 1;
			break;
			
		case EAST:
			this.location[1] = this.location[1] + 1;
			for (Part part : linked_parts)
				part.location[1] = part.location[1] + 1;
			break;
			
		case WEST:
			this.location[1] = this.location[1] - 1;
			for (Part part : linked_parts)
				part.location[1] = part.location[1] - 1;
			break;
		}
		grid.grid[this.location[0]][this.location[1]] = tag;
	}
	
	public String toString() {
		return String.format("i:%d j:%d", this.location[0], this.location[1]);
	}

}
