**Introduction to Artificial Intelligence**
=======================================
**Project 1: Where are my parts?!**
===============================

* ##Brief discussion
In this problem a grid with random size is generated with fence on its borders, a robot is divided into a random number of parts and these parts are spread around in the grid in the empty cells. Our objective was to get all the parts together avoiding the obstacles in the middle using different algorithms. Everything is visualized inorder for the user to understand how it goes.

* ##Search-tree-node ADT
It is an `abstract` object that `implements Comparable` and has variables (`state`, `parent`, `operator`, `depth`, `cost`).

* ##Search-problem ADT
It is an `abstract` object, has variables (`operators`, `initial_state`, `state_space`) and abstract functions (`search()`, `goal_test()`, `path_cost()`)

* ##Where-are-my-parts problem Object
It extends GenericSearchProblem.
has variables:

```
#!java

ArrayList<Grid> closed_states;  //states that have been reached
int expansions;  //number of nodes chosen for expansion during the search
Grid goalNode;  //node that succeeds the goal_test()

```
has enums:

```
#!java

enum SearchStrategy {BF, DF, ID, GR1, GR2, AS1, AS2};
enum Operator {NORTH, SOUTH, EAST, WEST};
  // For part moving (CLEAR: empty square, STOP: part or obstacle ahead, FAIL: fence ahead)
enum TempState {CLEAR, STOP, FAIL};
```

* ##Main Functions
    + ###`Grid GenGrid()`
It calls `Grid()` constructor, which creates a random grid, generates random number of obstacles and parts 
, assigns random locations for them and returns the generated `Grid`.

    + ###`ArrayList<Grid> expand(Grid node)`
It tries to apply each of the four operators on each part in the grid and returns only children whose state was not reached before (in `closed_states`) and can be done (obeying the rules of the game).

    + ###`boolean move(Grid node, Part part, Operator direction)`
It returns `true` if all linked parts of the given part can move in the given direction and stopped by either an other part or an obstacle (not a fence), updates the part location in the grid and updates the cost.

    + ###`void checkToLinkParts(SearchTreeNode node)`
It loops over the list of parts in the Grid node, checks for adjacent parts that are not yet linked and links them, by adding them to the part's `linked_parts_locations` and removing the linked parts from the Grid's `partsList`.

    + ###`boolean goal_test(SearchTreeNode node)`
Since `checkToLinkParts()` removes linked parts from the Grid's parts list, the goal node would be having only part in the list of parts. That's exactly what it does; if the list's size is 1 returns true else returns false.

    + ###`int path_cost(Part part, int i, int j, int[] correct)`
Given a Part (and it's linked_parts_locations), vertical steps (i), horizontal steps (j), since the i or j steps is the place of the stopping condition (obstacle/part) a correcting value of (-1 or 1) should be added to the #steps to correctly position the part adjacent to the stopping object (not over it).
cost equation:
`(Math.abs(i + j + correct[0] + correct[1])) * part.linked_parts_locations.size();`

    + ###`void paintBoard(String[][] gridState)`
This function is responsible for updating the UI with given grid state.
Given a grid's state, this function iterates over the 2-D array, if obstacle it adds a yellow box, if fence adds a red one, if it's empty a white box would be added and if a robotic part it adds a green box with it's ID written in it

    + ###`void visualize()`
This function uses a `SWING.TIMER` to animate the process of reaching the goal state from the initial state

* ##Search Algorithms
    * ###Breadth-First Search (BFS)
It calls `expand()` and adds the returned children (if any) at the end of the queue.

    * ###Depth-First Search (DFS)
It calls `expand()` and adds the returned children (if any) at the beginning of the queue.

    * ###Iterative-Deepening (ID)
I created some helper methods that enables me to limit a maximum depth for the depth-first search and loops continuously until the leaves of the tree are reached if there was no solution found on the way.

     * ###Greedy
It first calls `expand()` but before it adds the returned children to the queue, it evaluates each one of them with 2 heuristic functions and assign the heuristic value to each child of that node then sorts the public queue of states which is called state space according to the heuristic value of the nodes.

    * ###A-Star (A*)
The A* also calls `expand()` and do exactly as the greedy in the mission of assignning heuristic values to the nodes but in the A* it adds the path cost to that node and adds it to the heuristic values using the 2 heuristic functions mentioned in the greedy section.

* ##Heuristic Functions
The first heuristic function we have is the manhattan distance between all the parts on the grid, what it does is that it fix a certain part randomly generated then assume that that part is the goal then try to calculate the manhattan distance between the parts to that goal
then take the average of them to ensure admissibility and assign it to the node which is the grid.

The second heuristic function is the same method as mentioned above in calculating the heuristic function but take the min heuristic value of the parts and assign it to the node in the state space.

* ##Performance Comparison
Breadth-first search is the best when we are searching for completeness ,on the other hand the depth-first search can loop to infinity and dosnt find any solution.
Iterative deepening is complete because it solved the problem of the depth first search but the number of exploartions are better, the A* is the best one in terms of cost better than greedy which just choose the best option now neglecting what is considered over all the best solution.
