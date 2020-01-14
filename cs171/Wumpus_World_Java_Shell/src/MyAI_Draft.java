import java.util.*;
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
import java.util.ArrayList;

public class MyAI extends Agent
{
	
	// Moved data member to where the code allows (it's ok up here, but it's gonna be messy with the function underneath)
	
	public MyAI ( )
	{
		// ======================================================================
		// YOUR CODE BEGINS
		// ======================================================================
		// int score = 0;			// why count score?
		// int maxPenalty = -75;	// why this unused?
		this.agentDir = 0;
		this.agentX = 0;
		this.agentY = 0;
		this.rowDimension = 7;
		this.colDimension = 7;
		this.goldLooted = false;
		this.hasArrow = true;
		this.wumpusKilled = false;
		this.goHome = false;
		// this.start = true;		// only used once
		// this.visitedList = new boolean[7][7];		// why not integrate the explored boolean into the Tile?
		// this.stenchList = new boolean[7][7];
		// this.breezeList = new boolean[7][7];
		// this.possibleWumpusList = new int[7][7];
		// this.possiblePitList = new int[7][7];
		this.board = new Tile[7][7];
		for (int i = 0; i < 7; i++)
		{
			for (int j = 0; j < 7; j++)
				board[i][j] = new Tile(i, j);
		}

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
		
		
		// score--;	// why count score?
		if ((breeze || stench) && agentX == 0 && agentY == 0)
			return Action.CLIMB;
		
		// Update board
		// Set starting tile as visited
		board[agentX][agentY].explored = true;		// integrated explored boolean into Tile class
		
		if (goHome) {								// To reduce cost of updating board, check if agent is going
			// What agent will do to go to (0, 0)?
			
		}
		
		if (wumpusTile != null && hasArrow) {
			// Start shoot sequence at the wumpusTile
			
		}
		
		
		if (glitter && !goldLooted)
		{
			goldLooted = true;
			goHome = true;
			return Action.GRAB;
		}
		
		if (bump)
		{
			if (agentDir == 0) { // facing right
				colDimension = agentX + 1;
			}
			else if (agentDir == 3) { // facing up
				rowDimension = agentY + 1;
			}
			// Remove Tiles in the lists that exceeds the colDim || rowDim
			// Recalculate & choose another closest tile to explore
		}

		if (scream)
		{
			for (int i = 0; i < 7; i++)
			{
				for (int j = 0; j < 7; j++)
					// board[i][j].wumpus = false;
					board[i][j].PossWumpus = 0;
			}
			// Clear wumpusTile list (if there is)
			wumpusKilled = true;
			// Add possibleWumpusList into safeList (what if the wumpus is in the same tile as a pit?)
			// Add stenchList into safelist
		}

		if (breeze)								// I'm thinking of also putting these tiles into a list
		{
			if (!board[agentX][agentY].explored) {
				if (agentX > 0 && agentY > -1) {						// Left tile
					// board[agentX-1][agentY].pit = false;
					if (++(board[agentX - 1][agentY].PossPit) > 2) {
						pits.add(board[agentX - 1][agentY]);
					}
				}
				if (agentX > -1 && agentY > 0) {						// Down tile
					// board[agentX-1][agentY].pit = false;
					if (++(board[agentX][agentY - 1].PossPit) > 2) {
						pits.add(board[agentX][agentY - 1]);
					}
				}
				if (agentX < colDimension - 1 && agentY > -1) {			// Right tile
					// board[agentX-1][agentY].pit = false;
					if (++(board[agentX + 1][agentY].PossPit) > 2) {
						pits.add(board[agentX + 1][agentY]);
					}
				}
				if (agentX > -1 && agentY < rowDimension - 1) {			// Up tile
					// board[agentX-1][agentY].pit = false;
					if (++(board[agentX][agentY + 1].PossPit) > 2) {
						pits.add(board[agentX][agentY + 1]);
					}
				}
			}
		}

		if (stench && !wumpusKilled)				// I'm thinking of also putting these tiles into a list
		{
			if (!board[agentX][agentY].explored) {
				if (agentX > 0 && agentY > -1)							// Left tile
					if (++(board[agentX - 1][agentY].PossWumpus) > 1) {
						wumpusTile = board[agentX - 1][agentY];
					}
				if (agentX > -1 && agentY > 0)							// Down tile
					// board[agentX-1][agentY].wumpus = false;
					if (++(board[agentX][agentY - 1].PossWumpus) > 1) {
						wumpusTile = board[agentX][agentY - 1];
					}
				if (agentX < colDimension - 1 && agentY > -1)			// Right tile
					// board[agentX-1][agentY].wumpus = false;
					if (++(board[agentX + 1][agentY].PossWumpus) > 1) {
						wumpusTile = board[agentX + 1][agentY];
					}
				if (agentX > -1 && agentY < rowDimension - 1)			// Up tile
					// board[agentX-1][agentY].wumpus = false;
					if (++(board[agentX][agentY + 1].PossWumpus) > 1) {
						wumpusTile = board[agentX][agentY + 1];
					}
			}
		}

		if (stench && !WumpusKilled)				// Don't know what your intention is
		{
			int wumpusPos = 0;
			
		}

		// ======================================================================
		// YOUR CODE ENDS
		// ======================================================================
	}

