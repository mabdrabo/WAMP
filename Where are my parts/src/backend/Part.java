package backend;

import java.util.ArrayList;
import java.util.Arrays;

public class Part {
	
	int id;
	ArrayList<int[]> linked_parts_locations;
	static int count = 0;

	public Part(int i, int j) {
		this.id = count++;
		this.linked_parts_locations = new ArrayList<int[]>();
		linked_parts_locations.add(new int[] {i, j});
	}
	
	public Part() {
		this.linked_parts_locations = new ArrayList<int[]>();
	}
	
	public void linkPart(Grid grid, Part part) {
		for (int[] loc : part.linked_parts_locations) {
			if (!this.hasPart(loc)) {
				this.linked_parts_locations.add(loc);
			}
		}
		if (grid.parts.indexOf(part) > -1)
			grid.parts.remove(grid.parts.indexOf(part));
	}
	
	public boolean hasPart(int[] location) {
		for (int[] loc : this.linked_parts_locations)
			if (loc[0] == location[0] && loc[1] == location[1])
				return true;
		return false;
	}
	
	public boolean contains(Object part_location) {
		int[] location = (int[]) part_location; 
		for (int[] loc : this.linked_parts_locations)
			if (loc[0] == location[0] && loc[1] == location[1])
				return true;
		return false;
		
	}


	@Override
	public Part clone() {
		Part part = new Part();
		part.id = this.id;		
		for (int x=0; x<this.linked_parts_locations.size(); x++)
			part.linked_parts_locations.add(new int[] {this.linked_parts_locations.get(x)[0], this.linked_parts_locations.get(x)[1]});
		return part;
	}
	
	@Override
	public String toString() {
		return "P" + this.id + ": " + Arrays.deepToString(linked_parts_locations.toArray());
	}

}
