package backend;

public abstract class SearchTreeNode {

	String state;
	SearchTreeNode parent;
	int operator;
	int depth;
	int path_cost;
	
	public SearchTreeNode() {
		
	}
}
