package backend;

import java.util.ArrayList;
import java.util.Arrays;

public class Part {
	
	int id;
	ArrayList<int[]> linked_parts_locations;
	static int count = 0;

	public Part(int i, int j) {
		this.id = count++;
		linked_parts_locations = new ArrayList<int[]>();
		linked_parts_locations.add(new int[] {i, j});
	}
	
	public void linkPart(Grid grid, Part part) {
		for (int[] loc : part.linked_parts_locations) {
			if (!this.contains(loc)) {
				System.out.println("Link success");
				this.linked_parts_locations.add(loc);
			}
		}
		grid.parts.remove(grid.parts.indexOf(part));
	}
	
	public boolean hasPart(int[] location) {
		for (int[] loc : this.linked_parts_locations)
			if (loc[0] == location[0] && loc[1] == location[1])
				return true;
		return false;
	}
	
	public String toString() {
		return "P" + this.id + ": " + Arrays.deepToString(linked_parts_locations.toArray());
	}

	public boolean contains(Object o) {
		int[] location = (int[]) o;
		for (int[] loc : this.linked_parts_locations) {
			if (loc[0] == location[0] && loc[1] == location[1])
				return true;
		}
		return false;
		
	}
}
