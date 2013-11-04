package backend;

public abstract class SearchTreeNode {

	String[][] state;
	SearchTreeNode parent;
	Operator operator;
	int depth;
	int path_cost;
	
}
