package backend;

enum SearchStrategy {
	BREADTH_FIRST, DEPTH_FIRST, ITERATIVE_DEEPENING, GREEDY_1, GREEDY_2, A_STAR_1, A_STAR_2
	};
	
enum Operator {
	NORTH, SOUTH, EAST, WEST
	};

public abstract class GenericSearchProblem {
	
	int operators;	// A set of operators, or actions, available to the agent
	int[][] initial_state;
	int[][] state_space;	// the set of states reachable from the initial state by any sequence of actions
	
	public GenericSearchProblem() {
		
	}
	
	public boolean goal_test() {
	//	the agent applies to a state to determine if it is a goal state
		return false;
	}
	
	public void path_cost() {
	//	assigns cost to a sequence of actions.
	//	Typically, it is the sum of the costs of individual actions in the sequence.
	}

}