	// ======================================================================
	// YOUR CODE BEGINS
	// ======================================================================

	private class Tile {
		int x, y;						// coord of this tile
		int dist;						// how far this tile is from current position
		int PossWumpus = 0;				// if 2, can be sure there's Wumpus here
		int PossPit = 0;				// if 3, can be sure there's pit here
		boolean explored = false;
		Tile(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	private int tileDist (const Tile & toTile) {		// Function to calculate the exact cost from where agent stands to where the tile is
		if (toTile.x != agentX && toTile.y != agentY) {
			int expectedDir = -1, turnDist = 0;
			if (agentX > toTile.x)
		} else {
			return 0;
		}
	}
	
	private ArrayList<Tile> avail = new ArrayList(),				// List of adjacent safe tiles
		pits = new ArrayList(),										// List of pits found
		danger = new ArrayList();									// List of unsafe tiles (unknown wumpus or pits)
	
	private Stack<Tile> wayBack = new Stack();
	
	private Tile nextTile, wumpusTile;
	
	private Action lastAction;						
	
	/* class tile
	{
		boolean pit;
		boolean wumpus;

		tile()
		{
			this.pit = true;
			this.wumpus = true;
		}
	} */
	
	// Agent Variables
	/* private enum Direction {
		NORTH,						// Positive Y, up
		SOUTH,						// Negative Y, down
		EAST,						// Positive X, right
		WEST,						// Negative X, left
		DC							// Whichever direction is fine (mostly for desiredDir only)
	}
	
	private Direction curDir = Direction.EAST, desiredDir = Direction.DC; */
	
	// private int[][] directions = {{1,0}, {0,-1}, {-1,0}, {0,1}};		// where is this used?
	private boolean hasArrow; // True if the agent can shoot
	private boolean goldLooted; // True if gold was successfuly looted
	private boolean wumpusKilled;
	private int		agentDir;		// The direction the agent is facing: 0 - right, 1 - down, 2 - left, 3 - up
	private int		agentX;			// The column where the agent is located ( x-coord = col-coord )
	private int		agentY;			// The row where the agent is located ( y-coord = row-coord )
	private boolean goHome;		// Exit if conditions met
	// private boolean gameStart;		// Game Start (only used once)
	
	// Data Structure (Not sure what we want to use to store agent's actions/board data)
	// Set, Hashmap, Hashset, 2d Array, LinkedList, Stack...?
	// private boolean[][] visitedList;
	// private boolean[][] stenchList;
	// private boolean[][] breezeList;
	// private int[][] possibleWumpusList;
	// private int[][] possiblePitList;

	// Board Variables
	private int			colDimension;	// The number of columns the game board has
	private int			rowDimension;	// The number of rows the game board has
	private Tile[][]	board;			// The game board
	
	// ======================================================================
	// YOUR CODE ENDS
	// ======================================================================
}
