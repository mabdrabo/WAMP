package backend;

import java.util.Queue;

public abstract class GenericSearchProblem {
	
	enum operators {};	// A set of operators, or actions, available to the agent
	SearchTreeNode initial_state;
	Queue<SearchTreeNode> state_space;	// the set of states reachable from the initial state by any sequence of actions
	
	public abstract boolean goal_test(SearchTreeNode node);	//	the agent applies to a state to determine if it is a goal state.
	
	public abstract void path_cost();	//	assigns cost to a sequence of actions.
	//	Typically, it is the sum of the costs of individual actions in the sequence.

}
