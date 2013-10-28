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
		linked_parts.add(this);
	}
	
	public void linkPart(Part part) {
		this.linked_parts.add(part);
	}
	
	public String toString() {
		return String.format("i:%d j:%d", this.location[0], this.location[1]);
	}

}
