package backend;

import java.util.ArrayList;

public class Part extends SearchTreeNode{
	
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

}
