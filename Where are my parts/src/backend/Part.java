package backend;

import java.util.ArrayList;

public class Part {
	
	int[] location;
	ArrayList<int[]> linked_parts;

	public Part(int i, int j) {
		location = new int[2];
		location[0] = i;
		location[1] = j;
		linked_parts = new ArrayList<int[]>();
		linked_parts.add(new int[] {i, j});
	}
	
	public void linkPart(Part part) {
		linked_parts.add(new int[] {part.location[0], part.location[1]});
	}
	
	public String toString() {
		return String.format("i:%d j:%d", this.location[0], this.location[1]);
	}

}
