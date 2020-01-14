// ======================================================================
// FILE:        MyAI.java
//
// AUTHOR:      Abdullah Younis
//
// DESCRIPTION: This file contains your agent class, which you will
//              implement. You are responsible for implementing the
//              'getAction' function and any helper methods you feel you
//              need.
//
// NOTES:       - If you are having trouble understanding how the shell
//                works, look at the other parts of the code, as well as
//                the documentation.
//
//              - You are only allowed to make changes to this portion of
//                the code. Any changes to other portions of the code will
//                be lost when the tournament runs your code.
// ======================================================================

import java.util.*;

import javax.swing.Action;

public class MyAI extends Agent 
{
	private enum Direction {
		RIGHT, DOWN, LEFT, UP;
		public static Direction[] values = Direction.values();

		Direction turnRight() {				// Turn right
			return values[(this.ordinal() + 1) % values.length];
		}
		Direction turnLeft(){				// Turn left
			return values[(this.ordinal() + values.length - 1) % values.length];
		}
	}
	
	private class Tile {
		int[] coord;				// coord of this tile
		int PossWumpus = 0;			// if 2, can be sure there's Wumpus here
		int PossPit = 0;			// if 3, can be sure there's pit here
		Tile(int x, int y) {
			this.coord = new int[] {x, y};
		}
	}
	
	private int tileDist(int xTo, int yTo, int xFrom, int yFrom, boolean turns) {
		int diffX = xTo - xFrom,
			diffY = yTo - yFrom;
		if (turns) {
			int	turnCost = 0,
				curDir = direction.ordinal();
			// Turning cost
			if (diffX != 0 || diffY != 0) {
				if (diffX < 0) {							// Need to face left (Dir = 2)
					if (curDir == Direction.UP.ordinal() || curDir == Direction.DOWN.ordinal())
						turnCost += 1;
					else if (curDir == Direction.RIGHT.ordinal())
						turnCost += 2;
					else turnCost += 0;
					curDir = 2;								// curDir won't be changed when returned to caller
				} else if (diffX > 0) {						// Need to face right (Dir = 0)
					if (curDir == Direction.UP.ordinal() || curDir == Direction.DOWN.ordinal())
						turnCost += 1;
					else if (curDir == Direction.RIGHT.ordinal())
						turnCost += 0;
					else turnCost += 2;
					curDir = 0;
				}											// In case it's facing the same dir as expected, turnCost = 0, so I don't write it here.
				
				if (turnCost < 2) {
					if (diffY < 0) {						// Need to face down (Dir = 1)
						if (curDir == Direction.RIGHT.ordinal() || curDir == Direction.LEFT.ordinal())
							turnCost += 1;
						else if (curDir == Direction.UP.ordinal())
							turnCost += 2;
						else turnCost += 0;
					} else if (diffY > 0) {					// Need to face up (Dir = 3)
						if (curDir == Direction.RIGHT.ordinal() || curDir == Direction.LEFT.ordinal())
							turnCost += 1;
						else if (curDir == Direction.UP.ordinal())
							turnCost += 0;
						else turnCost += 2;
					}
				}
			} else
				return 0;
			// Estimated shortest dist = cardinal dist + turning cost
			return Math.abs(diffX) + Math.abs(diffY) + turnCost;
		} else
			return Math.abs(diffX) + Math.abs(diffY);
	}
	
	private int findListInt (ArrayList<int[]> searchList, int x, int y) {
		int[] temp;
		for (int i = 0; i < searchList.size(); ++i) {
			temp = searchList.get(i);
			if ((temp[0] == x) && (temp[1] == y))
				return i;
		}
		return -1;
	}
	
	private int findListTile (ArrayList<Tile> searchList, int x, int y) {
		Tile temp;
		for (int i = 0; i < searchList.size(); ++i) {
			temp = searchList.get(i);
			if ((temp.coord[0] == x) && (temp.coord[1] == y))
				return i;
		}
		return -1;
	}
	
