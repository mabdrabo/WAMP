package backend;

import java.util.ArrayList;

public abstract class GenericSearchProblem {
	
	Operator[] operators;	// A set of operators, or actions, available to the agent
	SearchTreeNode initial_state;
	ArrayList<SearchTreeNode> state_space;	// the set of states reachable from the initial state by any sequence of actions
	
	public GenericSearchProblem() {
		this.state_space = new ArrayList<SearchTreeNode>();
	}
	
	public abstract boolean goal_test(SearchTreeNode node);	//	the agent applies to a state to determine if it is a goal state.
	
	public abstract void path_cost();	//	assigns cost to a sequence of actions.
	//	Typically, it is the sum of the costs of individual actions in the sequence.

}
