
public class Wall extends Square {
	public Wall(Location loc, Blocks g, char ch) {
		super(loc, g, ch);
	}
	
	public String getImageName() {
		return "Wall";
	}
	
	public boolean canEnter() {
		return false;
	}
}
