
public class Box extends Thing {
	
	public Box(Square sq, Blocks g, char ch) {
		super(sq, g, ch);
	}
	public Box(Square sq, Blocks g, char ch, boolean isHidden) {
		super(sq, g, ch);
		
		setHidden(isHidden);
	}
	
	public String getImageName() {
		return "Box";
	}
}
