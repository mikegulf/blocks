//---------------------------------------------------------------80 columns---|

/* Thing.java
 * --------------
 */
abstract class Thing {
	protected Square square;
	protected Blocks game;
	protected char pch;

	public Thing(Square sq, Blocks g, char ch) {
		square = sq;
		game = g;
		pch = ch; 
	}

	public void drawImage(Location loc) {
		game.drawAtLocation(getImageName(), pch, loc);
	}

	public void drawSelf(Location loc) {
		drawImage(loc);
	}

	abstract public String getImageName();

	public Location getLocation() {
		return square.getLocation();
	}

	public Square getSquare() {
		return square;
	}

	public void setSquare(Square sq) {
		square = sq;
	}
}
