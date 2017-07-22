//---------------------------------------------------------------80 columns---|

/* Grid class
 * ----------
 * This class just a thin object cover around a 2-dimensional array.
 * The only thing interesting about it is that it conveniently does
 * the transformations from a "Location" object to the row/col index
 * into the 2-d array. Nothing too special about that, but it is
 * handy since the things, squares, and game tend to refer to position
 * via a Location object and often need to refer into the grid to get
 * the contents at that location.
 * You should not need to edit this class, you only need to understand
 * how to use it.
 */

public class Tracking {
	// a few private instance variables
	private final int moves = 1000;
	private int currentMove, numBoxes;
	private Object grid[][];

	public Tracking(int numBoxes) {
		// create new 2-d array, all entries null
		//this.moves = moves;
		this.numBoxes = numBoxes;
		grid = new Location[moves][numBoxes];
		currentMove = 0;
	}


	public Object[][] getGrid(){
		return grid;
	}
	
	/**
	 * Number of boxes in the level.
	 */
	public int getNumBoxes() {
		return numBoxes;
	}

	/**
	 * Number of moves.
	 */
	public int getNumMoves() {
		return moves;
	}
	
	public void setNextMove() {
		currentMove++;
	}
	
	public int getCurrentMove(){
		return currentMove;
	}
	

	/**
	 * Not a usual grid operation, but one that is useful for our game. It just
	 * randomly chooses row and column values from the valid range for this grid
	 * and return the element at that location. This is handy when you just need
	 * to pick a random square from the playing board, such as when moving the
	 * Jumper around.
	 */
	public Object randomElement() {
		int row = (int) (Math.random() * moves);
		int col = (int) (Math.random() * numBoxes);
		return grid[row][col];
	}

	/**
	 * 
	 */
	public void copyLocationsNextMove() {
		
		int previousMove = currentMove - 1;
		for (int box = 0; box < numBoxes; box++)
		{	
			grid[currentMove][box] = grid[previousMove][box];
		}
	}
	
	/**
	 * Sets an element at a location in grid, overwriting any previous contents.
	 * If location is out of bounds for this grid, an exception is thrown (just
	 * like on array or vector access)
	 */
	public void setElementAt(Location loc, int boxNum) {

		grid[currentMove][boxNum] = loc;
	}
	
}
