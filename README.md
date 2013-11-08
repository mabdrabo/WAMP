**Introduction to Artificial Intelligence**
=======================================
**Project 1: Where are my parts?!**
===============================

* ##Brief discussion
TODO

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
Given a Part (and it's linked_parts_locations), vertical steps (i), horizontal steps (j), since the i or j steps is the place of the stopping condition (obstacle/part) a correcting value of (-1 or 1) should be added to the #steps to correctly position the part adjacent to the stopping object (not over it)

cost equation:
```
#!java

(Math.abs(i + j + correct[0] + correct[1])) * part.linked_parts_locations.size();
```

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
TODO

    * ###Greedy
TODO

    * ###A-Star (A*)
TODO

* ##Heuristic Functions
TODO

* ##Performance Comparison
TODO