	private boolean checkPit(int pitX, int pitY) {
		if (!explored[pitX][pitY] && findListInt(available, pitX, pitY) < 0) {
			int index = 0;
			// Make sure this tile is in pit list
			if ((index = findListInt(pits, pitX, pitY)) < 0) {
				// Make sure this tile is in dangerous list
				Tile toCheck;
				if ((index = findListTile(dangerous, pitX, pitY)) < 0) {
					toCheck = new Tile(pitX, pitY);
					// Check if the tile is on the edges & corners (since we can't check adjacent tiles out of bound, we assume 1 breeze in those)
					if (!(pitX > 0 && pitX + 1 < colDimension && pitY > 0 && pitY + 1 < rowDimension))
						toCheck.PossPit += 1;
					dangerous.add(toCheck);
				} else
					toCheck = dangerous.get(index);
				// Start processing
				toCheck.PossPit += 1;
				// If 3, def pit
				if (toCheck.PossPit > 2) {
					pits.add(toCheck.coord);
					dangerous.remove(index);
				}
			}
			return true;
		}
		return false;
	}
	
	private boolean checkWumpus(int wumpusX, int wumpusY) {
		if (!explored[wumpusX][wumpusY] && findListInt(available, wumpusX, wumpusY) < 0) {
			int index = 0;
			Tile toCheck;
			if ((index = findListTile(dangerous, wumpusX, wumpusY)) < 0) {
				toCheck = new Tile(wumpusX, wumpusY);
				dangerous.add(toCheck);
			}
			else
				toCheck = dangerous.get(index);
			// Start processing
			toCheck.PossWumpus += 1;
			// If 2, def wumpus
			if (toCheck.PossWumpus > 1) {
				wumpus = toCheck;
				wumpusKnown = true;
			}
			return true;
		}
		return false;
	}
	
	private void agentMoved() {
		// General update
		// route.add(new int[] {agentX, agentY});
		switch (direction) {						// Directions additions and subtractions, if wrong, will be reverted if a bump is felt
			case DOWN:
				--agentY;
				break;
			case LEFT:
				--agentX;
				break;
			case UP:
				++agentY;
				break;
			case RIGHT:
				++agentX;
				break;
		}
	}
	
	private void goAdj(Direction adjDest) {
		switch ((direction.ordinal() - adjDest.ordinal()) % 4) {
			case 3:					// Agent: UP, desired: RIGHT
			case -1:
				actionToTile.add(Action.TURN_RIGHT);
				actionToTile.add(Action.FORWARD);
				break;
			case 2:
			case -2:
				actionToTile.add(Action.TURN_RIGHT);
				actionToTile.add(Action.TURN_RIGHT);
				actionToTile.add(Action.FORWARD);
				break;
			case 1:					// Agent : RIGHT, desired: UP
			case -3:
				actionToTile.add(Action.TURN_LEFT);
				actionToTile.add(Action.FORWARD);
				break;
			case 0:
				actionToTile.add(Action.FORWARD);
				break;
			default:
				break;
		}
	}
	
	private boolean findRoute(ArrayList<int[]> tempList, int[] from) {
		if (from[0] == agentX && from[1] == agentY)
			
			return true;
		else {
			
			// IMPORTANT NOTE: May be stuck with local minimums
			
			int tempDist;
			ArrayList<int[]> sortedDist = new ArrayList<>(); 
			// Check advancing left tile
			if (from[0] > 0 && explored[from[0] - 1][from[1]] && findListInt(tempList, from[0] - 1, from[1]) < 0) {
				int i = 0;
				tempDist = tileDist(from[0] - 1, from[1], agentX, agentY, false);
				for (i = 0; i < sortedDist.size() && sortedDist.get(i)[2] <= tempDist; ++i);
				sortedDist.add(i, new int[] {from[0] - 1, from[1], tempDist});
			}
			// Check advancing right tile
			if (from[0] + 1 < colDimension && explored[from[0] + 1][from[1]] && findListInt(tempList, from[0] + 1, from[1]) < 0) {
				int i = 0;
				tempDist = tileDist(from[0] + 1, from[1], agentX, agentY, false);
				for (i = 0; i < sortedDist.size() && sortedDist.get(i)[2] <= tempDist; ++i);
				sortedDist.add(i, new int[] {from[0] + 1, from[1], tempDist});
			}
			// Check advancing down tile
			if (from[1] > 0 && explored[from[0]][from[1] - 1] && findListInt(tempList, from[0], from[1] - 1) < 0) {
				int i = 0;
				tempDist = tileDist(from[0], from[1] - 1, agentX, agentY, false);
				for (i = 0; i < sortedDist.size() && sortedDist.get(i)[2] <= tempDist; ++i);
				sortedDist.add(i, new int[] {from[0], from[1] - 1, tempDist});
			}
			// Check advancing up tile
			if (from[1] + 1 < rowDimension && explored[from[0]][from[1] + 1] && findListInt(tempList, from[0], from[1] + 1) < 0) {
				int i = 0;
				tempDist = tileDist(from[0], from[1] + 1, agentX, agentY, false);
				for (i = 0; i < sortedDist.size() && sortedDist.get(i)[2] <= tempDist; ++i);
				sortedDist.add(i, new int[] {from[0], from[1] + 1, tempDist});
			}
			if (sortedDist.isEmpty()) {
				return false;
			}
			else {
				boolean result = false;
				int[] advancing = new int[] {sortedDist.get(0)[0], sortedDist.get(0)[1]};
				sortedDist.remove(0);
				tempList.add(advancing);
				// Check if the lowest dist tile can reach the distance. If not, try the next one until it's out of adj tiles
				while (!(result = findRoute(tempList, advancing))) {
					tempList.remove(tempList.size() - 1);
					if (!sortedDist.isEmpty()) {
						advancing = new int[] {sortedDist.get(0)[0], sortedDist.get(0)[1]};
						sortedDist.remove(0);
						tempList.add(advancing);
					} else
						break;
				}
				return result;
			}
		}
	}
	
