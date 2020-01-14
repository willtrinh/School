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

import java.util.Stack;
import java.util.PriorityQueue;
import java.util.Comparator;

public class MyAI extends Agent
{
	public MyAI ( )
	{
		// ======================================================================
		// YOUR CODE BEGINS
		// ======================================================================
		
		for (int i = 0; i < 7; ++i) {
			for (int j = 0; j < 7; ++j) {
				map[i][j] = new Tile(i, j);
			}
		}
		explored.add(map[curX][curY]);
		avail.add(map[curX + 1][curY]);
		avail.add(map[curX][curY + 1]);
		
		
		// ======================================================================
		// YOUR CODE ENDS
		// ======================================================================
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
		// ======================================================================
		// YOUR CODE BEGINS
		// ======================================================================
		
		if (curX == 0 && curY == 0 && (breeze || stench))
			return Action.CLIMB;
		else if (glitter) {
			explore = false;
			lastAction = Action.GRAB;
														// calculate desiredDir from wayBack stack
			return lastAction;
		}
		else if (lastAction != Action.FORWARD) {
			if (curDir == desiredDir) {
				lastAction = Action.FORWARD;
				return lastAction;
			}
			else {
				switch (desiredDir) {
					case NORTH:
						if (curDir != Direction.WEST) {
							lastAction = Action.TURN_LEFT;
						} else {
							lastAction = Action.TURN_RIGHT;
						}
					break;
					case SOUTH:
						if (curDir != Direction.EAST) {
							lastAction = Action.TURN_LEFT;
						} else {
							lastAction = Action.TURN_RIGHT;
						}
					break;
					case EAST:
						if (curDir != Direction.NORTH) {
							lastAction = Action.TURN_LEFT;
						} else {
							lastAction = Action.TURN_RIGHT;
						}
					break;
					case WEST:
						if (curDir != Direction.SOUTH) {
							lastAction = Action.TURN_LEFT;
						} else {
							lastAction = Action.TURN_RIGHT;
						}
					break;
					default:
						lastAction = Action.FORWARD;
														// update xy (or can deduce next getAction() call from curDir, curX, curY, and lastAction)
					break;
				}
				return lastAction;
		}
		else {
			if (bump) {
				switch (curDir) {
					case NORTH:
						limY = curY + 1;
						break;
					case EAST:
						limX = curX + 1;
						break;
				}
			}
			if (lastAction == Action.SHOOT && scream) WumpusKilled = true;
			// breeze or stench 
		}
		
		// ======================================================================
		// YOUR CODE ENDS
		// ======================================================================
	}
	
	// ======================================================================
	// YOUR CODE BEGINS
	// ======================================================================
	
	private void addAvail (int x, int y) {
		if (!explored.contains(map[x][y]) && !pits.contains(map[x][y]) && !danger.contains(map[x][y]))		// Afterwards if we want, we can have a chance-based addition even if it's in danger list
			avail.add(map[x][y]);
	}
	private class Tile {
		int x, y;						// coord of this tile
		int dist;						// how far this tile is from current position
		int PossWumpus = 0;				// if 2, can be sure there's Wumpus here
		int PossPit = 0;				// if 3, can be sure there's pit here
		Tile(int x, int y) { this.x = x; this.y = y; dist = Math.abs(curX - x) + Math.abs(curY - y); }
	}
	
	private class TileComparator implements Comparator<Tile> {		// Check Stack Overflow on Java Priority Queue
		@Override
		public int compare (Tile t1, Tile t2) {
			if (t1.dist < t2.dist) return -1;
			if (t1.dist > t2.dist) return 1;
			if (t1.PossWumpus < t2.PossWumpus) return -1;
			if (t1.PossWumpus > t2.PossWumpus) return 1;
			if (t1.PossPit < t2.PossPit) return -1;
			if (t1.PossPit > t2.PossPit) return 1;
			return 0;
		}
	}
	
	private enum Direction {
		NORTH,						// Positive Y, up
		SOUTH,						// Negative Y, down
		EAST,						// Positive X, right
		WEST,						// Negative X, left
		DC							// Whichever direction is fine (mostly for desiredDir only)
	}
	
	private Direction curDir = Direction.EAST, desiredDir = Direction.DC;
	
	private Action lastAction;
	
	private int curX = 0, curY = 0, limX = 7, limY = 7;
	
	private boolean WumpusKilled = false, arrowUsed = false, explore = true;	// explore sets to false when this agent doesn't want to explore more of the map, already explored everything it could, or found the gold
	
	private Stack<Tile> wayBack = new Stack();
	
	private Comparator<Tile> tileCompare = new TileComparator();
	
	private PriorityQueue<Tile> explored = new PriorityQueue(1, tileCompare), avail = new PriorityQueue(1, tileCompare), pits = new PriorityQueue(1, tileCompare), danger = new PriorityQueue(1, tileCompare);
	
	private Tile map[][] = new Tile[7][7], toGo;

	// ======================================================================
	// YOUR CODE ENDS
	// ======================================================================
}