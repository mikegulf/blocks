
abstract class Thing {
	protected Square square;
	protected Blocks game;
	protected char pch;
	protected boolean isHidden;

	public Thing(Square sq, Blocks g, char ch) {
		square = sq;
		game = g;
		pch = ch; 
		isHidden = false;
	}

	public void drawImage(Location loc) {
		game.drawAtLocation(getImageName(), isHidden ? 0 : pch, loc);
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
	
	public void setHidden(boolean hide) {
		isHidden = hide;
	}
	
	public void unHide() {
		setHidden(false);
	}
}