	private void goLowestAvailable() {
		if (moveRoute.isEmpty()){
			int[] tileCoord, minCoord;
			if (goHome || available.isEmpty()) {
				goHome = true;
				minCoord = new int[] {0, 0};
				moveRoute.add(minCoord);
			} else {						// Explored all we can, finding way home
				// Find closest available tile
				tileCoord = available.get(0);
				minCoord = tileCoord;
				int minDist = tileDist(tileCoord[0], tileCoord[1], agentX, agentY, true), index = 0;
				for (int i = 1; i < available.size(); ++i) {
					tileCoord = available.get(i);
					int tempDist = tileDist(tileCoord[0], tileCoord[1], agentX, agentY, true);
					if (minDist > tempDist) {
						minDist = tempDist;
						minCoord = tileCoord;
						index = i;
					}
				}
				moveRoute.add(available.remove(index));
			}
			if(findRoute(moveRoute, minCoord)) {
				moveRoute.remove(moveRoute.size() - 1);
			} else {
				System.out.println("Unable to find a route. Currently known: ");
				for (int i = 0; i < moveRoute.size(); ++i)
					System.out.println("\t" + moveRoute.get(i)[0] + ", " + moveRoute.get(i)[1]);
				System.out.println();
				return;
			}
		}
		
		// Search sequence to move there
		int[] lowestAdjTile = moveRoute.remove(moveRoute.size() - 1);
		Direction adjDir = Direction.LEFT;
		if (agentX - lowestAdjTile[0] > 0) {
			adjDir = Direction.LEFT;
		} else if (agentY - lowestAdjTile[1] > 0) {
			adjDir = Direction.DOWN;
		} else if (agentX - lowestAdjTile[0] < 0) {
			adjDir = Direction.RIGHT;
		}  else {
			adjDir = Direction.UP;
		}
		goAdj(adjDir);
	}
	
	private static final int MAX_SIZE = 7;
	private static final int MAX_DIST = MAX_SIZE * MAX_SIZE;
	private Direction direction;
	// private ArrayList<int[]> route;
	private ArrayList<int[]> moveRoute;
	private LinkedList<Action> actionToTile;		// [comment to delete] taken from reference, slightly modified
	private ArrayList<int[]> available;
	private ArrayList<int[]> pits;
	private ArrayList<Tile> dangerous;
	private Tile wumpus;							// Single tile where wumpus is
	private boolean goHome;
	private boolean hasArrow;						// True if the agent can shoot
	private boolean goldLooted;						// True if gold was successfuly looted
	private boolean wumpusKnown;
	private boolean wumpusKilled;
	private int		agentX;							// The column where the agent is located ( x-coord = col-coord )
	private int		agentY;							// The row where the agent is located ( y-coord = row-coord )
	private int		colDimension;					// The number of columns the game board has (i.e. X)
	private int		rowDimension;					// The number of rows the game board has (i.e. Y)
	private boolean[]	adjAvail;
	private boolean[][] explored;
	
