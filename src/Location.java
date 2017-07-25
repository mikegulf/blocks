//---------------------------------------------------------------80 columns---|

/* Location class
 * --------------
 * Just a simple row/col Location, not much more than a glorified structure.
 * A few helpful methods to create an adjacent location and compare two
 * locations for equality. Not much else.
 * You should not need to edit this class, you only need to understand
 * how to use it.
 */

public class Location {
	// public class constants ("enums")
	public static final int North = 0;
	public static final int East = 1;
	public static final int South = 2;
	public static final int West = 3;
	public static final int Northwest = 4;
	public static final int Northeast = 5;
	public static final int Southwest = 6;
	public static final int Southeast = 7;

	/**
	 * Creates a returns the opposite direction.
	 */
	public static int reverseDirection(int whichDir) {
		switch (whichDir) {
		case North:
			return South;
		case South:
			return North;
		case East:
			return West;
		case West:
			return East;
		case Northeast:
			return Southwest;
		case Northwest:
			return Southeast;
		case Southeast:
			return Northwest;
		case Southwest:
			return Northeast;
		default:
			throw new IndexOutOfBoundsException();
		}
	}

	// two private instance variables
	private int row, col;

	public Location(int r, int c) {
		row = r;
		col = c;
	}

	/**
	 * Creates a returns a new location which is the neighbor to the receiving
	 * location object in the direction specified by the parameter. If
	 * parameters is not one of the major directions listed above, this method
	 * raises an exception.
	 */
	public Location adjacentLocation(int whichDir) {
		switch (whichDir) {
		case North:
			return new Location(row + 1, col);
		case South:
			return new Location(row - 1, col);
		case East:
			return new Location(row, col + 1);
		case West:
			return new Location(row, col - 1);
		case Northeast:
			return new Location(row + 1, col + 1);
		case Northwest:
			return new Location(row + 1, col - 1);
		case Southeast:
			return new Location(row - 1, col + 1);
		case Southwest:
			return new Location(row - 1, col - 1);
		default:
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Overrides Object implementation of equals to test for equality on row and
	 * col, not just on Object pointer.
	 */
	public boolean equals(Object o) {
		return ((o instanceof Location) && ((Location) o).row == this.row && ((Location) o).col == this.col);
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

	/**
	 * Overrides Object implementation for debugging and what not. If you
	 * println a location, it uses toString to convert to a string first. This
	 * may be handy for debugging purposes.
	 */
	public String toString() {
		return col + "," + row;
	}
}
