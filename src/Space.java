
public class Space extends Square {
	public Space(Location loc, Blocks g, char ch) {
		super(loc, g, ch);
	}
	
	public String getImageName() {
		return "Space";
	}
	
	public boolean canEnter() {
		return false;
	}
}