	public MyAI() 
	{
		this.rowDimension = MAX_SIZE;
		this.colDimension = MAX_SIZE;
		this.goldLooted = false;
		this.hasArrow = true;
		this.wumpusKnown = false;
		this.wumpusKilled = false;
		this.goHome = false;
		this.direction = Direction.RIGHT;
		this.agentX = 0;
		this.agentY = 0;
		// this.route = new ArrayList<>();
		this.moveRoute = new ArrayList<>();
		this.available = new ArrayList<>();
		this.pits = new ArrayList<>();
		this.dangerous = new ArrayList<>();
		this.actionToTile = new LinkedList<>();
		this.explored = new boolean[MAX_SIZE][MAX_SIZE];
		for (int i = 0; i < MAX_SIZE; ++i)
			for (int j = 0; j < MAX_SIZE; ++j)
				explored[i][j] = false;
		adjAvail = new boolean[] {true, false, false, true};	// 0 - right, 1 - down, 2 - left, 3 - up
	}

	public Action getAction
	(
		boolean stench,
		boolean breeze,
		boolean glitter,
		boolean bump,
		boolean scream
	) 

	{
		if (glitter && !goldLooted) {
			goldLooted = true;
			goHome = true;
			// Flush moveRoute & actionToTile
			while (!moveRoute.isEmpty())
				moveRoute.remove(moveRoute.size() - 1);
			actionToTile.clear();
			return Action.GRAB;
		}
		if (scream) {
			wumpusKilled = true;
			wumpus.PossWumpus = 0;
			if (wumpus.PossPit == 0) {
				dangerous.remove(wumpus);
				available.add(wumpus.coord);
			}
			for (int i = 0; i < dangerous.size(); ++i) {
				if (dangerous.get(i).PossPit == 0) {
					available.add(dangerous.remove(i).coord);
				}
			}
		}
		
		if ((goHome || breeze || stench) && agentX == 0 && agentY == 0)
			return Action.CLIMB;
		
		if (actionToTile.size() > 0) {					// Agent will move as long as there's an Action on the queue
			Action takeAction = actionToTile.remove();
			switch (takeAction) {
				case FORWARD:
					agentMoved();
					break;
				case TURN_LEFT:
					direction = direction.turnLeft();
					break;
				case TURN_RIGHT:
					direction = direction.turnRight();
					break;
				case SHOOT:
					hasArrow = false;
					break;
				default:
					break;
			}
			return takeAction;
		}
		
		if (bump) {
			// route.remove(route.size() - 1);
			if (direction == Direction.RIGHT)
				colDimension = agentX--;
			else if (direction == Direction.UP)
				rowDimension = agentY--;
			else if (direction == Direction.DOWN)
				agentY = 0;
			else
				agentX = 0;
			// Scan available list for tiles with values >= dimension limit & remove
			for (int i = 0; i < available.size(); ++i) {
				int[] tempCoord = available.get(i);
				if (tempCoord[0] < 0 || tempCoord[0] >= colDimension ||
					tempCoord[1] < 0 || tempCoord[1] >= rowDimension) {
					available.remove(i);
					--i;
				}
			}
			// Search a new tile from available list & start sequence to move there
			while(!moveRoute.isEmpty())
				moveRoute.remove(moveRoute.size() - 1);
		}
		
		adjAvail = new boolean[] {true, true, true, true};
		
		if (goHome) {
			// At base
			if(agentX == 0 && agentY == 0){
				return Action.CLIMB;
			}
		}
		
		if (breeze && !explored[agentX][agentY]) {
			// Tiles that are adjacent to breeze can be pit
			if (agentX > 0 && checkPit(agentX - 1, agentY))
				adjAvail[2] = false;
			if (agentX + 1 < colDimension && checkPit(agentX + 1, agentY))
				adjAvail[0] = false;
			if (agentY > 0 && checkPit(agentX, agentY - 1))
				adjAvail[1] = false;
			if (agentY + 1 < rowDimension && checkPit(agentX, agentY + 1))
				adjAvail[3] = false;
		}
		if (stench && !wumpusKilled && !explored[agentX][agentY]) {
				// Update list of tiles (NOTE: don't assume out of bounds to be wumpus or this will not yield correct result)
				if (agentX > 0 && checkWumpus(agentX - 1, agentY))
					adjAvail[2] = false;
				if (agentX + 1 < colDimension && checkWumpus(agentX + 1, agentY))
					adjAvail[0] = false;
				if (agentY > 0 && checkWumpus(agentX, agentY - 1))
					adjAvail[1] = false;
				if (agentY + 1 < rowDimension && checkWumpus(agentX, agentY + 1))
					adjAvail[3] = false;
		}
		if (wumpusKnown && hasArrow && !goHome) {
			// Start shoot sequence
			Direction nextDirection = Direction.LEFT;
			if (agentX - wumpus.coord[0] > 0) {
				nextDirection = Direction.LEFT;
			} else if (agentY - wumpus.coord[1] > 0) {
				nextDirection = Direction.DOWN;
			} else if (agentX - wumpus.coord[0] < 0) {
				nextDirection = Direction.RIGHT;
			}  else {
				nextDirection = Direction.UP;
			}
			switch ((direction.ordinal() - nextDirection.ordinal()) % 4) {
				case 3:					// Agent: UP, desired: RIGHT
				case -1:
					actionToTile.add(Action.TURN_RIGHT);
					actionToTile.add(Action.SHOOT);
					break;
				case 2:
				case -2:
					actionToTile.add(Action.TURN_RIGHT);
					actionToTile.add(Action.TURN_RIGHT);
					actionToTile.add(Action.SHOOT);
					break;
				case 1:					// Agent : RIGHT, desired: UP
				case -3:
					actionToTile.add(Action.TURN_LEFT);
					actionToTile.add(Action.SHOOT);
					break;
				case 0:
					actionToTile.add(Action.SHOOT);
					break;
				default:
					break;
			}
			Action takeAction = actionToTile.remove();
			switch (takeAction) {
				case FORWARD:
					agentMoved();
					break;
				case TURN_LEFT:
					direction = direction.turnLeft();
					break;
				case TURN_RIGHT:
					direction = direction.turnRight();
					break;
				case SHOOT:
					hasArrow = false;
					break;
				default:
					break;
			}
			return takeAction;
		}
		// If the current tile is safe, adjacent tiles are safe too.
		if (!(breeze || stench) && !explored[agentX][agentY]) {
			int index = 0;
			if (agentX > 0) {
				if ((index = findListInt(pits, agentX - 1, agentY)) > -1)
					pits.remove(index);
				else if ((index = findListTile(dangerous, agentX - 1, agentY)) > -1)
					dangerous.remove(index);
			}
			if (agentX + 1 < colDimension) {
				if ((index = findListInt(pits, agentX + 1, agentY)) > -1)
					pits.remove(index);
				else if ((index = findListTile(dangerous, agentX + 1, agentY)) > -1)
					dangerous.remove(index);
			}
			if (agentY > 0) {
				if ((index = findListInt(pits, agentX, agentY - 1)) > -1)
					pits.remove(index);
				else if ((index = findListTile(dangerous, agentX, agentY - 1)) > -1)
					dangerous.remove(index);
			}
			if (agentY + 1 < rowDimension) {
				if ((index = findListInt(pits, agentX, agentY + 1)) > -1)
					pits.remove(index);
				else if ((index = findListTile(dangerous, agentX, agentY + 1)) > -1)
					dangerous.remove(index);
			}
		}
		
		// Update available tiles to include safe adjacent tiles
		if (!explored[agentX][agentY]) {
			if (agentX > 0 && !explored[agentX - 1][agentY] && findListInt(available, agentX - 1, agentY) < 0 && adjAvail[2]) {
				available.add(new int[] {agentX - 1, agentY});
			}
			if (agentX + 1 < colDimension && !explored[agentX + 1][agentY] && findListInt(available, agentX + 1, agentY) < 0 && adjAvail[0]) {
				available.add(new int[] {agentX + 1, agentY});
			}
			if (agentY > 0 && !explored[agentX][agentY - 1] && findListInt(available, agentX, agentY - 1) < 0 && adjAvail[1]) {
				available.add(new int[] {agentX, agentY - 1});
			}
			if (agentY + 1 < rowDimension && !explored[agentX][agentY + 1] && findListInt(available, agentX, agentY + 1) < 0 && adjAvail[3]) {
				available.add(new int[] {agentX, agentY + 1});
			}
		}
		
		// More general updates (agentMoved should not affect this)
		explored[agentX][agentY] = true;
		
		// Choose a tile with lowest cost to explore & decide on the course of actions
		goLowestAvailable();
		Action takeAction = actionToTile.remove();
		switch (takeAction) {
			case FORWARD:
				agentMoved();
				break;
			case TURN_LEFT:
				direction = direction.turnLeft();
				break;
			case TURN_RIGHT:
				direction = direction.turnRight();
				break;
			case SHOOT:
				hasArrow = false;
				break;
			default:
				break;
		}
		return takeAction;
	}
}